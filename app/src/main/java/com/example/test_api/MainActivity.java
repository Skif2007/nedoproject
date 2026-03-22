package  com.example.test_api;
//TO-DO: сделать иконку приложения и когда тосты
//        высвечиваются тоже заменить там иконку на свою, закинуть всё на git и подумать ка хостить
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    private EditText etFirstName, etLastName, etBirthYear, etEmail, etPhone, etPassword;
    private EditText etLoginEmail, etLoginPassword;
    private EditText etSearchName;
    private TextView tvResult;

    private final String BASE_URL = "http://10.127.122.56:8000";  //10.0.2.2
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etBirthYear = findViewById(R.id.etBirthYear);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        etSearchName = findViewById(R.id.etSearchName);
        tvResult = findViewById(R.id.tvResult);
        Button btnAddUser = findViewById(R.id.btnAddUser);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGetAllUsers = findViewById(R.id.btnGetAllUsers);
        Button btnSearchByName = findViewById(R.id.btnSearchByName);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btnGetAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAllUsers();
            }
        });

        btnSearchByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByName();
            }
        });
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String birthYearStr = etBirthYear.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();


        if (!validateRegistration(firstName, lastName, birthYearStr, email, phone, password)) {
            return;
        }

        int birthYear = Integer.parseInt(birthYearStr);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(BASE_URL + "/users/");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setDoOutput(true);

                    JSONObject user = new JSONObject();
                    user.put("first_name", firstName);
                    user.put("last_name", lastName);
                    user.put("birth_year", birthYear);
                    user.put("email", email);
                    user.put("phone", phone);
                    user.put("password", password);

                    OutputStream os = urlConnection.getOutputStream();
                    os.write(user.toString().getBytes("UTF-8"));
                    os.close();

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        final String result = "✅ Регистрация успешна:\n" + response.toString();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText(result);
                                etFirstName.setText("");
                                etLastName.setText("");
                                etBirthYear.setText("");
                                etEmail.setText("");
                                etPhone.setText("");
                                etPassword.setText("");
                            }
                        });
                    } else {
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                        String errorLine;
                        StringBuilder errorResponse = new StringBuilder();
                        while ((errorLine = errorReader.readLine()) != null) {
                            errorResponse.append(errorLine);
                        }
                        errorReader.close();
                        showError("Ошибка регистрации: " + responseCode + " - " + errorResponse.toString());
                    }
                } catch (Exception e) {
                    showError("Ошибка: " + e.getMessage());
                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
                }
            }
        });
    }

    private boolean validateRegistration(String firstName, String lastName, String birthYearStr,
                                         String email, String phone, String password) {
        if (TextUtils.isEmpty(firstName) || (TextUtils.isEmpty(lastName)) ||
                TextUtils.isEmpty(birthYearStr) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            int year = Integer.parseInt(birthYearStr);
            if (year < 1900 || year > 2025) {
                Toast.makeText(this, "Год рождения должен быть между 1900 и 2025", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Год рождения должен быть числом", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (!phone.matches("^[+0-9-]{10,15}$")) {
            Toast.makeText(this, "Телефон должен содержать от 10 до 15 цифр, + и -", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 4) {
            Toast.makeText(this, "Пароль должен быть не менее 4 символов", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void loginUser() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(BASE_URL + "/login");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setDoOutput(true);

                    JSONObject loginData = new JSONObject();
                    loginData.put("email", email);
                    loginData.put("password", password);

                    OutputStream os = urlConnection.getOutputStream();
                    os.write(loginData.toString().getBytes("UTF-8"));
                    os.close();

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        JSONObject userJson = new JSONObject(response.toString());
                        final String firstName = userJson.getString("first_name");
                        final String lastName = userJson.getString("last_name");
                        final String email = userJson.getString("email");
                        final String phone = userJson.getString("phone");
                        final int birthYear = userJson.getInt("birth_year");

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Вход выполнен", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                                intent.putExtra("first_name", firstName);
                                intent.putExtra("last_name", lastName);
                                intent.putExtra("email", email);
                                intent.putExtra("phone", phone);
                                intent.putExtra("birth_year", birthYear);
                                startActivity(intent);

                                etLoginEmail.setText("");
                                etLoginPassword.setText("");
                            }
                        });
                    } else {
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                        String errorLine;
                        StringBuilder errorResponse = new StringBuilder();
                        while ((errorLine = errorReader.readLine()) != null) {
                            errorResponse.append(errorLine);
                        }
                        errorReader.close();
                        showError("Ошибка входа: " + responseCode + " - " + errorResponse.toString());
                    }
                } catch (Exception e) {
                    showError("Ошибка: " + e.getMessage());
                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
                }
            }
        });
    }

    private void fetchAllUsers() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(BASE_URL + "/users/");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Accept", "application/json");

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        final String formatted = formatUsers(response.toString(), null);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText(formatted);
                            }
                        });
                    } else {
                        showError("Ошибка загрузки: " + responseCode);
                    }
                } catch (Exception e) {
                    showError("Ошибка: " + e.getMessage());
                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
                }
            }
        });
    }

    private void searchByName() {
        final String query = etSearchName.getText().toString().trim().toLowerCase();
        if (query.isEmpty()) {
            Toast.makeText(this, "Введите имя для поиска", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(BASE_URL + "/users/");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Accept", "application/json");

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        final String filtered = formatUsers(response.toString(), query);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvResult.setText(filtered);}
                        });
                    } else {
                        showError("Ошибка загрузки: " + responseCode);
                    }
                } catch (Exception e) {
                    showError("Ошибка: " + e.getMessage());
                } finally {
                    if (urlConnection != null) urlConnection.disconnect();
                }
            }
        });
    }

    private String formatUsers(String json, String filter) {
        try {
            JSONArray users = new JSONArray(json);
            StringBuilder sb = new StringBuilder();
            if (filter == null) {
                sb.append("📋 Все пользователи:\n");
            } else {
                sb.append("🔍 Результаты поиска по имени \"").append(filter).append("\":\n");
            }
            boolean found = false;
            for (int i = 0; i < users.length(); i++) {
                JSONObject u = users.getJSONObject(i);
                String firstName = u.getString("first_name");
                if (filter == null || firstName.toLowerCase().contains(filter)) {
                    found = true;
                    sb.append(u.getInt("id")).append(": ")
                            .append(firstName).append(" ")
                            .append(u.getString("last_name")).append(", ")
                            .append(u.getInt("birth_year")).append(", ")
                            .append(u.getString("email")).append(", ")
                            .append(u.getString("phone")).append("\n");
                }
            }
            if (!found && filter != null) {
                sb.append("Ничего не найдено.");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Ошибка парсинга JSON: " + e.getMessage();
        }
    }

    private void showError(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                tvResult.setText("❌ " + message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}