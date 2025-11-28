package com.example.baicuoiky_nhom13; // Đảm bảo package đúng với dự án của bạn

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.baicuoiky_nhom13.Adapter.BaiHatAdapter;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TrangChuActivity extends AppCompatActivity {

    private static final String TAG = "TrangChuActivity";

    // Khai báo View
    ListView lvBaiHat;
    TextView tvTenNguoiDung;
    ImageView imgProfile, imgSearch, imgPhanHoi, imgBXH;
    CardView cvYeuThich;

    // Khai báo dữ liệu
    ArrayList<BaiHat> arrBaiHat;
    BaiHatAdapter adapter;

    // Khai báo Firebase
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu);

        // 1. Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // 2. Ánh xạ View (Gọi hàm AnhXa để tìm ID)
        AnhXa();

        // Kiểm tra đăng nhập
        if (currentUser != null) {
            currentUserId = currentUser.getEmail();
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }

        // 3. Thiết lập ListView và Adapter
        arrBaiHat = new ArrayList<>();
        // Truyền currentUserId vào Adapter để xử lý chức năng Yêu thích
        adapter = new BaiHatAdapter(this, R.layout.item_baihat, arrBaiHat, currentUserId);
        lvBaiHat.setAdapter(adapter);

        // 4. Lấy danh sách bài hát từ Firestore
        LayDanhSachBaiHat();

        // 5. Sự kiện click vào item ListView (Mở Youtube)
        lvBaiHat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaiHat baiHatChon = arrBaiHat.get(position);

                // Logic tăng view đã được xử lý trong Adapter (nếu bạn đã làm theo hướng dẫn trước)
                // Nhưng nếu click vào item tổng quát thì xử lý mở Youtube ở đây:
                String youtubeLink = baiHatChon.getLinkBH();

                if (youtubeLink != null && !youtubeLink.isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(TrangChuActivity.this, "Lỗi: Không thể mở liên kết này", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(TrangChuActivity.this, "Bài hát này chưa có link Youtube", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 6. Các sự kiện click nút chức năng
        setupClickEvents();
    }

    // Dùng onResume để cập nhật thông tin User mỗi khi quay lại màn hình này
    @Override
    protected void onResume() {
        super.onResume();
        HienThiThongTinUser();
    }

    private void AnhXa() {
        lvBaiHat = findViewById(R.id.lvBaiHat);
        tvTenNguoiDung = findViewById(R.id.tvTenNguoiDung);
        imgProfile = findViewById(R.id.imgProfile);
        cvYeuThich = findViewById(R.id.cvYeuThich);
        imgSearch = findViewById(R.id.imgSearch);
        imgPhanHoi = findViewById(R.id.imgPhanHoi);
        imgBXH = findViewById(R.id.imgBXH);

    }

    private void setupClickEvents() {
        imgSearch.setOnClickListener(v -> {
            Intent intent = new Intent(TrangChuActivity.this, com.example.baicuoiky_nhom13.Activity.SearchActivity.class);
            startActivity(intent);
        });

        imgPhanHoi.setOnClickListener(v -> {
            Intent intent = new Intent(TrangChuActivity.this, com.example.baicuoiky_nhom13.Activity.PhanHoiActivity.class);
            startActivity(intent);
        });

        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(TrangChuActivity.this, com.example.baicuoiky_nhom13.Activity.ProfileUserActivity.class);
            startActivity(intent);
        });

        imgBXH.setOnClickListener(v -> {
            Intent intent = new Intent(TrangChuActivity.this, com.example.baicuoiky_nhom13.Activity.BangXepHangActivity.class);
            startActivity(intent);
        });

        cvYeuThich.setOnClickListener(v -> {
            Intent intent = new Intent(TrangChuActivity.this, com.example.baicuoiky_nhom13.Activity.YeuThichActivity.class);
            startActivity(intent);
        });
    }

    private void HienThiThongTinUser() {
        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();

            // Mặc định hiển thị email trước
            tvTenNguoiDung.setText(email);

            if (currentUserId != null) {
                db.collection("NGUOI_DUNG").document(currentUserId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            // Kiểm tra activity còn sống không
                            if (isFinishing() || isDestroyed()) return;

                            if (documentSnapshot.exists()) {
                                String hoTen = documentSnapshot.getString("hoTen");
                                String avatarUrl = documentSnapshot.getString("avatar");

                                if (hoTen != null && !hoTen.isEmpty()) {
                                    tvTenNguoiDung.setText(hoTen);
                                }

                                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                    Glide.with(TrangChuActivity.this)
                                            .load(avatarUrl)
                                            .placeholder(R.drawable.user)
                                            .error(R.drawable.user)
                                            .circleCrop()
                                            .into(imgProfile);
                                } else {
                                    imgProfile.setImageResource(R.drawable.user);
                                }
                            }
                        });
            }
        }
    }

    private void LayDanhSachBaiHat() {
        // Lắng nghe thay đổi thực (Realtime) từ collection BAI_HAT
        db.collection("BAI_HAT")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "Lỗi lắng nghe Firestore", error);
                            return;
                        }

                        if (value != null) {
                            // Cách xử lý an toàn nhất: Xóa list cũ, nạp lại toàn bộ
                            // Điều này tránh lỗi trùng lặp và lỗi logic khi update
                            arrBaiHat.clear();

                            for (DocumentSnapshot document : value.getDocuments()) {
                                try {
                                    BaiHat bh = document.toObject(BaiHat.class);
                                    if (bh != null) {
                                        bh.setIdBH(document.getId());
                                        arrBaiHat.add(bh);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Lỗi parse bài hát: " + e.getMessage());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
