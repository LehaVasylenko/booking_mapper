package com.example.demo.service;

import com.example.demo.mapper.OrderMapper;
import com.example.demo.model.DrugEntity;
import com.example.demo.model.ShopPoint;
import com.example.demo.model.UserDB;
import com.example.demo.model.api.BookingObject;
import com.example.demo.model.api.order_geoapteka.Order;
import com.example.demo.model.api.order_geoapteka.OrderPreps;
import com.example.demo.model.api.order_tabletki.*;
import com.example.demo.model.order.OrderDb;
import com.example.demo.model.order.PrepsInOrderDb;
import com.example.demo.model.order.State;
import com.example.demo.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 08.08.2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    UserRepository userRepository;
    ShopPointRepository shopPointRepository;
    OrderRepository orderRepository;
    DrugEntityRepository drugEntityRepository;
    StateRepository stateRepository;

    OrderMapper mapperService;
    OrderWriterService orderWriterService;
    BookingService service;

    @Async
    public CompletableFuture<List<TabletkiNewOrder>> processNewOrders(String branchId, String username) {
        Optional<UserDB> byUsername = userRepository.findByUsername(username);
        if (byUsername.isPresent()) {
            UserDB userDb = byUsername.get();
            Optional<ShopPoint> shop = getShopPoint(userDb, branchId);

            if (shop.isPresent()) {
                BookingObject askBookingBody = createBookingObject(userDb, shop.get(), branchId);
                List<TabletkiNewOrder> returnResult = new ArrayList<>();
                try {
                    List<Order> orders = Arrays
                            .stream(service.askBooking(askBookingBody).get())
                            .filter(order -> !order.getTest())
                            .toList();
                    for (Order order : orders) {
                        returnResult.add(mapperService.mapNew(orderWriterService.saveOrder(order, username).get(), order, username, branchId));
                    }
                    return CompletableFuture.completedFuture(returnResult);
                } catch (NullPointerException e) {
                    return CompletableFuture.completedFuture(new ArrayList<>());
                } catch (ExecutionException | InterruptedException e) {
                    log.error(e.getMessage());
                    return CompletableFuture.completedFuture(new ArrayList<>());
                }
            }
        }
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    private Optional<ShopPoint> getShopPoint(UserDB userDb, String branchId) {
        if (!branchId.equals("all")) {
            return shopPointRepository
                    .findByMorionCorpId(userDb.getMorionCorpId())
                    .stream()
                    .filter(shopPoint -> shopPoint.getShopExtId().equals(branchId))
                    .findFirst();
        } else {
            return shopPointRepository
                    .findByMorionCorpId(userDb.getMorionCorpId())
                    .stream()
                    .findFirst();
        }
    }

    private BookingObject createBookingObject(UserDB userDb, ShopPoint shopPoint, String branchId) {
        return BookingObject.builder()
                .payload(switch (branchId) {
                    case "all" -> List.of("*");
                    default -> List.of(shopPoint.getMorionShopId());
                })
                .morionKey(userDb.getMorionKey())
                .morionLogin(userDb.getMorionLogin())
                .build();
    }


    @Async
    public CompletableFuture<TabletkiResponseProcess> processOrders(List<TabletkiProcessOrder> orders, String username) {
        List<CompletableFuture<ProcessedDoc>> futures = orders.stream()
                .map(order -> processOrder(username, order))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<ProcessedDoc> results = futures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                    return TabletkiResponseProcess.builder()
                            .processedDocs(results)
                            .description("OK")
                            .build();
                });
    }

    @Async
    public CompletableFuture<TabletkiResponseProcess> processCancelReasonsToOrders(List<TabletkiCancelReason> cancelReasons, String username) {
        List<CompletableFuture<ProcessedDoc>> futures = cancelReasons.stream()
                .map(reason -> CompletableFuture.supplyAsync(() -> orderRepository.findById(UUID.fromString(reason.getId())))
                        .thenCompose(orderDbOpt -> orderDbOpt.map(orderDb -> {
                            State state = orderDb.getStates().get(orderDb.getStates().size() - 1);
                            if (!state.getState().equals("Canceled")) {
                                return CompletableFuture.completedFuture(
                                        ProcessedDoc.builder()
                                                .id(reason.getId())
                                                .result("Order with id '" + reason.getId() + "' not cancelled!")
                                                .build()
                                );
                            } else {
                                if (state.getReason().isEmpty()) {
                                    state.setReason(switch ((int) reason.getIdCancelReason()) {
                                        case 1 -> "Відмова споживача";
                                        case 2 -> "Вийшов термін бронювання";
                                        case 3 -> "Немає рецепту";
                                        case 5 -> "Недостатня кількість (по даним облікової системи)";
                                        case 6 -> "Недостатня кількість (під час комплектації по запиту на бронювання)";
                                        case 9 -> "Немає зв'язку з аптекою/магазином";
                                        default -> "Шось інше";
                                    });
                                    stateRepository.save(state);
                                    return CompletableFuture.completedFuture(
                                            ProcessedDoc.builder()
                                                    .id(reason.getId())
                                                    .result("SUCCESS")
                                                    .build()
                                    );
                                } else return CompletableFuture.completedFuture(
                                        ProcessedDoc.builder()
                                                .id(reason.getId())
                                                .result("Already received reason: " + state.getReason())
                                                .build()
                                );
                            }
                        }).orElse(CompletableFuture.completedFuture(
                                ProcessedDoc.builder()
                                        .id(reason.getId())
                                        .result("No order with id '" + reason.getId() + "' found!")
                                        .build()
                        )))
                ).toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<ProcessedDoc> results = futures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                    return TabletkiResponseProcess.builder()
                            .processedDocs(results)
                            .description("OK")
                            .build();
                });
    }

    @Async
    public CompletableFuture<ProcessedDoc> processOneOrder(TabletkiProcessOrder order, String username) {
        return processOrder(username, order);
    }

    @Async
    public CompletableFuture<ProcessedDoc> processOrder(String username, TabletkiProcessOrder order) {
        if (order.getStatusID() == 2) {
            if (!order.getExternalNmb().isEmpty()) {
                int i = orderRepository.updateShopOrderId(order.getExternalNmb(), UUID.fromString(order.getId()));
                log.info("{} updated: {} -> {}", i, order.getId(), order.getExternalNmb());
            }
            return CompletableFuture.completedFuture(
                    ProcessedDoc.builder()
                            .id(order.getId())
                            .result("SUCCESS")
                            .build()
            );
        } else if (order.getStatusID() != 4 && order.getStatusID() != 6 && order.getStatusID() != 7) {
            return CompletableFuture.completedFuture(
                    ProcessedDoc.builder()
                            .id(order.getId())
                            .result("Unexpected StateId value: " + order.getStatusID())
                            .build()
            );
        } else {
            return CompletableFuture.supplyAsync(() -> UUID.fromString(order.getId()))
                    .thenCompose(orderId -> orderRepository.findById(orderId)
                            .map(orderDb -> {
                                if (orderDb.getStates().get(orderDb.getStates().size() - 1).getState().equals("Completed") ||
                                        orderDb.getStates().get(orderDb.getStates().size() - 1).getState().equals("Canceled")) {
                                    return CompletableFuture.completedFuture(
                                            ProcessedDoc.builder()
                                                    .id(order.getId())
                                                    .result("You can't change Completed or Canceled state!")
                                                    .build()
                                    );
                                } else {
                                    Order request = Order.builder()
                                            .agent(orderDb.getAgent())
                                            .phone(orderDb.getPhone())
                                            .idShop(orderDb.getShopId())
                                            .extidShop(orderDb.getShopExtId())
                                            .idExtOrder(orderDb.getShopExtOrderId())
                                            .test(false)
                                            .idOrder(orderDb.getMorionOrderId())
                                            .state(switch ((int) order.getStatusID()) {
                                                case 4 -> "Confirmed";
                                                case 6 -> "Completed";
                                                default -> "Canceled";
                                            })
                                            .shipping(orderDb.getShipping())
                                            .reason(order.getCancelReason() != null ? order.getCancelReason() : "")
                                            .data(getPrepsInOrder(order.getRows(), orderDb, username))
                                            .build();
                                    orderWriterService.saveOrder(request, username);

                                    return userRepository.findByUsername(username)
                                            .map(userDb -> service.tellBooking(request, BookingObject.builder()
                                                            .payload(null)
                                                            .morionKey(userDb.getMorionKey())
                                                            .morionLogin(userDb.getMorionLogin())
                                                            .build())
                                                    .thenApply(result -> ProcessedDoc.builder()
                                                            .id(order.getId())
                                                            .result(result)
                                                            .build())
                                            )
                                            .orElse(CompletableFuture.completedFuture(
                                                    ProcessedDoc.builder()
                                                            .id(order.getId())
                                                            .result("User not found")
                                                            .build()));
                                }
                            })
                            .orElse(CompletableFuture.completedFuture(
                                    ProcessedDoc.builder()
                                            .id(order.getId())
                                            .result("No such order id")
                                            .build()
                            ))
                    );
        }
    }
    private List<OrderPreps> getPrepsInOrder(List<RowIncoming> rows, OrderDb baseOrder, String username) {
        return rows.stream()
                .map(input -> OrderPreps.builder()
                        .id(findMorionDrugId(baseOrder, input.getGoodsCode(), username))
                        .extId(input.getGoodsCode())
                        .price(input.getPriceShip())
                        .quant(input.getQtyShip())
                        .build())
                .collect(Collectors.toList());
    }

    private String findMorionDrugId(OrderDb baseOrder, String extId, String username) {
        List<PrepsInOrderDb> prepsInOrder = baseOrder
                .getStates()
                .get(0)
                .getPrepsInOrder();

        for (PrepsInOrderDb preps: prepsInOrder) {
            try {
                if (preps.getExtId().equals(extId)) return preps.getMorionId();
            } catch (NullPointerException ignored) {
                return preps.getMorionId();
            }
        }

        Optional<DrugEntity> byUserLoginAndDrugId = drugEntityRepository
                .findByUserLoginAndDrugId(username, extId);
        if (byUserLoginAndDrugId.isPresent()) {
            DrugEntity drugEntity = byUserLoginAndDrugId.get();
            return drugEntity.getMorionId() != null ? drugEntity.getMorionId() : "1";
        } else return "1";
    }


    @Async
    public CompletableFuture<List<TabletkiNewOrder>> getOrdersByDate(String branchId, String dateStr, String username) {
        LocalDateTime date = getDate(dateStr);
        return CompletableFuture.completedFuture(orderRepository
                .findByShopExtIdAndUsername(branchId, username)
                .stream()
                .filter(order -> order.getTimestamp().isAfter(date))
                .map(mapperService::mapOld)
                .toList());
    }

    private LocalDateTime getDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(dateStr, formatter);
        return date.atStartOfDay();
    }
}
