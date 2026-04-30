package com.sentineia.users;

import com.sentineia.base.BaseController;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController<User, UserService> {

    public UserController(UserService service) {
        super(service);
    }

    @PostMapping
    @Override
    public ResponseEntity<User> create(@Valid @RequestBody User entity) {
        return super.create(entity);
    }
}
