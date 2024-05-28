package last.demo.Post.repository.comment;

import last.demo.Post.entity.comment.ChildCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildCommentRepository extends JpaRepository<ChildCommentEntity, Long> {
}
