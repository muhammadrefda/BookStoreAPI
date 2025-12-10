package com.refda.bookstore.controller;

import com.refda.bookstore.dto.OrderRequest;
import com.refda.bookstore.model.Order;
import com.refda.bookstore.model.User;
import com.refda.bookstore.repository.OrderRepository;
import com.refda.bookstore.repository.UserRepository;
import com.refda.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // POST /orders (User Only)
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        // Ambil User yang sedang login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Order order = orderService.createOrder(user, orderRequest.getItems());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /orders (User lihat punya sendiri, Admin lihat semua)
    @GetMapping
    public List<Order> getAllOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // Cek Role
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            return orderRepository.findAll(); // Admin lihat semua
        } else {
            User user = userRepository.findByEmail(email).get();
            return orderRepository.findByUser(user); // User lihat punya sendiri
        }
    }

    // POST /orders/{id}/pay (Simulasi Bayar)
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long id) {
        try {
            Order order = orderService.payOrder(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        return orderRepository.findById(id)
                .map(order -> {
                    if (!isAdmin && !order.getUser().getEmail().equals(email)) {
                        return ResponseEntity.status(403).body("Unauthorized: Not your order");
                    }
                    return ResponseEntity.ok(order);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}