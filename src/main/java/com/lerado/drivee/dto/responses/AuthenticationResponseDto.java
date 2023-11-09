package com.lerado.drivee.dto.responses;

public record AuthenticationResponseDto(
        String accessToken,
        String refreshToken) {
}
