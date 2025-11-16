package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Database.MySQLite;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;

public class PhanQuyenActivity extends AppCompatActivity {
    ImageView imgQuayLai,imgAnhDaiDien;
    TextView tvTenNguoiDung,tvTenDangNhap,tvMatkhau,tvEmail,tvVaiTro;
    RadioButton rdQuanTriVien,rdNguoiDung;
    RadioGroup rgVaiTro;
    Button btnSave;
    // Vai trò đã chọn
    private int selectedVaiTro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phan_quyen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgQuayLai = findViewById(R.id.imgQuayLai);
        rdNguoiDung = findViewById(R.id.rdNguoiDung);
        rdQuanTriVien = findViewById(R.id.rdQuanTriVien);
        imgAnhDaiDien = findViewById(R.id.imgAnhDaiDien);
        tvTenNguoiDung = findViewById(R.id.tvTenNguoiDung);
        tvVaiTro = findViewById(R.id.tvVaiTro);
        tvTenDangNhap = findViewById(R.id.tvTenDangNhap);
        tvMatkhau = findViewById(R.id.tvMatkhau);
        tvEmail = findViewById(R.id.tvEmail);
        btnSave = findViewById(R.id.btnSave);
        rgVaiTro = findViewById(R.id.rgVaiTro);
        // Nhận Intent và lấy đối tượng NguoiDung
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        NguoiDung nguoiDung = (NguoiDung) data.get("nd_value");
        // xử lý sự kiện cho radioButton
        if (nguoiDung != null) {

            // Đặt trạng thái RadioButton theo vai trò hiện tại
            selectedVaiTro = nguoiDung.getVaiTro();
            if (selectedVaiTro == 1) {
                rdQuanTriVien.setChecked(true);
            } else {
                rdNguoiDung.setChecked(true);
            }

        }
        // Lắng nghe sự kiện thay đổi trên RadioGroup
        rgVaiTro.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdQuanTriVien) {
                    selectedVaiTro = 1; // Vai trò: Quản trị viên
                } else if (checkedId == R.id.rdNguoiDung) {
                    selectedVaiTro = 2; // Vai trò: Người dùng
                }
            }
        });
        // Xử lý nút Save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nguoiDung != null) {
                    // Lấy giá trị vai trò từ RadioButton (ví dụ: nếu rdQuanTriVien được chọn thì vai trò = 1)
                    int selectedVaiTro = rdQuanTriVien.isChecked() ? 1 : 2;

                    // Cập nhật vai trò trong đối tượng nguoiDung
                    nguoiDung.setVaiTro(selectedVaiTro);

                    // Cập nhật vào cơ sở dữ liệu
                    MySQLite db = new MySQLite(PhanQuyenActivity.this, MySQLite.DATABASE_NAME, null, 1);
                    db.capNhatVaiTroNguoiDung(nguoiDung.getId(), selectedVaiTro);

                    // Trả kết quả về cho Activity trước
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updated_user", nguoiDung);
                    setResult(123, resultIntent);
                    finish();
                }
            }
        });

        // hiển thị thông tin người dùng
        if (nguoiDung != null) {
            tvTenNguoiDung.setText(nguoiDung.getHoTen());
            tvTenDangNhap.setText(nguoiDung.getTenDN());
            tvMatkhau.setText(nguoiDung.getMatKhau());
            tvEmail.setText(nguoiDung.getEmail());
        }
        imgQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}