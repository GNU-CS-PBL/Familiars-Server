package last.demo.OAuth.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter {

    private final JwtTokenValidator jwtTokenValidator;

    // 로그아웃 상황 : redis에 있는 refreshToken을 삭제하는 명령을 실행하기 전에,
    //               클라이언트 측에서 Header rkqtpd 올바른 jwtAccessToken을 넘겼는지를 확인하는 사전 단계
    public void doFilterInternal(String result) throws ServletException, IOException {
        String jwtAceessToken = "";
        try {
            String jwtAccessToken = result;

            // 추출한 jwtAccessToken이 null값이 아니면서, 유효성이 인증된다면
            if (StringUtils.hasText(jwtAceessToken) && jwtTokenValidator.validateToken(jwtAceessToken)) {

            }
        } catch (NullPointerException | IllegalStateException e) {
            log.error("Not found jwtAceessToken // token : {토큰을 찾을 수 없다.}", jwtAceessToken);
            throw new RuntimeException("throw new not found token exception", e);
        } catch (MalformedJwtException e) {
            log.error("Invalid jwtAceessToken // token : {유효하지 않은 토큰이다.}", jwtAceessToken);
            throw new RuntimeException("throw new invalid token exception", e);
        } catch (ExpiredJwtException e) {
            log.error("EXPIRED jwtAceessToken // token : {만료된 토큰이다.}", jwtAceessToken);
            throw new RuntimeException("throw new expired token exception", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported jwtAceessToken // token : {}", jwtAceessToken);
            throw new RuntimeException("throw new unsupported token exception", e);
        } catch (Exception e) {
            log.error("====================================================");
            log.error("JwtFilter - doFilterInternal() 오류 발생");
            log.error("jwtAceesstoken : {}", jwtAceessToken);
            log.error("Exception Message : {}", e.getMessage());
            log.error("Exception StackTrace : {");
            e.printStackTrace();
            log.error("}");
            log.error("====================================================");
            throw new RuntimeException("throw new exception", e);
        }
    }

}
