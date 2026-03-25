package com.example.btn_nhom2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btn_nhom2.R;
import com.example.btn_nhom2.database.AppDatabase;
import com.example.btn_nhom2.entity.OrderDetail;
import com.example.btn_nhom2.entity.Product;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<OrderDetail> details;
    private AppDatabase db;

    public OrderDetailAdapter(List<OrderDetail> details, AppDatabase db) {
        this.details = details;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail detail = details.get(position);
        Product product = db.productDao().getProductById(detail.productId);
        if (product != null) {
            holder.tvName.setText(product.name);
        }
        holder.tvQty.setText("x" + detail.quantity);
        holder.tvPrice.setText(String.format("$%.2f", detail.unitPrice * detail.quantity));
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvOrderDetailName);
            tvQty = itemView.findViewById(R.id.tvOrderDetailQty);
            tvPrice = itemView.findViewById(R.id.tvOrderDetailPrice);
        }
    }
}
