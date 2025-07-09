package hexlet.code.controller;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskListDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${base-url:/api}" + "/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final TaskSpecification taskSpecification;

    @GetMapping
    public ResponseEntity<List<TaskDTO>> list(TaskListDTO params) {
        var spec = taskSpecification.build(params);
        var tasks = taskRepository.findAll(spec)
                .stream()
                .map(taskMapper::map)
                .toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(tasks);
    }

    @GetMapping("/{id}")
    public TaskDTO show(@PathVariable Long id) {
        var task = taskRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return taskMapper.map(task);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@RequestBody TaskCreateDTO taskDto) {
        var task = taskMapper.map(taskDto);
        var result = taskRepository.save(task);
        return taskMapper.map(result);
    }

    @PutMapping("/{id}")
    public TaskDTO update(@PathVariable Long id, @RequestBody TaskUpdateDTO taskUpdateDto) {
        var task = taskRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskMapper.update(taskUpdateDto, task);
        var result = taskRepository.save(task);
        return taskMapper.map(result);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}
