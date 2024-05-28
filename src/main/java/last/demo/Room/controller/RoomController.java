package last.demo.Room.controller;

import last.demo.OAuth.jwt.JwtTokenValidator;
import last.demo.Room.dto.RoomWaitingDto;
import last.demo.Room.dto.utils.*;
import last.demo.Room.dto.RoomDto;
import last.demo.Room.dto.RoomMemberDto;
import last.demo.Room.entity.RoomEntity;
import last.demo.Room.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RoomController {

    // 서비스 관련 출력
    @Autowired
    private RoomService roomService;

    @Autowired
    private JwtTokenValidator jwtTokenValidator;



    // #1 방 생성--------------------------------------------------------------------------------------------------------
    @PostMapping(value = "/api/room/create")
    public ResponseEntity<String> CreateRoom(@RequestHeader("Authorization") String jwtAccessToken,@RequestBody RoomCreateDto roomCreateDto) throws Exception {

    // 1. 일단 받아온 내용을 저장한다.
        RoomDto roomDto = new RoomDto();
        roomDto.setRoomName(roomCreateDto.getRoomName());
        roomDto.setRoomImage(roomCreateDto.getRoomImage());
        roomDto.setInviteCode(roomService.generateInviteCode()); // 방 초대코드 생성 후 저장
        RoomEntity roomEntity =  roomService.saveRoomInfo(roomDto);

    // 2. 저장후에 생성된 '방 테이블'의 pk 값인 ->  roomId를 가져온다.
        long roomId = roomEntity.getRoomId();

     // "Bearer " 부분을 제거하고 실제 토큰 값만 추출
        String jwtToken = jwtAccessToken.substring(7);

        System.out.println(jwtToken);

    // 3. jwtAccessToken에서 userID를 빼오고,
        Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);
        System.out.println("추출한 사용자 고유번호");
        System.out.println(userId);

    // 4. roomId, userID, alias, type = True(admin) 이 4가지를 roomMemberRepository에 저장한다.
        RoomMemberDto roomMemberDto = new RoomMemberDto();
        roomMemberDto.setAlias(roomCreateDto.getAlias());
        roomMemberDto.setRoomId(roomId);
        roomMemberDto.setUserId(userId);
        roomMemberDto.setType(true);
        roomService.saveRoomMemberInfo(roomMemberDto);

        return ResponseEntity.ok("Room created successfully.");

    }

    // #2 방 초대코드 조회------------------------------------------------------------------------------------------------
    @GetMapping(value = "/api/room/InviteCode")
    public ResponseEntity<RoomInviteCodeDto> getInviteCode(@RequestHeader("Authorization") String jwtAccessToken,
                                                           @RequestParam("roomId") Long roomId) {
        // 방장의 jwt 토큰에서 uid를 추출해서
        String jwtToken = jwtAccessToken.substring(7);
        Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

        String inviteCode = roomService.getInviteCode(roomId);
        if (inviteCode != null) {
            RoomInviteCodeDto roomInviteCodeDto = new RoomInviteCodeDto(inviteCode);
            return ResponseEntity.ok(roomInviteCodeDto);
        } else {
            return ResponseEntity.notFound().build(); // 초대 코드를 찾을 수 없는 경우 404 에러 반환
        }
    }


    // #3 방 가입 요청 && 가입대기 테이블 생성(초대코드 이용)-------------------------------------------------------------------
    @PostMapping(value = "/api/room/wait")
    public ResponseEntity<String> SaveWaitingRoom(@RequestHeader("Authorization") String jwtAccessToken, @RequestBody RoomJoinDto roomJoinDto) throws Exception  {

    //1. 초대코드가 , 사용자가 가입하려는 방의 초대코드와 일치하는지 확인
        String originInviteCode = roomService.getInviteCode(roomJoinDto.getRoomId()); // 기존 코드 조회
        String inviteCode = roomJoinDto.getInviteCode(); // 방 가입자가 가지고 있는 코드

        if (originInviteCode != null && originInviteCode.equals(inviteCode)) { // 초대코드가 존재하고, 기존 코드와 가입자 코드가 일치하는 경우
    //2. 일치하다면, jwtAccessToken에서 사용자 고유번호 userId를 꺼낸다.
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

    //3  방 가입 대기 테이블 생성
            RoomWaitingDto roomWaitingDto = new RoomWaitingDto();
            roomWaitingDto.setAlias(roomJoinDto.getAlias());
            roomWaitingDto.setApprove(false);
            roomWaitingDto.setRoomId(roomJoinDto.getRoomId());
            roomWaitingDto.setUserId(userId);
            roomService.saveRoomWaitingInfo(roomWaitingDto);

            return ResponseEntity.ok("Create a roomWaitingTable successfully.");
        } else { // 초대코드가 존재하지 않거나, 코드가 일치하지 않는 경우 처리 로직...
            return ResponseEntity.badRequest().body("Failed to join the room. Invalid invitation code.");
        }
    }


    // 방 가입대기 테이블 조회
    @GetMapping(value = "/api/room/wait")
    public  ResponseEntity<List<RoomWaitingInfoDto>> GetAllWaitingRoomInfo(@RequestHeader("Authorization") String jwtAccessToken,
                                                        @RequestParam ("roomId") Long roomId) throws Exception  {

        // 방장의 jwt 토큰에서 uid를 추출해서
        String jwtToken = jwtAccessToken.substring(7);
        Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

        // 방 구성원 테이블에 관리자 여부 type이 true인지만 확인
        Boolean check = roomService.checkType(roomId,userId);

        if (check == true) { // true라면(즉 관리자라면)
            // 방 고유번호를 가진 모든 '가입대기 Entity' 조회해서, 가입Entity에 해당하는 사용자에대한 정보를 찾아서 
            // 프론트에게 반환
            List<RoomWaitingInfoDto> roomWaitingInfoDtoList = roomService.getRoomWaitingInfoDtoList(roomId);
            return ResponseEntity.ok(roomWaitingInfoDtoList);
        } else { // 해당 방이 존재하지 않을때.
            return ResponseEntity.badRequest().body(null);
        }

    }

    // 방 가입 최종승인 && 방 가입 테이블 삭제
    @GetMapping("/api/room/join")
    public ResponseEntity<String> JoinRoom(@RequestHeader("Authorization") String jwtAccessToken,
                                           @RequestParam ("roomId") Long roomId,
                                           @RequestParam ("waitId") Long waitId) throws Exception {

        // 방장의 jwt 토큰에서 uid를 추출해서
        String jwtToken = jwtAccessToken.substring(7);
        Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);
        // 방 구성원 테이블에 관리자 여부 type이 true인지만 확인
        Boolean check = roomService.checkType(roomId,userId);

        if (check == true) { // true라면(즉 관리자라면)
            // 최종승인 = 방 구성원 테이블 생성

            // 1. 방 가입 대기 번호로 '가입대기 Entity' 찾아온다. -> userId 와 alias를 List로 반환 받는다.
            List<Object> list = roomService.getRoomWaitingInfo(waitId);

            if (list != null && !list.isEmpty()) {

                // 2. roomId, userId, alias, type(false)를 roomMemberDto에 입력후, roomMemberRepository에 저장
                RoomMemberDto roomMemberDto = new RoomMemberDto();
                roomMemberDto.setRoomId(roomId);
                roomMemberDto.setUserId((Long)list.get(0));
                roomMemberDto.setAlias((String)list.get(1));
                roomMemberDto.setType(false); // 구성원은 false 가짐
                roomService.saveRoomMemberInfo(roomMemberDto);

                // 3. 방 구성원 테이블이 완성되면 -> 해당 waitId를 가지는 방 가입대기Entity를 삭제한다.
                roomService.deleteRoomWaitingInfo(waitId);

            }

            return ResponseEntity.ok("Join a room successfully.");
        } else { // 관리자가 아니라면
            return ResponseEntity.badRequest().body("Failed to join the room. He is not a room master.");
        }

    }


    // 방 가입 거절 = 방 가입테이블 삭제
    @DeleteMapping(value = "/api/room/joinCancel")
    public ResponseEntity<String> DeleteWaitingRoomInfo(@RequestHeader("Authorization") String jwtAccessToken,
                                                        @RequestParam ("roomId") Long roomId,
                                                        @RequestParam ("waitId") Long waitId) throws Exception  {

        // 방장의 jwt 토큰에서 uid를 추출해서
        String jwtToken = jwtAccessToken.substring(7);
        Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);
        // 방 구성원 테이블에 관리자 여부 type이 true인지만 확인
        Boolean check = roomService.checkType(roomId,userId);


        if (check == true) { // true라면(즉 관리자라면)
            // 해당 waitId를 가지는 방 가입대기Entity를 삭제한다.
            roomService.deleteRoomWaitingInfo(waitId);

            return ResponseEntity.ok("Delete successfully.");
        } else { // 관리자가 아니라면
            return ResponseEntity.badRequest().body("Failed to delete Info. He is not a room master.");
        }

    }



    // #4 구성원 별칭 중복체크---------------------------------------------------------------------------------------------
    @GetMapping("/api/room/checkAlias")
    public ResponseEntity<?> checkAlias(@RequestParam("data") String alias) {
        try {
            boolean isNameAvailable = roomService.isNameAvailable(alias);
            return ResponseEntity.ok(isNameAvailable); // 해당 사용자 이름은 사용 가능함
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }


    // #5 방 목록 조회----------------------------------------------------------------------------------------------------
                     // (클라이언트는 요청시 page 값을 같이 param 으로 전달해야한다. 안할시 default 값은 page = 0 이다.)
    @GetMapping(value = "/api/room")
    public ResponseEntity<Map<String, Object>> getAllRooms(@RequestHeader("Authorization") String jwtAccessToken,
                                                           @PageableDefault(size = 8, sort = "roomId", direction = Sort.Direction.DESC) Pageable pageable) {

        // jwtAccessToken으로부터 사용자 UID를 추출합니다. -> 가능하다면 허가된 사용자
        String jwtToken = jwtAccessToken.substring(7);
        Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

        Page<RoomLoadAllDto> roomLoadAllDtoList = roomService.roomFindAll(pageable);  //서비스 객체에서 조회한 데이터 여러개를 DTO 객체에 담아서 List 자료구조에 주입
        Map<String, Object> response = new HashMap<>();
        response.put("boardTotalList", roomLoadAllDtoList.getContent());
        return ResponseEntity.ok(response); //이 코드는 맵 객체(response)를 반환합니다.
    }                                       // 클라이언트는 이 데이터를 JSON 형식으로 받아 사용할 수 있습니다.


    // #6 방 이미지 수정
    @PutMapping(value = "/api/room/image")
    public ResponseEntity<Object> updateRoomImage(@RequestHeader("Authorization") String jwtAccessToken,
                                                  @RequestParam("roomId") Long roomId, @RequestBody RoomImageDto roomImageDto) {
        try {

            // jwtAccessToken으로부터 사용자 UID를 추출합니다. -> 가능하다면 허가된 사용자
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            // 방 테이블에서 해당 roomId에 해당하는 정보를 찾고 사진 파일을 수정한다.
            String roomImage = roomImageDto.getRoomImage();
            roomService.updateRoomImage(roomId, roomImage);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "수정 완료");
            return new ResponseEntity<>(response, HttpStatus.OK); // 200 응답코드 반환

        } catch (RuntimeException e) {
            // 예외가 발생한 경우, 적절한 에러 메시지를 포함한 응답을 반환합니다.
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 응답코드 반환
        }
    }


    // # 7 방 이미지 삭제
    @DeleteMapping(value = "/api/room/image")
    public ResponseEntity<Object> deleteRoomImage(@RequestHeader("Authorization") String jwtAccessToken,
                                                  @RequestParam("roomId") Long roomId) {
        try {

            // jwtAccessToken으로부터 사용자 UID를 추출합니다. -> 가능하다면 허가된 사용자
            String jwtToken = jwtAccessToken.substring(7);
            Long userId = jwtTokenValidator.getUserIdFromRefreshToken(jwtToken);

            // 방 테이블에서 해당 roomId에 해당하는 정보를 찾고 사진 파일을 수정한다.
            roomService.deleteRoomImage(roomId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "삭제 완료");
            return new ResponseEntity<>(response, HttpStatus.OK); // 200 응답코드 반환

        } catch (RuntimeException e) {
            // 예외가 발생한 경우, 적절한 에러 메시지를 포함한 응답을 반환합니다.
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 응답코드 반환
        }
    }
    //
    @Autowired
    public RoomController(RoomService roomService){
        this.roomService=roomService;
    }
    @DeleteMapping(value = "/bombRoom")
    public ResponseEntity<String> bombRoom(@RequestBody Map<String, String> requestData) {
        try {
            roomService.bombRoom(requestData.get("roomMemberId"), requestData.get("roomId"));
            return ResponseEntity.status(HttpStatus.OK).body("Bomb Room successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Bomb Room");
        }
    }
}
