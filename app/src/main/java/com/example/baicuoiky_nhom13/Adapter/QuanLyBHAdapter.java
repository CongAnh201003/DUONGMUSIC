package com.example.baicuoiky_nhom13.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.baicuoiky_nhom13.Activity.QuanLyBaiHatActivity;
import com.example.baicuoiky_nhom13.Database.MySQLiteBaiHat;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;

import java.util.ArrayList;

public class QuanLyBHAdapter extends ArrayAdapter {
    Activity context;
    int resource;
    ArrayList<BaiHat> listBaiHat;
    MySQLiteBaiHat mySQLiteBaiHat;
    String sql="";
    public QuanLyBHAdapter(Activity context,int resource,ArrayList<BaiHat> listBaiHat){
        super(context,resource);
        this.context=context;
        this.resource=resource;
        this.listBaiHat=listBaiHat;
    }

    @Override
    public int getCount() {
        return this.listBaiHat.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View customView = layoutInflater.inflate(resource, null);

        ImageView imgQLAnhBaiHat = customView.findViewById(R.id.imgQLAnhBaiHat);
        TextView tvQLTenBaiHat = customView.findViewById(R.id.tvQLTenBaiHat);
        TextView tvQLTenCaSi = customView.findViewById(R.id.tvQLTenCaSi);
        ImageView imgXoaBH = customView.findViewById(R.id.imgXoaBaiHat);

        BaiHat baiHat = listBaiHat.get(position);
        tvQLTenBaiHat.setText(baiHat.getTenBaiHat());
        tvQLTenCaSi.setText(baiHat.getCaSi());
        if (baiHat.getHinhAnh().trim().length()>0){
            Glide.with(context.getBaseContext()).load(baiHat.getHinhAnh()).into(imgQLAnhBaiHat);
        }
        mySQLiteBaiHat = new MySQLiteBaiHat(context, MySQLiteBaiHat.DATABASE_NAME, null, 1);


        imgXoaBH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa Bài hát");
                builder.setMessage("Bạn thực sự muỗn xóa bài hát này ?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            sql = "DELETE FROM BAI_HAT WHERE id='" + baiHat.getId() + "';";
                            mySQLiteBaiHat.querySQL(sql);
                            ((QuanLyBaiHatActivity) context).loadQLBH();
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
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        return customView;
    }
}
