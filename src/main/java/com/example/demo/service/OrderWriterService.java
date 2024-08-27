package com.example.demo.service;

import com.example.demo.model.DrugEntity;
import com.example.demo.model.ShopPoint;
import com.example.demo.model.UserDB;
import com.example.demo.model.api.order_geoapteka.Order;
import com.example.demo.model.api.order_geoapteka.OrderPreps;
import com.example.demo.model.order.OrderDb;
import com.example.demo.model.order.PrepsInOrderDb;
import com.example.demo.model.order.State;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderWriterService {

    OrderRepository orderRepository;
    StateRepository stateRepository;
    PrepsInOrderRepository prepsInOrderRepository;
    UuidService uuidService;
    DrugEntityRepository drugEntityRepository;
    ShopPointRepository shopPointRepository;
    UserRepository userRepository;

    @Async
    @Transactional
    public CompletableFuture<String> saveOrder(Order order, String username) {
        return getOrderDb(order, username)
                .thenCompose(orderDb -> getState(order, orderDb)
                        .thenCompose(state -> savePrepsInOrder(order, state)
                                .thenApply(v -> orderDb.getTabletkiOrderId().toString())
                        )
                );
    }

    @Async
    public CompletableFuture<Void> savePrepsInOrder(Order order, State state) {
        return CompletableFuture.supplyAsync(() -> {
            List<PrepsInOrderDb> prepsInOrderDbList = new ArrayList<>();
            for (OrderPreps prep : order.getData()) {
                DrugEntity drugInfo = getDrugEntity(order, prep).join();
                PrepsInOrderDb prepsInOrderDb = PrepsInOrderDb.builder()
                        .state(state)
                        .morionId(prep.getId())
                        .extId(prep.getExtId())
                        .price(prep.getPrice())
                        .quant(prep.getQuant())
                        .drugName(drugInfo.getDrugName())
                        .drugProducer(drugInfo.getDrugProducer())
                        .build();
                prepsInOrderDbList.add(prepsInOrderDb);
            }
            prepsInOrderRepository.saveAll(prepsInOrderDbList);
            return null;
        });
    }

    @Async
    public CompletableFuture<DrugEntity> getDrugEntity(Order order, OrderPreps prep) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<ShopPoint> byShopExtId = shopPointRepository.findByShopExtId(order.getExtidShop());
            if (byShopExtId.isPresent()) {
                Optional<UserDB> byMorionCorpId = userRepository.findByMorionCorpId(byShopExtId.get().getMorionCorpId());
                if (byMorionCorpId.isPresent()) {
                    Optional<DrugEntity> byUserLoginAndDrugId = drugEntityRepository.findByUserLoginAndDrugId(byMorionCorpId.get().getUsername(), prep.getExtId());
                    if (byUserLoginAndDrugId.isPresent()) {
                        return byUserLoginAndDrugId.get();
                    }
                }
            }
            return DrugEntity.builder()
                    .drugName("No data")
                    .drugProducer("No data")
                    .build();
        });
    }

    @Async
    public CompletableFuture<State> getState(Order order, OrderDb orderDb) {
        return CompletableFuture.supplyAsync(() -> {
            State state = State.builder()
                    .order(orderDb)
                    .time(LocalDateTime.ofInstant(Instant.ofEpochSecond(order.getTimestamp() != null ? order.getTimestamp() : (System.currentTimeMillis() / 1000)), ZoneId.systemDefault()))
                    .state(order.getState())
                    .reason(order.getReason())
                    .build();
            stateRepository.save(state);
            return state;
        });
    }

    @Async
    public CompletableFuture<OrderDb> getOrderDb(Order order, String username) {
        return CompletableFuture.supplyAsync(() -> orderRepository.findByShopIdAndOrderId(order.getIdShop(), order.getIdOrder()))
                .thenCompose(optionalOrderDb -> {
                    if (optionalOrderDb.isPresent()) {
                        OrderDb orderDb = optionalOrderDb.get();
                        Hibernate.initialize(orderDb.getStates());
                        return CompletableFuture.completedFuture(orderDb);
                    } else {
                        return uuidService.generateUniqueUUID()
                                .thenCompose(uuid -> {
                                    OrderDb newOrderDb = OrderDb.builder()
                                            .tabletkiOrderId(uuid)
                                            .username(username)
                                            .morionOrderId(order.getIdOrder())
                                            .shopId(order.getIdShop())
                                            .shopExtId(order.getExtidShop())
                                            .phone(order.getPhone())
                                            .agent(order.getAgent())
                                            .timestamp(LocalDateTime.ofInstant(Instant.ofEpochSecond(order.getTimestamp()), ZoneId.systemDefault()))
                                            .shipping(order.getShipping())
                                            .build();

                                    return CompletableFuture.supplyAsync(() -> {
                                        orderRepository.save(newOrderDb);
                                        return newOrderDb;
                                    });
                                });
                    }
                });
    }
}

