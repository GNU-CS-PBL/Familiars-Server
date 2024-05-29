package last.demo.Post.repository.comment;

import last.demo.Post.entity.comment.ChildCommentEntity;
import last.demo.Post.entity.comment.PostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildCommentRepository extends JpaRepository<ChildCommentEntity, Long> {
    // #4 [부모댓글] + [자식댓글] 테이블 삭제
    List<ChildCommentEntity> findByParentCommentParentCommentId(Long parentCommentId);

    // 특정 부모댓글의 자식댓글들 조회
    List<ChildCommentEntity> findAllByParentCommentParentCommentId(Long parentCommentId);
}
