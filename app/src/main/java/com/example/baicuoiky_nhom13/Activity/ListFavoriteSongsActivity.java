package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Adapter.BaiHatYTAdapter;
import com.example.baicuoiky_nhom13.Database.MySQLite;
import com.example.baicuoiky_nhom13.Database.SQLiteYeuThich;
import com.example.baicuoiky_nhom13.Model.BaiHatYT;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;

import java.util.ArrayList;

public class ListFavoriteSongsActivity extends AppCompatActivity {
    ImageView imgBack;
    ListView lvBaiHatYT;
    TextView tvTenND;
    ArrayList<BaiHatYT> listBHYT;
    BaiHatYTAdapter baiHatAdapter;
    SQLiteYeuThich sqLiteYeuThich;
    String sql="";
    MySQLite mySQLite;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_danh_sach_yeu_thich);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgBack=findViewById(R.id.imgBack);
        lvBaiHatYT=findViewById(R.id.lvBaiHatYT);
        tvTenND=findViewById(R.id.tvTenND);
        sqLiteYeuThich=new SQLiteYeuThich(getBaseContext(),SQLiteYeuThich.DATABASE_NAME,null,1);
        mySQLite=new MySQLite(getBaseContext(),MySQLite.DATABASE_NAME,null,1);


        Intent intent=getIntent();
        String idnd=intent.getStringExtra("id");
        id=Integer.parseInt(idnd);

        NguoiDung nguoiDung1=mySQLite.loadNguoiDung(id);
        tvTenND.setText(nguoiDung1.getHoTen());
        //tvTest.setText(idnd);
        loadBHyt();
        lvBaiHatYT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent ngheNhacIntent=new Intent(getBaseContext(), TrinhNgheNhac2Activity.class);
                Bundle data=new Bundle();
                BaiHatYT baiHat=listBHYT.get(i);
                data.putSerializable("name",baiHat);
                ngheNhacIntent.putExtras(data);
                startActivity(ngheNhacIntent);
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    public void loadBHyt(){
        Intent intent=getIntent();
        String idnd=intent.getStringExtra("id");
        id=Integer.parseInt(idnd);
        sql="SELECT * FROM BAI_HAT_YT where id_nd = "+id;
        listBHYT=sqLiteYeuThich.DocDuLieuYT(sql);
        baiHatAdapter=new BaiHatYTAdapter(ListFavoriteSongsActivity.this,R.layout.lv_baihat_yt,listBHYT);
        lvBaiHatYT.setAdapter(baiHatAdapter);
    }
}