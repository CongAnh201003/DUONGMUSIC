package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class ProfileAdminActivity extends AppCompatActivity {

    // --- UI Components ---
    private ImageView imgQuayLai, imgAnhDaiDien;
    private TextView tvTenNguoiDung, tvMatkhau, tvEmail, tvVaiTro;
    private Button btnChinhSua, btnDangXuat;

    // --- Dữ liệu người dùng ---
    private NguoiDung adminUser;

    /**
     * Launcher để nhận kết quả trả về từ màn hình EditProfileAdminActivity.
     */
    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Nhận lại đối tượng NguoiDung đã được cập nhật
                    NguoiDung updatedAdmin = (NguoiDung) result.getData().getSerializableExtra("kq");
                    if (updatedAdmin != null) {
                        adminUser = updatedAdmin; // Cập nhật lại đối tượng hiện tại
                        displayUserData(); // Hiển thị lại thông tin mới lên UI
                        Toast.makeText(this, "Thông tin đã được cập nhật.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);
        applyWindowInsets();

        // Khởi tạo
        initViews();

        // Lấy dữ liệu admin từ Intent và hiển thị
        if (!loadAdminDataFromIntent()) {
            // Nếu không có dữ liệu, đóng Activity để tránh lỗi
            finish();
            return;
        }

        // Thiết lập sự kiện click
        setupClickListeners();
    }

    /**
     * Lấy dữ liệu admin từ Intent và hiển thị lên giao diện.
     * @return true nếu thành công, false nếu thất bại.
     */
    private boolean loadAdminDataFromIntent() {
        Intent intent = getIntent();
        adminUser = (NguoiDung) intent.getSerializableExtra("admin");

        if (adminUser != null) {
            displayUserData();
            return true;
        } else {
            Toast.makeText(this, "Lỗi: Không nhận được dữ liệu admin.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Hiển thị thông tin của admin lên các TextView và ImageView.
     */
    private void displayUserData() {
        if (adminUser == null) return;

        tvTenNguoiDung.setText(adminUser.getHoTen());
        tvEmail.setText(adminUser.getEmail());
        tvMatkhau.setText(adminUser.getMatKhau());
        tvVaiTro.setText("Quản trị viên");

        // === LỖI ĐÃ SỬA: Bỏ hoàn toàn logic liên quan đến tenDN ===

        // Dùng Picasso hoặc Glide để tải ảnh (nếu có)
        String imageUrl = adminUser.getAnhDaiDien();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).placeholder(R.drawable.user).into(imgAnhDaiDien);
        } else {
            imgAnhDaiDien.setImageResource(R.drawable.user); // Ảnh mặc định
        }
    }

    /**
     * Thiết lập các sự kiện click cho các nút.
     */
    private void setupClickListeners() {
        imgQuayLai.setOnClickListener(view -> {
            // Khi quay lại, gửi trả đối tượng admin đã được cập nhật (nếu có)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("kq", adminUser);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnDangXuat.setOnClickListener(view -> {
            // Đăng xuất khỏi Firebase Auth
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            // Xóa hết các Activity cũ và mở màn hình Login
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnChinhSua.setOnClickListener(view -> {
            Intent intentAdminEdit = new Intent(this, EditProfileAdminActivity.class);
            // Truyền đối tượng admin hiện tại sang màn hình sửa
            intentAdminEdit.putExtra("nguoi_dung", adminUser);
            editProfileLauncher.launch(intentAdminEdit);
        });
    }

    // --- Các hàm khởi tạo và hệ thống ---
    private void initViews() {
        imgQuayLai = findViewById(R.id.imgQuayLai);
        imgAnhDaiDien = findViewById(R.id.imgAnhDaiDien);
        tvTenNguoiDung = findViewById(R.id.tvTenNguoiDung);
        tvMatkhau = findViewById(R.id.tvMatkhau);
        tvEmail = findViewById(R.id.tvEmail);
        btnChinhSua = findViewById(R.id.btnChinhSua);
        tvVaiTro = findViewById(R.id.tvVaiTro);
        btnDangXuat = findViewById(R.id.btnDangXuat);
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Ghi đè nút Back để đảm bảo dữ liệu được trả về đúng cách.
     */
    @Override
    public void onBackPressed() {
        // Gửi trả đối tượng admin đã được cập nhật khi nhấn nút Back
        Intent resultIntent = new Intent();
        resultIntent.putExtra("kq", adminUser);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}
