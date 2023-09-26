package mate.academy.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.order.ChangeStatusDto;
import mate.academy.bookstore.dto.order.OrderDto;
import mate.academy.bookstore.dto.order.PlaceAnOrderDto;
import mate.academy.bookstore.dto.orderitem.OrderItemDto;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.service.OrderItemService;
import mate.academy.bookstore.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/orders")
@Tag(name = "Orders management", description = "Endpoint for managing orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @PostMapping
    @Operation(summary = "Create order",
            description = "Create new order if shopping cart not empty")
    public OrderDto placeAnOrder(
            @RequestBody @Valid PlaceAnOrderDto placeAnOrderDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeAnOrder(user.getId(), placeAnOrderDto);
    }

    @GetMapping
    @Operation(summary = "Get orders",
            description = "Get all orders for authenticated user")
    public List<OrderDto> getOrders(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrders(user.getId(), pageable);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update order status",
            description = "Update order status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public OrderDto updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid ChangeStatusDto changeStatusDto
    ) {
        return orderService.updateOrderStatus(id, changeStatusDto);
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get order items",
            description = "Get all order items in order by order id")
    public List<OrderItemDto> getAllOrderItemsByOrderId(
            Authentication authentication,
            @PathVariable Long orderId,
            Pageable pageable
    ) {
        User user = (User) authentication.getPrincipal();
        return orderItemService.findAllByOrderId(user.getId(), orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get order item",
            description = "Get order item by order id and item id")
    public OrderItemDto getOrderItemByOrderIdAndOrderItemId(
            Authentication authentication,
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {
        User user = (User) authentication.getPrincipal();
        return orderItemService.getByOrderIdAndItemId(user.getId(), orderId, itemId);
    }
}
