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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileUserActivity extends AppCompatActivity {
    private static final String TAG = "ProfileUserActivity";

    // Khai báo UI
    private ImageView imgQuayLai, imgAnhDaiDien;
    // Bỏ tvTenDangNhap vì không còn dùng
    private TextView tvTenNguoiDung, tvMatkhau, tvEmail;
    private Button btnChinhSua, btnDangXuat, btnXoaTaiKhoan;

    // Khai báo Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    // Đối tượng người dùng hiện tại
    private NguoiDung nguoiDungHienTai;

    // Launcher để nhận kết quả từ màn hình EditProfile
    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Cập nhật lại thông tin người dùng sau khi sửa thành công
                    NguoiDung updatedUser = (NguoiDung) result.getData().getSerializableExtra("kq");
                    if (updatedUser != null) {
                        nguoiDungHienTai = updatedUser; // Cập nhật đối tượng hiện tại
                        displayUserData(); // Hiển thị lại thông tin mới
                        Toast.makeText(this, "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bỏ EdgeToEdge vì nó gây lỗi ở một số file khác của bạn
        setContentView(R.layout.activity_profile_user);
        applyWindowInsets();

        // Khởi tạo
        initViews();
        initFirebase();

        // Lấy dữ liệu người dùng được truyền từ Activity trước
        if (getIntent().getExtras() != null) {
            nguoiDungHienTai = (NguoiDung) getIntent().getExtras().get("nguoi_dung");
            if (nguoiDungHienTai != null) {
                displayUserData();
            } else {
                Toast.makeText(this, "Lỗi: Không nhận được dữ liệu người dùng.", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        // Thiết lập sự kiện click
        setupClickListeners();
    }

    /**
     * Hiển thị thông tin của người dùng lên giao diện.
     */
    private void displayUserData() {
        if (nguoiDungHienTai == null) return;

        tvTenNguoiDung.setText(nguoiDungHienTai.getHoTen());
        tvEmail.setText(nguoiDungHienTai.getEmail());
        tvMatkhau.setText(nguoiDungHienTai.getMatKhau());

        // Không còn hiển thị tvTenDangNhap

        // Dùng Picasso hoặc Glide để tải ảnh đại diện
        String imageUrl = nguoiDungHienTai.getAnhDaiDien();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.user) // Ảnh tạm thời khi đang tải
                    .error(R.drawable.error_image) // Ảnh lỗi nếu không tải được
                    .into(imgAnhDaiDien);
        } else {
            imgAnhDaiDien.setImageResource(R.drawable.user); // Ảnh mặc định
        }
    }

    /**
     * Thiết lập các sự kiện click cho các nút.
     */
    private void setupClickListeners() {
        imgQuayLai.setOnClickListener(v -> finish());

        btnDangXuat.setOnClickListener(v -> {
            firebaseAuth.signOut(); // Đăng xuất khỏi Firebase
            Intent intent = new Intent(ProfileUserActivity.this, LoginActivity.class);
            // Xóa hết các Activity cũ và mở màn hình Login
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnXoaTaiKhoan.setOnClickListener(view -> showDeleteConfirmationDialog());

        btnChinhSua.setOnClickListener(view -> {
            Intent intentEdit = new Intent(ProfileUserActivity.this, EditProfileUserActivity.class);
            intentEdit.putExtra("nguoi_dung", nguoiDungHienTai);
            editProfileLauncher.launch(intentEdit);
        });
    }

    /**
     * Hiển thị hộp thoại xác nhận trước khi xóa tài khoản.
     */
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa tài khoản")
                .setMessage("Hành động này không thể hoàn tác. Bạn có chắc chắn muốn xóa tài khoản này vĩnh viễn không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteUserAccount())
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Thực hiện xóa tài khoản người dùng trên cả Firestore và Firebase Authentication.
     */
    private void deleteUserAccount() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String documentId = nguoiDungHienTai.getId();

        if (user == null || documentId == null || documentId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không thể xác thực người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bước 1: Xóa document trong Firestore
        firestore.collection("NGUOI_DUNG").document(documentId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Đã xóa dữ liệu người dùng trên Firestore.");
                    // Bước 2: Xóa tài khoản trên Firebase Authentication
                    user.delete()
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(ProfileUserActivity.this, "Đã xóa tài khoản thành công.", Toast.LENGTH_SHORT).show();
                                // Chuyển về màn hình đăng nhập
                                Intent intent = new Intent(ProfileUserActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Lỗi khi xóa tài khoản trên Authentication: ", e);
                                // Lỗi này thường xảy ra do người dùng cần phải đăng nhập lại gần đây
                                Toast.makeText(ProfileUserActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi xóa dữ liệu trên Firestore: ", e);
                    Toast.makeText(ProfileUserActivity.this, "Lỗi khi xóa dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // --- Các hàm khởi tạo và hệ thống ---
    private void initViews() {
        imgQuayLai = findViewById(R.id.imgQuayLai);
        imgAnhDaiDien = findViewById(R.id.imgAnhDaiDien);
        tvTenNguoiDung = findViewById(R.id.tvTenNguoiDung);
        tvEmail = findViewById(R.id.tvEmail);
        tvMatkhau = findViewById(R.id.tvMatkhau);
        btnChinhSua = findViewById(R.id.btnChinhSua);
        btnDangXuat = findViewById(R.id.btnDangXuat);
        btnXoaTaiKhoan = findViewById(R.id.btnXoaTaiKhoan);

        // Bỏ tvTenDangNhap
        // tvTenDangNhap=findViewById(R.id.tvTenDangNhap);
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
