package com.example.btn_nhom2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btn_nhom2.adapter.OrderDetailAdapter;
import com.example.btn_nhom2.database.AppDatabase;
import com.example.btn_nhom2.entity.Order;
import com.example.btn_nhom2.entity.OrderDetail;
import java.util.List;

public class OrderSummaryActivity extends AppCompatActivity {
    private RecyclerView rvDetails;
    private TextView tvTotal;
    private Button btnPay;
    private AppDatabase db;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Checkout (Thanh toán)");
        }

        db = AppDatabase.getInstance(this);
        orderId = getIntent().getIntExtra("ORDER_ID", -1);

        rvDetails = findViewById(R.id.rvOrderDetails);
        tvTotal = findViewById(R.id.tvTotalAmount);
        btnPay = findViewById(R.id.btnPay);

        rvDetails.setLayoutManager(new LinearLayoutManager(this));
        
        loadOrderData();

        btnPay.setOnClickListener(v -> {
            Order order = db.orderDao().getOrderById(orderId);
            if (order != null) {
                // Cập nhật trạng thái thành Paid
                order.status = "Paid";
                db.orderDao().update(order);
                
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                
                // Chuyển sang màn hình Hiển thị hóa đơn cuối cùng
                Intent intent = new Intent(this, InvoiceActivity.class);
                intent.putExtra("ORDER_ID", orderId);
                startActivity(intent);
                finish(); // Đóng màn hình checkout
            }
        });
    }

    private void loadOrderData() {
        List<OrderDetail> details = db.orderDetailDao().getOrderDetailsByOrderId(orderId);
        OrderDetailAdapter adapter = new OrderDetailAdapter(details, db);
        rvDetails.setAdapter(adapter);

        double total = 0;
        for (OrderDetail d : details) {
            total += d.unitPrice * d.quantity;
        }
        tvTotal.setText(String.format("Total: $%.2f", total));
        
        Order order = db.orderDao().getOrderById(orderId);
        if (order != null) {
            order.totalAmount = total;
            db.orderDao().update(order);
        }
    }
}
