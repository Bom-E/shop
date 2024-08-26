import com.bom.shop.security.jwtFacadePattern.JwtProperties;
import com.bom.shop.security.jwtFacadePattern.JwtTokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtTokenGeneratorTest{

    @Mock
    private JwtProperties jwtProperties;

    private JwtTokenGenerator jwtTokenGenerator;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        when(jwtProperties.getSecretKey()).thenReturn("testSecretKey");
        when(jwtProperties.getRefreshSecretKey()).thenReturn("testRefreshSecretKey");
        when(jwtProperties.getAccessExpireTime()).thenReturn(3600000L);
        when(jwtProperties.getRefreshExpireTime()).thenReturn(86400000L);

        jwtTokenGenerator = new JwtTokenGenerator(jwtProperties);
    }

    @Test
    void testGenerateToken(){
        String userEmail = "test@google.com";
        Collection<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
                , new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        String token = jwtTokenGenerator.generateToken(userEmail, authorities);

        assertNotNull(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(userEmail, claims.getSubject());
        assertTrue(claims.getExpiration().after(new Date()));
        List<String> roles = claims.get("roles", List.class);
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));

        long expectedExpiration = claims.getIssuedAt().getTime() + jwtProperties.getAccessExpireTime();
        assertEquals(expectedExpiration, claims.getExpiration().getTime());
    }

    @Test
    void testGenerateRefreshToken(){
        String userEmail = "test@google.com";
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

        String refreshToken = jwtTokenGenerator.generateRefreshToken(userEmail, roles);

        assertNotNull(refreshToken);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getRefreshSecretKey().getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        assertEquals(userEmail, claims.getSubject());
        assertTrue(claims.getExpiration().after(new Date()));

        long expectedExpiration = claims.getIssuedAt().getTime() + jwtProperties.getRefreshExpireTime();
        assertEquals(expectedExpiration, claims.getExpiration().getTime());

        assertNull(claims.get("roles"));
    }

}