package com.example.baicuoiky_nhom13.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baicuoiky_nhom13.Adapter.BaiHatAdapter;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class YeuThichActivity extends AppCompatActivity {

    ListView lvYeuThich;
    ImageView imgBack;
    ArrayList<BaiHat> arrBaiHatYeuThich;
    BaiHatAdapter adapter;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeu_thich);

        // 1. Ánh xạ
        lvYeuThich = findViewById(R.id.lvYeuThich);
        imgBack = findViewById(R.id.imgBack);

        // 2. Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy Email làm ID
        userId = currentUser.getEmail();

        // 3. Setup Adapter
        arrBaiHatYeuThich = new ArrayList<>();

        // QUAN TRỌNG: Tham số 'true' ở cuối nghĩa là: Đây là màn hình Yêu Thích -> Ẩn dấu +
        adapter = new BaiHatAdapter(this, R.layout.item_baihat, arrBaiHatYeuThich, userId, true);

        lvYeuThich.setAdapter(adapter);

        // 4. Sự kiện nút Back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 5. Load dữ liệu
        LayDanhSachYeuThich();
    }

    private void LayDanhSachYeuThich() {
        db.collection("NGUOI_DUNG").document(userId).collection("YeuThich")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(YeuThichActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            arrBaiHatYeuThich.clear();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                BaiHat baiHat = document.toObject(BaiHat.class);
                                if (baiHat != null) {
                                    baiHat.setIdBH(document.getId());
                                    arrBaiHatYeuThich.add(baiHat);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
