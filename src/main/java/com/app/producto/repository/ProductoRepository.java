package com.app.producto.repository;

import com.app.producto.model.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM producto p WHERE p.productId = :productId AND p.isActive = true")
    Optional<Producto> findByIdForUpdate(@Param("productId") Long productId);

    Optional<Producto> findByName(String name);

    Optional<Producto> findByNameIgnoreCase(String name);

    @Query(value = "SELECT * FROM producto WHERE LOWER(name) = LOWER(:name)", nativeQuery = true)
    Optional<Producto> findAnyByName(@Param("name") String name);

    @Query(value = "SELECT * FROM producto WHERE LOWER(name) = LOWER(:name)", nativeQuery = true)
    Optional<Producto> findAnyByNameIgnoreCase(@Param("name") String name);
}
