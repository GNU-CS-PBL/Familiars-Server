package last.demo.Room.entity;

import jakarta.persistence.*;
import last.demo.OAuth.entity.UserEntity;
import last.demo.Room.dto.RoomMemberDto;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMemberEntity { // 방 구성원 테이블

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMemberId; // 방 구성원 고유 번호

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "roomId") //RoomEntity의 기본 키를 나타내는 room_id가 roomMemberEntity에선 왜래키로 사용된다.
    @Column
    private Long roomId;

    @Column
    private Long userId;// 사용자 고유번호

    @Column
    private String alias; // 구성원 별칭

    @Column
    private Boolean type; // 관리자 여부

    @Column
    private Timestamp joinedDate; // 구성원 가입시간

    @Column
    private Timestamp leavedDate; // 구성원 탈퇴시간


    // DTO -> Entity 변환
    public static RoomMemberEntity toRoomMemberEntity(RoomMemberDto roomMemberDto) {
        if (roomMemberDto == null) {
            return null;
        }

        RoomMemberEntity roomMemberEntity = new RoomMemberEntity();
        roomMemberEntity.setRoomId(roomMemberDto.getRoomId());
        roomMemberEntity.setRoomMemberId(roomMemberDto.getRoomMemberId() != null ? roomMemberDto.getRoomMemberId() : 0L);
        roomMemberEntity.setUserId(roomMemberDto.getUserId());
        roomMemberEntity.setAlias(roomMemberDto.getAlias() != null ? roomMemberDto.getAlias() : null);
        roomMemberEntity.setType(roomMemberDto.getType());
        roomMemberEntity.setJoinedDate(roomMemberDto.getJoinedDate() != null ? roomMemberDto.getJoinedDate() : null);
        roomMemberEntity.setLeavedDate(roomMemberDto.getLeavedDate() != null ? roomMemberDto.getLeavedDate() : null);

        return roomMemberEntity;
    }
}
