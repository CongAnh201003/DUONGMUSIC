package com.example.baicuoiky_nhom13.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.baicuoiky_nhom13.Activity.PhanQuyenActivity;
import com.example.baicuoiky_nhom13.Activity.QL_NguoiDungActivity;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NguoiDungAdapter extends ArrayAdapter<NguoiDung> {
    Activity context;
    int resource;
    ArrayList<NguoiDung> listNguoiDung;
    private FirebaseFirestore firestore;

    public NguoiDungAdapter(Activity context, int resource, ArrayList<NguoiDung> listNguoiDung) {
        super(context, resource, listNguoiDung);
        this.context = context;
        this.resource = resource;
        this.listNguoiDung = listNguoiDung;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View customView = convertView;
        if (customView == null) {
            customView = LayoutInflater.from(context).inflate(resource, null);
        }

        ImageView imgAnhNguoiDung = customView.findViewById(R.id.imgAnhNguoiDung);
        TextView tvTenNguoiDung = customView.findViewById(R.id.tvTenNguoiDung);
        TextView tvVaiTro = customView.findViewById(R.id.tvVaiTro);
        ImageView imgXoaNguoiDung = customView.findViewById(R.id.imgXoaNguoiDung);
        ImageView imgPhanQuyen = customView.findViewById(R.id.imgPhanQuyen);

        NguoiDung nguoiDung = listNguoiDung.get(position);

        if (nguoiDung != null) {
            tvTenNguoiDung.setText(nguoiDung.getHoTen());

            // === LỖI 1 ĐÃ SỬA ===
            // So sánh chuỗi với chuỗi, vì getVaiTro() giờ trả về String
            if ("admin".equalsIgnoreCase(nguoiDung.getVaiTro())) {
                tvVaiTro.setText("Quản trị viên");
            } else {
                tvVaiTro.setText("Người dùng");
            }

            imgPhanQuyen.setOnClickListener(view -> {
                Bundle data = new Bundle();
                data.putSerializable("nd_value", nguoiDung);
                Intent phanquyenIntent = new Intent(context, PhanQuyenActivity.class);
                phanquyenIntent.putExtras(data);

                // === LỖI 2 ĐÃ SỬA ===
                // Gọi launcher đã được public trong Activity
                if (context instanceof QL_NguoiDungActivity) {
                    ((QL_NguoiDungActivity) context).themMoiTKLauncher.launch(phanquyenIntent);
                }
            });

            imgXoaNguoiDung.setOnClickListener(view -> {
                new AlertDialog.Builder(context)
                        .setTitle("Xóa Người dùng")
                        .setMessage("Bạn thực sự muốn xóa người dùng '" + nguoiDung.getHoTen() + "'? Hành động này không thể hoàn tác.")
                        .setPositiveButton("Có", (dialogInterface, i) -> xoaNguoiDungTrenFirebase(nguoiDung))
                        .setNegativeButton("Không", null)
                        .show();
            });
        }
        return customView;
    }

    private void xoaNguoiDungTrenFirebase(NguoiDung nguoiDung) {
        // === LỖI 3, 4, 5 ĐÃ SỬA ===
        // So sánh chuỗi với null, vì getId() giờ trả về String
        if (nguoiDung == null || nguoiDung.getId() == null || nguoiDung.getId().isEmpty()) {
            Toast.makeText(context, "Không thể xóa, thông tin người dùng không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        // getId() giờ trả về String, không cần chuyển đổi
        String documentId = nguoiDung.getId();

        firestore.collection("NGUOI_DUNG").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreDelete", "Đã xóa người dùng khỏi Firestore: " + documentId);
                    Toast.makeText(context, "Đã xóa người dùng thành công!", Toast.LENGTH_SHORT).show();

                    // Cập nhật lại danh sách trong Activity
                    if (context instanceof QL_NguoiDungActivity) {
                        ((QL_NguoiDungActivity) context).loadDataFromFirestore();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreDelete", "Lỗi khi xóa người dùng khỏi Firestore", e);
                    Toast.makeText(context, "Xóa người dùng thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
