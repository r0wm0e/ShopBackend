package org.example.shopbackend.order;

import org.example.shopbackend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByStripeSessionId(String stripeSessionId);

    List<Order> findOrderByUser(User user);
}