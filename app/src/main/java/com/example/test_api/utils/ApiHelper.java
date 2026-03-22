package com.example.test_api.utils;
import com.example.test_api.Config;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiHelper {

    private static final String TAG = "ApiHelper";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onSuccess(String response);
        void onError(String error);
    }

    // 🔗 Безопасная сборка полного URL
    private static String buildUrl(String endpoint) {
        String base = Config.API_BASE_URL;

        // Убираем слеш в конце base, если есть
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);

        // Добавляем слеш в начало endpoint, если нет
        if (!endpoint.startsWith("/")) endpoint = "/" + endpoint;

        String fullUrl = base + endpoint;
        Log.d(TAG, "🔗 Полный URL: " + fullUrl);
        return fullUrl;
    }

    public static void post(String endpoint, JSONObject body, Callback callback) {
        executor.execute(() -> {
            try {
                String fullUrl = buildUrl(endpoint);
                Log.d(TAG, "📤 POST " + fullUrl);
                Log.d(TAG, "📦 Body: " + body.toString());

                URL url = new URL(fullUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(Config.CONNECT_TIMEOUT);
                conn.setReadTimeout(Config.READ_TIMEOUT);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int code = conn.getResponseCode();
                Log.d(TAG, "📥 Ответ код: " + code);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(code >= 400 ? conn.getErrorStream() : conn.getInputStream())
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();
                conn.disconnect();

                Log.d(TAG, "📄 Ответ тело: " + response);

                if (code >= 200 && code < 300) {
                    handler.post(() -> callback.onSuccess(response.toString()));
                } else {
                    handler.post(() -> callback.onError("Ошибка " + code + ": " + response));
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Ошибка: " + e.getMessage(), e);
                handler.post(() -> callback.onError("Ошибка сети: " + e.getMessage()));
            }
        });
    }

    public static void get(String endpoint, Callback callback) {
        executor.execute(() -> {
            try {
                String fullUrl = buildUrl(endpoint);
                Log.d(TAG, "📤 GET " + fullUrl);

                URL url = new URL(fullUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(Config.CONNECT_TIMEOUT);
                conn.setReadTimeout(Config.READ_TIMEOUT);

                int code = conn.getResponseCode();
                Log.d(TAG, "📥 Ответ код: " + code);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(code >= 400 ? conn.getErrorStream() : conn.getInputStream())
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();
                conn.disconnect();

                Log.d(TAG, "📄 Ответ тело: " + response);

                if (code >= 200 && code < 300) {
                    handler.post(() -> callback.onSuccess(response.toString()));
                } else {
                    handler.post(() -> callback.onError("Ошибка " + code));
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Ошибка: " + e.getMessage(), e);
                handler.post(() -> callback.onError("Ошибка сети: " + e.getMessage()));
            }
        });
    }
}