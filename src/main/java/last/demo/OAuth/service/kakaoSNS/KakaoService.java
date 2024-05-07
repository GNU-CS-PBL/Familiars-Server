package last.demo.OAuth.service.kakaoSNS;

import last.demo.OAuth.dto.UserDto;
import last.demo.OAuth.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public interface KakaoService {

    public String getKakaoAccessToken (String code);
    public Long getIdToken(String accessToken) throws Exception ;
    public Long checkUserInfo(Long idToken) throws Exception  ;
    String createJwtAccessToken(Long userId);
    String createJwtRefreshToken(Long userId);
    Long getUserId(Long providerID);
    UserEntity saveUserInfo(UserDto userDto);

    boolean isNameAvailable(String name);
}
