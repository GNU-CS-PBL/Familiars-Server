package last.demo.Room.service;

import last.demo.OAuth.entity.UserEntity;
import last.demo.OAuth.repository.UserRepository;
import last.demo.Room.dto.RoomDto;
import last.demo.Room.dto.RoomMemberDto;
import last.demo.Room.dto.RoomWaitingDto;
import last.demo.Room.dto.utils.RoomLoadAllDto;
import last.demo.Room.dto.utils.RoomWaitingInfoDto;
import last.demo.Room.entity.RoomEntity;
import last.demo.Room.entity.RoomMemberEntity;
import last.demo.Room.entity.RoomWaitingEntity;
import last.demo.MyPage.repository.RoomMemberRepository;
import last.demo.MyPage.repository.RoomRepository;
import last.demo.Room.repository.RoomWaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomWaitingRepository roomWaitingRepository;
    private final UserRepository userRepository;

    // 방 생성시, 초대코드 생성 (무작위 8자리)
    public String generateInviteCode() {
        String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);

        // 8자리의 무작위 문자열 생성
        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    // 방 생성시, 방 테이블의 정보를 저장하고 반환하는 메소드
    public RoomEntity saveRoomInfo(RoomDto roomDto) {
        RoomEntity roomEntity = RoomEntity.toRoomEntity(roomDto); //DTO -> Entity 변환
        return roomRepository.save(roomEntity);
    }


    //  방 가입대기 테이블 생성
    public void saveRoomWaitingInfo(RoomWaitingDto roomWaitingDto) {
        RoomWaitingEntity roomWaitingEntity = RoomWaitingEntity.toRoomWaitingEntity(roomWaitingDto);
        roomWaitingRepository.save(roomWaitingEntity);

    }

    // 방 구성원 테이블의 정보를 저장하는 메소드
    public void saveRoomMemberInfo(RoomMemberDto roomMemberDto) {
        RoomMemberEntity roomMemberEntity = RoomMemberEntity.toRoomMemberEntity(roomMemberDto); //DTO -> Entity 변환
        roomMemberRepository.save(roomMemberEntity);
    }

    // 방 초대코드 조회 메소드
    public String getInviteCode(Long roomId) {
        RoomEntity roomEntity = roomRepository.findByRoomId(roomId);

        String code = roomEntity.getInviteCode();
        return code;
    }


    // 구성원 별칭 중복 체크 메소드
    public boolean isNameAvailable(String alias) {
        return !roomMemberRepository.existsByAlias(alias); //주어진 이름이 데이터베이스에 존재하지 않는다면 true를 반환
    }

    // 모든 방 조회
    public Page<RoomLoadAllDto> roomFindAll(Pageable pageable) {
        Page<RoomEntity> roomEntityList = roomRepository.findAll(pageable); //리포지토리에서 데이터들은 List 형태의 Entity 가 넘어오게 된다.

        // RoomEntity 목록을 RoomLoadAllDto 목록으로 변환하여 반환하고 있습니다.
        return new PageImpl<>(roomEntityList.stream().map(RoomLoadAllDto::toRoomLoadAllDto).collect(Collectors.toList()), pageable, roomEntityList.getTotalElements());
    }

    // 방 이미지 수정
    public void updateRoomImage(Long roomId, String roomImage) {
        // roomId를 사용해서 roomEntity를 조회한다.
        Optional<RoomEntity> OptionalRoomEntity = roomRepository.findById(roomId);
        // roomId에 해당하는 방 정보가 존재하는지 확인
        if(OptionalRoomEntity.isPresent()) {
            RoomEntity roomEntity = OptionalRoomEntity.get(); //optional 객체에서 Entity를 꺼낸다.

            roomEntity.setRoomImage(roomImage); // 추출한 roomEntity를 업데이트 한다.
            roomRepository.save(roomEntity); // 변경된 Entity를 저장한다.

        } else {
            // roomId에 해당하는 방이 존재하지 않을 경우 예외 처리를 수행합니다.
            throw new RuntimeException("해당 roomId에 해당하는 방이 존재하지 않습니다.");
        }

    }

    // 방 이미지 삭제
    public void deleteRoomImage(Long roomId) {
        // roomId를 사용해서 roomEntity를 조회한다.
        Optional<RoomEntity> OptionalRoomEntity = roomRepository.findById(roomId);

        // roomId에 해당하는 방 정보가 존재하는지 확인
        if(OptionalRoomEntity.isPresent()) {
            RoomEntity roomEntity = OptionalRoomEntity.get(); //optional 객체에서 Entity를 꺼낸다.

            // roomEntity에서 roomImage 변수의 값을 삭제한다.
            roomEntity.setRoomImage(null);

            roomRepository.save(roomEntity); // 변경된 Entity를 저장한다.

        } else {
            // roomId에 해당하는 방이 존재하지 않을 경우 예외 처리를 수행합니다.
            throw new RuntimeException("해당 roomId에 해당하는 방이 존재하지 않습니다.");
        }

    }

    // 방 구성원 테이블에서 Type의 여부를 확인
    public Boolean checkType(Long roomId, Long userId) {
        // roomId 와 userId를 모두 만족시키는 roomMemberEntity를 찾는다.
        Optional<RoomMemberEntity> OptionalRoomMemberEntity = roomMemberRepository.findByRoomIdAndUserId(roomId, userId);

        if(OptionalRoomMemberEntity.isPresent()) {
            RoomMemberEntity roomMemberEntity = OptionalRoomMemberEntity.get();
            boolean type = roomMemberEntity.getType();
            
            if(type){ // 방장이라면
                return true;
            }
        } return false; // 방 구성원이라면
    }

    //
    public List<RoomWaitingInfoDto> getRoomWaitingInfoDtoList(Long roomId) {
        List<RoomWaitingInfoDto> roomWaitingInfoDtoList = new ArrayList<>();
        // 가입대기 고유번호, 가입대기 승인여부, + [프로필 사진, 사용자 이름]

        // 방 고유 번호에 해당하는 모든, 가입대기 Entity들을 List로 찾는다.
        List<RoomWaitingEntity> roomWaitingEntityList = roomWaitingRepository.findAllByRoomId(roomId);

        for (RoomWaitingEntity roomWaitingEntity : roomWaitingEntityList) {
            // 가입대기 정보를 담을 Dto 객체 생성
            RoomWaitingInfoDto roomWaitingInfoDto = new RoomWaitingInfoDto();
            roomWaitingInfoDto.setWaitId(roomWaitingEntity.getWaitId()); //가입대기 고유번호
            roomWaitingInfoDto.setApprove(roomWaitingEntity.getApprove()); // 가입대기 승인 여부
            // 가입대기 Entity로부터 userId를 추출하여 해당하는 사용자 정보를 조회
            Long userId = roomWaitingEntity.getUserId();
            roomWaitingInfoDto.setUserId(userId);

            Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
            if (userEntityOptional.isPresent()) {
                UserEntity userEntity = userEntityOptional.get();
                // 프로필 사진과 사용자 이름 설정
                roomWaitingInfoDto.setImage(userEntity.getImage()); // 프로필 사진
                roomWaitingInfoDto.setName(userEntity.getName()); // 사용자 이름
            }

            roomWaitingInfoDtoList.add(roomWaitingInfoDto);
        }

        return roomWaitingInfoDtoList;


    }

    // 방 가입대기 Entity에서 userId와 alias를 List로 반환하는 메소드
    public List<Object> getRoomWaitingInfo(Long waitId) {
        Optional<RoomWaitingEntity> OptionalRoomWaitingEntity = roomWaitingRepository.findById(waitId);

        if(OptionalRoomWaitingEntity.isPresent()) {
            RoomWaitingEntity roomWaitingEntity = OptionalRoomWaitingEntity.get();

            List list = new ArrayList();
            list.add(roomWaitingEntity.getUserId());
            list.add(roomWaitingEntity.getAlias());

            return list;
        } else {
            return null; // Optional에 해당하는 RoomWaitingEntity가 없을 경우 null 반환
        }
    }

    // 방 가입대기Entity 삭제하는 메소드
    public void deleteRoomWaitingInfo(Long waitId) {
        // waitId로 RoomWaitingEntity 조회
        Optional<RoomWaitingEntity> OptionalRoomWaitingEntity = roomWaitingRepository.findByWaitId(waitId);
        System.out.println(OptionalRoomWaitingEntity);

        if(OptionalRoomWaitingEntity.isPresent()){
            RoomWaitingEntity roomWaitingEntity = OptionalRoomWaitingEntity.get();
            roomWaitingRepository.delete(roomWaitingEntity);
        }
    }

    //
    public void bombRoom(String roomMemberId, String roomId){
        RoomMemberEntity AdminRoomMember = roomMemberRepository.findById(Long.parseLong(roomMemberId))
                .orElseThrow(()->new RuntimeException("Room Admin not found"));
        List<RoomMemberEntity> roomMemberEntities = roomMemberRepository.findAllRoomMemberByRoomId(Long.parseLong(roomId));

        if(AdminRoomMember.getType()) {
            if(roomMemberEntities.size()==1) {
                roomRepository.deleteById(AdminRoomMember.getRoomMemberId());
                roomMemberRepository.deleteById(AdminRoomMember.getRoomMemberId());
                System.out.println("Room is bombed");
            }else{
                System.out.println("Room is not Empty");
            }
        }else{
            System.out.println("You are not Admin");
        }
    }
}
