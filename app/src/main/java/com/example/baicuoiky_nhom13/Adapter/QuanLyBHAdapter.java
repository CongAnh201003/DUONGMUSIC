package com.example.baicuoiky_nhom13.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.baicuoiky_nhom13.Activity.EditSongActivity;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class QuanLyBHAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<BaiHat> listBaiHat;
    private FirebaseFirestore firestore;

    public QuanLyBHAdapter(Context context, int layout, ArrayList<BaiHat> listBaiHat) {
        this.context = context;
        this.layout = layout;
        this.listBaiHat = listBaiHat;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public int getCount() {
        return listBaiHat.size();
    }

    @Override
    public Object getItem(int position) {
        return listBaiHat.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // ViewHolder chứa các thành phần giao diện của 1 dòng
    private class ViewHolder {
        ImageView imgHinh, imgEdit, imgDelete; // Đã thêm nút Sửa và Xóa
        TextView tvTenBH, tvTenCaSi, tvTheLoai;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);

            // Ánh xạ view từ layout_item_baihat.xml
            holder.imgHinh = convertView.findViewById(R.id.imgItemHinh);
            holder.tvTenBH = convertView.findViewById(R.id.tvItemTenBaiHat);
            holder.tvTenCaSi = convertView.findViewById(R.id.tvItemTenCaSi);
            holder.tvTheLoai = convertView.findViewById(R.id.tvItemTheLoai);

            // Ánh xạ 2 nút chức năng mới
            holder.imgEdit = convertView.findViewById(R.id.imgEdit);
            holder.imgDelete = convertView.findViewById(R.id.imgDelete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Lấy bài hát ở vị trí hiện tại
        BaiHat baiHat = listBaiHat.get(position);

        // Gán dữ liệu text
        holder.tvTenBH.setText(baiHat.getTenBH());
        holder.tvTenCaSi.setText(baiHat.getTenCaSi());
        holder.tvTheLoai.setText(baiHat.getTheLoai());

        // Load ảnh bằng Glide
        if (baiHat.getHinhAnh() != null && !baiHat.getHinhAnh().isEmpty()) {
            Glide.with(context)
                    .load(baiHat.getHinhAnh())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imgHinh);
        } else {
            holder.imgHinh.setImageResource(R.drawable.ic_launcher_background);
        }

        // --- XỬ LÝ SỰ KIỆN NÚT SỬA ---
        holder.imgEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditSongActivity.class);
            // Truyền đối tượng BaiHat sang màn hình sửa
            intent.putExtra("SONG_DATA", baiHat);

            // Gọi startActivityForResult thông qua context (được ép kiểu về Activity)
            // Request code 100 là một số ngẫu nhiên để định danh
            ((Activity) context).startActivityForResult(intent, 100);
        });

        // --- XỬ LÝ SỰ KIỆN NÚT XÓA ---
        holder.imgDelete.setOnClickListener(v -> {
            showConfirmDeleteDialog(baiHat.getIdBH(), baiHat.getTenBH(), position);
        });

        return convertView;
    }

    // Hàm hiển thị hộp thoại xác nhận xóa
    private void showConfirmDeleteDialog(String songId, String songName, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa bài hát \"" + songName + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteSongFromFirestore(songId, position);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Hàm thực hiện xóa trên Firestore
    private void deleteSongFromFirestore(String songId, int position) {
        firestore.collection("BAI_HAT").document(songId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Đã xóa bài hát thành công!", Toast.LENGTH_SHORT).show();
                    // Xóa khỏi danh sách hiển thị để cập nhật giao diện ngay lập tức
                    listBaiHat.remove(position);
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
