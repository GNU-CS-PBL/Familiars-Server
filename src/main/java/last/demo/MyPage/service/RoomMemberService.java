package last.demo.MyPage.service;


import last.demo.Room.dto.RoomMemberDto;
import last.demo.Room.entity.RoomMemberEntity;
import last.demo.MyPage.repository.RoomMemberRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
@Builder
public class RoomMemberService {
    private final RoomMemberRepository roomMemberRepository;

    public void insertRoomMember(RoomMemberDto roomMemberDTO){
        roomMemberRepository.save(RoomMemberEntity.toRoomMemberEntity(roomMemberDTO));
    }
    public void deleteRoomMember(RoomMemberDto roomMemberDTO){
        roomMemberRepository.deleteById(RoomMemberEntity.toRoomMemberEntity(roomMemberDTO).getRoomMemberId());
    }

    public RoomMemberDto getRoomMember(RoomMemberDto roomMemberDTO){
        RoomMemberEntity roomMemberEntity = roomMemberRepository.findById(roomMemberDTO.getRoomMemberId())
                .orElseThrow(() -> new RuntimeException("RoomMember not found"));

        return RoomMemberDto.toRoomMemberDto(roomMemberEntity);
    }

    public void updateRoomMember(RoomMemberDto roomMemberDTO){
        RoomMemberEntity updatedUser = roomMemberRepository.findById(roomMemberDTO.getRoomMemberId())
                .orElseThrow(() -> new RuntimeException("RoomMember not updated"));
        updatedUser.setAlias(roomMemberDTO.getAlias());
        roomMemberRepository.saveAndFlush(updatedUser);
    }
    //위에 4개는 엔티티 내에서 CRUD 테스트 코드 : 합칠 때 무시하면 될 듯
    public void updateRoomMemberAlias(RoomMemberDto roomMemberDTO){
        RoomMemberEntity updatedUser = roomMemberRepository.findById(roomMemberDTO.getRoomMemberId())
                .orElseThrow(() -> new RuntimeException("RoomMemberAlias not updated"));
        updatedUser.setAlias(roomMemberDTO.getAlias());
        roomMemberRepository.saveAndFlush(updatedUser);
    }

    public void exitRoom(RoomMemberDto roomMemberDTO){
        RoomMemberEntity targetRoomMemberEntity = RoomMemberEntity.toRoomMemberEntity(roomMemberDTO);
        if(targetRoomMemberEntity.getType()){
            System.out.println("You can't exit Room, Because You are Admin of this room");
        }else{
            roomMemberRepository.deleteById(RoomMemberEntity.toRoomMemberEntity(roomMemberDTO).getRoomMemberId());
        }
    }
    public void delegateAdmin(String delegatingUserId, String delegatedUserId){
        RoomMemberEntity delegatingUser = roomMemberRepository.findById(Long.parseLong(delegatingUserId))
                .orElseThrow(() -> new RuntimeException("delegatingUserId not updated"));
        delegatingUser.setType(false);
        roomMemberRepository.saveAndFlush(delegatingUser);
        RoomMemberEntity delegatedUser = roomMemberRepository.findById(Long.parseLong(delegatedUserId))
                .orElseThrow(() -> new RuntimeException("delegatedUserId not updated"));
        delegatedUser.setType(true);
        roomMemberRepository.saveAndFlush(delegatedUser);
    }

    public void kickUser(String selfRoomMemberId, String targetRoomMemberId){
        RoomMemberEntity selfRoomMember = roomMemberRepository.findById(Long.parseLong(selfRoomMemberId))
                .orElseThrow(() -> new RuntimeException("SelfRoomMember not found"));
        RoomMemberEntity targetRoomMember = roomMemberRepository.findById(Long.parseLong(targetRoomMemberId))
                .orElseThrow(() -> new RuntimeException("TargetRoomMember not found"));

        if(selfRoomMember.getType()) {
            roomMemberRepository.deleteById(targetRoomMember.getRoomMemberId());
        }else{
            System.out.println("You are not Admin");
        }
    }
}
