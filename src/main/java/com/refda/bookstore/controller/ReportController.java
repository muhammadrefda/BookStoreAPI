package com.refda.bookstore.controller;

import com.refda.bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
@PreAuthorize("hasAuthority('ADMIN')") // Hanya Admin
public class ReportController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/bestseller")
    public List<Object[]> getBestSellers() {
        // Ambil Top 3 buku terlaris
        return orderRepository.findBestSellers(PageRequest.of(0, 3));
    }

    // Kamu bisa tambahkan endpoint Sales/Omzet disini dengan logic serupa
}