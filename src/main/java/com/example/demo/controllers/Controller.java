package com.example.demo.controllers;

import com.example.demo.model.api.order_tabletki.*;
import com.example.demo.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * demo
 * Author: Vasylenko Oleksii
 * Date: 08.08.2024
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Controller {
    OrderService service;

    @GetMapping("/orders/{branchId}/0")
    public CompletableFuture<List<TabletkiNewOrder>> getNewOrder(@PathVariable String branchId,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        return service.processNewOrders(branchId, userDetails.getUsername());
    }

    @PostMapping("/orders")
    public CompletableFuture<TabletkiResponseProcess> processOrders(@RequestBody List<TabletkiProcessOrder> orders,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return service.processOrders(orders, userDetails.getUsername());
    }

    @PostMapping("/orders/newStatus")
    public CompletableFuture<ProcessedDoc> processOrder(@RequestBody TabletkiProcessOrder order,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        return service.processOneOrder(order, userDetails.getUsername());
    }


    @PostMapping("/orders/cancelledOrders")
    public CompletableFuture<TabletkiResponseProcess> describeCancelReasonOrder(@RequestBody List<TabletkiCancelReason> cancelReasons,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return service.processCancelReasonsToOrders(cancelReasons, userDetails.getUsername());
    }

    @GetMapping("/api/OrdersByDate/{branchID}/{dateStr}")
    public CompletableFuture<List<TabletkiNewOrder>> getOrdersByDate(@PathVariable String branchId,
                                                                     @PathVariable String dateStr,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {
        return service.getOrdersByDate(branchId, dateStr, userDetails.getUsername());
    }
}
