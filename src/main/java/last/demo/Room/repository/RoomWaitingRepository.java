package last.demo.Room.repository;

import last.demo.Room.entity.RoomWaitingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomWaitingRepository extends JpaRepository<RoomWaitingEntity, Long> {
    List<RoomWaitingEntity> findAllByRoomId(Long roomId);
    Optional<RoomWaitingEntity> findByWaitId(Long waitId);
}
