package last.demo.Post.entity.comment;

import jakarta.persistence.*;
import last.demo.Post.dto.comment.PostCommentDto;
import last.demo.Post.entity.PostEntity;
import last.demo.Post.entity.like.postComment.PostCommentLikeEntity;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
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
    private Long userId; // (댓글을 단) 작성자(사용자) 고유번호

    @Column
    private Long childCommentCount; // 자식댓글 수
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content; // 댓글 내용

    @Column
    private Timestamp createDate; // 댓글 작성 시간

    @Column
    private Timestamp modifyDate; // 댓글 수정 시간

    // PostEntity와의 양방향 관계 설정 (테이블에 post_id 컬럼이 만들어진다)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity postEntity; // 부모 게시글

    // 테이블에 나타나진 않지만, 자식 댓글 엔티티와의 [1:다] 관계를 지어주는 코드
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChildCommentEntity> childComments; // 자식 댓글 목록

    // 부모 댓글 좋아요 관련 ----------------------------------------------------------------------------------------------
    @Column
    private Long totalLikeCount; // (부모 댓글) 좋아요 총 갯수

    // 좋아요 엔티티와의 [1:다] 관계를 지어주는 코드(테이블에 나타나진 않는다.)
    @OneToMany(mappedBy = "postCommentEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostCommentLikeEntity> postCommentLikeEntities= new ArrayList<>();



    //DTO -> Entity
    public static PostCommentEntity toPostCommentEntity(PostCommentDto postCommentDto) {
        PostCommentEntity postCommentEntity = new PostCommentEntity();

        // 게시글의 기본 키 값을 받아와서 PostEntity 객체에 설정
        Long postId = postCommentDto.getPostId();
        if (postId != null) {
            PostEntity postEntity = new PostEntity();
            postEntity.setPostId(postId);
            postCommentEntity.setPostEntity(postEntity); // 양방향 관계에서는 이렇게 추가해줘야 한다.
        }

        postCommentEntity.setUserId(postCommentDto.getUserId() != null ? postCommentDto.getUserId() : 0L);
        postCommentEntity.setRoomId(postCommentDto.getRoomId() != null ? postCommentDto.getRoomId() : 0L);

        postCommentEntity.setChildCommentCount(0L);
        postCommentEntity.setContent(postCommentDto.getContent() != null ? postCommentDto.getContent() : null);
        postCommentEntity.setCreateDate(postCommentDto.getCreateDate() != null ? postCommentDto.getCreateDate() : null);
        postCommentEntity.setModifyDate(postCommentDto.getModifyDate() != null ? postCommentDto.getModifyDate() : null);

        return postCommentEntity;
    }

    //childCommentCount  증가 메서드
    public void incrementChildCommentCount() {
        this.childCommentCount += 1;
    }

    // childCommentCount 감소 메서드
    public void decrementChildCommentCount() {
        if (this.childCommentCount > 0) {
            this.childCommentCount -= 1;
        }
    }
}
