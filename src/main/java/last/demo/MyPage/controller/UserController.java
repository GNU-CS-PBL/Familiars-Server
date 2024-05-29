package last.demo.MyPage.controller;

import last.demo.OAuth.dto.UserDto;
import last.demo.MyPage.service.UserService;
import last.demo.OAuth.jwt.JwtTokenValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    private JwtTokenValidator jwtTokenValidator;

    @Autowired
    public UserController(UserService userService){
        this.userService=userService;
    }

    @PutMapping(value = "/updateUserName")
    public ResponseEntity<String> updateUserName(@RequestHeader("Authorization") String jwtAccessToken,
                                                 @RequestBody UserDto userDTO) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            userService.updateUserName(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body("UserName updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update userName");
        }
    }
    @PutMapping(value = "/updateUserBirth")
    public ResponseEntity<String> updateUserBirth(@RequestHeader("Authorization") String jwtAccessToken,
                                                  @RequestBody UserDto userDTO) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            userService.updateUserBirth(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body("UserBirth updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update userBirth");
        }
    }
    @PutMapping(value = "/updateUserMarketingAgree")
    public ResponseEntity<String> updateUserMarketingAgree(@RequestHeader("Authorization") String jwtAccessToken,
                                                           @RequestBody UserDto userDTO) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            userService.updateUserMarketingAgree(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body("UserMarketingAgree updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update userMarketingAgree");
        }
    }

    @DeleteMapping(value = "/unregisterUser")
    public ResponseEntity<String> unregisterUser(@RequestHeader("Authorization") String jwtAccessToken,
                                                 @RequestBody UserDto userDTO) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            userService.unregisterUser(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body("User unregistered successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unregister user");
        }
    }
}
