package last.demo.Post.repository.like.postComment;

import last.demo.Post.entity.like.postComment.PostCommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCommentLikeRepository extends JpaRepository<PostCommentLikeEntity, Long> {

    Optional<PostCommentLikeEntity> findByPostCommentEntityParentCommentIdAndUserId(Long parentCommentId, Long userId);

    List<PostCommentLikeEntity> findByPostCommentEntityParentCommentId(Long postId);
}
