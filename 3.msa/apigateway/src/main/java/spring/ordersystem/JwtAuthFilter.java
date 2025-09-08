package spring.ordersystem;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter {

    @Value("${jwt.secretKeyAt}")
    private String secretKey;

    private static final List<String> ALLOWED_PATHS = List.of(
            "/member/create",
            "/member/login",
            "/member/refresh-at",
            "/product/list"
    );

    private static final List<String> ADMIN_ONLY_PATH = List.of(
            "/member/list",
            "/product/create"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        토큰 검증
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String UrlPath = exchange.getRequest().getURI().getRawPath();

//        인증이 필요없는 경로는 바로 통과
        if(ALLOWED_PATHS.contains(UrlPath)) {
            return chain.filter(exchange);
        }

        try {
            if (bearerToken == null ||!bearerToken.startsWith("Bearer ")) {
                throw new IllegalArgumentException("인증 토큰이 없거나, 형식이 잘못되었습니다.");
            }

            String token = bearerToken.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            String role = claims.get("role", String.class);

//            admin권한 있어야 하는 url 검증
            if( ADMIN_ONLY_PATH.contains(UrlPath) && !role.equals("ADMIN")) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

//            header에 email, role 등 Payload값 세팅
//            X를 붙이는 것은 custom header를 의미하며 관례적 키워드
//            추후 서비스모듈에서 RequestHeader어노테이션
            ServerWebExchange serverWebExchange = exchange.mutate().request(r->
                            r.header("X-User-Email", email)
                            .header("X-User-Role", role))
            .build();

            return chain.filter(serverWebExchange);

        } catch (Exception e) {
            e.printStackTrace();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
