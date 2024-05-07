package last.demo.Room.repository;

import last.demo.OAuth.entity.UserEntity;
import last.demo.Room.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    RoomEntity findByRoomName(String roomName);
    RoomEntity findByRoomId(long roomId);
}
