package last.demo.Post.entity.like;

import jakarta.persistence.*;
import last.demo.Post.entity.PostEntity;
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
public class LikeEntity { // 게시글 1개당 여러개의 LikeEntity가 존재

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId; // 좋아요 고유번호

    @Column
    private Long roomId; //방 고유 번호

    @ManyToOne(fetch = FetchType.LAZY) // ManyToOne 관계 설정
    @JoinColumn(name = "postId", insertable = false, updatable = false) // PostEntity의 postId와 매핑
    private PostEntity postEntity; // PostEntity 를 참조하는 필드

    // 좋아요 누른 게시글 고유 번호 목록
    @Column
    private Long postId;

    @Column
    private Long userId; // 사용자 고유번호

    @Column
    private Timestamp createDate; // 게시글 작성 시간

    @Column
    private Timestamp modifyDate; // 게시글 수정 시간


//    // 특정 게시글의 postId를 postId리스트에서 조회해서 그 결과값을 반환하는 메소드
//    public boolean findByPostId(Long postId) {
//        // for-each문을 통해서 게시글을 찾는다.
//        for (Long post_Id : this.postId){
//            //게시글이 존재한다면
//            if(post_Id.equals(postId)) {
//                return true;
//            }
//            //게시글이 존재 안한다면
//        } return false;
//    }
//
//    // 좋아요한 게시글 고유 번호를 PostId리스트에서 삭제하는 메소드
//    public void removeLikedPostId(Long postId) {
//        this.postId.remove(postId);
//    }
//
//    // 좋아요한 게시글 고유 번호를 PostId리스트에서 추가하는 메소드
//    public void addLikedPostId(Long postId) {
//        this.postId.add(postId);
//    }

}
