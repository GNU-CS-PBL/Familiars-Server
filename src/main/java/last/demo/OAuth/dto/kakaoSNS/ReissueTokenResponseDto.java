package last.demo.OAuth.dto.kakaoSNS;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor // 기본생성자
@AllArgsConstructor // 모든 필드를 매개변수로 하는 생성자
public class ReissueTokenResponseDto {
    private String accessToken;
}
