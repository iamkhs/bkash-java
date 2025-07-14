package me.iamkhs.bkashjava.dto;

public record BkashTokenResponse(
        String id_token,
        String token_type,
        String refresh_token,
        String expires_in,
        String status) {}

