package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
// Bỏ MySQLite, thêm Firestore
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileAdminActivity extends AppCompatActivity {
    // Bỏ edtTenDangNhap vì không còn dùng
    private EditText edtTenNguoiDung, edtEmail, edtMatKhau;
    private Button btnLuu;
    private ImageView imgBack;
    private TextView tvVaiTro;

    // Khai báo Firestore
    private FirebaseFirestore firestore;
    // Đối tượng người dùng hiện tại đang được sửa
    private NguoiDung nguoiDungHienTai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_admin);
        applyWindowInsets();

        // Khởi tạo các thành phần
        initViews();
        firestore = FirebaseFirestore.getInstance();

        // Lấy dữ liệu người dùng được truyền từ Activity trước
        if (getIntent().getExtras() != null) {
            nguoiDungHienTai = (NguoiDung) getIntent().getExtras().get("nguoi_dung");
            if (nguoiDungHienTai != null) {
                // Hiển thị dữ liệu người dùng lên giao diện
                displayUserData();
            }
        }

        // Thiết lập các sự kiện click
        setupClickListeners();
    }

    /**
     * Hiển thị dữ liệu của người dùng lên các EditText.
     */
    private void displayUserData() {
        edtTenNguoiDung.setText(nguoiDungHienTai.getHoTen());
        edtEmail.setText(nguoiDungHienTai.getEmail());
        edtMatKhau.setText(nguoiDungHienTai.getMatKhau());
        tvVaiTro.setText("Quản trị viên");

        // Không cho phép sửa Email vì nó là định danh chính
        edtEmail.setEnabled(false);
        edtEmail.setFocusable(false);
    }

    /**
     * Thiết lập sự kiện click cho các nút.
     */
    private void setupClickListeners() {
        imgBack.setOnClickListener(view -> finish());
        btnLuu.setOnClickListener(view -> updateProfileInFirestore());
    }

    /**
     * Cập nhật thông tin người dùng lên Cloud Firestore.
     */
    private void updateProfileInFirestore() {
        String hoTenMoi = edtTenNguoiDung.getText().toString().trim();
        String matKhauMoi = edtMatKhau.getText().toString().trim();

        if (hoTenMoi.isEmpty() || matKhauMoi.isEmpty()) {
            Toast.makeText(this, "Họ tên và mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // ID của document trên Firestore chính là ID của người dùng (email đã mã hóa)
        String documentId = nguoiDungHienTai.getId();

        // Tạo một Map để chứa các trường cần cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("hoTen", hoTenMoi);
        updates.put("matKhau", matKhauMoi);
        // Không cập nhật vai trò hoặc email ở đây

        firestore.collection("NGUOI_DUNG").document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    // Cập nhật lại đối tượng người dùng hiện tại với thông tin mới
                    nguoiDungHienTai.setHoTen(hoTenMoi);
                    nguoiDungHienTai.setMatKhau(matKhauMoi);

                    // Trả kết quả về cho Activity trước để nó có thể cập nhật giao diện nếu cần
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("kq", nguoiDungHienTai);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("UpdateProfile", "Lỗi khi cập nhật document", e);
                });
    }

    /**
     * Ánh xạ các View từ file layout.
     */
    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        // Bỏ edtTenDangNhap vì không còn trong layout
        // edtTenDangNhap = findViewById(R.id.edtTenDangNhap);
        edtTenNguoiDung = findViewById(R.id.edtTenNguoiDung);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        btnLuu = findViewById(R.id.btnLuu);
        tvVaiTro = findViewById(R.id.tvVaiTro);
    }

    /**
     * Áp dụng padding cho màn hình để tránh bị che bởi các thanh hệ thống.
     */
    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
