package com.example.btn_nhom2.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.btn_nhom2.dao.*;
import com.example.btn_nhom2.entity.*;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "shopping_v2.db")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        AppDatabase database = getInstance(context);
                                        // Thêm dữ liệu mẫu
                                        database.categoryDao().insert(new Category("Electronics", "Gadgets and devices"));
                                        database.categoryDao().insert(new Category("Fashion", "Clothing and accessories"));
                                        
                                        database.productDao().insert(new Product("Smartphone", "Latest model", 999.99, 1));
                                        database.productDao().insert(new Product("Laptop", "Powerful laptop", 1499.00, 1));
                                        database.productDao().insert(new Product("T-Shirt", "Cotton t-shirt", 19.99, 2));
                                        
                                        database.userDao().insert(new User("admin", "123", "Default Admin"));
                                    });
                                }
                            })
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
