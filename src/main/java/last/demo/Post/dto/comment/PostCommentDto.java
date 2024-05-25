package last.demo.Post.dto.comment;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentDto {
    private Long postId; // (댓글이 달린) 게시글 고유 번호
    private Long roomId; // (댓글이 달린) 방 고유 번호
    private Long userId; // (댓글을 단) 작성한 사용자 고유 번호

    private String content; // 댓글 내용
    private Timestamp createDate; // 댓글 작성 시간
    private Timestamp modifyDate; // 댓글 수정 시간
}
