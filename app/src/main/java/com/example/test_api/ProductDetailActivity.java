package com.example.test_api;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView imageView = findViewById(R.id.detail_image);
        TextView nameView = findViewById(R.id.detail_name);
        TextView priceView = findViewById(R.id.detail_price);
        TextView descView = findViewById(R.id.detail_description);
        Button btnBuy = findViewById(R.id.btn_buy);
        Button btnCart = findViewById(R.id.btn_cart);
        ImageButton btnBack = findViewById(R.id.btn_back);

        String name = getIntent().getStringExtra("product_name");
        double price = getIntent().getDoubleExtra("product_price", 0);
        String description = getIntent().getStringExtra("product_description");
        String imageUrl = getIntent().getStringExtra("product_image_url");

        // Загружаем изображение
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.loadImage(imageUrl, imageView);

        nameView.setText(name);
        priceView.setText(String.format("$%.2f", price));
        descView.setText(description);

        btnBack.setOnClickListener(v -> finish()); // возврат к списку товаров

        // Кнопки пока не делают ничего
        btnBuy.setOnClickListener(v -> { });
        btnCart.setOnClickListener(v -> { });
    }
}
