package last.demo.Post.entity.comment;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long childCommentId; // 자식 댓글 고유 번호

    @Column
    private Long userId; // (댓글을 단) 작성자(사용자) 고유번호

    @Column
    private Long roomId; // (댓글이 달린) 방 고유 번호

    @Column
    private Long postId; // (댓글이 달린) 게시글 고유 번호

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content; // 댓글 내용

    @Column
    private Timestamp createDate; // 댓글 작성 시간

    @Column
    private Timestamp modifyDate; // 댓글 수정 시간

    // parent_comment_id 컬럼은 해당 엔티티 테이블에 보여지는 부분 (부모댓글 엔티티와 [1:다] 관계를 나타내어준다.)
    //parent_comment_id 컬럼은 외래 키로서, 자식 댓글이 어느 부모 댓글에 속하는지를 나타냅니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private PostCommentEntity parentComment; // 부모 댓글
}
