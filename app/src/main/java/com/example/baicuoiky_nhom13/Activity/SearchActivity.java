package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Adapter.BaiHatAdapter;
import com.example.baicuoiky_nhom13.Database.MySQLiteBaiHat;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    EditText edtSearch;
    ListView lvSearchResult;
    TextView tvResultSearch;
    BaiHatAdapter baiHatAdapter;
    ArrayList<BaiHat> listBaiHat, filteredList;
    MySQLiteBaiHat mySQLiteBaiHat;
    ImageView imgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Ánh xạ view
        imgBack = findViewById(R.id.imgBack);
        edtSearch = findViewById(R.id.edtSearch);
        lvSearchResult = findViewById(R.id.lvSearchResult);
        tvResultSearch = findViewById(R.id.tvResultSearch);

        // Khởi tạo cơ sở dữ liệu
        mySQLiteBaiHat = new MySQLiteBaiHat(getBaseContext(), MySQLiteBaiHat.DATABASE_NAME, null, 1);

        // Lấy dữ liệu từ cơ sở dữ liệu
        listBaiHat = mySQLiteBaiHat.DocDuLieu("SELECT * FROM BAI_HAT;");
        filteredList = new ArrayList<>(listBaiHat);

        // Thiết lập adapter
        baiHatAdapter = new BaiHatAdapter(this, R.layout.lv_baihat, filteredList);
        lvSearchResult.setAdapter(baiHatAdapter);


        // Tìm kiếm theo tên bài hát
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString().toLowerCase().trim();
                filteredList.clear();

                if (searchText.isEmpty()) {
                    filteredList.addAll(listBaiHat);
                } else {
                    for (BaiHat baiHat : listBaiHat) {
                        if (baiHat.getTenBaiHat().toLowerCase().contains(searchText)) {
                            filteredList.add(baiHat);
                        }
                    }
                }

                // Hiển thị trạng thái tìm kiếm
                tvResultSearch.setText(filteredList.isEmpty() ? "No results found" : "Result searches");

                // Cập nhật adapter
                baiHatAdapter.notifyDataSetChanged();
            }
        });
        // nut back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Xử lý sự kiện click vào một bài hát
        lvSearchResult.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            BaiHat selectedBaiHat = filteredList.get(position);

            // Chuyển sang màn hình phát nhạc
            Intent intent = new Intent(SearchActivity.this, TrinhNgheNhacActivity.class);
            intent.putExtra("name", selectedBaiHat);
            startActivity(intent);

            // Log kiểm tra dữ liệu
            Log.d("DEBUG", "Selected song: " + selectedBaiHat.getTenBaiHat());
        });


    }


}