package ec.edu.uteq.appweb.biblioteca.security;

import ec.edu.uteq.appweb.biblioteca.domain.Rol;
import ec.edu.uteq.appweb.biblioteca.domain.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * ============================================================================
 * TODO-U4-2 (Objetivo especifico 2 de la Guia): AUTENTICACION JWT STATELESS
 * ============================================================================
 *
 * Implemente esta clase con la libreria jjwt 0.13.0, ya declarada en el pom.
 *
 * Lo que debe emitir el token:
 *   - sub  : el username
 *   - rol  : el rol del usuario (ADMIN, BIBLIOTECARIO o LECTOR)
 *   - jti  : identificador unico del token (UUID), necesario para revocarlo
 *   - iat  : fecha de emision
 *   - exp  : fecha de expiracion, tomada de app.jwt.expiracion-minutos
 *
 * Firma: HMAC-SHA256 o superior, con la clave de app.jwt.secreto.
 * La clave NO se escribe en el codigo ni en application.yml con valor real:
 * se inyecta por variable de entorno. Un secreto versionado en Git invalida
 * el esquema completo.
 *
 * Metodos sugeridos:
 *   String generar(Usuario usuario)
 *   String extraerUsername(String token)
 *   String extraerRol(String token)
 *   String extraerJti(String token)
 *   boolean esValido(String token)
 *   long expiracionEnSegundos()
 *
 * Pista de arranque con jjwt 0.13.x:
 *   SecretKey clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
 *   String jwt = Jwts.builder()
 *           .subject(usuario.getUsername())
 *           .claim("rol", usuario.getRol().name())
 *           .id(UUID.randomUUID().toString())
 *           .issuedAt(Date.from(ahora))
 *           .expiration(Date.from(ahora.plus(duracion)))
 *           .signWith(clave)
 *           .compact();
 */
@Service
public class JwtService {

    // TODO-U4-2: inyecte @Value("${app.jwt.secreto}") y @Value("${app.jwt.expiracion-minutos}")

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${app.jwt.secreto}") String secreto,
                      @Value("${app.jwt.expiracion-minutos}") long expiracionMinutos) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
        this.expiracionMs = expiracionMinutos * 60 * 1000;
    }

    public String generar(Usuario usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionMs);

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("rol", usuario.getRol().name())
                .id(UUID.randomUUID().toString())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(clave)
                .compact();
    }

    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    public boolean esValido(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
