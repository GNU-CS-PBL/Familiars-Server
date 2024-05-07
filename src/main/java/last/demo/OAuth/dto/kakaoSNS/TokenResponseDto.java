package last.demo.OAuth.dto.kakaoSNS;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor // 기본생성자
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자
public class TokenResponseDto { //로그인 요청시, 생성된 JWT 토큰 2개를 담아서 클라이언트로 보내는 용도

    private String accessToken;
    private String refreshToken;
    private String message;
}
