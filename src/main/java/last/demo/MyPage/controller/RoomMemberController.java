package last.demo.MyPage.controller;

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
    public RoomMemberController(RoomMemberService roomMemberService){
        this.roomMemberService=roomMemberService;
    }

    @PostMapping(value = "/insertRoomMember")
    public ResponseEntity<String> insertRoomMember(@RequestBody RoomMemberDto roomMemberDTO) {
        try {
            roomMemberService.insertRoomMember(roomMemberDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("RoomMember added successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add RoomMember");
        }
    }

    @DeleteMapping(value = "/deleteRoomMember")
    public ResponseEntity<String> deleteRoomMember(@RequestBody RoomMemberDto roomMemberDTO) {
        try {
            roomMemberService.deleteRoomMember(roomMemberDTO);
            return ResponseEntity.status(HttpStatus.OK).body("RoomMember deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete RoomMember");
        }
    }

    @GetMapping(value = "/getRoomMember")
    public ResponseEntity<String> getRoomMember(@RequestBody RoomMemberDto roomMemberDTO) {
        try {
            roomMemberService.getRoomMember(roomMemberDTO);
            System.out.println("RoomMemberId Name is " + roomMemberService.getRoomMember(roomMemberDTO).getRoomMemberId());
            return ResponseEntity.status(HttpStatus.OK).body("RoomMember get successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get RoomMember");
        }
    }

    @PutMapping(value = "/updateRoomMember")
    public ResponseEntity<String> updateRoomMember(@RequestBody RoomMemberDto roomMemberDTO) {
        try {
            roomMemberService.updateRoomMember(roomMemberDTO);
            return ResponseEntity.status(HttpStatus.OK).body("RoomMember updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update RoomMember");
        }
    }
    //위에 4개는 엔티티 내에서 CRUD 테스트 코드 : 합칠 때 무시하면 될 듯
    @PutMapping(value = "/updateRoomMemberAlias")
    public ResponseEntity<String> updateRoomMemberAlias(@RequestBody RoomMemberDto roomMemberDTO) {
        try {
            roomMemberService.updateRoomMemberAlias(roomMemberDTO);
            return ResponseEntity.status(HttpStatus.OK).body("RoomMemberAlias updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update RoomMemberAlias");
        }
    }

    @DeleteMapping(value = "/exitRoom")
    public ResponseEntity<String> exitRoom(@RequestBody RoomMemberDto roomMemberDTO) {
        try {
            roomMemberService.exitRoom(roomMemberDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Room exited successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to exit Room");
        }
    }

    @PutMapping(value = "/delegateAdmin")
    public ResponseEntity<String> delegateAdmin(@RequestBody Map<String, String> requestData) {
        try {
            roomMemberService.delegateAdmin(requestData.get("delegatingUserId"), requestData.get("delegatedUserId"));
            return ResponseEntity.status(HttpStatus.OK).body("DelegatingAdmin updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update DelegatingAdmin");
        }
    }

    @DeleteMapping(value = "/kickUser")
    public ResponseEntity<String> kickUser(@RequestBody Map<String, String> requestData) {
        try {
            roomMemberService.kickUser(requestData.get("selfRoomMemberId"), requestData.get("targetRoomMemberId"));
            return ResponseEntity.status(HttpStatus.OK).body("Kicked User successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Kick User");
        }
    }
}