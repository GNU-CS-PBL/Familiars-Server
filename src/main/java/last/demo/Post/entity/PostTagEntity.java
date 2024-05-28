package last.demo.Post.entity;

import jakarta.persistence.*;
import last.demo.Post.dto.post.PostTagDto;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postTageId; // 게시물 태그 고유번호

    @Column
    private Long postId; // 게시물 ID

    @Column
    private Long userId; // 태그 하는 사용자 UID

    @Column
    private Long taggedUserId; // 태그될 사용자 UID

    @Column
    private Long roomId; //방 고유번호

    @Column
    private Timestamp createDate; // 게시물 태그 생성시간

    @Column
    private Timestamp modifyDate; // 게시물 태그 수정시간


//    @ManyToOne(fetch = FetchType.LAZY) // ManyToOne 관계 설정
//    @JoinColumn(name = "postId") // PostEntity의 postId와 매핑
//    private PostEntity postEntity; // PostEntity를 참조하는 필드


    //DTO -> Entity
    public static PostTagEntity toPostTagEntity(PostTagDto postTagDto) {
        PostTagEntity postTagEntity = new PostTagEntity();

        postTagEntity.setPostTageId(postTagDto.getPostTagId() != null ? postTagDto.getPostTagId() : 0L);

        // PostEntity 객체를 생성하여 postEntity 필드에 설정
        postTagEntity.setPostId(postTagDto.getPostId() != null ? postTagDto.getPostId() : 0L);
        postTagEntity.setUserId(postTagDto.getUserId() !=null ? postTagDto.getUserId() : 0L);
        postTagEntity.setTaggedUserId(postTagDto.getTaggedUserId() !=null ? postTagDto.getTaggedUserId() : 0L);
        postTagEntity.setRoomId(postTagDto.getRoomId() != null ? postTagDto.getRoomId() : 0L);
        postTagEntity.setCreateDate(postTagDto.getCreateDate() != null ? postTagDto.getCreateDate() : null);
        postTagEntity.setModifyDate(postTagDto.getModifyDate() != null ? postTagDto.getModifyDate() : null);


        return postTagEntity;
    }
}
