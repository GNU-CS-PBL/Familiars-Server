package last.demo.OAuth.controller.kakaoSNS;

import last.demo.OAuth.config.redis.RedisService;
import last.demo.OAuth.dto.UserDto;
import last.demo.OAuth.dto.kakaoSNS.RefreshTokenDto;
import last.demo.OAuth.dto.kakaoSNS.ReissueTokenResponseDto;
import last.demo.OAuth.dto.kakaoSNS.TokenResponseDto;
import last.demo.OAuth.entity.UserEntity;
import last.demo.OAuth.jwt.JwtAuthenticationFilter;
import last.demo.OAuth.jwt.JwtTokenValidator;
import last.demo.OAuth.service.kakaoSNS.KakaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import java.io.IOException;

@Controller
public class KakaoController {

    // 로그인 요청시, JWT 토큰 생성해서 프론트로 반환하는 로직
    
    @Autowired
    private KakaoService kakaoService;

    @Autowired
    private RedisService redisService; // jwtRefreshToken을 redis에 저장하기 위한 redisService 주입 코드

    @Autowired
    private JwtTokenValidator jwtTokenValidator;

    @ResponseBody
    @GetMapping("/callback")
    public void kakaoCallback(@RequestParam String code) {
        System.out.println(code);
        kakaoCallback2(code); // 두 번째 메소드 호출

    }

    @ResponseBody
    @RequestMapping("/callback")
    public void kakaoCallback2(@RequestParam String code) {
        String accessToken = kakaoService.getKakaoAccessToken(code);
        System.out.println(accessToken);

    }



