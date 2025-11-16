package com.example.baicuoiky_nhom13.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Database.MySQLite;
import com.example.baicuoiky_nhom13.R;

public class AddUserActivity extends AppCompatActivity {
    private EditText edtTenDangNhap, edtMatKhau, edtHoTen, edtEmail;
    private RadioGroup rgVaiTro;
    private Button btnThemNguoiDung;
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Ánh xạ các view
        edtTenDangNhap = findViewById(R.id.edtTenDangNhap);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtHoTen = findViewById(R.id.edtHoTen);
        edtEmail = findViewById(R.id.edtEmail);
        rgVaiTro = findViewById(R.id.rgVaiTro);
        btnThemNguoiDung = findViewById(R.id.btnThemNguoiDung);
        imgBack=findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Xử lý sự kiện khi bấm nút "Thêm Người Dùng"
        btnThemNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy dữ liệu từ các trường nhập liệu
                String tenDangNhap = edtTenDangNhap.getText().toString().trim();
                String matKhau = edtMatKhau.getText().toString().trim();
                String hoTen = edtHoTen.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                int vaiTro = (rgVaiTro.getCheckedRadioButtonId() == R.id.rbAdmin) ? 1 : 2;

                // Kiểm tra dữ liệu hợp lệ
                if (tenDangNhap.isEmpty() || matKhau.isEmpty() || hoTen.isEmpty() || email.isEmpty()) {
                    Toast.makeText(AddUserActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra tên đăng nhập trùng lặp trong SQLite
                MySQLite db = new MySQLite(AddUserActivity.this, MySQLite.DATABASE_NAME, null, 1);
                String checkSql = "SELECT COUNT(*) FROM NGUOI_DUNG WHERE ten_dang_nhap = '" + tenDangNhap + "'";
                Cursor cursor = db.getDataFromSQL(checkSql);

                if (cursor.moveToNext() && cursor.getInt(0) > 0) {
                    // Nếu tên đăng nhập đã tồn tại
                    Toast.makeText(AddUserActivity.this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                    cursor.close();
                    return;
                }
                cursor.close();

                // Thêm người dùng vào cơ sở dữ liệu
                String sql = "INSERT INTO NGUOI_DUNG (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro) " +
                        "VALUES ('" + tenDangNhap + "', '" + matKhau + "', '" + hoTen + "', '" + email + "', " + vaiTro + ");";

                try {
                    db.querySQL(sql);
                    Toast.makeText(AddUserActivity.this, "Thêm người dùng thành công!", Toast.LENGTH_SHORT).show();

                    // Quay lại Activity quản lý người dùng sau khi thêm
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(AddUserActivity.this, "Thêm người dùng thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}