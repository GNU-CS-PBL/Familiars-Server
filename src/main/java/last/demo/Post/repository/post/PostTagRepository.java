package last.demo.Post.repository.post;

import last.demo.Post.entity.PostTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTagEntity, Long> {

    List<PostTagEntity> findByPostEntityPostId(Long postId);
}
