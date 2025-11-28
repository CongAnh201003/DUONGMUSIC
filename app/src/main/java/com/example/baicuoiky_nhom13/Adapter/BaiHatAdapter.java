package com.example.baicuoiky_nhom13.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BaiHatAdapter extends ArrayAdapter<BaiHat> {
    private Activity context;
    private int resource;
    private ArrayList<BaiHat> listBaiHat;
    private FirebaseFirestore firestore;
    private String userId;
    private boolean isFavoriteList = false;

    public BaiHatAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat, String userId) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        this.userId = userId;
        this.firestore = FirebaseFirestore.getInstance();
        this.isFavoriteList = false;
    }

    public BaiHatAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat, String userId, boolean isFavoriteList) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        this.userId = userId;
        this.firestore = FirebaseFirestore.getInstance();
        this.isFavoriteList = isFavoriteList;
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
            holder.imgFavorite = convertView.findViewById(R.id.btnChonAnh); // Nút trái tim
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BaiHat baiHat = listBaiHat.get(position);

        if (baiHat != null) {
            holder.tvTenBaiHat.setText(baiHat.getTenBH());
            holder.tvTenCaSi.setText(baiHat.getTenCaSi());

            if (baiHat.getHinhAnh() != null && !baiHat.getHinhAnh().isEmpty()) {
                Glide.with(context).load(baiHat.getHinhAnh()).placeholder(R.drawable.user).into(holder.imgAnhBaiHat);
            } else {
                holder.imgAnhBaiHat.setImageResource(R.drawable.user);
            }

            // --- Xử lý nút Yêu thích ---
            if (userId == null) {
                holder.imgFavorite.setVisibility(View.GONE);
            } else {
                holder.imgFavorite.setVisibility(View.VISIBLE);
                checkFavoriteStatus(baiHat.getIdBH(), holder.imgFavorite);
                holder.imgFavorite.setOnClickListener(v -> toggleFavorite(baiHat, holder.imgFavorite));
            }

            // --- QUAN TRỌNG: CLICK VÀO ITEM ĐỂ MỞ YOUTUBE VÀ TĂNG VIEW ---
            convertView.setOnClickListener(v -> {
                // 1. Tăng view trên Firebase (Chạy ngầm)
                if (baiHat.getIdBH() != null) {
                    firestore.collection("BAI_HAT").document(baiHat.getIdBH())
                            .update("luotXem", FieldValue.increment(1))
                            .addOnFailureListener(e -> Log.e("Ranking", "Lỗi tăng view: " + e.getMessage()));
                }

                // 2. Mở Youtube
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baiHat.getLinkBH()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Không thể mở link bài hát: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return convertView;
    }

    private void checkFavoriteStatus(String baiHatId, ImageView icon) {
        if (userId == null || baiHatId == null) return;
        firestore.collection("NGUOI_DUNG").document(userId)
                .collection("YeuThich").document(baiHatId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Đã thích -> Hiện tim đỏ
                        icon.setImageResource(R.drawable.like);
                        icon.setTag("liked");
                    } else {
                        // Chưa thích -> Hiện tim trắng (not_like)
                        icon.setImageResource(R.drawable.not_like);
                        icon.setTag("unliked");
                    }
                });
    }

    private void toggleFavorite(BaiHat baiHat, ImageView icon) {
        String status = (String) icon.getTag();
        DocumentReference docRef = firestore.collection("NGUOI_DUNG").document(userId)
                .collection("YeuThich").document(baiHat.getIdBH());

        if ("liked".equals(status)) {
            docRef.delete().addOnSuccessListener(aVoid -> {
                icon.setImageResource(R.drawable.not_like); // Đổi về tim trắng
                icon.setTag("unliked");
                Toast.makeText(context, "Đã bỏ thích", Toast.LENGTH_SHORT).show();
            });
        } else {
            docRef.set(baiHat).addOnSuccessListener(aVoid -> {
                icon.setImageResource(R.drawable.like); // Đổi thành tim đỏ
                icon.setTag("liked");
                Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            });
        }
    }

    static class ViewHolder {
        ImageView imgAnhBaiHat;
        TextView tvTenBaiHat;
        TextView tvTenCaSi;
        ImageView imgFavorite;
    }
}
