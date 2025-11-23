package com.example.baicuoiky_nhom13.Activity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    // Bỏ edtTenDangNhap
    private EditText edtMatKhau, edtMatKhauhai, edtHoten, edtEmail;
    private Button btnDangKy;
    private ImageView imgBack;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        applyWindowInsets();
        initViews();
        initFirebase();
        setupClickListeners();
    }

    private void initViews() {
        // Bỏ ánh xạ edtTenDangNhap
        // edtTenDangNhap = findViewById(R.id.edtTenDangNhap);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtMatKhauhai = findViewById(R.id.edtMatKhauhai);
        edtHoten = findViewById(R.id.edtHoten);
        edtEmail = findViewById(R.id.edtEmail);
        btnDangKy = findViewById(R.id.btnDangKy);
        imgBack = findViewById(R.id.imgBack);
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void setupClickListeners() {
        imgBack.setOnClickListener(view -> finish());
        btnDangKy.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String matKhau1 = edtMatKhau.getText().toString().trim();
        String matKhau2 = edtMatKhauhai.getText().toString().trim();
        String hoTen = edtHoten.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (hoTen.isEmpty() || email.isEmpty() || matKhau1.isEmpty() || matKhau2.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }
        if (!matKhau1.equals(matKhau2)) {
            edtMatKhauhai.setError("Mật khẩu không trùng khớp");
            edtMatKhauhai.requestFocus();
            return;
        }
        if (matKhau1.length() < 6) {
            edtMatKhau.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtMatKhau.requestFocus();
            return;
        }

        // Bước 1: Tạo tài khoản trên Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, matKhau1)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Tạo tài khoản Auth thành công.");
                        // Bước 2: Lưu thông tin chi tiết vào Firestore
                        saveUserToFirestore(hoTen, email, matKhau1);
                    } else {
                        Log.w(TAG, "Tạo tài khoản Auth thất bại.", task.getException());
                        Toast.makeText(this, "Đăng ký thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String hoTen, String email, String matKhau) {
        // Tạo đối tượng người dùng mới
        NguoiDung user = new NguoiDung();
        user.setHoTen(hoTen);
        user.setEmail(email);
        user.setMatKhau(matKhau);
        user.setVaiTro("user"); // Mặc định tất cả đăng ký mới là "user"
        // Các trường khác như anhDaiDien có thể để trống và cập nhật sau

        // Mã hóa email để dùng làm ID cho document
        String emailId = email;
        user.setId(emailId); // Gán luôn ID cho đối tượng

        // Lưu đối tượng user vào Firestore
        firestore.collection("NGUOI_DUNG").document(emailId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Lưu thông tin vào Firestore thành công.");
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    // Đăng ký xong thì nên đăng xuất người dùng mới ra ngay
                    // để họ quay lại và tự đăng nhập
                    FirebaseAuth.getInstance().signOut();

                    finish(); // Quay về LoginActivity
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lưu Firestore.", e);
                    Toast.makeText(this, "Lỗi khi lưu dữ liệu: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