    // #1 로그인 요청 -> (기존회원의 경우) jWT 발급 or (신규회원의 경우) 회원가입 권하는 응답 -------------------------------------
    @PostMapping(value = "/api/callback")
    public ResponseEntity<TokenResponseDto> kakaoLogin(@RequestBody ReissueTokenResponseDto reissueTokenResponseDto)
            throws Exception {
        // 엑세스 토큰을 다시 카카오에 줘서 안에 있는 id_token을 받아온다.
        long idToken;
        try {
            idToken = kakaoService.getIdToken(reissueTokenResponseDto.getAccessToken());
        } catch (Exception e) {
            // idToken을 가져오는 중에 예외가 발생한 경우
            System.out.println("Exception occurred while getting idToken: " + e.getMessage());
            TokenResponseDto errorResponseDto = new TokenResponseDto();
            errorResponseDto.setMessage("카카오 서버에서 ID 토큰을 가져오는 중에 문제가 발생했습니다.");
            return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        System.out.println("idToken : " + idToken); // try 문이 정상적으로 실행되면, 해당 코드가 정상적으로 실행된다.

        // idToken와 관련한 사용자 정보가 DB에 저장되어 있는지 확인 저장 안되어 있으면(기존회원) -> String providerId 반환
        long providerID;
        try {
            // idToken와 관련한 사용자 정보가 DB에 저장되어 있는지 확인 -> 기존회원이라면 다시 idToken을 받아서 Jwt토큰 발급에 이용
            providerID = kakaoService.checkUserInfo(idToken);
            
        } catch (Exception e) {
            // saveUserInfo에서 예외가 발생한 경우
            // 저장 안되어 있으면(신규회원) -> 201응답코드로 회원가입을 진행해야한다고 프론트에게 응답
            System.out.println("Exception occurred while saving user info: " + e.getMessage());
            TokenResponseDto errorResponseDto = new TokenResponseDto();
            errorResponseDto.setMessage(e.getMessage());
            return new ResponseEntity<>(errorResponseDto, HttpStatus.CREATED);
        }

        // 기존회원의 카카오 UID(providerID)로 우리 서비스의 UID를 추출하는 코드
        Long userId = kakaoService.getUserId(providerID); // -> try 문이 정상적으로 실행되면, 해당 코드가 정상적으로 실행된다.

        // JWT 토큰 생성 (우리 서비스의 userId를 -> UID로 간주하고)
        String jwtAccessToken = kakaoService.createJwtAccessToken(userId);
        String jwtRefreshToken = kakaoService.createJwtRefreshToken(userId);

        // 생성된 토큰들을 로그로 출력
        System.out.println("Generated Access Token: " + jwtAccessToken);
        System.out.println("Generated Refresh Token: " + jwtRefreshToken);

        // 생성된 jwtRefreshToken 토큰을 성능을 위해 -> Redis에 저장한다.
        Long userUID = jwtTokenValidator.getUserIdFromRefreshToken(jwtRefreshToken);
        redisService.saveRefreshToken(jwtRefreshToken, userUID);

        // JWT 토큰을 응답 본문에 담아서 클라이언트에게 보냄
        TokenResponseDto tokenResponseDto = new TokenResponseDto();
        tokenResponseDto.setAccessToken(jwtAccessToken);
        tokenResponseDto.setRefreshToken(jwtRefreshToken);

        return new ResponseEntity<>(tokenResponseDto, HttpStatus.OK);
    }


    // #2 신규회원가입 : 사용자 정보 받아오기 (= SNS 사용자 인지 검증) + 회원정보를 DB에 저장 + JWT 토큰 발급 ----------------------
    @PostMapping(value = "/api/register")
    public ResponseEntity<TokenResponseDto> UserRegister(@RequestBody UserDto userDto) throws Exception {

        long idToken;
        try {
            // 1. userDto 에서 accessToken을 꺼내서 정상적으로 SNS 로그인을 통해서 회원가입을 하려는 신규회원인지 확인
            //    즉, idToken을 정상적으로 카카오 서버로 부터 불러올 수 있는지 확인
            idToken = kakaoService.getIdToken(userDto.getAccessToken());

        } catch (Exception e) {
            // saveUserInfo에서 발생한 예외 처리

            System.out.println("Exception occurred: " + e.getMessage());
            // 예외가 발생했을 때 201 Created 응답 코드와 예외 메시지를 반환
            TokenResponseDto errorResponseDto = new TokenResponseDto();
            errorResponseDto.setMessage(e.getMessage());
            return new ResponseEntity<>(errorResponseDto, HttpStatus.CREATED);
        }

        System.out.println("idToken : " + idToken); // 위의 try 문이 정상적으로 실행되면, 해당 코드 부터 정상실행된다.

        // 2. 확인이 되면, idToken을 userDto에 포함 시켜서
        // userDto를 userEntity로 변환해서 userRepository에 회원정보를 저장하여 해당 userEntity를 반환
        userDto.setProviderID(idToken);
        UserEntity userEntity = kakaoService.saveUserInfo(userDto);

        // 3. 카카오 UID(idToken)로 가입한 해당 사용자의 우리 서비스의 userId를 추출한다.
        Long userId = userEntity.getUserId();

        // JWT 토큰 생성 (우리 서비스의 userId를 -> UID로 간주하고)
        String jwtAccessToken = kakaoService.createJwtAccessToken(userId);
        String jwtRefreshToken = kakaoService.createJwtRefreshToken(userId);

        // 생성된 토큰들을 로그로 출력
        System.out.println("Generated Access Token: " + jwtAccessToken);
        System.out.println("Generated Refresh Token: " + jwtRefreshToken);

        // 생성된 jwtRefreshToken 토큰을 성능을 위해 -> Redis에 저장한다.
        Long userUID = jwtTokenValidator.getUserIdFromRefreshToken(jwtRefreshToken);
        System.out.println(userUID);
        redisService.saveRefreshToken(jwtRefreshToken, userUID); // Redis에 저장

        // JWT 토큰을 응답 본문에 담아서 클라이언트에게 보냄
        TokenResponseDto tokenResponseDto = new TokenResponseDto();
        tokenResponseDto.setAccessToken(jwtAccessToken);
        tokenResponseDto.setRefreshToken(jwtRefreshToken);

        return new ResponseEntity<>(tokenResponseDto, HttpStatus.OK);
    }


    // RefreshToken을 이용해서 Redis에서 userId 조회
    @GetMapping("/api/AccessTokenValue")
    public ResponseEntity<Object> AccessTokenValueView(@RequestParam("refreshToken") String refreshToken) {

        Long result = (Long) redisService.getUserIdFromRefreshToken(refreshToken);

        // 결과가 정수로 반환되었는지 확인
        if (result != null) {
            System.out.println("userId: " + result); // 터미널에 출력
            return ResponseEntity.ok(result);
        } else {
            System.out.println("Invalid result type"); // 터미널에 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid result type");
        }
    }


    // #3 로그아웃 (redis에서 jwtRefreshToken 삭제) -----------------------------------------------------------------------
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @DeleteMapping("/api/logout") // header 값으로 jwtAccessToken을 넘겨줘야함
    public ResponseEntity logout(@RequestHeader("Authorization") String result, @RequestBody RefreshTokenDto refreshTokenDto) throws ServletException, IOException {

        // Header로 부터 받은 jwtAccessToken의 유효성을 검사한다.
        jwtAuthenticationFilter.doFilterInternal(result); // 현재  result에 jwtAccessToken 이 들어있다.

        // redis에서 jwtRefreshToken를 삭제한다.
        redisService.deleteRefreshToken(refreshTokenDto.getRefreshToken());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }


    // #4 jwtRefreshToken을 이용해서, 만료된(만료 이전의) jwtAccessToken을 재발급 해줌 ----------------------------------------
    @PostMapping("/api/jwtAccessToken/reissue")
    public ResponseEntity requestRefresh(@RequestBody RefreshTokenDto refreshTokenDto){
        try {
            // 1. 전달받은 jwtRefreshToken에서 userUID를 추출한다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(refreshTokenDto.getRefreshToken());

            // 2. 전달받은 jwtRefreshToken를 key값으로 redis에서 userUID를 조회해서 가져온다.
            Long result = redisService.getUserIdFromRefreshToken(refreshTokenDto.getRefreshToken());

            System.out.println(result);

            // 1번과 2번의 값이 같으면 새로운 jwtAccessToken을 생성한다.
            if (userId != null && userId.equals(result)) {
                String jwtAccessToken = kakaoService.createJwtAccessToken(userId);

                // 기존 refresh토큰과 새로 생성된 access 토큰을 응답 본문에 담아서 클라이언트에게 보냄
                ReissueTokenResponseDto reissueTokenResponseDto = new ReissueTokenResponseDto();
                reissueTokenResponseDto.setAccessToken(jwtAccessToken);
                return new ResponseEntity<>(reissueTokenResponseDto, HttpStatus.OK);
            }

            // userUUID가 null이거나 일치하지 않는 경우에 대한 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process refresh request v1");

        } catch (Exception e) {
            // 예외가 발생한 경우에 대한 처리를 수행합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process refresh request v2");
        }

    }

    // #5 사용자 이름(name) 중복체크
    @GetMapping("/api/auth/checkName")
    public ResponseEntity<?> checkName(@RequestParam("data") String name) {
        try {
            boolean isNameAvailable = kakaoService.isNameAvailable(name);
            return ResponseEntity.ok(isNameAvailable); // 해당 사용자 이름은 사용 가능함
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    @DeleteMapping(value = "/api/unregister")
    public ResponseEntity<String> UserUnregister(@RequestHeader("Authorization") String jwtAccessToken, @RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        try {
            // 1. Authorization 헤더에서 JWT 토큰을 추출하고 검증
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            if (userId == null) {
                throw new Exception("Invalid JWT token");
            }

            // 2. 유효한 사용자임을 확인했으므로 데이터베이스에서 사용자 정보를 삭제
            kakaoService.deleteUserInfo(userId);

            // 3. Redis에서 해당 사용자의 refresh token을 삭제
            redisService.deleteRefreshToken(refreshTokenDto.getRefreshToken());

            // 4. 성공 메시지 반환
            return new ResponseEntity<>("User unregistered successfully", HttpStatus.OK);
        } catch (Exception e) {
            // 예외 처리
            System.out.println("Exception occurred: " + e.getMessage());
            return new ResponseEntity<>("Unregistration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
