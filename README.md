# Bookstore REST API (Level 2 Technical Test)

Backend RESTful API untuk sistem Toko Buku Online dengan fitur Authentication (JWT), manajemen stok buku, transaksi order (atomic), dan pelaporan sederhana.

Project ini dibangun menggunakan **Spring Boot 3.4** dan **Java 21**, mengimplementasikan arsitektur Controller-Service-Repository.

## 🛠 Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.4.0
- **Database:** MariaDB (Compatible with MySQL)
- **ORM:** Spring Data JPA (Hibernate)
- **Security:** Spring Security + JWT (JSON Web Token)
- **Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Tools:** Maven, Lombok

## 🚀 Fitur Utama
### 1. Authentication & Authorization
- Register & Login (User / Admin).
- JWT Token Based Authentication.
- Role-based Access Control (RBAC).

### 2. Management (CRUD)
- **Categories:** CRUD (Admin Only).
- **Books:** - CRUD (Admin Only) support Image Base64.
    - Search by Title/Author, Filter by Category, Pagination (Public/User).
    - **Bonus:** Endpoint `GET /books/{id}/image` untuk render gambar langsung di browser.

### 3. Order Processing
- Multi-item Order support.
- **Atomic Transaction:** Stok berkurang otomatis, rollback jika stok tidak cukup.
- Validasi Stok & Kalkulasi Total Harga otomatis.
- Simulasi Payment (Change status PENDING -> PAID).

### 4. Reporting
- Admin Dashboard: Melihat Buku Terlaris (Bestseller).

---

## Cara Menjalankan Project

### 1. Persiapan Database
Pastikan MariaDB/MySQL sudah berjalan, lalu buat database kosong:
```sql
CREATE DATABASE bookstore_db;
```
### 2. Konfigurasi
Buka file src/main/resources/application.properties dan sesuaikan username/password database Anda:
````
spring.datasource.username=root
spring.datasource.password=password_anda
````

### 3. Run Application
Jalankan project menggunakan Maven atau IDE (IntelliJ IDEA):
````
./mvnw spring-boot:run
````

## Dokumentasi API (Swagger UI)
````
http://localhost:8080/swagger-ui/index.html
````

## Catatan Tambahan
Image Upload: Gambar dikirim dalam format String Base64 pada endpoint POST /books.

Validation: Validasi stok dilakukan secara real-time saat pembuatan Order. Jika salah satu item stoknya kurang, seluruh transaksi dibatalkan (Rollback).

