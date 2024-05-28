package last.demo.Post.entity;

import jakarta.persistence.*;
import last.demo.Post.dto.post.PostDto;
import last.demo.Post.entity.comment.ChildCommentEntity;
import last.demo.Post.entity.comment.PostCommentEntity;
import last.demo.Post.entity.like.LikeEntity;
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
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId; // 게시물 고유번호

    @Column
    private Long roomId; //방 고유 번호

    @Column
    private Long roomMemberId; //방 구성원 고유번호

    @Column
    private Long userId; //작성자(사용자) 고유번호

    @Column
    private Long postCommentCount; // 부모 댓글 수

    @Column(columnDefinition = "varchar(50)")
    private String title; // 게시글 제목

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content; // 게시글 내용

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(columnDefinition = "LONGTEXT")
    private List<String> postImage = new ArrayList<>(); // 게시글 이미지들을 담는 ArrayList

    @Column
    private Long totalLikeCount; // 좋아요 총 갯수

    @Column
    private Timestamp createDate; // 게시글 작성 시간

    @Column
    private Timestamp modifyDate; // 게시글 수정 시간

   // 좋아요 엔티티와의 [1:다] 관계를 지어주는 코드(테이블에 나타나진 않는다.)
    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeEntity> likeEntities = new ArrayList<>();

//    // 태그  엔티티와의 [1:다] 관계를 지어주는 코드(테이블에 나타나진 않는다.)
//    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PostTagEntity> postTags = new ArrayList<>();

    // 부모 댓글 엔티티와의 [1:다] 관계를 지어주는 코드(테이블에 나타나진 않는다.)
    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostCommentEntity> parentComments = new ArrayList<>();

    //DTO -> Entity
    public static PostEntity toPostEntity(PostDto postDto) {
        PostEntity postEntity = new PostEntity();

        postEntity.setPostId(postDto.getPostId() != null ? postDto.getPostId() : 0L);
        postEntity.setUserId(postDto.getUserId() != null ? postDto.getUserId() : 0L);
        postEntity.setRoomId(postDto.getRoomId() != null ? postDto.getRoomId() : 0L);
        postEntity.setRoomMemberId(postDto.getUserId() != null ? postDto.getUserId() : 0L);
        postEntity.setContent(postDto.getContent() != null ? postDto.getContent() : null);
        postEntity.setTitle(postDto.getTitle() != null ? postDto.getTitle() : null);
        postEntity.setTotalLikeCount(postDto.getTotalLikeCount() != null ? postDto.getTotalLikeCount() : 0L);
        postEntity.setCreateDate(postDto.getCreateDate() != null ? postDto.getCreateDate() : null);
        postEntity.setModifyDate(postDto.getModifyDate() != null ? postDto.getModifyDate() : null);
        postEntity.setPostCommentCount(0L);

        // PostDto에서 postImage를 가져와서 PostEntity의 postImage에 설정합니다.
        postEntity.setPostImage(postDto.getPostImage() != null ? new ArrayList<>(postDto.getPostImage()) : new ArrayList<>());


        return postEntity;
    }


    // postCommentCount 증가 메서드
    public void incrementPostCommentCount() {
        this.postCommentCount += 1;
    }

    // postCommentCount 감소 메서드
    public void decrementPostCommentCount() {
        if (this.postCommentCount > 0) {
            this.postCommentCount -= 1;
        }
    }

}
