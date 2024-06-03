package last.demo.Post.repository.like.post;

import last.demo.Post.entity.like.post.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    
    // 해당 게시글에 특정 사용자가 좋아요 누른 LikeEntity를 전부 조회
    Optional<LikeEntity> findByPostEntityPostIdAndUserId(Long postId, Long userId);

    // 게시글 삭제시, 관련 좋아요 list 삭제하는 코드
    List<LikeEntity> findByPostEntity_PostId(Long postId);


}
