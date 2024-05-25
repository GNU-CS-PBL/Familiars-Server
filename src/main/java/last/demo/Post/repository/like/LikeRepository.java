package last.demo.Post.repository.like;

import last.demo.Post.entity.PostEntity;
import last.demo.Post.entity.like.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {


    PostEntity findByPostId(Long postId);

    Optional<LikeEntity> findByPostIdAndUserId(Long postId, Long userId);

}
