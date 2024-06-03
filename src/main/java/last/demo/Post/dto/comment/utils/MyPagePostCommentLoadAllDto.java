package last.demo.Post.dto.comment.utils;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPagePostCommentLoadAllDto { // 마이페이지에서 내가 쓴 모든 (부모)댓글 조회

    // UserEntity로 부터 가져오는 정보
    private String name; // 사용자 닉네임
    private String image; // 사용자 사진

    // 부모댓글 PostCommentEntity로 부터 가져오는 정보
    private Long parentCommentId; // 부모댓글 고유 번호
    private String content; // 댓글 내용

}
