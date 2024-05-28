package last.demo.Post.repository.like;

import last.demo.Post.entity.PostEntity;
import last.demo.Post.entity.like.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    Optional<LikeEntity> findByPostEntityPostIdAndUserId(Long postId, Long userId);

    // 게시글 삭제시, 관련 좋아요 list 삭제하는 코드
    List<LikeEntity> findByPostEntity_PostId(Long postId);


}
