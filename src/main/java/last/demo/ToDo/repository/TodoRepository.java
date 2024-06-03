package last.demo.ToDo.repository;

import last.demo.Room.entity.RoomMemberEntity;
import last.demo.ToDo.dto.TodoDto;
import last.demo.ToDo.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    List<TodoEntity> findAllByUserId(Long userId);

    List<TodoEntity> findAllByRoomId(Long roomId);
}
