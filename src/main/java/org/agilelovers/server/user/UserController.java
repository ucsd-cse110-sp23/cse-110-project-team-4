package org.agilelovers.server.user;

import org.agilelovers.server.question.common.errors.UserNotFoundError;
import org.springframework.web.bind.annotation.*;
@RestController
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/api/users")
    public UserDocument getUser(@RequestBody UserDocument user) {
        return users.findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .orElseThrow(() -> new UserNotFoundError(user.getUsername()));
    }

    @PostMapping("/api/users")
    public UserDocument createUser(@RequestBody UserDocument user) {
        return users.save(user);
    }

    @PutMapping("/api/users/{id}")
    public UserDocument updateEmail(@RequestBody String email,
                                    @PathVariable String id) {
        return users.findById(id)
                .map(user -> {
                    user.setEmail(email);
                    return users.save(user);
                }).orElseThrow(() -> new UserNotFoundError(id));
    }
}
