package last.demo.Post.dto.like;

import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeDto {

    private Long likeId; // 좋아요 고유 번호
    private Long postId;  // 게시물 고유 번호
    private Long roomId; // 방 고유 번호
    private Long userId; // 사용자 고유 번호
    private Timestamp createDate; // 좋아요 작성 시간
    private Timestamp modifyDate; // 좋아요 수정 시간
}
