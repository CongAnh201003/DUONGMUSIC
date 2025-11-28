package com.example.baicuoiky_nhom13.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.example.baicuoiky_nhom13.Activity.TrangQuanLyOfAdminActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initFirebase();
        setupListeners();
    }

    private void initViews() {
        // Kiểm tra lại ID XML của bạn, nếu XML là username_input thì để nguyên,
        // còn nếu là edt_email thì sửa lại cho khớp
        edtEmail = findViewById(R.id.username_input);
        edtPassword = findViewById(R.id.password_input);
        btnLogin = findViewById(R.id.btnSignup);
        tvRegister = findViewById(R.id.tvDangKy);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.setCancelable(false);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(view -> handleLogin());
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        }
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập Email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập Mật khẩu");
            return;
        }

        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            fetchUserDataAndNavigate(firebaseUser.getEmail());
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserDataAndNavigate(String email) {
        // === SỬA ĐỔI QUAN TRỌNG TẠI ĐÂY ===
        // Database của bạn đang dùng ID là "admin@gmail.com" (giữ nguyên dấu chấm)
        // Nên ta KHÔNG thay thế dấu chấm bằng dấu phẩy nữa.
        String documentId = email;

        Log.d("LoginCheck", "Đang tìm Document ID: " + documentId);

        db.collection("NGUOI_DUNG").document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            NguoiDung user = document.toObject(NguoiDung.class);
                            if (user != null) {
                                user.setId(documentId);
                                user.setEmail(email);

                                // GỌI HÀM ĐIỀU HƯỚNG PHÂN QUYỀN
                                navigateUser(user);
                            }
                        } else {
                            // Nếu vẫn không tìm thấy, thử tìm bằng dấu phẩy (phòng hờ user khác)
                            fetchUserDataFallback(email);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi kết nối Database", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Hàm phụ: Nếu tìm bằng dấu chấm thất bại, thử tìm bằng dấu phẩy
    private void fetchUserDataFallback(String email) {
        String documentId = email.replace(".", ",");
        db.collection("NGUOI_DUNG").document(documentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                NguoiDung user = task.getResult().toObject(NguoiDung.class);
                if (user != null) {
                    user.setId(documentId);
                    user.setEmail(email);
                    navigateUser(user);
                }
            } else {
                Toast.makeText(LoginActivity.this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateUser(NguoiDung user) {
        Intent intent;
        // Trim() để xóa khoảng trắng thừa nếu có
        String role = user.getVaiTro() != null ? user.getVaiTro().trim() : "";

        if ("admin".equalsIgnoreCase(role)) {
            Log.d("Login", "User is Admin -> Go to Admin Dashboard");
            intent = new Intent(LoginActivity.this, TrangQuanLyOfAdminActivity.class);
        } else {
            Log.d("Login", "User is Normal -> Go to Home");
            intent = new Intent(LoginActivity.this, com.example.baicuoiky_nhom13.TrangChuActivity.class);
        }

        intent.putExtra("nguoi_dung", user);
        startActivity(intent);
        finish();
    }
}
