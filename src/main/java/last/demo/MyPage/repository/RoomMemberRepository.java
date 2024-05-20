package last.demo.MyPage.repository;

import last.demo.Room.entity.RoomEntity;
import last.demo.Room.entity.RoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMemberEntity, Long> {
    boolean existsByAlias(String alias);


    // userId를 기준으로 해당 유저가 속한 방의 정보를 조회하는 메소드를 추가합니다.
    Optional<RoomMemberEntity> findByUserId(Long userId);

    // roomId를 기준으로 해당 방에 속한 모든 구성원을 조회하는 메소드를 추가합니다.
    List<RoomMemberEntity> findByRoomId(Long roomId);

    Optional<RoomMemberEntity> findByRoomIdAndUserId(Long roomId, Long userId);

    List<RoomMemberEntity> findAllRoomMemberByRoomId(Long RoomId);
}
