package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;

public class ProfileAdminActivity extends AppCompatActivity {
    ImageView imgQuayLai,imgAnhDaiDien;
    TextView tvTenNguoiDung,tvTenDangNhap,tvMatkhau,tvEmail,tvVaiTro;
    Button btnChinhSua,btnDangXuat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_admin);
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
        tvVaiTro=findViewById(R.id.tvVaiTro);
        btnDangXuat=findViewById(R.id.btnDangXuat);

        Intent intent=getIntent();
        Bundle data=intent.getExtras();
        NguoiDung nguoiDung= (NguoiDung) data.get("admin");

        tvTenNguoiDung.setText(nguoiDung.getHoTen());
        tvEmail.setText(nguoiDung.getEmail());
        tvMatkhau.setText(nguoiDung.getMatKhau());
        tvTenDangNhap.setText(nguoiDung.getTenDN());
        tvVaiTro.setText("Quản trị viên");

        //NguoiDung nguoiDung=new NguoiDung(idND,tenDN,makhau,hoten,email,"",1);

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

        btnChinhSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAminEdit=new Intent(getBaseContext(), EditProfileAdminActivity.class);
                Bundle data=new Bundle();
                data.putSerializable("nguoi_dung",nguoiDung);
                intentAminEdit.putExtras(data);
                editProfileLauncher.launch(intentAminEdit);
            }
        });

    }
    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Cap nhat lai thong tin nguoi dung sau khi sua
                    Intent data = result.getData();
                    Bundle data1=data.getExtras();
                    NguoiDung nd= (NguoiDung) data1.get("kq");

                    tvTenDangNhap.setText(nd.getTenDN());
                    tvTenNguoiDung.setText(nd.getHoTen());
                    tvEmail.setText(nd.getEmail());
                    tvMatkhau.setText(nd.getMatKhau());

                }
            }
    );
}