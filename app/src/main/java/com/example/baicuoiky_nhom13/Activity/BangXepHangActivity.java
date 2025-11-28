package com.example.baicuoiky_nhom13.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baicuoiky_nhom13.Adapter.BaiHatAdapter;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class BangXepHangActivity extends AppCompatActivity {

    private ListView lvBangXepHang;
    private ImageView imgBack;
    private ArrayList<BaiHat> listTopBaiHat;
    private BaiHatAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang_xep_hang);

        db = FirebaseFirestore.getInstance();
        lvBangXepHang = findViewById(R.id.lvBangXepHang);
        imgBack = findViewById(R.id.imgBackBXH);

        listTopBaiHat = new ArrayList<>();

        // Lấy userId để phục vụ chức năng Yêu thích trong Adapter
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : null;

        // Tái sử dụng Adapter cũ để hiển thị danh sách
        adapter = new BaiHatAdapter(this, R.layout.item_baihat, listTopBaiHat, userId);
        lvBangXepHang.setAdapter(adapter);

        imgBack.setOnClickListener(v -> finish());

        loadRanking();
    }

    private void loadRanking() {
        db.collection("BAI_HAT")
                .orderBy("luotXem", Query.Direction.DESCENDING) // Sắp xếp GIẢM DẦN theo lượt xem
                .limit(20) // Chỉ lấy Top 20 bài
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listTopBaiHat.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            BaiHat baiHat = document.toObject(BaiHat.class);
                            baiHat.setIdBH(document.getId());
                            listTopBaiHat.add(baiHat);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải BXH: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
