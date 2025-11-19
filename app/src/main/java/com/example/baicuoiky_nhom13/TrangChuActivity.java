package com.example.baicuoiky_nhom13;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.baicuoiky_nhom13.Activity.LoginActivity;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.google.firebase.auth.FirebaseAuth;

public class TrangChuActivity extends AppCompatActivity {

    // Khai báo các biến view khớp với XML
    private ImageView imgProfile, imgTrangChu, imgSearch, imgThuVien, imgPhanHoi;
    private TextView tvTenNguoiDung;
    private CardView cvYeuThich;
    private ListView lvBaiHat;

    // Biến dữ liệu người dùng
    private NguoiDung currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu);

        initViews();
        loadUserDataFromIntent();
        setupListeners();
    }

    private void initViews() {
        // Ánh xạ ID từ file activity_trang_chu.xml
        imgProfile = findViewById(R.id.imgProfile);
        tvTenNguoiDung = findViewById(R.id.tvTenNguoiDung);
        cvYeuThich = findViewById(R.id.cvYeuThich);
        lvBaiHat = findViewById(R.id.lvBaiHat);

        // Bottom Navigation Icons
        imgTrangChu = findViewById(R.id.imgTrangChu);
        imgSearch = findViewById(R.id.imgSearch);
        imgThuVien = findViewById(R.id.ThuVien); // Chú ý: ID trong xml là ThuVien (viết hoa)
        imgPhanHoi = findViewById(R.id.imgPhanHoi);
    }

    private void loadUserDataFromIntent() {
        Intent intent = getIntent();

        // Kiểm tra xem có dữ liệu 'nguoi_dung' gửi từ LoginActivity qua không
        if (intent != null && intent.hasExtra("nguoi_dung")) {
            // Ép kiểu về object NguoiDung (Yêu cầu class NguoiDung phải implements Serializable)
            currentUser = (NguoiDung) intent.getSerializableExtra("nguoi_dung");
        }

        if (currentUser != null) {
            // 1. Hiển thị tên người dùng
            String name = currentUser.getHoTen();
            if (name == null || name.isEmpty()) {
                name = currentUser.getEmail(); // Nếu không có tên thì hiện email
            }
            tvTenNguoiDung.setText(name);

            // 2. Kiểm tra quyền Admin
            if ("admin".equalsIgnoreCase(currentUser.getVaiTro())) {
                Toast.makeText(this, "Xin chào Quản trị viên: " + name, Toast.LENGTH_SHORT).show();
                // Tại đây bạn có thể hiển thị thêm nút quản lý nếu muốn
            }
        } else {
            // Nếu vào trang chủ mà không có dữ liệu user (lỗi), đẩy về trang login
            // Kiểm tra thêm Firebase Auth để chắc chắn
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                dangXuat();
            }
        }
    }

    private void setupListeners() {
        // 1. Sự kiện click vào Avatar (imgProfile) để Đăng xuất
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        // 2. Sự kiện click vào Bài hát yêu thích
        cvYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TrangChuActivity.this, "Mở danh sách yêu thích", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(TrangChuActivity.this, YeuThichActivity.class);
                // startActivity(intent);
            }
        });

        // 3. Sự kiện Bottom Navigation
        imgSearch.setOnClickListener(v -> Toast.makeText(this, "Chức năng Tìm kiếm", Toast.LENGTH_SHORT).show());
        imgThuVien.setOnClickListener(v -> Toast.makeText(this, "Chức năng Thư viện", Toast.LENGTH_SHORT).show());
        imgPhanHoi.setOnClickListener(v -> Toast.makeText(this, "Chức năng Phản hồi", Toast.LENGTH_SHORT).show());

        // List View Bài hát
        // Bạn cần tạo Adapter để hiển thị dữ liệu lên lvBaiHat
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản " +
                (currentUser != null ? currentUser.getEmail() : "") + "?");

        builder.setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dangXuat();
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void dangXuat() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(TrangChuActivity.this, LoginActivity.class);
        // Xóa stack activity để không back lại được
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
