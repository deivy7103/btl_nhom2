package com.example.btn_nhom2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btn_nhom2.adapter.CategoryAdapter;
import com.example.btn_nhom2.adapter.ProductAdapter;
import com.example.btn_nhom2.database.AppDatabase;
import com.example.btn_nhom2.entity.Category;
import com.example.btn_nhom2.entity.Order;
import com.example.btn_nhom2.entity.Product;
import com.example.btn_nhom2.entity.User;
import com.example.btn_nhom2.util.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvCategories, rvProducts;
    private TextView tvWelcome;
    private Button btnLogin, btnCart;
    private AppDatabase db;
    private PreferenceManager pref;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        pref = new PreferenceManager(this);

        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogin = findViewById(R.id.btnLogin);
        btnCart = findViewById(R.id.btnCart);

        setupRecyclerViews();
        checkAndSeedData();

        btnLogin.setOnClickListener(v -> {
            if (pref.isLoggedIn()) {
                pref.logout();
                updateUI();
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btnCart.setOnClickListener(v -> {
            if (!pref.isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            
            Order pendingOrder = db.orderDao().getPendingOrderByUser(pref.getUserId());
            if (pendingOrder != null) {
                Intent intent = new Intent(this, OrderSummaryActivity.class);
                intent.putExtra("ORDER_ID", pendingOrder.id);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndSeedData() {
        List<Category> categories = db.categoryDao().getAllCategories();
        if (categories == null || categories.isEmpty()) {
            db.categoryDao().insert(new Category("Electronics", "Gadgets"));
            db.categoryDao().insert(new Category("Fashion", "Clothes"));
            db.productDao().insert(new Product("Smartphone", "High-end phone", 999.9, 1));
            db.productDao().insert(new Product("Laptop", "Gaming laptop", 1500.0, 1));
            db.productDao().insert(new Product("Jean", "Blue jean", 45.0, 2));
            if (db.userDao().login("admin", "123") == null) {
                db.userDao().insert(new User("admin", "123", "Default Admin"));
            }
        }
        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        refreshData();
    }

    private void setupRecyclerViews() {
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), category -> {
            List<Product> products = db.productDao().getProductsByCategory(category.id);
            productAdapter.setProducts(products);
        });
        rvCategories.setAdapter(categoryAdapter);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(new ArrayList<>(), product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.id);
            startActivity(intent);
        });
        rvProducts.setAdapter(productAdapter);
    }

    private void refreshData() {
        List<Category> categories = db.categoryDao().getAllCategories();
        if (categories != null && !categories.isEmpty()) {
            categoryAdapter = new CategoryAdapter(categories, category -> {
                List<Product> products = db.productDao().getProductsByCategory(category.id);
                productAdapter.setProducts(products);
            });
            rvCategories.setAdapter(categoryAdapter);
        }

        List<Product> allProducts = db.productDao().getAllProducts();
        if (allProducts != null && !allProducts.isEmpty()) {
            productAdapter.setProducts(allProducts);
        }
    }

    private void updateUI() {
        if (pref.isLoggedIn()) {
            User user = db.userDao().getUserById(pref.getUserId());
            if (user != null) {
                tvWelcome.setText("Hello, " + user.fullName);
            }
            btnLogin.setText("Logout");
        } else {
            tvWelcome.setText("Welcome!");
            btnLogin.setText("Login");
        }
    }
}
