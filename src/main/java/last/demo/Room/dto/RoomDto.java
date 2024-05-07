package last.demo.Room.dto;

import last.demo.Room.entity.RoomEntity;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto { // 방 정보 DTO

    private Long roomId; // 방 고유번호
    private String roomName; // 방 이름
    private String inviteCode; // 방 초대코드
    private String roomImage; // 방 이미지
    private Timestamp createdDate; // 방 생성시간
    private Timestamp lastModifiedDate; // 방 수정시간
    private Timestamp imageCreateDate; // 방 이미지 생성시간
    private Timestamp imageModifiedDate; // 방 이미지 수정시간



    // Entity -> DTO 변환
    public static RoomDto toRoomDto(RoomEntity roomEntity) {

        if (roomEntity == null) {
            return null;
        }

        RoomDto roomDto = RoomDto.builder()
                .roomId(roomEntity.getRoomId())
                .roomName(roomEntity.getRoomName() != null ? roomEntity.getRoomName() : null)
                .inviteCode(roomEntity.getInviteCode() != null ? roomEntity.getInviteCode() : null)
                .roomImage(roomEntity.getRoomImage() != null ? roomEntity.getRoomImage() : null)
                .createdDate(roomEntity.getCreatedDate())
                .lastModifiedDate(roomEntity.getLastModifiedDate())
                .imageCreateDate(roomEntity.getImageCreateDate())
                .imageModifiedDate(roomEntity.getImageModifiedDate())
                .build();

        return roomDto;
    }
}
