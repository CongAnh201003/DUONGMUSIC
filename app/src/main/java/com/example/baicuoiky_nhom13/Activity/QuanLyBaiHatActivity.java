package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Adapter.QuanLyBHAdapter;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class QuanLyBaiHatActivity extends AppCompatActivity {
    private static final String TAG = "QuanLyBaiHatActivity";

    private ImageView imgQuayLai;
    private ListView lvQlBaiHat;
    private FloatingActionButton fabThemBaiHat;

    private QuanLyBHAdapter quanLyBHAdapter;
    private ArrayList<BaiHat> listBaiHat;
    private FirebaseFirestore firestore;
    private ActivityResultLauncher<Intent> addEditSongLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_bai_hat);
        applyWindowInsets();

        initViews();
        initFirebase();
        initListView();
        initLauncher();

        loadSongsFromFirestore();
        setupClickListeners();
    }

    public void loadSongsFromFirestore() {
        firestore.collection("BAI_HAT") // Đọc từ collection BAI_HAT
                .orderBy("tenBH", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        listBaiHat.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BaiHat baiHat = document.toObject(BaiHat.class);
                            baiHat.setIdBH(document.getId());
                            listBaiHat.add(baiHat);
                        }
                        quanLyBHAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Lỗi khi tải danh sách bài hát: ", task.getException());
                        Toast.makeText(this, "Lỗi khi tải dữ liệu. Hãy kiểm tra Security Rules.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews() {
        imgQuayLai = findViewById(R.id.imgQuayLai);
        lvQlBaiHat = findViewById(R.id.lvQlBaiHat);
        fabThemBaiHat = findViewById(R.id.fabThemBaiHat);
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initListView() {
        listBaiHat = new ArrayList<>();
        quanLyBHAdapter = new QuanLyBHAdapter(this, R.layout.lv_quanly_baihat, listBaiHat);
        lvQlBaiHat.setAdapter(quanLyBHAdapter);
    }

    private void initLauncher() {
        addEditSongLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadSongsFromFirestore();
                    }
                });
    }

    private void setupClickListeners() {
        imgQuayLai.setOnClickListener(v -> finish());

        // Sự kiện click nút (+) để mở màn hình thêm bài hát mới
        fabThemBaiHat.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddSongActivity.class);
            addEditSongLauncher.launch(intent);
        });

        lvQlBaiHat.setOnItemClickListener((parent, view, position, id) -> {
            BaiHat selectedSong = listBaiHat.get(position);
            openYoutubeLink(selectedSong.getLinkBH());
        });
    }

    // Hàm để mở link YouTube
    private void openYoutubeLink(String url) {
        if (url == null || url.trim().isEmpty()) {
            Toast.makeText(this, "Link bài hát không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở link. Vui lòng cài đặt ứng dụng YouTube.", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyWindowInsets() {
        // ... (hàm này giữ nguyên)
    }
}
