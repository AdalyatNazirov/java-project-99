package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("${base-url:/api}" + "/users")
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @GetMapping()
    public ResponseEntity<List<UserDTO>> list() {
        var users = userRepository.findAll().stream().map(userMapper::map).toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    @GetMapping(path = "/{id}")
    public UserDTO show(@PathVariable Long id) {
        var user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.map(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO userCreateDto) {
        var user = userMapper.map(userCreateDto);
        var result = userRepository.save(user);

        return userMapper.map(result);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    public void destroy(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDto) {
        var user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        if (userUpdateDto.getPasswordDigest() != null) {
            userMapper.update(userUpdateDto, user);
        }

        userMapper.update(userUpdateDto, user);
        var result = userRepository.save(user);
        return userMapper.map(result);
    }
}
