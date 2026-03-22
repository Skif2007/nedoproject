package com.example.test_api;

import android.util.Log;

public class Config {

    private static final String TAG = "Config";

    // 🔥 Получаем URL и сразу логируем
    public static final String API_BASE_URL = initBaseUrl();

    private static String initBaseUrl() {
        String url = BuildConfig.API_BASE_URL;
        Log.d(TAG, "=== API_BASE_URL из BuildConfig: '" + url + "' ===");

        // 🔧 Фолбэк на случай, если BuildConfig не подхватился
        if (url == null || url.isEmpty() || url.equals("null")) {
            Log.w(TAG, "⚠️ BuildConfig.API_BASE_URL пустой! Используем дефолт.");
            return "http://10.0.2.2:8000"; // эмулятор
            // Для телефона: "http://192.168.1.X:8000"
        }

        // 🔧 Убираем лишние слеши в конце
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        // 🔧 Добавляем http:// если нет
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            Log.w(TAG, "⚠️ В URL нет протокола, добавляем http://");
            url = "http://" + url;
        }

        Log.d(TAG, "✅ Итоговый API_BASE_URL: '" + url + "'");
        return url;
    }

    public static final int CONNECT_TIMEOUT = 10_000;
    public static final int READ_TIMEOUT = 15_000;
}