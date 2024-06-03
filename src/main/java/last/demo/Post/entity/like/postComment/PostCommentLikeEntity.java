package last.demo.Post.entity.like.postComment;

import jakarta.persistence.*;
import last.demo.Post.entity.comment.PostCommentEntity;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId; // 좋아요 고유번호

    @Column
    private Long roomId; //방 고유 번호

    @Column
    private Long postId; //게시글 고유 번호

    @Column
    private Long userId; // (부모댓글에 좋아요 누른) 사용자 고유번호

    @Column
    private Timestamp createDate; // 좋아요 생성 시간

    // PostCommentEntity와의 양방향 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private PostCommentEntity postCommentEntity;
}
