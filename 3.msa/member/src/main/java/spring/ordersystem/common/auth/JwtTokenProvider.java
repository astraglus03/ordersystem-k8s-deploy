package spring.ordersystem.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import spring.ordersystem.member.entity.Member;
import spring.ordersystem.member.repository.MemberRepository;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

//    @Qualifier를 사용하기 위해선 생성자 주입방법을 사용해야함.
    public JwtTokenProvider(MemberRepository memberRepository, @Qualifier("rtInventory") RedisTemplate<String, String> redisTemplate) {
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
    }

    @Value("${jwt.expirationAt}")
    private int expirationAt; // 토큰 만료 시간 (분 단위)
    @Value("${jwt.secretKeyAt}")
    private String secretKeyAt; // 비밀 키

    @Value("${jwt.expirationRt}")
    private int expirationRt; // 토큰 만료 시간 (분 단위)
    @Value("${jwt.secretKeyRt}")
    private String secretKeyRt; // 비밀 키

    private Key secret_at_key; // 비밀 키 At객체
    private Key secret_rt_key; // 비밀 키 Rt객체

    @PostConstruct
    public void init() {
        secret_at_key = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKeyAt), SignatureAlgorithm.HS512.getJcaName());
        secret_rt_key = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKeyRt), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createAtToken(Member member) {
        String email = member.getEmail();
        String role = member.getRole().toString();

        Claims claims = Jwts.claims().setSubject(email);

        claims.put("role", role); // 역할 정보 추가

        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationAt * 60 * 1000L)) // 토큰 유효기간 설정
                .signWith(secret_at_key)
                .compact();
        return accessToken;
    }

    public String createRtToken(Member member) {
        String email = member.getEmail();
        String role = member.getRole().toString();

        Claims claims = Jwts.claims().setSubject(email);

        claims.put("role", role); // 역할 정보 추가

        Date now = new Date();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationRt * 60 * 1000L)) // 토큰 유효기간 설정
                .signWith(secret_rt_key)
                .compact();

//        refreshToken을 redis에 저장 : key-value 형태로 저장
        redisTemplate.opsForValue().set(member.getEmail(),refreshToken);
//        redisTemplate.opsForValue().set(member.getEmail(),refreshToken, 200, TimeUnit.DAYS);

        return refreshToken;
    }

    public Member validateRt(String refreshToken) {

//        rt그 자체를 검증
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret_rt_key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        String email = claims.getSubject();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 회원이 존재하지 않습니다."));

//       redis에 저장된 refreshToken과 비교
        String redisAt = redisTemplate.opsForValue().get(member.getEmail());
        if (!redisAt.equals(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }


        return member;
    }
}
