package last.demo.Post.repository.like;

import last.demo.Post.entity.like.UserLikeListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // 사용자별 좋아요 누른 리스트 관리
public interface UserLikeListRepository extends JpaRepository<UserLikeListEntity, Long> {

    Optional<UserLikeListEntity> findByUserId(Long userId);
}
