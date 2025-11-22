package com.example.baicuoiky_nhom13.Adapter;

import android.app.Activity;
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
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BaiHatAdapter extends ArrayAdapter<BaiHat> {
    private static final String TAG = "BaiHatAdapter";
    private Activity context;
    private int resource;
    private ArrayList<BaiHat> listBaiHat;

    private FirebaseFirestore firestore;
    private String userId;

    public BaiHatAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat, String userId) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        this.firestore = FirebaseFirestore.getInstance();
        this.userId = userId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(resource, null);
            holder = new ViewHolder();
            holder.imgAnhBaiHat = convertView.findViewById(R.id.imgAnhBaiHat);
            holder.tvTenBaiHat = convertView.findViewById(R.id.tvTenBaiHat);
            holder.tvTenCaSi = convertView.findViewById(R.id.tvTenCaSi);
            holder.imgAdd = convertView.findViewById(R.id.btnChonAnh);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BaiHat baiHat = listBaiHat.get(position);

        if (baiHat != null) {
            holder.tvTenBaiHat.setText(baiHat.getTenBH());
            holder.tvTenCaSi.setText(baiHat.getTenCaSi());

            if (baiHat.getHinhAnh() != null && !baiHat.getHinhAnh().isEmpty()) {
                Glide.with(context).load(baiHat.getHinhAnh()).into(holder.imgAnhBaiHat);
            } else {
                holder.imgAnhBaiHat.setImageResource(R.drawable.album);
            }

            holder.imgAdd.setOnClickListener(view -> addSongToFavorites(baiHat, holder.imgAdd));
        }

        return convertView;
    }

    private void addSongToFavorites(BaiHat baiHat, ImageView addButton) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(context, "Vui lòng đăng nhập để sử dụng chức năng này", Toast.LENGTH_SHORT).show();
            return;
        }

        if (baiHat == null || baiHat.getIdBH() == null || baiHat.getIdBH().isEmpty()) {
            Toast.makeText(context, "Lỗi: Không thể xác định bài hát", Toast.LENGTH_SHORT).show();
            return;
        }

        String baiHatId = baiHat.getIdBH();

        DocumentReference favoriteSongRef = firestore.collection("NGUOI_DUNG").document(userId)
                .collection("YeuThich").document(baiHatId);

        favoriteSongRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Toast.makeText(context, "Bài hát đã có trong danh sách yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteSongRef.set(baiHat)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Đã thêm bài hát " + baiHatId + " vào danh sách yêu thích.");
                                Toast.makeText(context, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();

                                // === LỖI ĐÃ SỬA: Thay thế bằng icon có sẵn ===
                                addButton.setImageResource(android.R.drawable.ic_menu_save); // Đổi icon thành biểu tượng lưu
                                addButton.setEnabled(false);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Lỗi khi thêm vào yêu thích: ", e);
                                Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Log.e(TAG, "Lỗi khi kiểm tra bài hát yêu thích: ", task.getException());
                Toast.makeText(context, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class ViewHolder {
        ImageView imgAnhBaiHat;
        TextView tvTenBaiHat;
        TextView tvTenCaSi;
        ImageView imgAdd;
    }
}
