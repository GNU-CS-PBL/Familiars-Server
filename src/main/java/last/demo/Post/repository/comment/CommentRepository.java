package last.demo.Post.repository.comment;

import last.demo.Post.entity.comment.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<PostCommentEntity, Long> {

    List<PostCommentEntity> findByPostEntityPostId(Long postId);


    Optional<PostCommentEntity> findById(Long parentCommentId);

    List<PostCommentEntity> findAllByPostEntityPostId(Long postId);

    List<PostCommentEntity> findAllByUserId(Long userId);
}
