package org.example.shopbackend.order;

import lombok.AllArgsConstructor;
import org.example.shopbackend.user.User;
import org.example.shopbackend.user.UserProfileOrderDTO;
import org.example.shopbackend.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
@AllArgsConstructor
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/all")
    public ResponseEntity<List<UserProfileOrderDTO>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<UserProfileOrderDTO> orders = orderService.getOrdersByUsername(user);

        return ResponseEntity.ok(orders);
    }
}
