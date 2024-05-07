package last.demo.Room.dto.utils;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomCreateDto { // 방 생성용 DTO

    private String roomName; // 방 이름
    private String roomImage; // 방 이미지
    private String alias; // 구성원 별칭
}
