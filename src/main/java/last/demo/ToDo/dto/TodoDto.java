package last.demo.ToDo.dto;

import last.demo.ToDo.entity.TodoEntity;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDto {

    private Long todoId;
    private Long userId;
    private Long roomId;
    private String content;
    private Boolean checked;
    private Timestamp createDate;
    private Timestamp modifiedDate;

    public static TodoDto toTodoDto(TodoEntity todoEntity){
        TodoDto todoDto = new TodoDto();

        todoDto.setTodoId(todoEntity.getTodoId());
        todoDto.setRoomId(todoEntity.getRoomId());
        todoDto.setUserId(todoEntity.getUserId());
        todoDto.setContent(todoEntity.getContent());
        todoDto.setChecked(todoEntity.getChecked());
        todoDto.setCreateDate(todoEntity.getCreateDate());
        todoDto.setModifiedDate(todoEntity.getModifiedDate());

        return todoDto;
    }
}
