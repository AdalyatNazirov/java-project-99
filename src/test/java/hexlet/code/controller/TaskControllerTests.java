package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskControllerTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    private Task testTask;
    private TaskStatus testTaskStatus;
    private User testUser;
    private Label testLabel;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testTaskStatus = taskStatusRepository.findBySlug("draft").get();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        testLabel = labelRepository.findByName("bug").get();

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setTaskStatus(testTaskStatus);
        testTask.setAssignee(testUser);
    }

    @Test
    public void testList() throws Exception {
        taskRepository.save(testTask);
        var result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var expected = taskRepository.findAll().stream().map(taskMapper::map).toList();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize(expected.size());

        List<TaskDTO> userDTOs = om.readValue(body, new TypeReference<>() {
        });
        assertThat(userDTOs).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testListWithFilter() throws Exception {
        taskRepository.save(testTask);

        var anotherStatus = taskStatusRepository.findBySlug("to_review").get();

        var anotherTask = Instancio.of(modelGenerator.getTaskModel()).create();
        anotherTask.setTaskStatus(anotherStatus);
        anotherTask.setAssignee(testUser);
        taskRepository.save(anotherTask);

        var result = mockMvc.perform(get("/api/tasks"
                                         + "?assigneeId=" + testUser.getId()
                                         + "&status=" + testTaskStatus.getSlug()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize(1);
        assertThatJson(body).node("[0].status").isEqualTo(testTaskStatus.getSlug());
        assertThatJson(body).node("[0].assignee_id").isEqualTo(testUser.getId());
    }

    @Test
    public void testListWithLabelWilter() throws Exception {
        taskRepository.save(testTask);

        var anotherTask = Instancio.of(modelGenerator.getTaskModel()).create();
        anotherTask.setTaskStatus(testTaskStatus);
        anotherTask.setAssignee(testUser);
        anotherTask.getLabels().addAll(labelRepository.findAll());
        taskRepository.save(anotherTask);

        var result = mockMvc.perform(get("/api/tasks?labelId=" + testLabel.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize(1);
        assertThatJson(body).node("[0].status").isEqualTo(testTaskStatus.getSlug());
        assertThatJson(body).node("[0].assignee_id").isEqualTo(testUser.getId());
    }

    @Test
    public void testShow() throws Exception {
        taskRepository.save(testTask);

        var result = mockMvc.perform(get("/api/tasks/" + testTask.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()),
                v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId())
        );
    }

    @Test
    public void testShowReturnNotFound() throws Exception {
        var result = mockMvc.perform(get("/api/tasks/10000000").with(jwt()))
                .andExpect(status().isNotFound())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("Task with id 10000000 not found");
    }

    @Test
    public void testCreate() throws Exception {
        var taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setName("Test Task");
        taskCreateDTO.setDescription("Test Description");
        taskCreateDTO.setStatus(testTaskStatus.getSlug());
        taskCreateDTO.setAssigneeId(testUser.getId());

        var request = post("/api/tasks").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskCreateDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var tasks = taskRepository.findAll();
        var task = tasks.stream()
                .filter(t -> t.getName().equals("Test Task"))
                .findFirst()
                .orElse(null);

        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo(taskCreateDTO.getName());
        assertThat(task.getDescription()).isEqualTo(taskCreateDTO.getDescription());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(taskCreateDTO.getStatus());
        assertThat(task.getAssignee().getId()).isEqualTo(taskCreateDTO.getAssigneeId());
    }

    @Test
    public void testCreateWithLabel() throws Exception {
        var taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setName("Test Task");
        taskCreateDTO.setDescription("Test Description");
        taskCreateDTO.setStatus(testTaskStatus.getSlug());
        taskCreateDTO.setAssigneeId(testUser.getId());
        taskCreateDTO.setLabelIds(new HashSet<>() {{
            add(testLabel.getId());
        }});

        var request = post("/api/tasks").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskCreateDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var tasks = taskRepository.findAll();
        var task = tasks.stream()
                .filter(t -> t.getName().equals("Test Task"))
                .findFirst()
                .orElse(null);

        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo(taskCreateDTO.getName());
        assertThat(task.getDescription()).isEqualTo(taskCreateDTO.getDescription());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(taskCreateDTO.getStatus());
        assertThat(task.getAssignee().getId()).isEqualTo(taskCreateDTO.getAssigneeId());
        assertThat(task.getLabels()).hasSize(1);
        assertThat(task.getLabels().iterator().next().getId()).isEqualTo(testLabel.getId());
    }

    @Test
    public void testUpdate() throws Exception {
        taskRepository.save(testTask);

        var taskUpdateDTO = new TaskUpdateDTO();
        taskUpdateDTO.setName(org.openapitools.jackson.nullable.JsonNullable.of("Updated Task"));
        taskUpdateDTO.setDescription(org.openapitools.jackson.nullable.JsonNullable.of("Updated Description"));

        var request = put("/api/tasks/{id}", testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskUpdateDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedTask = taskRepository.findById(testTask.getId()).get();

        assertThat(updatedTask.getName()).isEqualTo("Updated Task");
        assertThat(updatedTask.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    public void testPartialUpdate() throws Exception {
        taskRepository.save(testTask);
        var newLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(newLabel);

        var dto = new HashMap<String, Object>();
        dto.put("taskLabelIds", new HashSet<>() {{
            add(newLabel.getId());
        }});

        var request = put("/api/tasks/{id}", testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedTask = taskRepository.findById(testTask.getId()).get();

        assertThat(updatedTask.getLabels()).hasSize(1);
        assertThat(updatedTask.getLabels().iterator().next().getId()).isEqualTo(newLabel.getId());
    }

    @Test
    public void testUpdateLabels() throws Exception {
        taskRepository.save(testTask);

        var dto = new HashMap<String, Object>();
        dto.put("title", "Partially Updated Task");

        var request = put("/api/tasks/{id}", testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedTask = taskRepository.findById(testTask.getId()).get();

        assertThat(updatedTask.getName()).isEqualTo("Partially Updated Task");
        assertThat(updatedTask.getDescription()).isEqualTo(testTask.getDescription());
    }

    @Test
    public void testDestroy() throws Exception {
        var task = taskRepository.save(testTask);

        mockMvc.perform(delete("/api/tasks/" + task.getId()).with(jwt()))
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(taskRepository.findById(task.getId())).isEmpty();
    }

    @Test
    public void testDestroyWhenNotExists() throws Exception {
        Long id = 11L;
        assertThat(taskRepository.findById(id)).isEmpty();

        mockMvc.perform(delete("/api/tasks/" + id).with(jwt()))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
