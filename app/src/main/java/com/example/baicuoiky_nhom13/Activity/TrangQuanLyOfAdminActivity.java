package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.example.baicuoiky_nhom13.TrangChuActivity;
import com.google.firebase.auth.FirebaseAuth;

public class TrangQuanLyOfAdminActivity extends AppCompatActivity {

    private TextView tvTenAdmin;
    private ImageView imgQuanLyNhac, imgQuanLyUser;
    private Button btnTrangProfileAdmin;

    private NguoiDung currentAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_quan_ly_of_admin);

        initViews();
        loadAdminData();
        setupListeners();
    }

    private void initViews() {
        tvTenAdmin = findViewById(R.id.tvTenAdmin);
        imgQuanLyNhac = findViewById(R.id.imgQuanLyNhac);
        imgQuanLyUser = findViewById(R.id.imgQuanLyUser);
        btnTrangProfileAdmin = findViewById(R.id.btnTrangProfileAdmin);
    }

    private void loadAdminData() {
        // Nhận dữ liệu từ LoginActivity gửi sang
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("nguoi_dung")) {
            currentAdmin = (NguoiDung) intent.getSerializableExtra("nguoi_dung");
        }

        if (currentAdmin != null) {
            String name = currentAdmin.getHoTen();
            if (name == null || name.isEmpty()) name = "Admin";
            tvTenAdmin.setText("Xin chào, " + name);
        } else {
            // Nếu không có dữ liệu (ví dụ chạy trực tiếp), check firebase
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }
    }

    private void setupListeners() {
        // 1. Nút Quản lý nhạc (Ví dụ: chuyển sang activity quản lý bài hát)
        imgQuanLyNhac.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Quản lý bài hát", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, QuanLyBaiHatActivity.class);
            startActivity(i);
        });

        // 2. Nút Quản lý người dùng (Ví dụ: chuyển sang activity quản lý user)
        imgQuanLyUser.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Quản lý người dùng", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, QL_NguoiDungActivity.class);
            startActivity(i);
        });

        // 3. Nút đến trang cá nhân (Ở đây tôi đang để nó chuyển về TrangChuActivity nhưng với giao diện Admin,
        // hoặc bạn có thể tạo một Activity Profile riêng)
        btnTrangProfileAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(TrangQuanLyOfAdminActivity.this, TrangChuActivity.class);
            // Truyền tiếp user admin sang trang chủ để hiển thị đúng avatar/tên
            intent.putExtra("nguoi_dung", currentAdmin);
            startActivity(intent);
        });
    }
}
