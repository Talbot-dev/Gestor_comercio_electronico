package com.app.config;

import com.app.usuario.model.Rol;
import com.app.usuario.model.Usuario;
import com.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("manual")
@RequiredArgsConstructor
public class AdminBootstrapConfiguration {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner createDefaultAdminIfNotExists() {
        return args -> {
            String nombreAdmin = "admin";
            String passwordPlano = "admin123";

            Usuario usuario = usuarioRepository.findAnyByNombreIgnoreCase(nombreAdmin).orElse(null);

            if (usuario == null) {
                usuario = new Usuario();
                usuario.setNombre(nombreAdmin);
                usuario.setCiudad("Pruebas");
                usuario.setRol(Rol.ADMIN);
                usuario.setIsActive(true);
            }

            usuario.setPassword(passwordEncoder.encode(passwordPlano));
            usuario.setCiudad("Pruebas");
            usuario.setRol(Rol.ADMIN);
            usuario.setIsActive(true);
            usuario.setDeletedAt(null);

            usuarioRepository.save(usuario);
        };
    }
}

