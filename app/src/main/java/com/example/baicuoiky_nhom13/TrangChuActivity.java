package com.example.baicuoiky_nhom13;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Activity.ListFavoriteSongsActivity;
import com.example.baicuoiky_nhom13.Activity.PhanHoiActivity;
import com.example.baicuoiky_nhom13.Activity.ProfileUserActivity;
import com.example.baicuoiky_nhom13.Activity.SearchActivity;
import com.example.baicuoiky_nhom13.Activity.TrinhNgheNhacActivity;
import com.example.baicuoiky_nhom13.Adapter.BaiHatAdapter;
import com.example.baicuoiky_nhom13.Database.MySQLite;
import com.example.baicuoiky_nhom13.Database.MySQLiteBaiHat;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.Model.NguoiDung;

import java.util.ArrayList;

public class TrangChuActivity extends AppCompatActivity {
    TextView tvTenNguoiDung;
    ImageView imgProfile,imgTrangChu,imgPhanHoi,imgSearch;
    ListView lvBaiHat;
    BaiHatAdapter baiHatAdapter;
    ArrayList<BaiHat> listBaiHat;
    MySQLiteBaiHat mySQLiteBaiHat;
    String sql="";
    MySQLite mySQLite;
    CardView cvYeuThich;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trang_chu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvTenNguoiDung=findViewById(R.id.tvTenNguoiDung);
        imgProfile=findViewById(R.id.imgProfile);
        imgTrangChu=findViewById(R.id.imgTrangChu);
        imgPhanHoi=findViewById(R.id.imgPhanHoi);
        imgSearch=findViewById(R.id.imgSearch);
        lvBaiHat=findViewById(R.id.lvBaiHat);
        cvYeuThich = findViewById(R.id.cvYeuThich);
        mySQLite=new MySQLite(getBaseContext(),MySQLite.DATABASE_NAME,null,1);

        Intent intent=getIntent();
        NguoiDung nguoiDung= (NguoiDung) intent.getSerializableExtra("nguoi_dung");
        tvTenNguoiDung.setText(nguoiDung.getHoTen());
        id=nguoiDung.getId();

        listBaiHat=new ArrayList<>();

        mySQLiteBaiHat=new MySQLiteBaiHat(getBaseContext(),MySQLiteBaiHat.DATABASE_NAME,null,1);


        loadBH();


        lvBaiHat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent ngheNhacIntent=new Intent(getBaseContext(), TrinhNgheNhacActivity.class);
                Bundle data=new Bundle();
                BaiHat baiHat=listBaiHat.get(i);
                data.putSerializable("name",baiHat);
                ngheNhacIntent.putExtras(data);
                startActivity(ngheNhacIntent);
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileUserIntent=new Intent(getBaseContext(), ProfileUserActivity.class);
                NguoiDung nguoiDung1=mySQLite.loadNguoiDung(id);
                Bundle data=new Bundle();
                data.putSerializable("nguoi_dung",nguoiDung1);
                profileUserIntent.putExtras(data);
                ProfileLauncher.launch(profileUserIntent);
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searcIntent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(searcIntent);

            }
        });
        imgPhanHoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phanHoiIntent=new Intent(getBaseContext(), PhanHoiActivity.class);
                startActivity(phanHoiIntent);

            }
        });
        cvYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(getBaseContext(), ListFavoriteSongsActivity.class);
                String idnd=id+"";
                intent1.putExtra("id",idnd);
                startActivity(intent1);
            }
        });
        onBackPressed();
    }
    public void loadBH(){
        sql="SELECT * FROM BAI_HAT;";
        listBaiHat=mySQLiteBaiHat.DocDuLieu(sql);
        baiHatAdapter=new BaiHatAdapter(TrangChuActivity.this,R.layout.lv_baihat,listBaiHat);
        lvBaiHat.setAdapter(baiHatAdapter);
    }
    private final ActivityResultLauncher<Intent> ProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String hoten=data.getStringExtra("hoten");
                    tvTenNguoiDung.setText(hoten);


                }
            }
    );
    public int idND(){
        Intent intent=getIntent();
        NguoiDung nguoiDung= (NguoiDung) intent.getSerializableExtra("nguoi_dung");
        //tvTenNguoiDung.setText(nguoiDung.getHoTen());
        id=nguoiDung.getId();
        return id;
    }
    @Override
    public void onBackPressed() {
        // Không làm gì cả để chặn nút Back
    }
}