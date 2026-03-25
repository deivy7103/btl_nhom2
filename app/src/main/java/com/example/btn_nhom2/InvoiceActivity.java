package com.example.btn_nhom2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btn_nhom2.adapter.OrderDetailAdapter;
import com.example.btn_nhom2.database.AppDatabase;
import com.example.btn_nhom2.entity.Order;
import com.example.btn_nhom2.entity.OrderDetail;
import com.example.btn_nhom2.entity.User;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {
    private RecyclerView rvDetails;
    private TextView tvUser, tvDate, tvTotal, tvStatus;
    private Button btnBack;
    private AppDatabase db;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Hóa đơn");
        }

        db = AppDatabase.getInstance(this);
        orderId = getIntent().getIntExtra("ORDER_ID", -1);

        rvDetails = findViewById(R.id.rvInvoiceDetails);
        tvUser = findViewById(R.id.tvInvoiceUser);
        tvDate = findViewById(R.id.tvInvoiceDate);
        tvTotal = findViewById(R.id.tvInvoiceTotal);
        tvStatus = findViewById(R.id.tvInvoiceStatus);
        btnBack = findViewById(R.id.btnBackToHome);

        rvDetails.setLayoutManager(new LinearLayoutManager(this));
        
        loadInvoiceData();

        btnBack.setOnClickListener(v -> {
            // Quay về MainActivity và xóa các activity khác trong stack
            android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadInvoiceData() {
        Order order = db.orderDao().getOrderById(orderId);
        if (order != null) {
            User user = db.userDao().getUserById(order.userId);
            tvUser.setText("Khách hàng: " + (user != null ? user.fullName : "Unknown"));
            tvDate.setText("Ngày đặt: " + order.orderDate);
            tvTotal.setText(String.format("Tổng tiền: $%.2f", order.totalAmount));
            tvStatus.setText("Trạng thái: " + order.status);

            List<OrderDetail> details = db.orderDetailDao().getOrderDetailsByOrderId(orderId);
            OrderDetailAdapter adapter = new OrderDetailAdapter(details, db);
            rvDetails.setAdapter(adapter);
        }
    }
}
