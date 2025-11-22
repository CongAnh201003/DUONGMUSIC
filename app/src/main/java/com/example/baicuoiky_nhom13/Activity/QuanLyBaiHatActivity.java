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

    // Khai báo view
    private ImageView imgQuayLai;
    private ListView lvQlBaiHat;
    private FloatingActionButton fabThemBaiHat;

    // Khai báo biến xử lý dữ liệu
    private QuanLyBHAdapter quanLyBHAdapter;
    private ArrayList<BaiHat> listBaiHat;
    private FirebaseFirestore firestore;

    // Launcher để nhận kết quả từ màn hình Thêm/Sửa (reload list khi quay về)
    private ActivityResultLauncher<Intent> addEditSongLauncher;

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
        initLauncher();

        // Tải dữ liệu và cài đặt sự kiện
        loadSongsFromFirestore();
        setupClickListeners();
    }

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
        // Lưu ý: R.layout.lv_quanly_baihat là layout của từng dòng item (bạn cần tạo file này)
        quanLyBHAdapter = new QuanLyBHAdapter(this, R.layout.layout_item_baihat, listBaiHat);
        lvQlBaiHat.setAdapter(quanLyBHAdapter);
    }

    // --- Khởi tạo Launcher để reload list khi thêm/sửa xong ---
    private void initLauncher() {
        addEditSongLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Nếu Thêm hoặc Sửa thành công thì tải lại danh sách
                        loadSongsFromFirestore();
                    }
                });
    }

    // --- Hàm tải dữ liệu từ Firestore ---
    public void loadSongsFromFirestore() {
        // Hiển thị thông báo hoặc ProgressBar nếu cần
        // ...

        firestore.collection("BAI_HAT") // Tên Collection phải khớp với Database
                .orderBy("tenBH", Query.Direction.ASCENDING) // Sắp xếp theo tên bài hát
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        listBaiHat.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển document thành Object BaiHat
                            BaiHat baiHat = document.toObject(BaiHat.class);
                            // Gán ID document vào object để sau này dùng cho Sửa/Xóa
                            baiHat.setIdBH(document.getId());
                            listBaiHat.add(baiHat);
                        }
                        quanLyBHAdapter.notifyDataSetChanged();
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
            // Sử dụng launcher để khi Add xong quay lại sẽ tự reload list
            addEditSongLauncher.launch(intent);
        });

        // 3. Click vào item trong List để Sửa (Update)
        lvQlBaiHat.setOnItemClickListener((parent, view, position, id) -> {
            BaiHat selectedSong = listBaiHat.get(position);

            // Chuyển sang màn hình Sửa (ví dụ EditSongActivity) hoặc dùng lại AddSongActivity với cờ edit
            // Ở đây giả sử bạn muốn dùng AddSongActivity để sửa luôn (cần update logic bên đó)
            // Hoặc tạo Activity mới là EditSongActivity

            Intent intent = new Intent(QuanLyBaiHatActivity.this, EditSongActivity.class); // Bạn cần tạo Activity này
            intent.putExtra("SONG_ID", selectedSong.getIdBH());
            intent.putExtra("SONG_DATA", selectedSong); // Class BaiHat cần implements Serializable
            addEditSongLauncher.launch(intent);
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
