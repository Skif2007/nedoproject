package com.example.test_api;

public class Config {
    // Адрес берётся из BuildConfig, но можно переопределить для тестов
    public static final String API_BASE_URL = BuildConfig.API_BASE_URL;

    // Таймауты
    public static final int CONNECT_TIMEOUT = 10_000; // 10 сек
    public static final int READ_TIMEOUT = 15_000;
}