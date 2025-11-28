package com.example.baicuoiky_nhom13.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;import android.util.Log;
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
import com.google.firebase.firestore.SetOptions;

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
        edtEmail.setText(currentEmail); // Set email ngay lập tức

        // 4. Load dữ liệu cũ (QUAN TRỌNG)
        loadCurrentData();

        // 5. Xử lý sự kiện
        handleEvents();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        // Đảm bảo ID trong XML khớp (xem lại file XML của bạn nếu ID khác)
        imgAnhDaiDien = findViewById(R.id.imgAnhDaiDien);

        // Lưu ý: ID của bạn trong ảnh có thể là edtHoTen, edtPass... hãy kiểm tra kỹ
        // Ở đây tôi dùng ID tiêu chuẩn, bạn hãy sửa lại trong XML hoặc sửa ở đây cho khớp
        edtTenNguoiDung = findViewById(R.id.edtTenNguoiDung);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtEmail = findViewById(R.id.edtEmail);
        btnLuu = findViewById(R.id.btnLuu);

        // Khóa không cho sửa Email
        edtEmail.setEnabled(false);
        edtEmail.setAlpha(0.5f);
    }

    private void loadCurrentData() {
        if (currentEmail != null) {
            db.collection("NGUOI_DUNG").document(currentEmail)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String hoTen = documentSnapshot.getString("hoTen");
                            // Không load mật khẩu lên EditText để bảo mật, hoặc để trống
                            String avatarUrl = documentSnapshot.getString("avatar");

                            if (hoTen != null) edtTenNguoiDung.setText(hoTen);

                            // Load ảnh
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                Glide.with(this).load(avatarUrl).circleCrop().into(imgAnhDaiDien);
                            }
                        } else {
                            // Nếu Document chưa tồn tại (Trường hợp của bạn)
                            // Tạo sẵn dữ liệu rỗng để tránh lỗi lần sau
                            Toast.makeText(this, "Hồ sơ chưa có dữ liệu, vui lòng cập nhật!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Log.e("EditProfile", "Lỗi load: " + e.getMessage()));
        }
    }

    private void handleEvents() {
        imgBack.setOnClickListener(v -> finish());

        btnLuu.setOnClickListener(v -> {
            String newHoTen = edtTenNguoiDung.getText().toString().trim();
            String newMatKhau = edtMatKhau.getText().toString().trim();

            if (newHoTen.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên hiển thị", Toast.LENGTH_SHORT).show();
                return;
            }

            updateProfile(newHoTen, newMatKhau);
        });
    }

    private void updateProfile(String hoTen, String matKhau) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Đang lưu...");
        pd.show();

        // Tạo Map dữ liệu
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("hoTen", hoTen);
        userUpdates.put("email", currentEmail); // Lưu luôn email vào DB cho chắc
        userUpdates.put("vaiTro", "user"); // Mặc định user thường

        // Lưu ý: Không nên lưu mật khẩu vào Firestore (kém bảo mật),
        // nhưng nếu bài tập yêu cầu hiển thị lại pass cũ thì lưu tạm.
        if (!matKhau.isEmpty()) {
            userUpdates.put("matKhau", matKhau);
        }

        // QUAN TRỌNG: Dùng set(..., SetOptions.merge()) để nếu chưa có doc thì nó tự tạo
        db.collection("NGUOI_DUNG").document(currentEmail)
                .set(userUpdates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {

                    // Nếu có đổi pass auth
                    if (!matKhau.isEmpty()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null) user.updatePassword(matKhau);
                    }

                    pd.dismiss();
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
