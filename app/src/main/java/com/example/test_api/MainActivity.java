package com.example.test_api;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.test_api.utils.ApiHelper;
import com.example.test_api.utils.ToastUtils;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etBirthYear, etEmail, etPhone, etPassword;
    private EditText etLoginEmail, etLoginPassword;
    private EditText etSearchName;
    private TextView tvResult;

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

        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnAllUsers = findViewById(R.id.btnAllUsers);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnRegister.setOnClickListener(v -> registerUser());
        btnLogin.setOnClickListener(v -> loginUser());
        btnAllUsers.setOnClickListener(v -> fetchAllUsers());
        btnSearch.setOnClickListener(v -> searchByName());
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String birthYearStr = etBirthYear.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(firstName, lastName, birthYearStr, email, phone, password)) return;

        try {
            JSONObject user = new JSONObject();
            user.put("first_name", firstName);
            user.put("last_name", lastName);
            user.put("birth_year", Integer.parseInt(birthYearStr));
            user.put("email", email);
            user.put("phone", phone);
            user.put("password", password);

            ApiHelper.post("/users/", user, new ApiHelper.Callback() {
                @Override
                public void onSuccess(String response) {
                    ToastUtils.success(MainActivity.this, getString(R.string.success_register));
                    clearRegistrationFields();
                    tvResult.setText("✅ " + response);
                }
                @Override
                public void onError(String error) {
                    ToastUtils.error(MainActivity.this, error);
                    tvResult.setText("❌ " + error);
                }
            });
        } catch (Exception e) {
            ToastUtils.error(this, "Ошибка: " + e.getMessage());
        }
    }

    private boolean validateInput(String fn, String ln, String by, String email, String phone, String pass) {
        if (TextUtils.isEmpty(fn) || TextUtils.isEmpty(ln) || TextUtils.isEmpty(by) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(pass)) {
            ToastUtils.error(this, getString(R.string.fill_all));
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ToastUtils.error(this, getString(R.string.invalid_email));
            return false;
        }
        try {
            int year = Integer.parseInt(by);
            if (year < 1900 || year > 2025) {
                ToastUtils.error(this, getString(R.string.invalid_year));
                return false;
            }
        } catch (NumberFormatException e) {
            ToastUtils.error(this, getString(R.string.invalid_year));
            return false;
        }
        if (!phone.matches("^[+0-9-]{10,15}$")) {
            ToastUtils.error(this, getString(R.string.invalid_phone));
            return false;
        }
        if (pass.length() < 4) {
            ToastUtils.error(this, getString(R.string.short_password));
            return false;
        }
        return true;
    }

    private void loginUser() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            ToastUtils.error(this, getString(R.string.fill_all));
            return;
        }

        try {
            JSONObject data = new JSONObject();
            data.put("email", email);
            data.put("password", password);

            ApiHelper.post("/login", data, new ApiHelper.Callback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject user = new JSONObject(response);
                        Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                        intent.putExtra("first_name", user.getString("first_name"));
                        intent.putExtra("last_name", user.getString("last_name"));
                        intent.putExtra("email", user.getString("email"));
                        intent.putExtra("phone", user.getString("phone"));
                        intent.putExtra("birth_year", user.getInt("birth_year"));
                        startActivity(intent);
                        ToastUtils.success(MainActivity.this, getString(R.string.success_login));
                    } catch (Exception e) {
                        ToastUtils.error(MainActivity.this, "Ошибка парсинга: " + e.getMessage());
                    }
                }
                @Override
                public void onError(String error) {
                    ToastUtils.error(MainActivity.this, error);
                }
            });
        } catch (Exception e) {
            ToastUtils.error(this, "Ошибка: " + e.getMessage());
        }
    }

    private void fetchAllUsers() {
        ApiHelper.get("/users/", new ApiHelper.Callback() {
            @Override
            public void onSuccess(String response) {
                tvResult.setText(formatUsers(response, null));
            }
            @Override
            public void onError(String error) {
                ToastUtils.error(MainActivity.this, error);
                tvResult.setText("❌ " + error);
            }
        });
    }

    private void searchByName() {
        String query = etSearchName.getText().toString().trim().toLowerCase();
        if (query.isEmpty()) {
            ToastUtils.error(this, "Введите имя для поиска");
            return;
        }
        ApiHelper.get("/users/", new ApiHelper.Callback() {
            @Override
            public void onSuccess(String response) {
                tvResult.setText(formatUsers(response, query));
            }
            @Override
            public void onError(String error) {
                ToastUtils.error(MainActivity.this, error);
            }
        });
    }

    private String formatUsers(String json, String filter) {
        try {
            org.json.JSONArray users = new org.json.JSONArray(json);
            StringBuilder sb = new StringBuilder();
            sb.append(filter == null ? "📋 Все пользователи:\n" : "🔍 Поиск \"" + filter + "\":\n");
            boolean found = false;
            for (int i = 0; i < users.length(); i++) {
                JSONObject u = users.getJSONObject(i);
                String name = u.getString("first_name");
                if (filter == null || name.toLowerCase().contains(filter)) {
                    found = true;
                    sb.append(u.getInt("id")).append(": ")
                            .append(name).append(" ").append(u.getString("last_name")).append(", ")
                            .append(u.getInt("birth_year")).append(", ")
                            .append(u.getString("email")).append("\n");
                }
            }
            if (!found && filter != null) sb.append("Ничего не найдено.");
            return sb.toString();
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    private void clearRegistrationFields() {
        etFirstName.setText("");
        etLastName.setText("");
        etBirthYear.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etPassword.setText("");
    }
}