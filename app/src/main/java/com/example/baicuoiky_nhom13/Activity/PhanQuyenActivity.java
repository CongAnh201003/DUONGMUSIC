package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PhanQuyenActivity extends AppCompatActivity {
    private static final String TAG = "PhanQuyenActivity";

    // --- UI Components ---
    private ImageView imgQuayLai;
    private TextView tvTenNguoiDung, tvVaiTroHienTai;
    private EditText edtHoTen, edtEmail, edtMatKhau;
    private RadioButton rdQuanTriVien, rdNguoiDung;
    private RadioGroup rgVaiTro;
    private Button btnSave;

    // --- Dữ liệu ---
    private NguoiDung nguoiDung;

    // --- Firebase ---
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phan_quyen);
        applyWindowInsets();

        // Khởi tạo
        initViews();
        firestore = FirebaseFirestore.getInstance();

        // Lấy dữ liệu người dùng từ Intent và hiển thị
        if (!loadUserDataFromIntent()) {
            finish(); // Dừng lại nếu không có dữ liệu
            return;
        }

        // Thiết lập sự kiện
        setupClickListeners();
    }

    /**
     * Lấy dữ liệu người dùng từ Intent và hiển thị lên giao diện.
     * @return true nếu thành công, false nếu thất bại.
     */
    private boolean loadUserDataFromIntent() {
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        if (data == null) {
            Toast.makeText(this, "Lỗi: Không nhận được dữ liệu người dùng.", Toast.LENGTH_LONG).show();
            return false;
        }

        nguoiDung = (NguoiDung) data.get("nd_value");

        if (nguoiDung != null) {
            // Hiển thị thông tin lên các trường
            tvTenNguoiDung.setText(nguoiDung.getHoTen());
            edtHoTen.setText(nguoiDung.getHoTen());
            edtEmail.setText(nguoiDung.getEmail());
            edtMatKhau.setText(nguoiDung.getMatKhau());

            // Hiển thị và chọn đúng vai trò hiện tại
            String vaiTro = nguoiDung.getVaiTro();
            if ("admin".equalsIgnoreCase(vaiTro)) {
                tvVaiTroHienTai.setText("Quản trị viên");
                rdQuanTriVien.setChecked(true);
            } else {
                tvVaiTroHienTai.setText("Người dùng");
                rdNguoiDung.setChecked(true);
            }
            return true;
        } else {
            Toast.makeText(this, "Lỗi: Dữ liệu người dùng không hợp lệ.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Thiết lập sự kiện click cho các nút.
     */
    private void setupClickListeners() {
        imgQuayLai.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> updateAllChangesToFirestore());
    }

    /**
     * Cập nhật tất cả các thay đổi (họ tên, mật khẩu, vai trò) lên Firestore.
     */
    private void updateAllChangesToFirestore() {
        if (nguoiDung == null || nguoiDung.getId() == null || nguoiDung.getId().isEmpty()) {
            Toast.makeText(this, "Lỗi: Không xác định được ID người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu mới từ các trường EditText và RadioGroup
        String hoTenMoi = edtHoTen.getText().toString().trim();
        String matKhauMoi = edtMatKhau.getText().toString().trim();
        String vaiTroMoi = rdQuanTriVien.isChecked() ? "admin" : "user";

        if (hoTenMoi.isEmpty() || matKhauMoi.isEmpty()) {
            Toast.makeText(this, "Họ tên và mật khẩu không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo một Map để chứa tất cả các trường cần cập nhật
        Map<String, Object> updates = new HashMap<>();
        updates.put("hoTen", hoTenMoi);
        updates.put("matKhau", matKhauMoi);
        updates.put("vaiTro", vaiTroMoi);

        String documentId = nguoiDung.getId();

        firestore.collection("NGUOI_DUNG").document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    // Báo hiệu cho Activity trước (QL_NguoiDungActivity) để nó tải lại danh sách
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi cập nhật: ", e);
                    Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // --- Các hàm khởi tạo và hệ thống ---

    private void initViews() {
        imgQuayLai = findViewById(R.id.imgQuayLai);
        tvTenNguoiDung = findViewById(R.id.tvTenNguoiDung);
        tvVaiTroHienTai = findViewById(R.id.tvVaiTroHienTai);
        edtHoTen = findViewById(R.id.edtHoTen);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        rgVaiTro = findViewById(R.id.rgVaiTro);
        rdQuanTriVien = findViewById(R.id.rdQuanTriVien);
        rdNguoiDung = findViewById(R.id.rdNguoiDung);
        btnSave = findViewById(R.id.btnSave);

        // Không cho phép sửa Email vì nó là định danh chính
        edtEmail.setEnabled(false);
        edtEmail.setFocusable(false);
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
