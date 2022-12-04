package com.platform.order.security;

public record Token(String header, int expirySeconds) {
}