package last.demo.Room.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomWaitingDto {

    private Long waitId; // 가입대기 고유번호
    private Long roomId; // 가입 하려는 방의 고유번호
    private Long userId; // 가입 하려는 사용자 고유번호
    private Boolean approve; // 가입대기 승인여부
    private String alias; // 구성원 별칭

    private Timestamp createdDate; //가입대기 승인시간
    private Timestamp lastModifiedDate;// 가입대기 수정시간

}
