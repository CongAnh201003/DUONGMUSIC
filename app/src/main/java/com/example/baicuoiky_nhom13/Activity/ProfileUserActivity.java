package com.example.baicuoiky_nhom13.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.baicuoiky_nhom13.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileUserActivity extends AppCompatActivity {

    // Khai báo View
    private ImageView imgQuayLai, imgAnhDaiDien;
    private TextView tvTenNguoiDung, tvTenDangNhap, tvMatkhau, tvEmail;
    // Thêm TextView cảnh báo xóa
    private TextView tvCanhBaoXoa;
    private Button btnChinhSua, btnDangXuat, btnXoaTaiKhoan;

    // Khai báo Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentEmail = currentUser.getEmail();

        loadUserData();
        handleEvents();
    }

    private void initViews() {
        imgQuayLai = findViewById(R.id.imgQuayLai);
        imgAnhDaiDien = findViewById(R.id.imgAnhDaiDien);
        tvTenNguoiDung = findViewById(R.id.tvTenNguoiDung);
        tvTenDangNhap = findViewById(R.id.tvTenDangNhap);
        tvMatkhau = findViewById(R.id.tvMatkhau);
        tvEmail = findViewById(R.id.tvEmail);

        // Ánh xạ View mới
        tvCanhBaoXoa = findViewById(R.id.tvCanhBaoXoa);

        btnChinhSua = findViewById(R.id.btnChinhSua);
        btnDangXuat = findViewById(R.id.btnDangXuat);
        btnXoaTaiKhoan = findViewById(R.id.btnXoaTaiKhoan);
    }

    private void loadUserData() {
        tvEmail.setText(currentEmail);

        if (currentEmail != null) {
            db.collection("NGUOI_DUNG").document(currentEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String hoTen = documentSnapshot.getString("hoTen");
                                String matKhau = documentSnapshot.getString("matKhau");
                                String avatarUrl = documentSnapshot.getString("avatar");

                                // Lấy vai trò
                                String vaiTro = documentSnapshot.getString("vaiTro");

                                // --- XỬ LÝ ẨN HIỆN NÚT XÓA ---
                                if ("admin".equals(vaiTro)) {
                                    // Nếu là admin thì ẩn đi
                                    tvCanhBaoXoa.setVisibility(View.GONE);
                                    btnXoaTaiKhoan.setVisibility(View.GONE);
                                } else {
                                    // Nếu không phải admin (là user) thì hiện lên
                                    tvCanhBaoXoa.setVisibility(View.VISIBLE);
                                    btnXoaTaiKhoan.setVisibility(View.VISIBLE);
                                }
                                // -------------------------------

                                if (hoTen != null && !hoTen.isEmpty()) {
                                    tvTenNguoiDung.setText(hoTen);
                                    tvTenDangNhap.setText(hoTen);
                                } else {
                                    tvTenNguoiDung.setText("Chưa cập nhật");
                                    tvTenDangNhap.setText("Chưa cập nhật");
                                }

                                if (matKhau != null) {
                                    tvMatkhau.setText(matKhau);
                                } else {
                                    tvMatkhau.setText("******");
                                }

                                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                    try {
                                        Glide.with(ProfileUserActivity.this)
                                                .load(avatarUrl)
                                                .placeholder(R.drawable.user)
                                                .error(R.drawable.user)
                                                .into(imgAnhDaiDien);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    imgAnhDaiDien.setImageResource(R.drawable.user);
                                }

                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(ProfileUserActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show());
        }
    }

    private void handleEvents() {
        imgQuayLai.setOnClickListener(v -> finish());

        // Code Đăng xuất đã sửa
        btnDangXuat.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(ProfileUserActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

            // Chuyển về LoginActivity và xóa stack
            Intent intent = new Intent(ProfileUserActivity.this, com.example.baicuoiky_nhom13.Activity.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnXoaTaiKhoan.setOnClickListener(v -> confirmDeleteAccount());

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
        if (currentEmail != null) {
            db.collection("NGUOI_DUNG").document(currentEmail)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileUserActivity.this, "Đã xóa tài khoản!", Toast.LENGTH_SHORT).show();
                                            // Chuyển về màn hình Login sau khi xóa
                                            Intent intent = new Intent(ProfileUserActivity.this, com.example.baicuoiky_nhom13.Activity.LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(ProfileUserActivity.this, "Cần đăng nhập lại để xóa tài khoản.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(ProfileUserActivity.this, "Lỗi xóa dữ liệu Firestore", Toast.LENGTH_SHORT).show());
        }
    }
}
