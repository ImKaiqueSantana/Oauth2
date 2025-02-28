package com.GrupoSv.springsecurity.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
