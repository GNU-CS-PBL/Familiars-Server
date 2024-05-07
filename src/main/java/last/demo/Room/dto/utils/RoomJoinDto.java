package last.demo.Room.dto.utils;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomJoinDto { // 방 가입 DTO

    private Long roomId; // 가입 하려는 방의 고유번호
    private String inviteCode; // 방 초대코드
    private String alias; // 구성원 별칭
}
