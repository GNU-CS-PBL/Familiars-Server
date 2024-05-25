package last.demo.Post.entity.like;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLikeListEntity { // 사용자별, 좋아요를 누른 게시글 번호 목록을 관리하는 테이블

    @Id // pk 컬럼 지정. 필수
    @Column(name = "userId")
    private Long userId;

    // 좋아요 누른 게시글 고유 번호 목록
    @ElementCollection(fetch = FetchType.EAGER) // 실제 값을 포함하는 엔티티를 조인 테이블로 설정
    @Column(name = "postId")
    private List<Long> postId = new ArrayList<>();


    // 좋아요한 게시글 고유 번호를 추가하는 메소드
    public void addLikedPostId(Long postId) {

        this.postId.add(postId);
    }

    // 좋아요한 게시글 고유 번호를 삭제하는 메소드
    public void removeLikedPostId(Long postId) {

        this.postId.remove(postId);
    }


    //특정 게시글 id를 board_give_id 리스트에서 조회하여 반환 하는 메소드
    public boolean findByPostId(Long postId) {
        // for-each 문을 통해서 게시글을 찾는다.
        for (Long post_Id : this.postId) {
            //게시글이 존재한다면
            if (post_Id.equals(postId)) {
                return true;
            }
        } return false;  // 게시글이 존재하지 않으면 0L 반환
    }

}
