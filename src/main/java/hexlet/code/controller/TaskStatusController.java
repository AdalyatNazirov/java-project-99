package hexlet.code.controller;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
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
@RequestMapping("${base-url:/api}" + "/task_statuses")
@AllArgsConstructor
public class TaskStatusController {

    private TaskStatusRepository taskStatusRepository;

    private TaskStatusMapper taskStatusMapper;

    @GetMapping
    public ResponseEntity<List<TaskStatusDTO>> list() {
        var taskStatuses = taskStatusRepository.findAll()
                .stream()
                .map(taskStatusMapper::map)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .body(taskStatuses);
    }

    @GetMapping("/{id}")
    public TaskStatusDTO show(@PathVariable Long id) {
        var taskStatus = taskStatusRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Status not found"));
        return taskStatusMapper.map(taskStatus);
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public TaskStatusDTO create(@RequestBody TaskStatusCreateDTO taskStatusCreateDto) {
        var taskStatus = taskStatusMapper.map(taskStatusCreateDto);
        var result = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(result);
    }

    @PutMapping("/{id}")
    public TaskStatusDTO update(@PathVariable Long id, @RequestBody TaskStatusUpdateDTO taskStatusUpdateDto) {
        var taskStatus = taskStatusRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task Status not found"));
        taskStatusMapper.update(taskStatusUpdateDto, taskStatus);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        taskStatusRepository.deleteById(id);
    }
}
