package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Database.MySQLite;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;

public class EditProfileAdminActivity extends AppCompatActivity {
    EditText edtTenDangNhap, edtTenNguoiDung, edtEmail, edtMatKhau;
    Button btnLuu;
    ImageView imgBack,imgAnhDaiDien;
    MySQLite mySQLite;
    TextView tvVaiTro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgBack = findViewById(R.id.imgBack);
        edtTenDangNhap = findViewById(R.id.edtTenDangNhap);
        edtTenNguoiDung = findViewById(R.id.edtTenNguoiDung);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        btnLuu = findViewById(R.id.btnLuu);
        tvVaiTro=findViewById(R.id.tvVaiTro);

        Intent intent=getIntent();

        Bundle data=intent.getExtras();
        NguoiDung nguoiDung= (NguoiDung) data.get("nguoi_dung");
        edtTenNguoiDung.setText(nguoiDung.getHoTen());
        edtEmail.setText(nguoiDung.getEmail());
        edtMatKhau.setText(nguoiDung.getMatKhau());
        edtTenDangNhap.setText(nguoiDung.getTenDN());
        tvVaiTro.setText("Quản trị viên");

        mySQLite = new MySQLite(this, MySQLite.DATABASE_NAME, null, 1);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lay du lieu moi tu EditText
                String tenDN = edtTenDangNhap.getText().toString();
                String hoTen = edtTenNguoiDung.getText().toString();
                String email = edtEmail.getText().toString();
                String matKhau = edtMatKhau.getText().toString();

                // Cap nhat du lieu vao CSDL
                String updateSQL = "UPDATE NGUOI_DUNG SET " +
                        "ten_dang_nhap = '" + tenDN + "', " +
                        "ho_ten = '" + hoTen + "', " +
                        "email = '" + email + "', " +
                        "mat_khau = '" + matKhau + "' " +
                        "WHERE id = '" + nguoiDung.getId() + "'";
                NguoiDung nd=new NguoiDung(nguoiDung.getId(),tenDN,matKhau,hoTen,email,"",1);
                mySQLite.querySQL(updateSQL);
                Toast.makeText(getBaseContext(),"Cập nhật thành công",Toast.LENGTH_SHORT).show();
                Intent kq=new Intent();
                Bundle data=new Bundle();
                data.putSerializable("kq",nd);
                kq.putExtras(data);
                setResult(RESULT_OK,kq);
                finish();
            }
        });

    }
}