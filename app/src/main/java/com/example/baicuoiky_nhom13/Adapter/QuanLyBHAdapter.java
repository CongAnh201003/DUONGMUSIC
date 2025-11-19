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
import com.example.baicuoiky_nhom13.Activity.QuanLyBaiHatActivity;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
// Thêm import cho Firestore
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

// Sửa lại class signature cho đúng chuẩn
public class QuanLyBHAdapter extends ArrayAdapter<BaiHat> {
    private static final String TAG = "QuanLyBHAdapter";
    private Activity context;
    private int resource;
    private ArrayList<BaiHat> listBaiHat;

    // Thêm FirebaseFirestore
    private FirebaseFirestore firestore;

    // Sửa lại constructor cho đúng chuẩn và khởi tạo Firestore
    public QuanLyBHAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        this.firestore = FirebaseFirestore.getInstance();
    }

    // ArrayAdapter đã có getCount(), không cần override lại

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Tối ưu hóa ListView bằng ViewHolder pattern
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(resource, null);
            holder = new ViewHolder();
            holder.imgQLAnhBaiHat = convertView.findViewById(R.id.imgQLAnhBaiHat);
            holder.tvQLTenBaiHat = convertView.findViewById(R.id.tvQLTenBaiHat);
            holder.tvQLTenCaSi = convertView.findViewById(R.id.tvQLTenCaSi);
            holder.imgXoaBH = convertView.findViewById(R.id.imgXoaBaiHat);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Lấy đối tượng BaiHat ở vị trí hiện tại
        BaiHat baiHat = listBaiHat.get(position);

        if (baiHat != null) {
            // === LỖI ĐÃ SỬA: Dùng đúng tên phương thức từ Model ===
            holder.tvQLTenBaiHat.setText(baiHat.getTenBH());
            holder.tvQLTenCaSi.setText(baiHat.getTenCaSi());

            // Dùng Glide để hiển thị ảnh từ URL
            if (baiHat.getHinhAnh() != null && !baiHat.getHinhAnh().isEmpty()) {
                Glide.with(context).load(baiHat.getHinhAnh()).into(holder.imgQLAnhBaiHat);
            } else {
                holder.imgQLAnhBaiHat.setImageResource(R.drawable.album); // Ảnh mặc định
            }

            // Thiết lập sự kiện click cho nút xóa
            holder.imgXoaBH.setOnClickListener(view -> showDeleteConfirmationDialog(baiHat));
        }

        return convertView;
    }

    /**
     * Hiển thị hộp thoại xác nhận trước khi xóa bài hát.
     */
    private void showDeleteConfirmationDialog(BaiHat baiHat) {
        new AlertDialog.Builder(context)
                .setTitle("Xóa Bài hát")
                .setMessage("Bạn thực sự muốn xóa bài hát '" + baiHat.getTenBH() + "'?")
                .setPositiveButton("Có", (dialogInterface, i) -> {
                    deleteSongFromFirestore(baiHat);
                })
                .setNegativeButton("Không", null)
                .show();
    }

    /**
     * Xóa document bài hát khỏi collection "BaiHat" trên Firestore.
     */
    private void deleteSongFromFirestore(BaiHat baiHat) {
        // === LỖI ĐÃ SỬA: Dùng getIdBH() để lấy ID của document ===
        if (baiHat == null || baiHat.getIdBH() == null || baiHat.getIdBH().isEmpty()) {
            Toast.makeText(context, "Lỗi: Không thể xác định bài hát để xóa.", Toast.LENGTH_SHORT).show();
            return;
        }
        String songIdToDelete = baiHat.getIdBH();

        firestore.collection("BaiHat").document(songIdToDelete)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Đã xóa bài hát: " + songIdToDelete);
                    Toast.makeText(context, "Xóa bài hát thành công!", Toast.LENGTH_SHORT).show();

                    // === LỖI ĐÃ SỬA: Gọi hàm load mới trong Activity ===
                    // Cập nhật lại danh sách bằng cách gọi hàm public trong QuanLyBaiHatActivity
                    if (context instanceof QuanLyBaiHatActivity) {
                        ((QuanLyBaiHatActivity) context).loadSongsFromFirestore();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi xóa bài hát: ", e);
                    Toast.makeText(context, "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // Lớp ViewHolder để tối ưu hiệu suất ListView
    static class ViewHolder {
        ImageView imgQLAnhBaiHat;
        TextView tvQLTenBaiHat;
        TextView tvQLTenCaSi;
        ImageView imgXoaBH;
    }
}
