package ec.edu.uteq.appweb.biblioteca.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ec.edu.uteq.appweb.biblioteca.security.JwtAuthenticationFilter;
/**
 * ============================================================================
 * TODO-U4-2: CADENA DE SEGURIDAD
 * ============================================================================
 *
 * Tal como esta, la aplicacion arranca con TODO abierto para que usted pueda
 * probar los controladores antes de tener el JWT listo. Eso es deliberado y
 * temporal: no se entrega asi.
 *
 * Debe dejarla en este estado final:
 *   - csrf deshabilitado (la API es stateless y no usa formularios de sesion).
 *   - SessionCreationPolicy.STATELESS.
 *   - Publicos: POST /api/v1/auth/login, /swagger-ui/**, /v3/api-docs/**,
 *     /api/docs, /actuator/health.
 *   - El resto de /api/v1/** exige autenticacion.
 *   - Registrar JwtAuthenticationFilter antes de UsernamePasswordAuthenticationFilter.
 *   - Devolver 401 cuando no hay autenticacion y 403 cuando el rol no alcanza,
 *     ambos en formato ProblemDetail.
 *
 * La autorizacion fina por rol se declara con @PreAuthorize en los controladores,
 * habilitada por @EnableMethodSecurity, que ya esta puesto.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO-U4-2: sustituir esta configuracion permisiva por la definitiva.
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/docs", "/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
