package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import hexlet.code.util.TestDataCleaner;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
public class LabelControllerTests {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private LabelRepository labelRepository;

    private Label testLabel;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private Faker faker;

    @Autowired
    private TestDataCleaner testDataCleaner;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testDataCleaner.cleanAll();

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
    }

    @Test
    public void testList() throws Exception {
        labelRepository.save(testLabel);

        var result = mockMvc.perform(get("/api/labels").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        var expected = labelRepository.findAll().stream().map(labelMapper::map).toList();
        assertThatJson(body).isArray().hasSize(expected.size());

        List<LabelDTO> labelDTOs = om.readValue(body, new TypeReference<>() {
        });
        assertThat(labelDTOs).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        labelRepository.save(testLabel);

        var result = mockMvc.perform(get("/api/labels/" + testLabel.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    public void testShowReturnNotFound() throws Exception {
        var result = mockMvc.perform(get("/api/labels/10000000").with(jwt()))
                .andExpect(status().isNotFound())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("Label with id 10000000 not found");
    }

    @Test
    public void testCreate() throws Exception {
        var labelCreateDTO = new LabelCreateDTO("Test Label");

        var request = post("/api/labels").with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(labelCreateDTO));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var labels = labelRepository.findAll();
        var label = labels.stream()
                .filter(l -> l.getName().equals("Test Label"))
                .findFirst()
                .orElse(null);

        assertThat(label).isNotNull();
        assertThat(label.getName()).isEqualTo(labelCreateDTO.getName());
    }

    @Test
    public void testUpdate() throws Exception {
        labelRepository.save(testLabel);

        var labelUpdateDTO = new LabelUpdateDTO(org.openapitools.jackson.nullable.JsonNullable.of("Updated Label"));

        var request = put("/api/labels/{id}", testLabel.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(labelUpdateDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedLabel = labelRepository.findById(testLabel.getId()).get();

        assertThat(updatedLabel.getName()).isEqualTo("Updated Label");
    }

    @Test
    public void testPartialUpdate() throws Exception {
        labelRepository.save(testLabel);

        var dto = new HashMap<String, Object>();
        dto.put("name", "Partially Updated Label");

        var request = put("/api/labels/{id}", testLabel.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedLabel = labelRepository.findById(testLabel.getId()).get();

        assertThat(updatedLabel.getName()).isEqualTo("Partially Updated Label");
    }

    @Test
    public void testDestroy() throws Exception {
        var label = labelRepository.save(testLabel);

        mockMvc.perform(delete("/api/labels/" + label.getId()).with(jwt()))
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(labelRepository.findById(label.getId())).isEmpty();
    }

    @Test
    public void testDestroyWhenNotExists() throws Exception {
        Long id = 11L;
        assertThat(labelRepository.findById(id)).isEmpty();

        mockMvc.perform(delete("/api/labels/" + id).with(jwt()))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
