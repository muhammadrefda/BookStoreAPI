package com.refda.bookstore.repository;

import com.refda.bookstore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<Entity, TipeDataID>
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Cek email ada atau ngga (buat validasi register)
    Boolean existsByEmail(String email);
}