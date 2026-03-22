package com.example.test_api;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShopActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private final String BASE_URL = "http://10.127.122.56:8000"; // для эмулятора
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        recyclerView = findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Устанавливаем обработчик для NavigationView
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchProducts();
    }

    private void fetchProducts() {
        executor.execute(() -> {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(BASE_URL + "/products/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    JSONArray jsonArray = new JSONArray(response.toString());
                    List<Product> productList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String name = obj.getString("name");
                        double price = obj.getDouble("price");
                        String description = obj.getString("description");
                        String imageUrl = BASE_URL + obj.getString("image_url");
                        productList.add(new Product(id, name, price, description, imageUrl));
                    }

                    handler.post(() -> {
                        ProductAdapter adapter = new ProductAdapter(productList, product -> {
                            Intent intent = new Intent(ShopActivity.this, ProductDetailActivity.class);
                            intent.putExtra("product_id", product.getId());
                            intent.putExtra("product_name", product.getName());
                            intent.putExtra("product_price", product.getPrice());
                            intent.putExtra("product_description", product.getDescription());
                            intent.putExtra("product_image_url", product.getImageUrl());
                            startActivity(intent);
                        });
                        recyclerView.setAdapter(adapter);
                    });
                } else {
                    showError("Ошибка загрузки товаров: " + responseCode);
                }
            } catch (Exception e) {
                showError("Ошибка: " + e.getMessage());
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
        });
    }

    private void showError(final String message) {
        handler.post(() -> Toast.makeText(ShopActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    // Обработка выбора пунктов меню
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Toast.makeText(this, "Главная", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_profile) {
            Toast.makeText(this, "Профиль", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show();
            // Здесь можно вернуться на экран логина, например:
            // Intent intent = new Intent(ShopActivity.this, MainActivity.class);
            // startActivity(intent);
            // finish();
        }
        drawerLayout.closeDrawers(); // Закрываем меню после выбора
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}