package com.example.btn_nhom2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btn_nhom2.database.AppDatabase;
import com.example.btn_nhom2.entity.Order;
import com.example.btn_nhom2.entity.OrderDetail;
import com.example.btn_nhom2.entity.Product;
import com.example.btn_nhom2.util.PreferenceManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    private TextView tvName, tvPrice, tvDescription;
    private Button btnAddToCart;
    private AppDatabase db;
    private PreferenceManager pref;
    private int productId;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Enable Back Button in Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Product Detail");
        }

        db = AppDatabase.getInstance(this);
        pref = new PreferenceManager(this);
        productId = getIntent().getIntExtra("PRODUCT_ID", -1);

        tvName = findViewById(R.id.tvDetailName);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvDescription = findViewById(R.id.tvDetailDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        product = db.productDao().getProductById(productId);
        if (product != null) {
            tvName.setText(product.name);
            tvPrice.setText(String.format("$%.2f", product.price));
            tvDescription.setText(product.description);
        }

        btnAddToCart.setOnClickListener(v -> handleAddToCart());
    }

    private void handleAddToCart() {
        if (!pref.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để mua hàng!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        int userId = pref.getUserId();
        Order pendingOrder = db.orderDao().getPendingOrderByUser(userId);
        long orderId;

        if (pendingOrder == null) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            Order newOrder = new Order(userId, currentDate, "Pending", 0.0);
            orderId = db.orderDao().insert(newOrder);
        } else {
            orderId = pendingOrder.id;
        }

        // Thêm sản phẩm vào OrderDetails
        db.orderDetailDao().insert(new OrderDetail((int) orderId, productId, 1, product.price));
        
        showContinueShoppingDialog((int) orderId);
    }

    private void showContinueShoppingDialog(int orderId) {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Đã thêm sản phẩm vào đơn hàng. Bạn có muốn tiếp tục mua sắm không?")
                .setPositiveButton("Tiếp tục mua", (dialog, which) -> {
                    // Quay lại danh sách sản phẩm (MainActivity)
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                })
                .setNegativeButton("Thanh toán", (dialog, which) -> {
                    // Đi tới màn hình hóa đơn (Checkout)
                    Intent intent = new Intent(this, OrderSummaryActivity.class);
                    intent.putExtra("ORDER_ID", orderId);
                    startActivity(intent);
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Quay lại màn hình trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
