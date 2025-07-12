package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.AuthRequest;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import hexlet.code.util.TestDataCleaner;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private TestDataCleaner testDataCleaner;

    @BeforeEach
    public void setUp() {
        testDataCleaner.cleanAll();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
    }

    @Test
    public void testLogin() throws Exception {
        UserCreateDTO userDto = new UserCreateDTO();
        userDto.setFirstName(testUser.getFirstName());
        userDto.setLastName(testUser.getLastName());
        userDto.setEmail(testUser.getEmail());
        userDto.setPasswordDigest(testUser.getPasswordDigest());

        userRepository.save(userMapper.map(userDto));

        var authRequest = new AuthRequest();
        authRequest.setUsername(testUser.getEmail());
        authRequest.setPassword(testUser.getPasswordDigest());

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(authRequest));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThat(body).isNotBlank();
    }

    @Test
    public void testLoginFail() throws Exception {

        var authRequest = new AuthRequest();
        authRequest.setUsername("hexlet@example.com");
        authRequest.setPassword("qwerty2");

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(authRequest));

        var result = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThat(body).isEqualTo("Invalid credentials");
    }
}
