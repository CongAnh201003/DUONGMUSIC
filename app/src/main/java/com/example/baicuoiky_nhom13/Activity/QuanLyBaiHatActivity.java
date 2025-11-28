package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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

    // Khai báo view
    private ImageView imgQuayLai;
    private ListView lvQlBaiHat;
    private FloatingActionButton fabThemBaiHat;

    // Khai báo biến xử lý dữ liệu
    private QuanLyBHAdapter quanLyBHAdapter;
    private ArrayList<BaiHat> listBaiHat;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_bai_hat);

        // Thiết lập padding cho hệ thống (status bar, nav bar)
        applyWindowInsets();

        // Khởi tạo
        initViews();
        initFirebase();
        initListView();

        // Cài đặt sự kiện click (nhưng chưa gọi loadSongsFromFirestore ở đây)
        setupClickListeners();
    }

    // --- QUAN TRỌNG: CẬP NHẬT DỮ LIỆU KHI MÀN HÌNH HIỆN LÊN ---
    @Override
    protected void onResume() {
        super.onResume();
        // Bất kể khi nào quay lại màn hình này (từ Edit, từ Add, hay mở lại app)
        // Hàm này sẽ chạy để tải dữ liệu mới nhất từ server.
        loadSongsFromFirestore();
    }
    // ---------------------------------------------------------

    // --- Hàm khởi tạo View ---
    private void initViews() {
        imgQuayLai = findViewById(R.id.imgQuayLai);
        lvQlBaiHat = findViewById(R.id.lvQlBaiHat);
        fabThemBaiHat = findViewById(R.id.fabThemBaiHat);
    }

    // --- Hàm khởi tạo Firebase ---
    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    // --- Hàm khởi tạo ListView và Adapter ---
    private void initListView() {
        listBaiHat = new ArrayList<>();
        // Lưu ý: R.layout.layout_item_baihat là layout của từng dòng item
        quanLyBHAdapter = new QuanLyBHAdapter(this, R.layout.layout_item_baihat, listBaiHat);
        lvQlBaiHat.setAdapter(quanLyBHAdapter);
    }

    // --- Hàm tải dữ liệu từ Firestore ---
    public void loadSongsFromFirestore() {
        // Có thể thêm ProgressBar show() ở đây nếu muốn

        firestore.collection("BAI_HAT") // Tên Collection phải khớp với Database
                .orderBy("tenBH", Query.Direction.ASCENDING) // Sắp xếp theo tên bài hát A-Z
                .get()
                .addOnCompleteListener(task -> {
                    // Kiểm tra Activity còn sống không để tránh crash
                    if (isFinishing() || isDestroyed()) return;

                    if (task.isSuccessful() && task.getResult() != null) {
                        listBaiHat.clear(); // Xóa list cũ để nạp list mới
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Chuyển document thành Object BaiHat
                                BaiHat baiHat = document.toObject(BaiHat.class);
                                // Gán ID document vào object để sau này dùng cho Sửa/Xóa
                                baiHat.setIdBH(document.getId());
                                listBaiHat.add(baiHat);
                            } catch (Exception e) {
                                Log.e(TAG, "Lỗi parse dữ liệu bài hát: " + e.getMessage());
                            }
                        }
                        quanLyBHAdapter.notifyDataSetChanged(); // Cập nhật giao diện
                    } else {
                        Log.e(TAG, "Lỗi tải danh sách: ", task.getException());
                        Toast.makeText(this, "Không tải được dữ liệu bài hát.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- Cài đặt các sự kiện Click ---
    private void setupClickListeners() {
        // 1. Nút quay lại
        imgQuayLai.setOnClickListener(v -> finish());

        // 2. Nút Thêm bài hát (+)
        fabThemBaiHat.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyBaiHatActivity.this, AddSongActivity.class);
            startActivity(intent);
            // Không cần finish(), khi thêm xong quay lại -> onResume chạy -> load lại list
        });

        // 3. Click vào item trong List để Sửa (Update)
        lvQlBaiHat.setOnItemClickListener((parent, view, position, id) -> {
            BaiHat selectedSong = listBaiHat.get(position);

            Intent intent = new Intent(QuanLyBaiHatActivity.this, EditSongActivity.class);
            intent.putExtra("SONG_ID", selectedSong.getIdBH());
            // Class BaiHat đã implements Serializable nên truyền object thoải mái
            intent.putExtra("SONG_DATA", selectedSong);
            startActivity(intent);
            // Không cần finish(), khi sửa xong quay lại -> onResume chạy -> load lại list
        });
    }

    // --- Hàm xử lý giao diện Edge-to-Edge (tùy chọn) ---
    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
