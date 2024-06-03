package last.demo.ToDo.entity;


import jakarta.persistence.*;
import last.demo.ToDo.dto.TodoDto;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoId;

    @Column
    private Long roomId;

    @Column
    private Long userId;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column
    private Boolean checked;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp modifiedDate;

    public static TodoEntity toTodoEntity(TodoDto todoDto){
        TodoEntity todoEntity = new TodoEntity();

        todoEntity.setTodoId(todoDto.getTodoId()!=null?todoDto.getTodoId():null);
        todoEntity.setRoomId(todoDto.getRoomId()!=null?todoDto.getRoomId():null);
        todoEntity.setUserId(todoDto.getUserId()!=null?todoDto.getUserId():null);
        todoEntity.setContent(todoDto.getContent()!=null?todoDto.getContent():"");
        todoEntity.setChecked(todoDto.getChecked()!=null?todoDto.getChecked():false);
        todoEntity.setCreateDate(todoDto.getCreateDate()!=null?todoDto.getCreateDate():null);
        todoEntity.setModifiedDate(todoDto.getModifiedDate()!=null?todoDto.getModifiedDate():null);

        return todoEntity;
    }
}
