package com.refda.bookstore.repository;

import com.refda.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author, Pageable pageable);

    Page<Book> findByCategoryId(Integer categoryId, Pageable pageable);

    @Query("SELECT MAX(b.price), MIN(b.price), AVG(b.price) FROM Book b")
    Object[] findPriceStats();
}