package com.example.baicuoiky_nhom13.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileUserActivity extends AppCompatActivity {

    private ImageView imgBack, imgAnhDaiDien;
    private EditText edtTenNguoiDung, edtMatKhau, edtEmail;
    private Button btnLuu;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 1. Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 2. Ánh xạ View
        initViews();

        // 3. Lấy User hiện tại
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentEmail = currentUser.getEmail();

        // 4. Load dữ liệu cũ
        loadCurrentData();

        // 5. Xử lý sự kiện
        handleEvents();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        imgAnhDaiDien = findViewById(R.id.imgAnhDaiDien);
        edtTenNguoiDung = findViewById(R.id.edtTenNguoiDung);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtEmail = findViewById(R.id.edtEmail);
        btnLuu = findViewById(R.id.btnLuu);

        // --- QUAN TRỌNG: KHÓA KHÔNG CHO SỬA EMAIL ---
        edtEmail.setEnabled(false);
        edtEmail.setFocusable(false);
        edtEmail.setAlpha(0.5f); // Làm mờ để người dùng biết không sửa được
    }

    private void loadCurrentData() {
        // Hiển thị email ngay lập tức
        edtEmail.setText(currentEmail);

        if (currentEmail != null) {
            db.collection("NGUOI_DUNG").document(currentEmail)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String hoTen = documentSnapshot.getString("hoTen");
                            String matKhau = documentSnapshot.getString("matKhau");
                            String avatarUrl = documentSnapshot.getString("avatar");

                            if (hoTen != null) edtTenNguoiDung.setText(hoTen);
                            if (matKhau != null) edtMatKhau.setText(matKhau);

                            // Load ảnh nếu có
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                try {
                                    Glide.with(EditProfileUserActivity.this)
                                            .load(avatarUrl)
                                            .placeholder(R.drawable.user)
                                            .error(R.drawable.user)
                                            .into(imgAnhDaiDien);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }

    private void handleEvents() {
        imgBack.setOnClickListener(v -> finish());

        btnLuu.setOnClickListener(v -> {
            String newHoTen = edtTenNguoiDung.getText().toString().trim();
            String newMatKhau = edtMatKhau.getText().toString().trim();

            if (newHoTen.isEmpty() || newMatKhau.isEmpty()) {
                Toast.makeText(EditProfileUserActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            updateProfile(newHoTen, newMatKhau);
        });
    }

    private void updateProfile(String hoTen, String matKhau) {
        // Tạo Map dữ liệu để update
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("hoTen", hoTen);
        userUpdates.put("matKhau", matKhau);

        // Cập nhật Firestore
        db.collection("NGUOI_DUNG").document(currentEmail)
                .update(userUpdates)
                .addOnSuccessListener(aVoid -> {

                    // Cập nhật mật khẩu trên Authentication (Quan trọng)
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.updatePassword(matKhau)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(EditProfileUserActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                        finish(); // Quay về màn hình Profile
                                    } else {
                                        Toast.makeText(EditProfileUserActivity.this, "Đã lưu thông tin, nhưng lỗi đổi mật khẩu (cần đăng nhập lại).", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                    } else {
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileUserActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
