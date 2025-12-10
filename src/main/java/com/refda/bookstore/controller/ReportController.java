package com.refda.bookstore.controller;

import com.refda.bookstore.repository.BookRepository;
import com.refda.bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@PreAuthorize("hasAuthority('ADMIN')")
public class ReportController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/bestseller")
    public List<Object[]> getBestSellers() {
        // Ambil Top 3 buku terlaris
        return orderRepository.findBestSellers(PageRequest.of(0, 3));
    }

    @GetMapping("/sales")
    public Map<String, Object> getSalesReport() {
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        Long totalSold = orderRepository.calculateTotalBooksSold();

        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        if (totalSold == null) totalSold = 0L;

        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", totalRevenue);
        response.put("totalBooksSold", totalSold);

        return response;
    }

    @GetMapping("/prices")
    public Map<String, Object> getPriceStats() {
        Object[] stats = bookRepository.findPriceStats();
        Object[] data = (Object[]) stats[0];

        Map<String, Object> response = new HashMap<>();
        response.put("maxPrice", data[0]);
        response.put("minPrice", data[1]);
        response.put("avgPrice", data[2]);

        return response;
    }


}