package ru.practicum.shareit.user;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    @Validated
    public CreateUserDto create(@Valid @RequestBody CreateUserDto createUserDto) {
        return userService.addUser(createUserDto);
    }

    @PatchMapping("/{id}")
    @Validated
    public CreateUserDto update(@PathVariable Long id, @Valid @RequestBody UpdateUserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @GetMapping("/{id}")
    public CreateUserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping
    public List<CreateUserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
