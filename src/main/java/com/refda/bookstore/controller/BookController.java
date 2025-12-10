package com.refda.bookstore.controller;

import com.refda.bookstore.model.Book;
import com.refda.bookstore.model.Category;
import com.refda.bookstore.repository.BookRepository;
import com.refda.bookstore.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        if (book.getCategory() != null && book.getCategory().getId() != null) {
            Category cat = categoryRepository.findById(book.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            book.setCategory(cat);
        }
        return ResponseEntity.ok(bookRepository.save(book));
    }

    @GetMapping
    public Page<Book> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId) {

        Pageable pageable = PageRequest.of(page, size);

        if (categoryId != null) {
            return bookRepository.findByCategoryId(categoryId, pageable);
        } else if (search != null && !search.isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(search, search, pageable);
        } else {
            return bookRepository.findAll(pageable);
        }
    }

    // GET Detail
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT: Update (Admin Only)
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(bookDetails.getTitle());
                    book.setAuthor(bookDetails.getAuthor());
                    book.setPrice(bookDetails.getPrice());
                    book.setStock(bookDetails.getStock());
                    book.setYear(bookDetails.getYear());
                    book.setImageBase64(bookDetails.getImageBase64());

                    if(bookDetails.getCategory() != null) {
                        Category cat = categoryRepository.findById(bookDetails.getCategory().getId())
                                .orElseThrow(() -> new RuntimeException("Category not found"));
                        book.setCategory(cat);
                    }

                    return ResponseEntity.ok(bookRepository.save(book));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: Admin Only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.delete(book);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getBookImage(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        String base64Image = book.getImageBase64();

        if (base64Image == null || base64Image.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String mimeType = "image/png"; // fallback default
        if (base64Image.startsWith("data:") && base64Image.contains(";")) {
            mimeType = base64Image.substring(
                    base64Image.indexOf(":") + 1,
                    base64Image.indexOf(";")
            );
        }

        String cleanBase64 = base64Image;
        if (base64Image.contains(",")) {
            cleanBase64 = base64Image.split(",")[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(cleanBase64);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .body(imageBytes);
    }

}