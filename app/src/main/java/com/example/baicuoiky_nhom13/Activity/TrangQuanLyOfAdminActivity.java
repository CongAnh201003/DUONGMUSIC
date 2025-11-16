package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Database.MySQLite;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;

public class TrangQuanLyOfAdminActivity extends AppCompatActivity {
    TextView tvTenAdmin;
    ImageView imgQuanLyNhac,imgQuanLyUser;
    Button btnTrangProfileAdmin;
    MySQLite mySQLite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trang_quan_ly_of_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvTenAdmin=findViewById(R.id.tvTenAdmin);
        imgQuanLyNhac=findViewById(R.id.imgQuanLyNhac);
        imgQuanLyUser=findViewById(R.id.imgQuanLyUser);
        btnTrangProfileAdmin=findViewById(R.id.btnTrangProfileAdmin);
        mySQLite=new MySQLite(getBaseContext(),MySQLite.DATABASE_NAME,null,1);

        Intent intent=getIntent();
        NguoiDung nguoiDung= (NguoiDung) intent.getSerializableExtra("admin");
        tvTenAdmin.setText(nguoiDung.getHoTen());
        String hoten=nguoiDung.getHoTen();
        String mk=nguoiDung.getMatKhau();
        String email=nguoiDung.getEmail();
        String tenDN=nguoiDung.getTenDN();
        String id=nguoiDung.getId()+"";
        int idND=Integer.parseInt(id);

        imgQuanLyNhac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent QuanLyNhacIntent=new Intent(getBaseContext(), QuanLyBaiHatActivity.class);
                startActivity(QuanLyNhacIntent);
            }
        });

        imgQuanLyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent QLNGuoiDungIntent=new Intent(getBaseContext(), QL_NguoiDungActivity.class);
                startActivity(QLNGuoiDungIntent);
            }
        });
        btnTrangProfileAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileAdminIntent=new Intent(getBaseContext(),ProfileAdminActivity.class);
                NguoiDung nguoiDung1=mySQLite.loadNguoiDung(idND);
                Bundle data=new Bundle();
                data.putSerializable("admin",nguoiDung1);
                profileAdminIntent.putExtras(data);
                ProfileLauncher.launch(profileAdminIntent);
            }
        });
        onBackPressed();
    }
    private final ActivityResultLauncher<Intent> ProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String hoten=data.getStringExtra("hoten");
                    tvTenAdmin.setText(hoten);


                }
            }
    );
    @Override
    public void onBackPressed() {
        // Không làm gì cả để chặn nút Back
    }

}