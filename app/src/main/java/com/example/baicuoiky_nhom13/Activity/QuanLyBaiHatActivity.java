package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Adapter.QuanLyBHAdapter;
import com.example.baicuoiky_nhom13.Database.MySQLiteBaiHat;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;

import java.util.ArrayList;

public class QuanLyBaiHatActivity extends AppCompatActivity {
    ImageView imgQuayLai;
    ListView lvQlBaiHat;
    QuanLyBHAdapter quanLyBHAdapter;
    ArrayList<BaiHat> listBaiHat;
    MySQLiteBaiHat mySQLiteBaiHat;
    String sql="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_ly_bai_hat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgQuayLai=findViewById(R.id.imgQuayLai);
        lvQlBaiHat=findViewById(R.id.lvQlBaiHat);
        imgQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listBaiHat=new ArrayList<>();
        mySQLiteBaiHat=new MySQLiteBaiHat(getBaseContext(),MySQLiteBaiHat.DATABASE_NAME,null,1);
        loadQLBH();
        lvQlBaiHat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    }
    public void loadQLBH(){
        sql="SELECT * FROM BAI_HAT;";
        listBaiHat=mySQLiteBaiHat.DocDuLieu(sql);
        quanLyBHAdapter=new QuanLyBHAdapter(QuanLyBaiHatActivity.this,R.layout.lv_quanly_baihat,listBaiHat);
        lvQlBaiHat.setAdapter(quanLyBHAdapter);
    }
}