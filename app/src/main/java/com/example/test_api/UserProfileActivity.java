package com.example.test_api;
import com.example.test_api.Config;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvBirthYear = findViewById(R.id.tvBirthYear);
        Button btnLogout = findViewById(R.id.btnLogout);
        String firstName = getIntent().getStringExtra("first_name");
        String lastName = getIntent().getStringExtra("last_name");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
        int birthYear = getIntent().getIntExtra("birth_year", 0);
        tvWelcome.setText("Добро пожаловать, " + firstName + "!");
        tvName.setText("Имя: " + firstName + " " + lastName);
        tvEmail.setText("Email: " + email);
        tvPhone.setText("Телефон: " + phone);
        tvBirthYear.setText("Год рождения: " + birthYear);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}