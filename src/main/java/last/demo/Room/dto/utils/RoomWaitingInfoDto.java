package last.demo.Room.dto.utils;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomWaitingInfoDto {

    private Long waitId; // 가입대기 고유 번호
    private Long userId; // 사용자 고유번호
    private Boolean approve; // 가입대기 승인 여부

    private String image; // 사용자 프로필 사진
    private String name; // 사용자 이름


}
