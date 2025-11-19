package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Adapter.BaiHatYTAdapter;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ListFavoriteSongsActivity extends AppCompatActivity {
    private static final String TAG = "ListFavoriteSongs";

    // Khai báo UI components
    private ImageView imgBack;
    private ListView lvBaiHatYT;
    private TextView tvTenND;

    // Khai báo cho ListView và Adapter
    private ArrayList<BaiHat> listBHYT;
    private BaiHatYTAdapter baiHatAdapter;

    // Khai báo Firebase
    private FirebaseFirestore firestore;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // === LỖI ĐÃ SỬA: Xóa dòng EdgeToEdge.enable(this) ===
        setContentView(R.layout.activity_danh_sach_yeu_thich);
        applyWindowInsets();

        // Khởi tạo
        initViews();
        initFirebase();

        // Lấy User ID từ Intent
        Intent intent = getIntent();
        currentUserId = intent.getStringExtra("id_user");

        // === SỬA LỖI: Khởi tạo ListView sau khi đã có currentUserId ===
        initListView(); // Chuyển xuống đây để có thể truyền userId cho Adapter

        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu không có user ID
            return;
        }

        // Tải dữ liệu từ Firestore
        loadUserInfo();
        loadFavoriteSongs();

        // Thiết lập sự kiện click
        setupClickListeners();
    }

    /**
     * Tải thông tin người dùng (tên) và hiển thị lên TextView.
     */
    private void loadUserInfo() {
        firestore.collection("NGUOI_DUNG").document(currentUserId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String hoTen = document.getString("hoTen");
                            tvTenND.setText(hoTen != null ? hoTen : "Người dùng");
                        } else {
                            Log.d(TAG, "Không tìm thấy document người dùng");
                            tvTenND.setText("Người dùng không tồn tại");
                        }
                    } else {
                        Log.d(TAG, "Lỗi khi lấy thông tin người dùng: ", task.getException());
                    }
                });
    }

    /**
     * Tải danh sách bài hát yêu thích từ sub-collection của người dùng.
     * === LỖI ĐÃ SỬA: Chuyển sang 'public' để Adapter có thể gọi ===
     */
    public void loadFavoriteSongs() {
        firestore.collection("NGUOI_DUNG").document(currentUserId).collection("YeuThich")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        listBHYT.clear(); // Xóa danh sách cũ
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BaiHat baiHat = document.toObject(BaiHat.class);
                            // === LỖI ĐÃ SỬA: Gán ID cho đối tượng ===
                            baiHat.setIdBH(document.getId());
                            listBHYT.add(baiHat);
                            // === LỖI ĐÃ SỬA: Giờ đã có thể gọi getTenBH() ===
                            Log.d(TAG, "Đã tải bài hát yêu thích: " + baiHat.getTenBH());
                        }
                        baiHatAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Lỗi khi tải danh sách bài hát yêu thích: ", task.getException());
                        Toast.makeText(this, "Lỗi khi tải danh sách yêu thích.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Thiết lập các sự kiện click cho các View.
     */
    private void setupClickListeners() {
        imgBack.setOnClickListener(view -> finish());

        lvBaiHatYT.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent ngheNhacIntent = new Intent(ListFavoriteSongsActivity.this, TrinhNgheNhacActivity.class);
            ngheNhacIntent.putExtra("danh_sach_bai_hat", listBHYT);
            ngheNhacIntent.putExtra("vi_tri", i);
            startActivity(ngheNhacIntent);
        });
    }

    // --- Các hàm khởi tạo ---

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        lvBaiHatYT = findViewById(R.id.lvBaiHatYT);
        tvTenND = findViewById(R.id.tvTenND);
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initListView() {
        listBHYT = new ArrayList<>();
        // Truyền currentUserId vào Adapter
        baiHatAdapter = new BaiHatYTAdapter(this, R.layout.lv_baihat_yt, listBHYT, currentUserId);
        lvBaiHatYT.setAdapter(baiHatAdapter);
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
