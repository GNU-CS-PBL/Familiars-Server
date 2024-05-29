package last.demo.Post.dto.comment.utils;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildCommentLoadAllDto { // 특정 부모댓글 하단의 '답글달기'를 눌렀을때, 관련 모든 대댓글 조회

    // UserEntity로 부터 가져오는 정보
    private String name; // 사용자 닉네임
    private String image; // 사용자 사진

    // 자식댓글 ChildCommentEntity로 부터 가져오는 정보
    private Long childCommentId; // 자식 댓글 고유 번호
    private String content; // 댓글 내용
    private Timestamp createDate; // 댓글 작성 시간


}
