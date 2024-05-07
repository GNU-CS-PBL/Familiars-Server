package last.demo.Room.dto.utils;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInviteCodeDto { // 초대코드 조회용 DTO

    private String inviteCode;
}
