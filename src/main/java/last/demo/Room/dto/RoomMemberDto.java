package last.demo.Room.dto;

import last.demo.Room.entity.RoomMemberEntity;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMemberDto {

    private Long roomMemberId; // 방 구성원 고유 번호
    private Long userId; // 사용자 고유번호
    private Long roomId; // 방 고유 번호
    private String alias; // 구성원 별칭
    private Boolean type; // 관리자 여부
    private Timestamp joinedDate; // 구성원 가입시간
    private Timestamp leavedDate; // 구성원 탈퇴시간


    // Entity -> DTO 변환
    public static RoomMemberDto toRoomMemberDto(RoomMemberEntity roomMemberEntity) {
        if (roomMemberEntity == null) {
            return null;
        }

        RoomMemberDto roomMemberDto = new RoomMemberDto();

        roomMemberDto.setRoomMemberId(roomMemberEntity.getRoomMemberId());
        roomMemberDto.setUserId(roomMemberDto.getUserId());
        roomMemberDto.setAlias(roomMemberEntity.getAlias() != null ? roomMemberEntity.getAlias() : null);
        roomMemberDto.setType(roomMemberEntity.getType());
        roomMemberDto.setJoinedDate(roomMemberEntity.getJoinedDate() != null ? roomMemberEntity.getJoinedDate() : null);
        roomMemberDto.setLeavedDate(roomMemberEntity.getLeavedDate() != null ? roomMemberEntity.getLeavedDate() : null);

        return roomMemberDto;
    }
}
