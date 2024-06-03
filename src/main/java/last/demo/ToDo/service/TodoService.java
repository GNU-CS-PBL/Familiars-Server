package last.demo.ToDo.service;

import last.demo.ToDo.dto.TodoDto;
import last.demo.ToDo.entity.TodoEntity;
import last.demo.ToDo.repository.TodoRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

@AllArgsConstructor
@Service
@Builder
public class TodoService {

    private final TodoRepository todoRepository;

    public void insertTodo(TodoDto todoDto){
        todoRepository.save(TodoEntity.toTodoEntity(todoDto));
    }

    public void deleteTodo(TodoDto todoDto){
        TodoEntity targetTodo = todoRepository.findById(todoDto.getTodoId())
                .orElseThrow(()->new RuntimeException("There is no target Todo"));
        if(targetTodo.getUserId()==todoDto.getUserId()){
            todoRepository.deleteById(TodoEntity.toTodoEntity(todoDto).getTodoId());
        }else{
            System.out.println("This Todo is not yours");
        }
    }

    public void updateCheck(TodoDto todoDto){
        TodoEntity targetTodo = todoRepository.findById(todoDto.getTodoId())
                .orElseThrow(() -> new RuntimeException("There is no target Todo"));

        if(targetTodo.getUserId()==todoDto.getUserId()){
            if(!targetTodo.getChecked())
                targetTodo.setChecked(true);
            else
                targetTodo.setChecked(false);
            todoRepository.saveAndFlush(targetTodo);
        }else{
            System.out.println("This Todo is not yours");
        }
    }

    public List<TodoDto> getAllMyTodoByUserId(Long userId) {
        List<TodoEntity> todoEntities = todoRepository.findAllByUserId(userId);
        List<TodoDto> todoDtos = todoEntities.stream().map(TodoDto::toTodoDto).collect(Collectors.toList());
        return todoDtos;
    }
    public List<TodoDto> getAllRoomTodoByRoomId(Long roomId) {
        List<TodoEntity> todoEntities = todoRepository.findAllByRoomId(roomId);
        List<TodoDto> todoDtos = todoEntities.stream().map(TodoDto::toTodoDto).collect(Collectors.toList());
        return todoDtos;
    }
}
