package hexlet.code.util;

import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataCleaner {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    public void cleanAll() {
        taskRepository.deleteAll();
        taskRepository.flush();
        labelRepository.deleteAll();
        labelRepository.flush();
        taskStatusRepository.deleteAll();
        taskStatusRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }
}
