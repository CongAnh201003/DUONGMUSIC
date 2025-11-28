package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileUserActivity extends AppCompatActivity {

    // Khai báo View
    private ImageView imgQuayLai, imgAnhDaiDien;
    private TextView tvTenNguoiDung, tvTenDangNhap, tvMatkhau, tvEmail;
    private TextView tvCanhBaoXoa; // Thêm TextView cảnh báo xóa
    private Button btnChinhSua, btnDangXuat, btnXoaTaiKhoan;

    // Khai báo Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 2. Ánh xạ View
        initViews();

        // 3. Xử lý sự kiện
        handleEvents();

        // LƯU Ý: Không gọi loadUserData() ở đây nữa
        // Nó sẽ được gọi trong onResume()
    }

    // --- PHẦN QUAN TRỌNG: CẬP NHẬT DỮ LIỆU KHI QUAY LẠI TỪ MÀN HÌNH EDIT ---
    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Nếu chưa đăng nhập -> Đăng xuất ra Login
            finish();
            return;
        }

        currentEmail = currentUser.getEmail();

        // Gọi hàm tải dữ liệu mỗi khi màn hình hiện lên
        loadUserData();
    }
    // -----------------------------------------------------------------------

    private void initViews() {
        imgQuayLai = findViewById(R.id.imgQuayLai);
        imgAnhDaiDien = findViewById(R.id.imgAnhDaiDien);
        tvTenNguoiDung = findViewById(R.id.tvTenNguoiDung);
        tvTenDangNhap = findViewById(R.id.tvTenDangNhap);
        tvMatkhau = findViewById(R.id.tvMatkhau);
        tvEmail = findViewById(R.id.tvEmail);
        tvCanhBaoXoa = findViewById(R.id.tvCanhBaoXoa);

        btnChinhSua = findViewById(R.id.btnChinhSua);
        btnDangXuat = findViewById(R.id.btnDangXuat);
        btnXoaTaiKhoan = findViewById(R.id.btnXoaTaiKhoan);
    }

    private void loadUserData() {
        tvEmail.setText(currentEmail);
        // Luôn hiển thị ẩn mật khẩu vì lý do bảo mật
        tvMatkhau.setText("******");

        if (currentEmail != null) {
            db.collection("NGUOI_DUNG").document(currentEmail)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // Kiểm tra Activity còn sống không
                        if (isFinishing() || isDestroyed()) return;

                        if (documentSnapshot.exists()) {
                            String hoTen = documentSnapshot.getString("hoTen");
                            String avatarUrl = documentSnapshot.getString("avatar");
                            String vaiTro = documentSnapshot.getString("vaiTro");

                            // --- Xử lý ẩn hiện nút xóa theo vai trò ---
                            if ("admin".equals(vaiTro)) {
                                tvCanhBaoXoa.setVisibility(View.GONE);
                                btnXoaTaiKhoan.setVisibility(View.GONE);
                            } else {
                                tvCanhBaoXoa.setVisibility(View.VISIBLE);
                                btnXoaTaiKhoan.setVisibility(View.VISIBLE);
                            }

                            // --- Cập nhật tên ---
                            if (hoTen != null && !hoTen.isEmpty()) {
                                tvTenNguoiDung.setText(hoTen);
                                tvTenDangNhap.setText(hoTen);
                            } else {
                                tvTenNguoiDung.setText("Chưa cập nhật");
                                tvTenDangNhap.setText("Chưa cập nhật");
                            }

                            // --- Cập nhật ảnh ---
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(ProfileUserActivity.this)
                                        .load(avatarUrl)
                                        .placeholder(R.drawable.user)
                                        .error(R.drawable.user)
                                        .circleCrop() // Cắt tròn ảnh
                                        .into(imgAnhDaiDien);
                            } else {
                                imgAnhDaiDien.setImageResource(R.drawable.user);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (!isFinishing()) {
                            Toast.makeText(ProfileUserActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void handleEvents() {
        imgQuayLai.setOnClickListener(v -> finish());

        // Nút Đăng xuất
        btnDangXuat.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(ProfileUserActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        });

        // Nút Xóa tài khoản
        btnXoaTaiKhoan.setOnClickListener(v -> confirmDeleteAccount());

        // Nút Chỉnh sửa -> Chuyển sang màn hình Edit
        btnChinhSua.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileUserActivity.this, com.example.baicuoiky_nhom13.Activity.EditProfileUserActivity.class);
            startActivity(intent);
        });
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản? Dữ liệu sẽ mất vĩnh viễn.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUser())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteUser() {
        // Khóa nút để tránh bấm nhiều lần
        btnXoaTaiKhoan.setEnabled(false);

        if (currentEmail != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                // 1. Xóa Auth User trước (Quan trọng hơn về bảo mật)
                user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // 2. Nếu xóa Auth thành công -> Xóa dữ liệu Firestore
                                db.collection("NGUOI_DUNG").document(currentEmail)
                                        .delete()
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(ProfileUserActivity.this, "Đã xóa tài khoản vĩnh viễn!", Toast.LENGTH_SHORT).show();
                                            navigateToLogin();
                                        });
                            } else {
                                // Lỗi xóa Auth (thường do cần đăng nhập lại)
                                btnXoaTaiKhoan.setEnabled(true);
                                Toast.makeText(ProfileUserActivity.this, "Bảo mật: Vui lòng đăng xuất và đăng nhập lại để thực hiện xóa tài khoản.", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    }

    private void navigateToLogin() {
        // Chuyển về LoginActivity và xóa toàn bộ stack Activity cũ
        Intent intent = new Intent(ProfileUserActivity.this, com.example.baicuoiky_nhom13.Activity.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
