package last.demo.MyPage.repository;

import last.demo.OAuth.entity.UserEntity;
import last.demo.Room.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import last.demo.Room.entity.RoomMemberEntity;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    RoomEntity findByRoomName(String roomName);
    RoomEntity findByRoomId(long roomId);

    List<RoomMemberEntity> findAllRoomMemberByRoomId(Long RoomId);
}
