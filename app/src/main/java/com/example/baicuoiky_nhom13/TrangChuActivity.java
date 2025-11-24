package com.example.baicuoiky_nhom13;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;import android.widget.AdapterView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
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
    ImageView imgProfile;
    CardView cvYeuThich;

    // Khai báo dữ liệu
    ArrayList<BaiHat> arrBaiHat;
    BaiHatAdapter adapter;

    // Khai báo Firebase
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String currentUserId; // Có thể là Email hoặc UID tùy vào cách bạn lưu

    ImageView imgSearch;

    ImageView imgPhanHoi;

    ImageView imgTinNhan;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu); // Đảm bảo tên file XML đúng

        // 1. Khởi tạo
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ImageView imgSearch = findViewById(R.id.imgSearch);
        ImageView imgPhanHoi = findViewById(R.id.imgPhanHoi);
        ImageView imgProfile = findViewById(R.id.imgProfile);
        ImageView imgTinNhan = findViewById(R.id.imgTinNhan);



        // Kiểm tra đăng nhập
        if (currentUser != null) {
            // Do database bạn lưu ID là Email, nên ta lấy Email làm UserID để truyền vào Adapter
            currentUserId = currentUser.getEmail();
        } else {
            // Chưa đăng nhập -> Chuyển về màn hình Login (tùy bạn xử lý)
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            // finish(); return; // Nếu muốn bắt buộc đăng nhập
        }

        // 2. Ánh xạ View
        AnhXa();

        // 3. Thiết lập ListView và Adapter
        arrBaiHat = new ArrayList<>();
        // Truyền currentUserId vào Adapter để xử lý chức năng Yêu thích
        adapter = new BaiHatAdapter(this, R.layout.item_baihat, arrBaiHat, currentUserId);
        lvBaiHat.setAdapter(adapter);

        // 4. Hiển thị thông tin người dùng (Tên, Ảnh đại diện)
        HienThiThongTinUser();

        // 5. Lấy danh sách bài hát từ Firestore
        LayDanhSachBaiHat();

        // 6. Sự kiện click vào item trong ListView (Để mở link Youtube)
        lvBaiHat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaiHat baiHatChon = arrBaiHat.get(position);
                String youtubeLink = baiHatChon.getLinkBH();

                if (youtubeLink != null && !youtubeLink.isEmpty()) {
                    try {
                        // Tạo Intent để mở đường dẫn
                        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(youtubeLink));
                        // Cờ này giúp mở ứng dụng Youtube nếu có, thay vì mở tab mới trong app
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        // Phòng trường hợp link lỗi hoặc không có ứng dụng nào mở được link
                        Toast.makeText(TrangChuActivity.this, "Lỗi: Không thể mở liên kết này", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(TrangChuActivity.this, "Bài hát này chưa có link Youtube", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 3. Bắt sự kiện click
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo lệnh chuyển từ TrangChuActivity sang SearchActivity
                // Lưu ý: Nếu bạn để SearchActivity trong thư mục con Activity thì phải import đúng
                Intent intent = new Intent(TrangChuActivity.this, com.example.baicuoiky_nhom13.Activity.SearchActivity.class);
                startActivity(intent);
            }
        });


        imgPhanHoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrangChuActivity.this, com.example.baicuoiky_nhom13.Activity.PhanHoiActivity.class);
                startActivity(intent);
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrangChuActivity.this, com.example.baicuoiky_nhom13.Activity.ProfileUserActivity.class);
                startActivity(intent);
            }
        });

        imgTinNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrangChuActivity.this, com.example.baicuoiky_nhom13.Activity.ChatAIActivity.class);
                startActivity(intent);
            }
        });

        // 7. Sự kiện click vào CardView Yêu Thích

        cvYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang màn hình YeuThichActivity
                Intent intent = new Intent(TrangChuActivity.this, com.example.baicuoiky_nhom13.Activity.YeuThichActivity.class);
                startActivity(intent);
                Toast.makeText(TrangChuActivity.this, "Mở danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AnhXa() {
        lvBaiHat = findViewById(R.id.lvBaiHat);
        tvTenNguoiDung = findViewById(R.id.tvTenNguoiDung);
        imgProfile = findViewById(R.id.imgProfile);
        cvYeuThich = findViewById(R.id.cvYeuThich);
        imgSearch = findViewById(R.id.imgSearch);
        imgPhanHoi = findViewById(R.id.imgPhanHoi);
        imgProfile = findViewById(R.id.imgProfile);
    }

    private void HienThiThongTinUser() {
        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();

            // Hiển thị tạm email lên tên người dùng
            tvTenNguoiDung.setText(email);

            // Nếu muốn lấy tên thật và avatar từ collection NGUOI_DUNG:
            if (currentUserId != null) {
                db.collection("NGUOI_DUNG").document(currentUserId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String hoTen = documentSnapshot.getString("hoTen");
                                // String avatarUrl = documentSnapshot.getString("avatar"); // Nếu có lưu avatar

                                if (hoTen != null) tvTenNguoiDung.setText(hoTen);
                                // if (avatarUrl != null) Glide.with(this).load(avatarUrl).into(imgProfile);
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
                            // Dùng vòng lặp qua các thay đổi để tối ưu hiệu suất
                            for (DocumentChange dc : value.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        // Khi có bài hát mới thêm vào DB
                                        BaiHat baiHatMoi = dc.getDocument().toObject(BaiHat.class);
                                        // Gán ID document vào object để dễ xử lý về sau
                                        baiHatMoi.setIdBH(dc.getDocument().getId());
                                        arrBaiHat.add(baiHatMoi);
                                        break;

                                    case MODIFIED:
                                        // Khi có bài hát bị sửa -> Cập nhật lại list
                                        // (Code đơn giản: clear list load lại, hoặc tìm index để replace)
                                        // Ở đây mình làm đơn giản là clear load lại cho dễ hiểu:
                                        arrBaiHat.clear();
                                        for (DocumentChange refreshDc : value.getDocumentChanges()) {
                                            if (refreshDc.getType() == DocumentChange.Type.ADDED || refreshDc.getType() == DocumentChange.Type.MODIFIED)
                                                // Logic này cần viết lại full query nếu muốn update chuẩn
                                                // Để đơn giản cho bài cuối kỳ, khi Modified ta có thể bỏ qua hoặc reload activity
                                                break;
                                        }
                                        break;

                                    case REMOVED:
                                        // Xử lý xóa
                                        break;
                                }
                            }

                            // NẾU LÀM CÁCH ĐƠN GIẢN NHẤT (KHÔNG CẦN REALTIME TYPE):
                            // Xóa list cũ, add toàn bộ list mới
                            arrBaiHat.clear();
                            for (com.google.firebase.firestore.DocumentSnapshot document : value.getDocuments()) {
                                BaiHat bh = document.toObject(BaiHat.class);
                                if (bh != null) {
                                    bh.setIdBH(document.getId()); // Lấy ID document set vào object
                                    arrBaiHat.add(bh);
                                }
                            }

                            adapter.notifyDataSetChanged(); // Cập nhật giao diện
                        }
                    }
                });
    }
}
