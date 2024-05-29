package last.demo.Post.dto.comment.utils;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentLoadAllDto { // 특정 게시글의 댓글 아이콘을 눌렀을때, 모든 댓글 조회

    // UserEntity로 부터 가져오는 정보
    private String name; // 사용자 닉네임 
    private String image; // 사용자 사진 

    // 부모댓글 PostCommentEntity로 부터 가져오는 정보
    private Long parentCommentId; // 부모댓글 고유 번호
    private String content; // 댓글 내용
    private Long totalLikeCount; // (부모 댓글) 좋아요 총 갯수
    private Timestamp createDate; // 댓글 작성 시간


}
