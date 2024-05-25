package last.demo.Post.repository.post;

import last.demo.Post.entity.PostEntity;
import last.demo.Room.entity.RoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Optional<PostEntity> findById(Long postId);

    // 게시글 전체 조회 (특정 방 안의 모든 게시글)
    Page<PostEntity> findAllByRoomId(Long roomId, Pageable pageable);


    // 게시글 전체 조회 (마이페이지)
    Page<PostEntity> findAllByUserId(Long userId, Pageable pageable);
}
