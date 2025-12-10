package com.refda.bookstore.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "books")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    private BigDecimal price;

    private Integer stock;
    private Integer year;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String imageBase64;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}