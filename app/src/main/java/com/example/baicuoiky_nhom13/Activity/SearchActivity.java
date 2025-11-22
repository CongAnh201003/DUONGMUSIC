package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Adapter.BaiHatAdapter;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
// Thêm các import cần thiết cho Firebase
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    // Khai báo UI
    private EditText edtSearch;
    private ListView lvSearchResult;
    private TextView tvResultSearch;
    private ImageView imgBack;

    // Khai báo Adapter và danh sách
    private BaiHatAdapter baiHatAdapter;
    private ArrayList<BaiHat> searchResults; // Danh sách chỉ chứa kết quả tìm kiếm

    // Khai báo Firebase
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bỏ EdgeToEdge để tránh lỗi không cần thiết
        setContentView(R.layout.activity_search);
        applyWindowInsets();

        // Khởi tạo các thành phần
        initViews();
        initFirebase();
        initListView();
        setupClickListeners();
        setupSearchListener();
    }

    /**
     * Tải và tìm kiếm bài hát trực tiếp trên Firestore.
     * @param searchText Từ khóa tìm kiếm.
     */
    private void searchSongsInFirestore(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            searchResults.clear();
            baiHatAdapter.notifyDataSetChanged();
            tvResultSearch.setVisibility(View.GONE); // Ẩn text view khi không có từ khóa
            return;
        }

        // Chuyển từ khóa thành chữ thường để tìm kiếm không phân biệt hoa/thường
        String queryText = searchText.toLowerCase(Locale.ROOT);

        firestore.collection("BaiHat")
                // Firebase Firestore không hỗ trợ tìm kiếm "contains" trực tiếp một cách hiệu quả.
                // Cách tiếp cận phổ biến là tìm các document bắt đầu bằng từ khóa.
                // Ví dụ: tìm "sơn t" sẽ ra "Sơn Tùng M-TP..."
                .orderBy("tenBH_lowercase") // Cần một trường chữ thường để tìm kiếm và sắp xếp
                .startAt(queryText)
                .endAt(queryText + "\uf8ff") // \uf8ff là một ký tự Unicode rất cao, giúp tạo ra một khoảng tìm kiếm
                .limit(20) // Giới hạn 20 kết quả để tối ưu
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        searchResults.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BaiHat baiHat = document.toObject(BaiHat.class);
                            baiHat.setIdBH(document.getId());
                            searchResults.add(baiHat);
                        }
                        baiHatAdapter.notifyDataSetChanged();

                        // Hiển thị trạng thái kết quả
                        if (searchResults.isEmpty()) {
                            tvResultSearch.setText("Không tìm thấy kết quả nào");
                            tvResultSearch.setVisibility(View.VISIBLE);
                        } else {
                            tvResultSearch.setVisibility(View.GONE); // Ẩn khi có kết quả
                        }
                    } else {
                        Log.e(TAG, "Lỗi khi tìm kiếm: ", task.getException());
                        Toast.makeText(this, "Lỗi tìm kiếm.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- CÁC HÀM KHỞI TẠO VÀ THIẾT LẬP ---

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        edtSearch = findViewById(R.id.edtSearch);
        lvSearchResult = findViewById(R.id.lvSearchResult);
        tvResultSearch = findViewById(R.id.tvResultSearch);
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initListView() {
        searchResults = new ArrayList<>();
        // Cần truyền thêm userId, tạm thời để trống
        baiHatAdapter = new BaiHatAdapter(this, R.layout.layout_item_baihat, searchResults, "");
        lvSearchResult.setAdapter(baiHatAdapter);
    }

    private void setupClickListeners() {
        imgBack.setOnClickListener(view -> finish());

        lvSearchResult.setOnItemClickListener((parent, view, position, id) -> {
            BaiHat selectedBaiHat = searchResults.get(position);

            // Chuyển sang màn hình phát nhạc, gửi cả danh sách kết quả và vị trí
            Intent intent = new Intent(SearchActivity.this, TrinhNgheNhacActivity.class);
            intent.putExtra("danh_sach_bai_hat", searchResults);
            intent.putExtra("vi_tri", position);
            startActivity(intent);

            Log.d(TAG, "Đã chọn bài hát: " + selectedBaiHat.getTenBH());
        });
    }

    private void setupSearchListener() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Thực hiện tìm kiếm khi người dùng gõ
                searchSongsInFirestore(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
