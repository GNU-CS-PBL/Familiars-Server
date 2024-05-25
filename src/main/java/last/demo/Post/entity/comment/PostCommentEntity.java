package last.demo.Post.entity.comment;

import jakarta.persistence.*;
import last.demo.Post.dto.comment.PostCommentDto;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long parentCommentId; // 부모댓글 고유 번호

    @Column
    private Long roomId; // (댓글이 달린) 방 고유 번호

    @Column
    private Long postId; // (댓글이 달린) 게시글 고유 번호

    @Column
    private Long userId; // (댓글을 단) 작성자(사용자) 고유번호
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content; // 댓글 내용

    @Column
    private Timestamp createDate; // 댓글 작성 시간

    @Column
    private Timestamp modifyDate; // 댓글 수정 시간

    // 테이블에 나타나진 않지만, 자식 댓글 엔티티와의 [1:다] 관계를 지어주는 코드
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChildCommentEntity> childComments; // 자식 댓글 목록



    //DTO -> Entity
    public static PostCommentEntity toPostCommentEntity(PostCommentDto postCommentDto) {
        PostCommentEntity postCommentEntity = new PostCommentEntity();

        postCommentEntity.setPostId(postCommentDto.getPostId() != null ? postCommentDto.getPostId() : 0L);
        postCommentEntity.setUserId(postCommentDto.getUserId() != null ? postCommentDto.getUserId() : 0L);
        postCommentEntity.setRoomId(postCommentDto.getRoomId() != null ? postCommentDto.getRoomId() : 0L);

        postCommentEntity.setContent(postCommentDto.getContent() != null ? postCommentDto.getContent() : null);
        postCommentEntity.setCreateDate(postCommentDto.getCreateDate() != null ? postCommentDto.getCreateDate() : null);
        postCommentEntity.setModifyDate(postCommentDto.getModifyDate() != null ? postCommentDto.getModifyDate() : null);

        return postCommentEntity;
    }

}
