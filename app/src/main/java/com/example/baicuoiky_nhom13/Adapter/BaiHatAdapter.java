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

    // Biến này dùng để biết adapter đang được dùng ở đâu
    private boolean isFavoriteList = false;

    // Constructor 1: Dùng cho Trang Chủ
    public BaiHatAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat, String userId) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        this.firestore = FirebaseFirestore.getInstance();
        this.userId = userId;
        this.isFavoriteList = false; // Mặc định là false (Trang chủ)
    }

    // Constructor 2: Dùng cho màn hình Yêu Thích (truyền true để ẩn nút tim hoặc xử lý khác)
    public BaiHatAdapter(Activity context, int resource, ArrayList<BaiHat> listBaiHat, String userId, boolean isFavoriteList) {
        super(context, resource, listBaiHat);
        this.context = context;
        this.resource = resource;
        this.listBaiHat = listBaiHat;
        this.firestore = FirebaseFirestore.getInstance();
        this.userId = userId;
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

            // Load ảnh bài hát
            if (baiHat.getHinhAnh() != null && !baiHat.getHinhAnh().isEmpty()) {
                Glide.with(context).load(baiHat.getHinhAnh()).into(holder.imgAnhBaiHat);
            } else {
                holder.imgAnhBaiHat.setImageResource(R.drawable.album);
            }

            // --- XỬ LÝ NÚT TIM (LIKE/UNLIKE) ---
            if (isFavoriteList) {
                // 1. Nếu đang ở màn hình Yêu Thích:
                // Bạn có thể chọn: Ẩn nút đi HOẶC hiện nút Xóa.
                // Ở đây mình chọn Ẩn đi cho gọn, hoặc bạn có thể hiện tim đỏ luôn.
                holder.imgFavorite.setVisibility(View.GONE);

            } else {
                // 2. Nếu ở Trang Chủ:
                holder.imgFavorite.setVisibility(View.VISIBLE);

                // Kiểm tra trạng thái bài hát để set icon ban đầu (Đỏ hay Trắng)
                checkFavoriteStatus(baiHat.getIdBH(), holder.imgFavorite);

                // Bắt sự kiện bấm vào tim
                holder.imgFavorite.setOnClickListener(view -> {
                    toggleFavorite(baiHat, holder.imgFavorite);
                });
            }
        }

        return convertView;
    }

    // Hàm kiểm tra trạng thái (Tim đỏ hay trắng) khi mới load danh sách
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
                })
                .addOnFailureListener(e -> {
                    // Lỗi thì cứ để mặc định là chưa thích
                    icon.setImageResource(R.drawable.not_like);
                    icon.setTag("unliked");
                });
    }

    // Hàm xử lý: Thích hoặc Bỏ thích khi bấm vào
    private void toggleFavorite(BaiHat baiHat, ImageView icon) {
        if (userId == null) {
            Toast.makeText(context, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        String status = (String) icon.getTag(); // Lấy trạng thái hiện tại
        DocumentReference docRef = firestore.collection("NGUOI_DUNG").document(userId)
                .collection("YeuThich").document(baiHat.getIdBH());

        if ("liked".equals(status)) {
            // Đang thích -> Bấm vào là BỎ THÍCH (Xóa khỏi DB)
            docRef.delete().addOnSuccessListener(aVoid -> {
                icon.setImageResource(R.drawable.not_like); // Đổi về tim trắng
                icon.setTag("unliked");
                Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Lỗi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            // Chưa thích -> Bấm vào là THÍCH (Thêm vào DB)
            docRef.set(baiHat).addOnSuccessListener(aVoid -> {
                icon.setImageResource(R.drawable.like); // Đổi thành tim đỏ
                icon.setTag("liked");
                Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Lỗi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    static class ViewHolder {
        ImageView imgAnhBaiHat;
        TextView tvTenBaiHat;
        TextView tvTenCaSi;
        ImageView imgFavorite; // Đổi tên biến cho dễ hiểu (trước là imgAdd)
    }
}
