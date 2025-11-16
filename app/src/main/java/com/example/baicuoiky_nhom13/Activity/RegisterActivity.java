package com.example.baicuoiky_nhom13.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtTenDangNhap, edtMatKhau, edtMatKhauhai, edtHoten, edtEmail;
    private Button btnDangKy;
    private ImageView imgBack;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        edtTenDangNhap = findViewById(R.id.edtTenDangNhap);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtMatKhauhai = findViewById(R.id.edtMatKhauhai);
        edtHoten = findViewById(R.id.edtHoten);
        edtEmail = findViewById(R.id.edtEmail);
        btnDangKy = findViewById(R.id.btnDangKy);
        imgBack = findViewById(R.id.imgBack);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        imgBack.setOnClickListener(view -> finish());

        btnDangKy.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String tenDN = edtTenDangNhap.getText().toString().trim();
        String matKhau1 = edtMatKhau.getText().toString().trim();
        String matKhau2 = edtMatKhauhai.getText().toString().trim();
        String hoTen = edtHoten.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        // Kiểm tra dữ liệu
        if (tenDN.isEmpty() || matKhau1.isEmpty() || matKhau2.isEmpty() || hoTen.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!matKhau1.equals(matKhau2)) {
            Toast.makeText(this, "Mật khẩu không trùng nhau", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo tài khoản Firebase Auth
        firebaseAuth.createUserWithEmailAndPassword(email, matKhau1)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserToFirestore(tenDN, hoTen, email);
                    } else {
                        Toast.makeText(this, "Lỗi đăng ký Firebase: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String tenDN, String hoTen, String email) {
        // Tạo object NguoiDung (không lưu mật khẩu)
        NguoiDung user = new NguoiDung();
        user.setTenDN(tenDN);
        user.setHoTen(hoTen);
        user.setEmail(email);
        user.setVaiTro(2); // 2 = người dùng bình thường

        // Encode email làm document ID
        String emailId = email.replace(".", ",");

        firestore.collection("NGUOI_DUNG")
                .document(emailId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // quay về LoginActivity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lưu Firestore: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }
}
