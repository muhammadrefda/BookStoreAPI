package com.refda.bookstore.repository;

import com.refda.bookstore.model.Order;
import com.refda.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // User cuma bisa lihat order miliknya sendiri
    List<Order> findByUser(User user);
    // Query untuk Bestseller (Group by buku, sum quantity, sort desc)
    @Query("SELECT i.book, SUM(i.quantity) as totalSold FROM OrderItem i GROUP BY i.book ORDER BY totalSold DESC")
    List<Object[]> findBestSellers(Pageable pageable);
}