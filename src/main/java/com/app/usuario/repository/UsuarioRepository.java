package com.app.usuario.repository;

import com.app.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreIgnoreCase(String nombre);

    @Query(value = "SELECT * FROM usuario WHERE LOWER(nombre) = LOWER(:nombre)", nativeQuery = true)
    Optional<Usuario> findAnyByNombreIgnoreCase(@Param("nombre") String nombre);
}
