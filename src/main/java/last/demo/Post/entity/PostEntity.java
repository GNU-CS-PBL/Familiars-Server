package last.demo.Post.entity;

import jakarta.persistence.*;
import last.demo.Post.dto.post.PostDto;
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

        // PostDto에서 postImage를 가져와서 PostEntity의 postImage에 설정합니다.
        postEntity.setPostImage(postDto.getPostImage() != null ? new ArrayList<>(postDto.getPostImage()) : new ArrayList<>());


        return postEntity;
    }
}
