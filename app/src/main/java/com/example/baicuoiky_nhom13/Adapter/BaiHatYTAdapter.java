package com.example.baicuoiky_nhom13.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.baicuoiky_nhom13.Activity.ListFavoriteSongsActivity;
import com.example.baicuoiky_nhom13.Model.BaiHat; // Sửa thành Model BaiHat chuẩn
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

// Đổi BaiHatYT thành BaiHat để thống nhất
public class BaiHatYTAdapter extends ArrayAdapter<BaiHat> {
    private static final String TAG = "BaiHatYTAdapter";
    private Activity context;
    private int resource;
    private ArrayList<BaiHat> listBaiHat;

    // Thêm FirebaseFirestore và userId
    private FirebaseFirestore firestore;
    private String userId;

    public BaiHatYTAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat, String userId) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        // Khởi tạo Firestore và lấy userId
        this.firestore = FirebaseFirestore.getInstance();
        this.userId = userId;
    }

    // ArrayAdapter đã có getCount(), không cần override lại

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(resource, null);
        }

        // Ánh xạ các View trong item layout
        ImageView imgAnhBaiHat = convertView.findViewById(R.id.imgAnhBaiHat);
        TextView tvTenBaiHat = convertView.findViewById(R.id.tvTenBaiHat);
        TextView tvTenCaSi = convertView.findViewById(R.id.tvTenCaSi);
        ImageView imgXoaYT = convertView.findViewById(R.id.imgXoaYT);

        // Lấy đối tượng BaiHat ở vị trí hiện tại
        BaiHat baiHat = listBaiHat.get(position);

        if (baiHat != null) {
            // Hiển thị thông tin bài hát
            tvTenBaiHat.setText(baiHat.getTenBH());
            tvTenCaSi.setText(baiHat.getTenCaSi());
            // Dùng Glide để hiển thị ảnh từ URL
            if (baiHat.getHinhAnh() != null && !baiHat.getHinhAnh().isEmpty()) {
                Glide.with(context).load(baiHat.getHinhAnh()).into(imgAnhBaiHat);
            } else {
                imgAnhBaiHat.setImageResource(R.drawable.album); // Ảnh mặc định
            }

            // Thiết lập sự kiện click cho nút xóa (bỏ thích)
            imgXoaYT.setOnClickListener(view -> showDeleteConfirmationDialog(baiHat));
        }
        return convertView;
    }

    /**
     * Hiển thị hộp thoại xác nhận trước khi xóa (bỏ thích) bài hát.
     * @param baiHat Bài hát cần xóa.
     */
    private void showDeleteConfirmationDialog(BaiHat baiHat) {
        new AlertDialog.Builder(context)
                .setTitle("Bỏ thích bài hát")
                .setMessage("Bạn có chắc muốn bỏ thích bài hát '" + baiHat.getTenBH() + "'?")
                .setPositiveButton("Có", (dialogInterface, i) -> {
                    // Gọi hàm xóa bài hát khỏi Firestore
                    removeSongFromFavorites(baiHat);
                })
                .setNegativeButton("Không", null)
                .show();
    }

    /**
     * Xóa document bài hát khỏi sub-collection "YeuThich" trên Firestore.
     * @param baiHat Bài hát cần xóa.
     */
    private void removeSongFromFavorites(BaiHat baiHat) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(context, "Lỗi: Không xác định được người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (baiHat == null || baiHat.getIdBH() == null || baiHat.getIdBH().isEmpty()) {
            Toast.makeText(context, "Lỗi: Không xác định được bài hát.", Toast.LENGTH_SHORT).show();
            return;
        }

        // ID của document bài hát trong sub-collection "YeuThich"
        String baiHatId = baiHat.getIdBH();

        firestore.collection("NGUOI_DUNG").document(userId)
                .collection("YeuThich").document(baiHatId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Đã xóa bài hát " + baiHatId + " khỏi danh sách yêu thích.");
                    Toast.makeText(context, "Đã bỏ thích", Toast.LENGTH_SHORT).show();

                    // Cập nhật lại giao diện bằng cách gọi hàm public trong Activity
                    if (context instanceof ListFavoriteSongsActivity) {
                        ((ListFavoriteSongsActivity) context).loadFavoriteSongs();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi bỏ thích bài hát: ", e);
                    Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
