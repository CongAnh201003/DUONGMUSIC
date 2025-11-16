package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.example.baicuoiky_nhom13.TrangChuActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText username_input, password_input;
    Button btnLogin;
    TextView tvDangKy;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username_input = findViewById(R.id.username_input);
        password_input = findViewById(R.id.password_input);
        btnLogin = findViewById(R.id.btnSignup);
        tvDangKy = findViewById(R.id.tvDangKy);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Chuyển sang trang đăng ký
        tvDangKy.setOnClickListener(view -> {
            Intent DangKyIntent = new Intent(getBaseContext(), RegisterActivity.class);
            startActivity(DangKyIntent);
        });

        btnLogin.setOnClickListener(view -> {
            String email = username_input.getText().toString().trim();
            String password = password_input.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Admin cứng
            if(email.equals("admin@gmail.com") && password.equals("123456")){
                NguoiDung admin = new NguoiDung();
                admin.setTenDN("admin");
                admin.setHoTen("Quản trị viên");
                admin.setEmail("admin@gmail.com");
                admin.setVaiTro(1);

                Intent intentAdmin = new Intent(this, TrangQuanLyOfAdminActivity.class);
                intentAdmin.putExtra("admin", admin);
                startActivity(intentAdmin);
                return;
            }

            // Tài khoản Firebase Auth
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Encode email để lấy document Firestore
                            String emailId = email.replace(".", ",");

                            firestore.collection("NGUOI_DUNG")
                                    .document(emailId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            NguoiDung user = documentSnapshot.toObject(NguoiDung.class);
                                            if (user != null) {
                                                if (user.getVaiTro() == 1) {
                                                    Intent intentAdmin = new Intent(this, TrangQuanLyOfAdminActivity.class);
                                                    intentAdmin.putExtra("admin", user);
                                                    startActivity(intentAdmin);
                                                } else if (user.getVaiTro() == 2) {
                                                    Intent intentUser = new Intent(this, TrangChuActivity.class);
                                                    intentUser.putExtra("nguoi_dung", user);
                                                    startActivity(intentUser);
                                                }
                                            } else {
                                                Toast.makeText(this, "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(this, "Người dùng không tồn tại!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Lỗi đọc Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });


    }
}
