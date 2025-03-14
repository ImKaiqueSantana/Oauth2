package com.GrupoSv.springsecurity.controllers;


import com.GrupoSv.springsecurity.dto.LoginRequest;
import com.GrupoSv.springsecurity.dto.LoginResponse;
import com.GrupoSv.springsecurity.entities.Role;
import com.GrupoSv.springsecurity.entities.User;
import com.GrupoSv.springsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;

    @Autowired
    private final UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public TokenController(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){

        var user  = userRepository.findByUsername(loginRequest.username());

        if (user.isEmpty() || !user.get().isloginIsCorrect(loginRequest, bCryptPasswordEncoder)){
            throw new BadCredentialsException("User or password is invalid");
        }

        var scope = user.get().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        var now = Instant.now();
        var expiresIn = 300L;
        var claims = JwtClaimsSet.builder()
        .issuer("myback-end")
               .subject(user.get().getUserId().toString())
               .expiresAt(now.plusSeconds(expiresIn))
                .issuedAt(now)
                .claim("scope",scope)
                .build();
        var  jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));
    }



   }



