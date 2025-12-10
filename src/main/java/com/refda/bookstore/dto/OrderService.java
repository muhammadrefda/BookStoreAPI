package com.refda.bookstore.service;

import com.refda.bookstore.dto.OrderItemRequest;
import com.refda.bookstore.model.*;
import com.refda.bookstore.repository.BookRepository;
import com.refda.bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional // PENTING: Menjamin transaksi Atomic (All or Nothing)
    public Order createOrder(User user, List<OrderItemRequest> itemRequests) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING); // Default status
        order.setItems(new ArrayList<>());

        BigDecimal grandTotal = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : itemRequests) {
            // 1. Ambil data buku
            Book book = bookRepository.findById(itemReq.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found: " + itemReq.getBookId()));

            // 2. Validasi Stok
            if (book.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException("Stok tidak cukup untuk buku: " + book.getTitle());
            }

            // 3. Kurangi Stok
            book.setStock(book.getStock() - itemReq.getQuantity());
            bookRepository.save(book); // Update stok di DB

            // 4. Hitung Harga
            BigDecimal subTotal = book.getPrice().multiply(new BigDecimal(itemReq.getQuantity()));
            grandTotal = grandTotal.add(subTotal);

            // 5. Masukkan ke list items order
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(book.getPrice()); // Harga saat beli (snapshot)

            order.getItems().add(orderItem);
        }

        order.setTotalPrice(grandTotal);
        return orderRepository.save(order);
    }

    // Simulasi Pembayaran
    public Order payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Cek status biar gak double pay
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order is already processed");
        }

        order.setStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }
}