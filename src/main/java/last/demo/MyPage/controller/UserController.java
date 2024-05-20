package last.demo.MyPage.controller;

import last.demo.OAuth.dto.UserDto;
import last.demo.MyPage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService=userService;
    }

    @PutMapping(value = "/updateUserName")
    public ResponseEntity<String> updateUserName(@RequestBody UserDto userDTO) {
        try {
            userService.updateUserName(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body("UserName updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update userName");
        }
    }
    @PutMapping(value = "/updateUserBirth")
    public ResponseEntity<String> updateUserBirth(@RequestBody UserDto userDTO) {
        try {
            userService.updateUserBirth(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body("UserBirth updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update userBirth");
        }
    }
    @PutMapping(value = "/updateUserMarketingAgree")
    public ResponseEntity<String> updateUserMarketingAgree(@RequestBody UserDto userDTO) {
        try {
            userService.updateUserMarketingAgree(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body("UserMarketingAgree updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update userMarketingAgree");
        }
    }

    @DeleteMapping(value = "/unregisterUser")
    public ResponseEntity<String> unregisterUser(@RequestBody UserDto userDTO) {
        try {
            userService.unregisterUser(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body("User unregistered successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unregister user");
        }
    }
}
