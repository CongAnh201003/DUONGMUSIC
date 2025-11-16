package com.example.baicuoiky_nhom13.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.baicuoiky_nhom13.Database.MySQLite;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;

import java.util.ArrayList;

public class NguoiDungAdapter extends ArrayAdapter {
    Activity context;
    int resource;
    ArrayList<NguoiDung> listNguoiDung;
    MySQLite mySQLite;
    String sql="";
    public NguoiDungAdapter(Activity context,int resource,ArrayList<NguoiDung> listNguoiDung){
        super(context,resource);
        this.context=context;
        this.resource=resource;
        this.listNguoiDung=listNguoiDung;
    }

    @Override
    public int getCount() {
        return this.listNguoiDung.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View customView = layoutInflater.inflate(resource, null);

        ImageView imgAnhNguoiDung = customView.findViewById(R.id.imgAnhNguoiDung);
        TextView tvTenNguoiDung = customView.findViewById(R.id.tvTenNguoiDung);
        TextView tvVaiTro = customView.findViewById(R.id.tvVaiTro);
        ImageView imgXoaNguoiDung = customView.findViewById(R.id.imgXoaNguoiDung);
        ImageView imgPhanQuyen = customView.findViewById(R.id.imgPhanQuyen);
        mySQLite = new MySQLite(context, MySQLite.DATABASE_NAME, null, 1);

        NguoiDung nguoiDung = listNguoiDung.get(position);
        tvTenNguoiDung.setText(nguoiDung.getHoTen());
        if (nguoiDung.getVaiTro() == 1) {
            tvVaiTro.setText("Quản trị viên");
        } else {
            tvVaiTro.setText("Người dùng");
        }

        imgPhanQuyen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle data = new Bundle();
                data.putSerializable("nd_value", nguoiDung);
                Intent phanquyenIntent = new Intent(context, PhanQuyenActivity.class);
                phanquyenIntent.putExtras(data);
                context.startActivityForResult(phanquyenIntent, 123);
            }
        });
        imgXoaNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa Người dùng");
                builder.setMessage("Bạn thực sự muỗn xóa người dùng này?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            sql = "DELETE FROM NGUOI_DUNG WHERE id='" + nguoiDung.getId() + "';";
                            mySQLite.querySQL(sql);
                            ((QL_NguoiDungActivity) context).loadData();
                            dialogInterface.dismiss();
                        } catch (Exception e) {
                            Log.d("Lỗi DELETE SQL", e.toString());
                            Toast.makeText(context, "Lỗi DELETE CSDL", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
            }
        });


        return customView;
    }
}
