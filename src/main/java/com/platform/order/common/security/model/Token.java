package com.platform.order.common.security.model;

public record Token(String header, int expirySeconds) {
}