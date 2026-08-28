package ec.edu.uteq.appweb.biblioteca.web.controller;

import ec.edu.uteq.appweb.biblioteca.domain.Usuario;
import ec.edu.uteq.appweb.biblioteca.repository.UsuarioRepository;
import ec.edu.uteq.appweb.biblioteca.security.JwtService;
import ec.edu.uteq.appweb.biblioteca.web.dto.ApiResponse;
import ec.edu.uteq.appweb.biblioteca.web.dto.LoginRequest;
import ec.edu.uteq.appweb.biblioteca.web.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * TODO-U4-2: autenticacion.
 *
 *   POST /api/v1/auth/login   recibe LoginRequest, valida con BCrypt contra
 *                             UsuarioRepository.findByUsernameAndActivoTrue,
 *                             y devuelve LoginResponse dentro de ApiResponse.
 *                             El token va en la cabecera Authorization de las
 *                             siguientes peticiones o en una cookie HttpOnly.
 *   POST /api/v1/auth/logout  invalida el token por su jti (opcional, suma en la rubrica).
 *
 * Credenciales sembradas por Flyway en V3__usuarios.sql:
 *   admin / Admin123!          rol ADMIN
 *   bibliotecario / Biblio123! rol BIBLIOTECARIO
 *   lector / Lector123!        rol LECTOR
 *
 * Un login fallido debe devolver 401, no 200 con success=false.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    // TODO-U4-2
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(request.username())
                .orElseThrow(() -> new ec.edu.uteq.appweb.biblioteca.exception.RecursoNoEncontradoException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new ec.edu.uteq.appweb.biblioteca.exception.ReglaNegocioException("Credenciales incorrectas");
        }

        String token = jwtService.generar(usuario);

        LoginResponse respuesta = new LoginResponse(
                usuario.getUsername(),
                usuario.getRol().name(),
                "Bearer",
                3600
        );

        return ResponseEntity.ok(ApiResponse.success(respuesta, "Login exitoso"));
    }
}
