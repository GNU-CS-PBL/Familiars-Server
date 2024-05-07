package last.demo.OAuth.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenValidator { // jwtAccessToken 유효성 검증

    @Value("${jwt.secret}") // application.properties에 설정한 Secret키를 value로 가져와서 secretKey 변수에 대입한다.
    private String secretKey;

    // 클라이어트로 부터 받아온 jwtAccessToken이 유효한 토큰인지 검증
    // 1. 서명한 secrete키가 올바른 키였는지
    // 2. 토큰 만료시간이 지났는지
    // 둘다 정답일 경우 try 구문에서 true를 반환한다.
    public boolean validateToken(String token) {
        try {
            // 토큰에서 추출한 secret key와 기존의 secret key 비교
            String extractedSecretKey = Jwts.parserBuilder().build().parseClaimsJws(token).getSignature(); // 토큰에서 서명키(secretkey) 추출
            if (!extractedSecretKey.equals(secretKey)) {
                log.error("토큰에 내장된 서명키가 올바르지 않습니다.");
                return false;
            }
            // 토큰의 만료 시간 확인
            Date expiration = Jwts.parserBuilder().build().parseClaimsJws(token).getBody().getExpiration(); // 토큰에서 만료 시간을 추출합니다.
            return expiration.after(new Date()); //토큰의 만료 시간이 현재 시간보다 이후인지 확인하여 토큰의 유효성을 판단합니다. true 반환

        } catch (ExpiredJwtException e) { // 예외처리 1
            log.error("Expired token: {토큰 시간이 만료 되었습니다. 새로 발급받아야 합니다.}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            log.error("Invalid token: {유효하지 않은 토큰입니다.}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Exception occurred while validating token: {그 이외의 예외사항이 발생했습니다.}", e.getMessage());
            return false;
        }
    }

    // 방금 생성한 jwtRefreshToken 으로 부터 사용자 고유 UID를 꺼냄
    public Long getUserIdFromRefreshToken(String jwtAccessToken) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException {
        try {
            // 시크릿 키를 바이트 배열로 변환
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            // Base64로 디코딩된 키 생성
            Key key = Keys.hmacShaKeyFor(keyBytes);

            System.out.println("출력1");
            // JWT 토큰 파싱 및 본문(클레임) 획득
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwtAccessToken)
                    .getBody();
            System.out.println("출력2");
            // 클레임 존재 여부 확인
            if (!claims.containsKey("userId")) {
                throw new IllegalArgumentException("Token does not contain userId claim");
            }
            System.out.println("출력3");

            // 클레임 값의 유효성 검사 및 안전한 형변환
            Object userIdObject = claims.get("userId");
            if (!(userIdObject instanceof Number)) {
                throw new IllegalArgumentException("userId claim is not of number type");
            }

            System.out.println("출력4");
            // 클레임 값의 유효성 검사 및 안전한 형변환
            Number userIdNumber = (Number) userIdObject;
            System.out.println("출력5");
            return userIdNumber.longValue();
        } catch (JwtException e) {
            throw e; // 예외 다시 던지기
        } catch (Exception e) {
            throw new JwtException("Error decoding JWT token: " + e.getMessage());
        }
    }

}