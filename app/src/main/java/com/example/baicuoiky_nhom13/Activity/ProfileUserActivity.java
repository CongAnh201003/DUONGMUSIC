package com.example.baicuoiky_nhom13.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Database.MySQLite;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.squareup.picasso.Picasso;

public class ProfileUserActivity extends AppCompatActivity {
    ImageView imgQuayLai,imgAnhDaiDien;
    TextView tvTenNguoiDung,tvTenDangNhap,tvMatkhau,tvEmail;
    Button btnChinhSua,btnDangXuat,btnXoaTaiKhoan;
    MySQLite mySQLite;
    // Khai bao
    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Cap nhat lai thong tin nguoi dung sau khi sua
                    Intent data = result.getData();
                    String tenDangNhapMoi = data.getStringExtra("tenDangNhap");
                    String hoTenMoi = data.getStringExtra("hoTen");
                    String emailMoi = data.getStringExtra("email");
                    String matKhauMoi = data.getStringExtra("matKhau");
                    String imageUrl = data.getStringExtra("anhDaiDien");

                    tvTenDangNhap.setText(tenDangNhapMoi);
                    tvTenNguoiDung.setText(hoTenMoi);
                    tvEmail.setText(emailMoi);
                    tvMatkhau.setText(matKhauMoi);


                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get()
                                .load(imageUrl)
                                .placeholder(R.drawable.user) // Ảnh tạm thời
                                .error(R.drawable.error_image) // Ảnh lỗi
                                .into(imgAnhDaiDien);
                    }

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgQuayLai=findViewById(R.id.imgQuayLai);
        imgAnhDaiDien=findViewById(R.id.imgAnhDaiDien);
        tvTenNguoiDung=findViewById(R.id.tvTenNguoiDung);
        tvTenDangNhap=findViewById(R.id.tvTenDangNhap);
        tvMatkhau=findViewById(R.id.tvMatkhau);
        tvEmail=findViewById(R.id.tvEmail);
        btnChinhSua=findViewById(R.id.btnChinhSua);
        btnDangXuat=findViewById(R.id.btnDangXuat);
        btnXoaTaiKhoan=findViewById(R.id.btnXoaTaiKhoan);
        mySQLite = new MySQLite(this, MySQLite.DATABASE_NAME, null, 1);


        Intent intent=getIntent();
        Bundle data=intent.getExtras();
        NguoiDung nguoiDung= (NguoiDung) data.get("nguoi_dung");
        tvTenNguoiDung.setText(nguoiDung.getHoTen());
        tvEmail.setText(nguoiDung.getEmail());
        tvTenDangNhap.setText(nguoiDung.getTenDN());
        tvMatkhau.setText(nguoiDung.getMatKhau());

        imgQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=getIntent();
                intent1.putExtra("hoten",tvTenNguoiDung.getText());
                setResult(RESULT_OK,intent1);
                finish();
            }
        });

        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent trangDNIntent=new Intent(getBaseContext(), LoginActivity.class);
                startActivity(trangDNIntent);
                finish();
            }
        });
        // xóa tài khoản
        btnXoaTaiKhoan.setOnClickListener(view -> {
            new AlertDialog.Builder(ProfileUserActivity.this)
                    .setTitle("Xác nhận xóa tài khoản")
                    .setMessage("Bạn chắc chắn muốn xóa tài khoản?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Xoa tai khoan
                            String tenDangNhap = tvTenDangNhap.getText().toString();
                            mySQLite.querySQL("DELETE FROM NGUOI_DUNG WHERE id = '" + nguoiDung.getId() + "'");

                            // Chuyen ve trang dang nhap
                            Intent trangDNIntent = new Intent(ProfileUserActivity.this, LoginActivity.class);
                            startActivity(trangDNIntent);
                            finish();
                        }
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });


        btnChinhSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentEdit=new Intent(getBaseContext(), EditProfileUserActivity.class);
                Bundle data=new Bundle();
                data.putSerializable("nguoi_dung",nguoiDung);
                intentEdit.putExtras(data);
                editProfileLauncher.launch(intentEdit);
            }
        });
    }
}