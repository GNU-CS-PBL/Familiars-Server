package last.demo.OAuth.service.kakaoSNS;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import last.demo.OAuth.dto.UserDto;
import last.demo.OAuth.entity.UserEntity;
import last.demo.OAuth.jwt.JwtTokenProvider;
import last.demo.OAuth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoServiceImpl implements KakaoService {

    private final UserRepository userRepository;

    // 카카오 accessToken 발급받는 코드

    public String getKakaoAccessToken(String code) {
        String access_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";
        String client_id = "3c2fa35198e7022215b85eb3172881d4";
        String client_secret = "A9cmlg8KVAKw4097CW8BiNiiuOswAjq7";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // POST 요청 설정
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // 요청 파라미터 설정
            StringBuilder postData = new StringBuilder();
            postData.append("grant_type=authorization_code");
            postData.append("&client_id=").append(client_id);
            postData.append("&client_secret=").append(client_secret);
            postData.append("&redirect_uri=http://localhost:8080/callback");
            postData.append("&code=").append(code);

            // 요청 바디에 파라미터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");
                os.write(postDataBytes);
                os.flush();
            }

            // 응답 코드 확인
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            // 응답 내용 읽기
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println("response body : " + response.toString());
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }



    // #1 로그인 요청시 받은 accessToken으로 사용자의 idToken을 받아오는 메소드 ------------------------------------------------
    @Override
    public Long getIdToken(String accessToken) throws Exception {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        try {
            // access_token을 이용하여 사용자 정보 조회
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken); // 전송할 header 작성, access_token 전송

            // 결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            // 요청을 통해 얻은 JSON 타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            System.out.println("response body : " + result);

            // Gson 라이브러리로 JSON 파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            long idToken = element.getAsJsonObject().get("id").getAsLong();

            System.out.println("id : " + idToken);

            br.close();
            return idToken;
        } catch (IOException e) {
            throw new Exception("카카오 서버에서 사용자 정보를 가져오는 중에 문제가 발생했습니다.");
        }
    }



    // #2 idToken 과 관련한 정보가 DB에 있는지 확인하고 -> 있다면 그대로 다시 idToken을 반환하는 메소드 ---------------------------
    @Override
    public Long checkUserInfo(Long idToken) throws Exception {


        /* DB에 사용자 정보가 이미 있는지 확인
            DB에 providerId로 사용자 정보를 조회했을 때, 사용자 정보가 DB에 없다면 신규회원으로 간주
            => 201 응답으로 신규회원가입 해야한다고 프론트에게 알려준다.

            사용자 정보가 있으면, return providerID 를 해주는 로직
        */
        Optional<UserEntity> existingUser = userRepository.findByProviderID(idToken);


        if (existingUser.isEmpty()) {
            // 이미 존재하지 않는 사용자이므로, 클라이언트에게 201 Created 응답 코드와 신규회원가입 필요 메시지를 반환
            throw new Exception("신규회원가입이 필요합니다.");
        }

        // 이미 사용자 정보가 DB에 있으면, 카카오에서 받아온 UID를 반환

        return idToken;
    }


    // #3 회원가입시, 회원정보 저장하는 메소드 -------------------------------------------------------------------------------
    @Override
    public UserEntity saveUserInfo(UserDto userDto) {
        UserEntity userEntity = UserEntity.toUserEntity(userDto); //DTO -> Entity 변환
        return userRepository.save(userEntity);
    }


    // #4 userId(패밀리어 서비스 사용자의 고유 UID) 를 추출하는 메소드---------------------------------------------------------
    @Override
    public Long getUserId(Long providerID) {

        Optional<UserEntity> optionalUserEntity = userRepository.findByProviderID(providerID);
        if (optionalUserEntity.isPresent()) {
            UserEntity userEntity = optionalUserEntity.get();
            // 여기서 userEntity의 다른 필드 값을 사용하면 됩니다.
            Long userId = userEntity.getUserId();
            // 필요에 따라서 추가적인 작업을 수행할 수 있습니다.
            return userId;
        }
        return null;
    }


    // JWT 토큰 생성하는 로직
    private final JwtTokenProvider jwtTokenProvider;

    // #5 JWT access 토큰 생성 메소드 ------------------------------------------------------------------------------------
    @Override
    public String createJwtAccessToken(Long userId) {
        return jwtTokenProvider.createAccessToken(userId);
    }

    // #6 JWT refresh 토큰 생성 메소드 -----------------------------------------------------------------------------------
    @Override
    public String createJwtRefreshToken(Long userId) {

        return jwtTokenProvider.createRefreshToken(userId);
    }

    // #7 사용자 이름 중복 체크 메소드
    @Override
    public boolean isNameAvailable(String name) {
        return !userRepository.existsByName(name); //주어진 이름이 데이터베이스에 존재하지 않는다면 true를 반환
    }
}

