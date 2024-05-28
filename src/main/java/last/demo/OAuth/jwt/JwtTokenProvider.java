package last.demo.OAuth.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}") //application.properties에 설정한 Secret키를 value로 가져와서 secretKey 변수에 대입한다.
    private String secretKey;

    @Value("${JWT.ISSUER}")
    private String ISSUER; // 토큰 발급자

    //JWT 토큰 만료시간 설정
    public final Long ACCESS_TOKEN_EXPIRE_COUNT = 60 * 60 * 1000L; // 60 * 1000L = 1분  ->  1분 * 60 = 60분
    public final Long REFRESH_TOKEN_EXPIRE_COUNT = 20160 * 60 * 1000L; // 60 * 1000L = 1분  ->  1분 * 20160 = 20160분(2주)

    // jwt access 토큰 생성
    public String createAccessToken(Long userId)  {

        //payload에 첫번째 요소에 userId 넣자.
        Claims claims = Jwts.claims().setSubject(ISSUER); //claims 는 payload에 들어갈 내용 3가지를 통틀어 말한다.
        claims.put("userId", userId); // 사용자 ID 추가

        /*
        claims -- sub 부분 : 발행자 ISSUER 넣기
               -- userId 부분 : 우리 서비스 사용자의 고유 UID
         */


        //secret key 문자열을 바이트 배열로 바꿔준다.
        byte[] accessSecret = this.secretKey.getBytes(StandardCharsets.UTF_8);

        //JwtAccessToken 토큰을 생성하는 부분
        String JwtAccessToken = Jwts.builder() // builder는 JwtBuilder를 반환해준다. (Builder 패턴)
                .setHeaderParam("type", "Jwt") // 헤더에 type을 추가함
                .setClaims(claims) // claims를 추가된 JwtBuilder를 반환해준다.
                .setIssuedAt(new Date()) //현재 시간이 추가된 JwtBuilder를 반환해준다.
                .setExpiration(new Date(new Date().getTime() + this.ACCESS_TOKEN_EXPIRE_COUNT)) // 현재시간으로 부터 30분 뒤에 만료되는 시간이 추가된
                                                                                                // JwtBuilder를 반환
                .signWith(Keys.hmacShaKeyFor(accessSecret)) //바이트화된 시크릿키를 HMAC이라는 알고리즘으로 서명을 해준 -> JwtBulder가 반환된다.
                .compact(); //JwtBuilder 객체를 사용하여 JWT 문자열을 생성

        // 생성된 토큰의 앞뒤 공백 제거
        JwtAccessToken = JwtAccessToken.trim();

        return JwtAccessToken; // 문자열로 이루어짐
    }

    // JwtRefreshToken 토큰 생성 (refresh 토큰에는 만료 기간 외에 별다른 정보를 담아주지 않았습니다.)
    public String createRefreshToken(long userId) {

        //payload에 첫번째 요소에 userId 넣자.
        Claims claims = Jwts.claims().setSubject(ISSUER); //claims 는 payload에 들어갈 내용 3가지를 통틀어 말한다.
        claims.put("userId", userId); //payload 두번 째 요소에 빈 스트링 넣자.

        /*
        claims -- sub 부분 : 발행자 ISSUER 넣기
               -- userId 부분 : 우리 서비스 사용자의 고유 UID
         */

        //secret key 문자열을 바이트 배열로 바꿔준다.
        byte[] accessSecret = this.secretKey.getBytes(StandardCharsets.UTF_8);


        String JwtRefressToken = Jwts.builder() // builder는 JwtBuilder를 반환해준다. (Builder 패턴)
                .setHeaderParam("type", "Jwt") // 헤더에 type을 추가함
                .setClaims(claims) // claims를 추가된 JwtBuilder를 반환해준다.
                .setIssuedAt(new Date()) //현재 시간이 추가된 JwtBuilder를 반환해준다.
                .setExpiration(new Date(new Date().getTime() + this.REFRESH_TOKEN_EXPIRE_COUNT)) // 현재시간으로 부터 2주 뒤에 만료되는 시간이 추가된
                // JwtBuilder를 반환
                .signWith(Keys.hmacShaKeyFor(accessSecret)) //바이트화된 시크릿키를 HMAC이라는 알고리즘으로 서명을 해준 -> JwtBulder가 반환된다.
                .compact(); //JwtBuilder 객체를 사용하여 JWT 문자열을 생성

        // 생성된 토큰의 앞뒤 공백 제거
        JwtRefressToken = JwtRefressToken.trim();

        return JwtRefressToken; // 문자열로 이루어짐
    }
}
