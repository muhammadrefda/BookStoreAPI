package com.refda.bookstore.repository;

import com.refda.bookstore.model.Order;
import com.refda.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    @Query("SELECT i.book, SUM(i.quantity) as totalSold FROM OrderItem i GROUP BY i.book ORDER BY totalSold DESC")
    List<Object[]> findBestSellers(Pageable pageable);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = 'PAID'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT SUM(i.quantity) FROM OrderItem i JOIN i.order o WHERE o.status = 'PAID'")
    Long calculateTotalBooksSold();
}