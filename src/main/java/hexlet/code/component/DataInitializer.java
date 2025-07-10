package hexlet.code.component;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@Profile("!test")
public class DataInitializer implements ApplicationRunner {

    private final Map<String, String> defaultStatuses = new LinkedHashMap<>() {
        {
            put("draft", "Draft");
            put("to_review", "To Review");
            put("to_be_fixed", "To Be Fixed");
            put("to_publish", "To Publish");
            put("published", "Published");
        }
    };
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskStatusMapper taskStatusMapper;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private LabelMapper labelMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            var userData = new UserCreateDTO();
            userData.setFirstName("First");
            userData.setLastName("Last");
            userData.setEmail("hexlet@example.com");
            userData.setPasswordDigest("qwerty");
            var user = userMapper.map(userData);
            userRepository.save(user);
        }

        if (taskStatusRepository.findAll().isEmpty()) {
            var taskStatuses = defaultStatuses.entrySet().stream()
                    .map(entry -> new TaskStatusCreateDTO(entry.getValue(), entry.getKey()))
                    .map(taskStatusMapper::map)
                    .toList();
            taskStatusRepository.saveAll(taskStatuses);
        }

        if (labelRepository.findAll().isEmpty()) {
            var labels = Arrays.stream(new String[]{"feature", "bug"})
                    .map(LabelCreateDTO::new)
                    .map(labelMapper::map)
                    .toList();
            labelRepository.saveAll(labels);
        }
    }
}
