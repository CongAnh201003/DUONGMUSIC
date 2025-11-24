package com.example.baicuoiky_nhom13.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baicuoiky_nhom13.Adapter.BaiHatAdapter;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ImageView imgBack;
    EditText edtSearch;
    ListView lvSearchResult;

    ArrayList<BaiHat> arrBaiHatGoc; // Danh sách gốc lấy từ DB
    ArrayList<BaiHat> arrBaiHatTimKiem; // Danh sách hiển thị sau khi lọc
    BaiHatAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 1. Ánh xạ
        imgBack = findViewById(R.id.imgBack);
        edtSearch = findViewById(R.id.edtSearch);
        lvSearchResult = findViewById(R.id.lvSearchResult);

        db = FirebaseFirestore.getInstance();

        // 2. Setup ListView và Adapter
        arrBaiHatGoc = new ArrayList<>();
        arrBaiHatTimKiem = new ArrayList<>();

        // Lưu ý: Ở màn hình Search thì không cần chức năng Yêu thích/Thêm,
        // nên ta có thể để tham số cuối là true (ẩn nút) hoặc false tùy bạn.
        // Ở đây mình để false (hiện nút +) nhưng không truyền UserID (null) để chỉ xem thôi.
        adapter = new BaiHatAdapter(this, R.layout.item_baihat, arrBaiHatTimKiem, null, false);
        lvSearchResult.setAdapter(adapter);

        // 3. Nút Back
        imgBack.setOnClickListener(v -> finish());

        // 4. Tải toàn bộ bài hát về trước (để tìm kiếm nhanh trên Client)
        loadAllSongs();

        // 5. Bắt sự kiện gõ phím để tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSong(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAllSongs() {
        db.collection("BAI_HAT").get().addOnSuccessListener(queryDocumentSnapshots -> {
            arrBaiHatGoc.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                BaiHat bh = doc.toObject(BaiHat.class);
                if (bh != null) {
                    bh.setIdBH(doc.getId());
                    arrBaiHatGoc.add(bh);
                }
            }
            // Ban đầu chưa tìm gì thì có thể để trống hoặc hiện tất cả (tùy bạn)
            // arrBaiHatTimKiem.addAll(arrBaiHatGoc);
            // adapter.notifyDataSetChanged();
        });
    }

    private void searchSong(String keyword) {
        arrBaiHatTimKiem.clear();
        if (keyword.isEmpty()) {
            // Nếu xóa hết chữ thì xóa list hiển thị
            adapter.notifyDataSetChanged();
            return;
        }

        String key = keyword.toLowerCase();

        for (BaiHat baiHat : arrBaiHatGoc) {
            // Tìm theo tên bài hát hoặc tên ca sĩ
            if (baiHat.getTenBH().toLowerCase().contains(key) ||
                    baiHat.getTenCaSi().toLowerCase().contains(key)) {
                arrBaiHatTimKiem.add(baiHat);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
