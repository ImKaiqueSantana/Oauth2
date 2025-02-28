package com.GrupoSv.springsecurity.controllers;


import com.GrupoSv.springsecurity.dto.CreateUserDto;
import com.GrupoSv.springsecurity.entities.Role;
import com.GrupoSv.springsecurity.entities.User;
import com.GrupoSv.springsecurity.repository.RoleRepository;
import com.GrupoSv.springsecurity.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
public class UserController {


    private RoleRepository roleRepository;

    private UserRepository userRepository;

    private  BCryptPasswordEncoder passwordEncoder;


    public UserController(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    @PostMapping("/users")
    public ResponseEntity<Void> newUser(@RequestBody CreateUserDto dto){
       var basicRole =  roleRepository.findByName(Role.Values.BASIC.name());
       var username = userRepository.findByUsername(dto.username());

       if(username.isPresent()){
           throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
       }
        var user = new User();
       user.setUsername(dto.username());
       user.setPassword(passwordEncoder.encode(dto.password()));
       user.setRoles(Set.of(basicRole));
       userRepository.save(user);
    return ResponseEntity.ok().build();

    }


    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listUsers(){
        var all = userRepository.findAll();
        return ResponseEntity.ok(all);
    }
}
