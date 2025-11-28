package com.example.baicuoiky_nhom13.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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

    // Biến cờ kiểm tra xem có phải đang ở màn hình Yêu Thích không
    private boolean isFavoriteList = false;

    // Biến cờ bật chế độ xếp hạng (hiện số 1, 2, 3...)
    private boolean isRanking = false;

    public void setRanking(boolean ranking) {
        this.isRanking = ranking;
    }

    // --- CONSTRUCTOR 1: Dùng cho Trang Chủ, Bảng Xếp Hạng (4 tham số) ---
    public BaiHatAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat, String userId) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        this.userId = userId;
        this.firestore = FirebaseFirestore.getInstance();
        this.isFavoriteList = false; // Mặc định là false
    }

    // --- CONSTRUCTOR 2: Dùng cho Yêu Thích, Tìm Kiếm (5 tham số - ĐÂY LÀ CÁI BẠN THIẾU) ---
    public BaiHatAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat, String userId, boolean isFavoriteList) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        this.userId = userId;
        this.firestore = FirebaseFirestore.getInstance();
        this.isFavoriteList = isFavoriteList; // Gán giá trị truyền vào
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            // Đảm bảo dùng đúng item_baihat
            convertView = layoutInflater.inflate(resource, null);

            holder = new ViewHolder();
            holder.imgAnhBaiHat = convertView.findViewById(R.id.imgAnhBaiHat);
            holder.tvTenBaiHat = convertView.findViewById(R.id.tvTenBaiHat);
            holder.tvTenCaSi = convertView.findViewById(R.id.tvTenCaSi);
            holder.imgFavorite = convertView.findViewById(R.id.btnLove);
            holder.tvRank = convertView.findViewById(R.id.tvRank); // Ánh xạ số thứ tự
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BaiHat baiHat = listBaiHat.get(position);

        if (baiHat != null) {
            holder.tvTenBaiHat.setText(baiHat.getTenBH());
            holder.tvTenCaSi.setText(baiHat.getTenCaSi());

            // Load ảnh
            if (baiHat.getHinhAnh() != null && !baiHat.getHinhAnh().isEmpty()) {
                Glide.with(context).load(baiHat.getHinhAnh())
                        .placeholder(R.drawable.user)
                        .into(holder.imgAnhBaiHat);
            } else {
                holder.imgAnhBaiHat.setImageResource(R.drawable.user);
            }

            // --- LOGIC HIỆN SỐ RANK (Cho BXH) ---
            if (isRanking) {
                holder.tvRank.setVisibility(View.VISIBLE);
                holder.tvRank.setText(String.valueOf(position + 1));

                // Tô màu Top 1, 2, 3
                if (position == 0) holder.tvRank.setTextColor(Color.YELLOW);
                else if (position == 1) holder.tvRank.setTextColor(Color.LTGRAY);
                else if (position == 2) holder.tvRank.setTextColor(Color.parseColor("#CD7F32"));
                else holder.tvRank.setTextColor(Color.WHITE);
            } else {
                holder.tvRank.setVisibility(View.GONE);
            }

            // --- LOGIC YÊU THÍCH ---
            if (userId == null) {
                // Nếu chưa đăng nhập thì ẩn nút tim
                holder.imgFavorite.setVisibility(View.GONE);
            } else {
                holder.imgFavorite.setVisibility(View.VISIBLE);

                if (isFavoriteList) {
                    // Nếu đang ở màn hình Yêu Thích: Mặc định là tim đỏ (vì đã thích rồi mới vào đây)
                    // Hoặc bạn có thể set icon thùng rác nếu muốn chức năng xóa
                    holder.imgFavorite.setImageResource(R.drawable.like);
                    holder.imgFavorite.setTag("liked");
                } else {
                    // Nếu ở Trang Chủ/BXH: Phải kiểm tra trạng thái từ Firebase
                    checkFavoriteStatus(baiHat.getIdBH(), holder.imgFavorite);
                }

                // Sự kiện click tim
                holder.imgFavorite.setOnClickListener(v -> toggleFavorite(baiHat, holder.imgFavorite));
            }

            // Logic click item -> Mở Youtube & Tăng view
            convertView.setOnClickListener(v -> {
                // Tăng view
                if (baiHat.getIdBH() != null) {
                    firestore.collection("BAI_HAT").document(baiHat.getIdBH())
                            .update("luotXem", FieldValue.increment(1));
                }
                // Mở Youtube
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baiHat.getLinkBH()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Lỗi mở link", Toast.LENGTH_SHORT).show();
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
            // Bỏ thích -> Xóa khỏi DB
            docRef.delete().addOnSuccessListener(aVoid -> {
                if (isFavoriteList) {
                    // Nếu đang ở màn hình Yêu Thích, xóa xong thì xóa luôn item khỏi list hiển thị
                    listBaiHat.remove(baiHat);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Đã xóa khỏi danh sách", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu ở Trang Chủ, chỉ đổi icon
                    icon.setImageResource(R.drawable.not_like);
                    icon.setTag("unliked");
                    Toast.makeText(context, "Đã bỏ thích", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Thích -> Thêm vào DB
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
        TextView tvRank;
    }
}
