package com.example.btn_nhom2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btn_nhom2.database.AppDatabase;
import com.example.btn_nhom2.entity.User;
import com.example.btn_nhom2.util.PreferenceManager;
// login
public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnSubmit;
    private AppDatabase db;
    private PreferenceManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = AppDatabase.getInstance(this);
        pref = new PreferenceManager(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSubmit = findViewById(R.id.btnSubmitLogin);

        btnSubmit.setOnClickListener(v -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();

            User loggedInUser = db.userDao().login(user, pass);
            if (loggedInUser != null) {
                pref.setLogin(loggedInUser.id);
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
