package last.demo.ToDo.controller;

import last.demo.Room.dto.RoomMemberDto;
import last.demo.Room.entity.RoomMemberEntity;
import last.demo.ToDo.dto.TodoDto;
import last.demo.ToDo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService){
        this.todoService = todoService;
    }

    @PostMapping(value="/insertTodo")
    public ResponseEntity<String> insertTodo(@RequestBody TodoDto todoDTO) {
        try {
            todoService.insertTodo(todoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Todo added successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add Todo");
        }
    }

    @DeleteMapping(value="/deleteTodo")
    public ResponseEntity<String> deleteTodo(@RequestBody TodoDto todoDTO) {
        try {
            todoService.deleteTodo(todoDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Todo deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete Todo");
        }
    }

    @PutMapping(value = "/updateCheck")
    public ResponseEntity<String> delegateAdmin(@RequestBody TodoDto todoDTO) {
        try {
            todoService.updateCheck(todoDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Check updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update check");
        }
    }

    @GetMapping(value = "/getMyTodo")
    public ResponseEntity<List<TodoDto>> getAllMyTodoByUserId(@RequestBody TodoDto todoDto) {
        List<TodoDto> todoDtos = todoService.getAllMyTodoByUserId(todoDto.getUserId());
        return ResponseEntity.ok().body(todoDtos);
    }

    @GetMapping(value = "/getRoomTodo")
    public ResponseEntity<List<TodoDto>> getAllRoomTodoByRoomId(@RequestBody TodoDto todoDto) {
        List<TodoDto> todoDtos = todoService.getAllRoomTodoByRoomId(todoDto.getRoomId());
        return ResponseEntity.ok().body(todoDtos);
    }
}
