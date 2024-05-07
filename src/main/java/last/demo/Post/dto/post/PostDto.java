package last.demo.Post.dto.post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import last.demo.Room.entity.RoomMemberEntity;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

    private Long postId;  //게시글 고유 번호
    private Long roomId; //방 고유 번호
    private Long userId; // 작성한 사용자 고유 번호
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private List<Long> taggedUserId  = new ArrayList<>(); // 게시글에 태그될 사용자 UID ArrayList
    private List<String> postImage = new ArrayList<>(); // 게시글 이미지들을 담는 ArrayList
    private Long totalLikeCount; // 좋아요 총 갯수
    private Timestamp createDate; // 게시글 작성 시간
    private Timestamp modifyDate; // 게시글 수정 시간

//    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL) //postId를 이용해서 두 엔티티가 1:N 관계를 맺는다.
//    private List<ReactionEntity> reactionEntities = new ArrayList<>(); //reactionEntities는 reactionEntity들의 리스트
//
//    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
//    private List<PostCommentEntity> postCommentEntities = new ArrayList<>();
//
//    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
//    private List<PostTagEntity> postTagEntities = new ArrayList<>();



}
