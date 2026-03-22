package com.example.test_api;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.test_api.utils.ApiHelper;
import com.example.test_api.utils.ToastUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // 🔝 Добавляем кнопку "Назад" в ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Магазин");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchProducts();
    }

    private void fetchProducts() {
        ApiHelper.get("/products/", new ApiHelper.Callback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    List<Product> products = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        products.add(new Product(
                                obj.getInt("id"),
                                obj.getString("name"),
                                obj.getDouble("price"),
                                obj.getString("description"),
                                // 🔗 Формируем полный URL картинки
                                obj.getString("image_url").startsWith("http")
                                        ? obj.getString("image_url")
                                        : Config.API_BASE_URL + obj.getString("image_url")
                        ));
                    }
                    ProductAdapter adapter = new ProductAdapter(products, product -> {
                        Intent intent = new Intent(ShopActivity.this, ProductDetailActivity.class);
                        intent.putExtra("product_id", product.getId());
                        intent.putExtra("product_name", product.getName());
                        intent.putExtra("product_price", product.getPrice());
                        intent.putExtra("product_description", product.getDescription());
                        intent.putExtra("product_image_url", product.getImageUrl());
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    ToastUtils.error(ShopActivity.this, "Ошибка: " + e.getMessage());
                }
            }
            @Override
            public void onError(String error) {
                ToastUtils.error(ShopActivity.this, error);
            }
        });
    }

    // 🔙 Кнопка "Назад" в ActionBar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // 🍔 Меню (опционально)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
            return true;
        } else if (id == R.id.nav_logout) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}