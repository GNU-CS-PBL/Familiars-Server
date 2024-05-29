package last.demo.MyPage.controller;

import last.demo.OAuth.jwt.JwtTokenValidator;
import last.demo.Room.dto.RoomMemberDto;
import last.demo.Room.entity.RoomMemberEntity;
import last.demo.MyPage.service.RoomMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class RoomMemberController {
    private final RoomMemberService roomMemberService;

    @Autowired
    private JwtTokenValidator jwtTokenValidator;

    @Autowired
    public RoomMemberController(RoomMemberService roomMemberService){
        this.roomMemberService=roomMemberService;
    }

    @PostMapping(value = "/insertRoomMember")
    public ResponseEntity<String> insertRoomMember(@RequestHeader("Authorization") String jwtAccessToken,
                                                   @RequestBody RoomMemberDto roomMemberDTO) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            roomMemberService.insertRoomMember(roomMemberDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("RoomMember added successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add RoomMember");
        }
    }

    @DeleteMapping(value = "/deleteRoomMember")
    public ResponseEntity<String> deleteRoomMember(@RequestHeader("Authorization") String jwtAccessToken,
                                                   @RequestBody RoomMemberDto roomMemberDTO) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            roomMemberService.deleteRoomMember(roomMemberDTO);
            return ResponseEntity.status(HttpStatus.OK).body("RoomMember deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete RoomMember");
        }
    }

    @GetMapping(value = "/getRoomMember")
    public ResponseEntity<String> getRoomMember(@RequestHeader("Authorization") String jwtAccessToken,
                                                @RequestBody RoomMemberDto roomMemberDTO) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            roomMemberService.getRoomMember(roomMemberDTO);
            System.out.println("RoomMemberId Name is " + roomMemberService.getRoomMember(roomMemberDTO).getRoomMemberId());
            return ResponseEntity.status(HttpStatus.OK).body("RoomMember get successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get RoomMember");
        }
    }

    @PutMapping(value = "/updateRoomMember")
    public ResponseEntity<String> updateRoomMember(@RequestHeader("Authorization") String jwtAccessToken,
                                                   @RequestBody RoomMemberDto roomMemberDTO) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            roomMemberService.updateRoomMember(roomMemberDTO);
            return ResponseEntity.status(HttpStatus.OK).body("RoomMember updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update RoomMember");
        }
    }
    //위에 4개는 엔티티 내에서 CRUD 테스트 코드 : 합칠 때 무시하면 될 듯
    @PutMapping(value = "/updateRoomMemberAlias")
    public ResponseEntity<String> updateRoomMemberAlias(@RequestHeader("Authorization") String jwtAccessToken,
                                                        @RequestBody RoomMemberDto roomMemberDTO) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            roomMemberService.updateRoomMemberAlias(roomMemberDTO);
            return ResponseEntity.status(HttpStatus.OK).body("RoomMemberAlias updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update RoomMemberAlias");
        }
    }

    @DeleteMapping(value = "/exitRoom")
    public ResponseEntity<String> exitRoom(@RequestHeader("Authorization") String jwtAccessToken,
                                           @RequestBody RoomMemberDto roomMemberDTO) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            roomMemberService.exitRoom(roomMemberDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Room exited successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to exit Room");
        }
    }

    @PutMapping(value = "/delegateAdmin")
    public ResponseEntity<String> delegateAdmin(@RequestHeader("Authorization") String jwtAccessToken,
                                                @RequestBody Map<String, String> requestData) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            roomMemberService.delegateAdmin(requestData.get("delegatingUserId"), requestData.get("delegatedUserId"));
            return ResponseEntity.status(HttpStatus.OK).body("DelegatingAdmin updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update DelegatingAdmin");
        }
    }

    @DeleteMapping(value = "/kickUser")
    public ResponseEntity<String> kickUser(@RequestHeader("Authorization") String jwtAccessToken,
                                           @RequestBody Map<String, String> requestData) {
        try {
            // 사용자 UID를 추출
            String jwtToken = jwtAccessToken.substring(7);  // jwtAccessToken으로부터 사용자 UID를 추출합니다.
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            roomMemberService.kickUser(requestData.get("selfRoomMemberId"), requestData.get("targetRoomMemberId"));
            return ResponseEntity.status(HttpStatus.OK).body("Kicked User successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Kick User");
        }
    }
}