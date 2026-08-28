package ec.edu.uteq.appweb.biblioteca.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * ============================================================================
 * TODO-U4-2: FILTRO QUE AUTENTICA CADA PETICION A PARTIR DEL JWT
 * ============================================================================
 *
 * Debe, en este orden:
 *   1. Leer el token de la cabecera Authorization: Bearer &lt;token&gt;
 *      (opcionalmente tambien de una cookie HttpOnly llamada access_token).
 *   2. Si no hay token, dejar pasar la peticion sin autenticar: el filtro NO
 *      rechaza, de eso se encarga la cadena de seguridad.
 *   3. Si hay token y es valido, construir un UsernamePasswordAuthenticationToken
 *      con las autoridades derivadas del claim rol, prefijadas con "ROLE_",
 *      y colocarlo en el SecurityContextHolder.
 *   4. Si el token es invalido o expiro, limpiar el contexto y continuar.
 *
 * Cuidado con un error frecuente: si escribe la respuesta de error aqui dentro,
 * se rompe el contrato de ProblemDetail que ya implementa GlobalExceptionHandler.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest peticion,
                                    HttpServletResponse respuesta,
                                    FilterChain cadena) throws ServletException, IOException {

        String header = peticion.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtService.esValido(token)) {
                String username = jwtService.extraerUsername(token);
                String rol = jwtService.extraerRol(token);

                List<SimpleGrantedAuthority> autoridades = List.of(
                        new SimpleGrantedAuthority("ROLE_" + rol)
                );

                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(username, null, autoridades);
                autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(peticion));

                SecurityContextHolder.getContext().setAuthentication(autenticacion);
    }
        }

        cadena.doFilter(peticion, respuesta);
    }
}
