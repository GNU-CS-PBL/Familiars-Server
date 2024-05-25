package last.demo.Room.dto.utils;

import last.demo.Room.entity.RoomEntity;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomLoadAllDto {

    private Long roomId; //방 고유번호
    private String roomName; // 방 이름
    private String roomImage; // 방 이미지


    // Entity -> DTO 변환
    public static RoomLoadAllDto toRoomLoadAllDto(RoomEntity roomEntity) {

        if (roomEntity == null) {
            return null;
        }

        RoomLoadAllDto roomLoadAllDto = RoomLoadAllDto.builder()
                .roomId(roomEntity.getRoomId())
                .roomName(roomEntity.getRoomName() != null ? roomEntity.getRoomName() : null)
                .roomImage(roomEntity.getRoomImage() != null ? roomEntity.getRoomImage() : null)

                .build();

        return roomLoadAllDto;
    }

}
