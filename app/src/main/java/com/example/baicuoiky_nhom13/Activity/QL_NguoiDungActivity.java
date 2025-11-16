package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Adapter.NguoiDungAdapter;
import com.example.baicuoiky_nhom13.Database.MySQLite;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class QL_NguoiDungActivity extends AppCompatActivity {
    ImageView imgQuayLai;
    ListView lvQlNguoiDung;
    FloatingActionButton fabThemNguoiDung;
    NguoiDungAdapter nguoiDungAdapter;
    ArrayList<NguoiDung> listNguoiDung;
    MySQLite database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ql_nguoi_dung);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgQuayLai=findViewById(R.id.imgQuayLai);
        lvQlNguoiDung=findViewById(R.id.lvQlNguoiDung);
        fabThemNguoiDung=findViewById(R.id.fabThemNguoiDung);


        // Khởi tạo database
        database = new MySQLite(this, MySQLite.DATABASE_NAME, null, 1);

        // Tải dữ liệu người dùng từ database
        loadData();

        // them nguoi dung
        fabThemNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QL_NguoiDungActivity.this, AddUserActivity.class);
                //startActivity(intent);
                themMoiTKLauncher.launch(intent);
            }
        });
        imgQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    // Hàm tải dữ liệu người dùng từ database
    public void loadData() {
        String sql = "SELECT * FROM NGUOI_DUNG";
        listNguoiDung = database.docDuLieu(sql);

        // Khởi tạo adapter và gắn vào ListView
        nguoiDungAdapter = new NguoiDungAdapter(QL_NguoiDungActivity.this, R.layout.lv_nguoidung, listNguoiDung);
        lvQlNguoiDung.setAdapter(nguoiDungAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 123 && requestCode==123) {
            // Nếu việc thêm người dùng thành công, tải lại dữ liệu người dùng
            loadData();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại danh sách người dùng khi quay lại màn hình
        loadData();
    }
    ActivityResultLauncher themMoiTKLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode()==RESULT_OK){
                        loadData();
                    }
                }
            });
}