package com.refda.bookstore.repository;

import com.refda.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Fitur Search (Title atau Author) + Pagination
    // SELECT * FROM books WHERE title LIKE %keyword% OR author LIKE %keyword%
    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author, Pageable pageable);

    // Fitur Filter by Category
    Page<Book> findByCategoryId(Integer categoryId, Pageable pageable);
}