package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.Adapter.NguoiDungAdapter;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class QL_NguoiDungActivity extends AppCompatActivity {
    private static final String TAG = "QL_NguoiDungActivity";

    // Khai báo các thành phần UI
    private ImageView imgQuayLai;
    private ListView lvQlNguoiDung;
    private FloatingActionButton fabThemNguoiDung;

    // Khai báo Adapter và danh sách
    private NguoiDungAdapter nguoiDungAdapter;
    private ArrayList<NguoiDung> listNguoiDung;

    // Khai báo Firebase
    private FirebaseFirestore firestore;

    // ActivityResultLauncher để xử lý kết quả trả về từ các Activity khác
    public ActivityResultLauncher<Intent> themMoiTKLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ql_nguoi_dung);
        applyWindowInsets();

        // Khởi tạo các thành phần
        initViews();
        initFirebase();
        initListView();
        initLaunchers();

        // Tải dữ liệu ban đầu từ Firestore
        loadDataFromFirestore();

        // Thiết lập các sự kiện click
        setupClickListeners();
    }

    /**
     * Áp dụng padding cho màn hình để không bị che bởi thanh hệ thống.
     */
    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Ánh xạ các thành phần giao diện từ layout.
     */
    private void initViews() {
        imgQuayLai = findViewById(R.id.imgQuayLai);
        lvQlNguoiDung = findViewById(R.id.lvQlNguoiDung);
        fabThemNguoiDung = findViewById(R.id.fabThemNguoiDung);
    }

    /**
     * Khởi tạo các dịch vụ của Firebase.
     */
    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Khởi tạo ListView, Adapter và danh sách dữ liệu.
     */
    private void initListView() {
        listNguoiDung = new ArrayList<>();
        nguoiDungAdapter = new NguoiDungAdapter(this, R.layout.lv_nguoidung, listNguoiDung);
        lvQlNguoiDung.setAdapter(nguoiDungAdapter);
    }

    /**
     * Khởi tạo ActivityResultLauncher để nhận kết quả từ các Activity khác.
     */
    private void initLaunchers() {
        themMoiTKLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Nếu kết quả là OK (thêm/sửa thành công), tải lại dữ liệu
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Cập nhật danh sách...", Toast.LENGTH_SHORT).show();
                        loadDataFromFirestore();
                    }
                }
        );
    }

    /**
     * Thiết lập sự kiện click cho các nút.
     */
    private void setupClickListeners() {
        // Sự kiện click nút thêm người dùng
        fabThemNguoiDung.setOnClickListener(view -> {
            Intent intent = new Intent(QL_NguoiDungActivity.this, AddUserActivity.class);
            themMoiTKLauncher.launch(intent);
        });

        // Sự kiện click nút quay lại
        imgQuayLai.setOnClickListener(view -> finish());
    }

    /**
     * Tải danh sách người dùng từ Cloud Firestore và cập nhật ListView.
     * Hàm này được khai báo là 'public' để Adapter có thể gọi nó.
     */
    public void loadDataFromFirestore() {
        firestore.collection("NGUOI_DUNG")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Xóa dữ liệu cũ trước khi thêm mới để tránh trùng lặp
                        listNguoiDung.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển đổi document thành đối tượng NguoiDung
                            NguoiDung nguoiDung = document.toObject(NguoiDung.class);
                            // Gán ID của document (emailId) cho đối tượng
                            nguoiDung.setId(document.getId());
                            listNguoiDung.add(nguoiDung);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        // Thông báo cho Adapter để cập nhật lại giao diện
                        nguoiDungAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Lỗi khi tải dữ liệu người dùng.", task.getException());
                        Toast.makeText(QL_NguoiDungActivity.this, "Lỗi khi tải dữ liệu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
