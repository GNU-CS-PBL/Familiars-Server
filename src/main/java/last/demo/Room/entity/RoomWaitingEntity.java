package last.demo.Room.entity;

import jakarta.persistence.*;
import last.demo.Room.dto.RoomDto;
import last.demo.Room.dto.RoomWaitingDto;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomWaitingEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long waitId; // 가입대기 고유 번호

    @Column
    private Long roomId; // 방 고유번호

    @Column
    private Long userId; // 사용자 고유번호

    @Column
    private Boolean approve; // 가입대기 승인여부

    @Column
    private String alias; // 방 구성원 별칭
    
    @Column
    private Timestamp createdDate; //가입대기 승인시간

    @Column
    private Timestamp lastModifiedDate;// 가입대기 수정시간

    // DTO -> Entity 변환
    public static RoomWaitingEntity toRoomWaitingEntity(RoomWaitingDto roomWaitingDto) {
        RoomWaitingEntity roomWaitingEntity = new RoomWaitingEntity();

        roomWaitingEntity.setRoomId(roomWaitingDto.getRoomId() != null ? roomWaitingDto.getRoomId() : 0L);
        roomWaitingEntity.setUserId(roomWaitingDto.getUserId() != null ? roomWaitingDto.getUserId() : 0L);
        roomWaitingEntity.setWaitId(roomWaitingDto.getWaitId() != null ? roomWaitingDto.getWaitId() : 0L);
        roomWaitingEntity.setApprove(roomWaitingDto.getApprove() != null ? roomWaitingDto.getApprove() : false);
        roomWaitingEntity.setAlias(roomWaitingDto.getAlias() != null ? roomWaitingDto.getAlias() : null);
        roomWaitingEntity.setCreatedDate(roomWaitingDto.getCreatedDate() != null ? roomWaitingDto.getCreatedDate() : null);
        roomWaitingEntity.setLastModifiedDate(roomWaitingDto.getLastModifiedDate() != null ? roomWaitingDto.getLastModifiedDate() : null);

        return roomWaitingEntity;
    }

}
