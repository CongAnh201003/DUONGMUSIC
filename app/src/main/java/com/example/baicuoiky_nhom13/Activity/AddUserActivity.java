package com.example.baicuoiky_nhom13.Activity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.R;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddUserActivity extends AppCompatActivity {
    private static final String TAG = "AddUserActivity";

    private EditText edtMatKhau, edtHoTen, edtEmail;
    private RadioGroup rgVaiTro;
    private Button btnThemNguoiDung;
    private ImageView imgBack;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bỏ EdgeToEdge để nhất quán
        setContentView(R.layout.activity_add_user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initFirebase();
        setupClickListeners();
    }

    private void initViews() {
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtHoTen = findViewById(R.id.edtHoTen);
        edtEmail = findViewById(R.id.edtEmail);
        rgVaiTro = findViewById(R.id.rgVaiTro);
        btnThemNguoiDung = findViewById(R.id.btnThemNguoiDung);
        imgBack = findViewById(R.id.imgBack);
        rgVaiTro.check(R.id.rbUser);
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void setupClickListeners() {
        imgBack.setOnClickListener(view -> finish());
        btnThemNguoiDung.setOnClickListener(view -> themNguoiDung());
    }

    private void themNguoiDung() {
        String matKhau = edtMatKhau.getText().toString().trim();
        String hoTen = edtHoTen.getText().toString().trim();
        String emailMoi = edtEmail.getText().toString().trim();
        int selectedRoleId = rgVaiTro.getCheckedRadioButtonId();

        if (hoTen.isEmpty() || emailMoi.isEmpty() || matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailMoi).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }
        if (matKhau.length() < 6) {
            edtMatKhau.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtMatKhau.requestFocus();
            return;
        }

        // === BƯỚC 1: LƯU THÔNG TIN ADMIN HIỆN TẠI ===
        FirebaseUser currentAdmin = firebaseAuth.getCurrentUser();
        if (currentAdmin == null) {
            Toast.makeText(this, "Lỗi: Admin đã bị đăng xuất. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            // Có thể điều hướng về Login ở đây
            return;
        }
        String adminEmail = currentAdmin.getEmail();
        // Bạn phải có mật khẩu của admin để đăng nhập lại.
        // Đây là một hạn chế. Tạm thời hardcode mật khẩu admin.
        String adminPassword = "111111"; // <<== MẬT KHẨU ADMIN CỨNG

        // === BƯỚC 2: TẠO USER MỚI ===
        firebaseAuth.createUserWithEmailAndPassword(emailMoi, matKhau)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Tạo tài khoản Auth cho user mới thành công.");
                        String emailId = emailMoi.replace(".", ",");
                        String vaiTro = (selectedRoleId == R.id.rbAdmin) ? "admin" : "user";
                        saveUserToFirestore(emailId, hoTen, emailMoi, matKhau, vaiTro, adminEmail, adminPassword);
                    } else {
                        Log.w(TAG, "Tạo tài khoản thất bại", task.getException());
                        Toast.makeText(AddUserActivity.this, "Thêm người dùng thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String emailId, String hoTen, String email, String matKhau, String vaiTro, String adminEmail, String adminPassword) {
        Map<String, Object> user = new HashMap<>();
        user.put("hoTen", hoTen);
        user.put("email", email);
        user.put("matKhau", matKhau);
        user.put("vaiTro", vaiTro);

        firestore.collection("NGUOI_DUNG").document(emailId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Lưu thông tin vào Firestore thành công.");
                    // === BƯỚC 3: ĐĂNG XUẤT USER MỚI RA ===
                    firebaseAuth.signOut();
                    // === BƯỚC 4: ĐĂNG NHẬP LẠI ADMIN ===
                    firebaseAuth.signInWithEmailAndPassword(adminEmail, adminPassword)
                            .addOnCompleteListener(reloginTask -> {
                                if (reloginTask.isSuccessful()) {
                                    Log.d(TAG, "Đăng nhập lại admin thành công.");
                                    // === BƯỚC 5: KẾT THÚC ACTIVITY ===
                                    Toast.makeText(AddUserActivity.this, "Thêm người dùng thành công!", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Log.e(TAG, "Lỗi khi đăng nhập lại admin.", reloginTask.getException());
                                    Toast.makeText(AddUserActivity.this, "Lỗi nghiêm trọng: Mất phiên đăng nhập admin.", Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lưu vào Firestore: ", e);
                    Toast.makeText(AddUserActivity.this, "Lỗi khi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
