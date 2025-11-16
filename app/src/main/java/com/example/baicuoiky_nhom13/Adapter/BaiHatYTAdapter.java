package com.example.baicuoiky_nhom13.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.baicuoiky_nhom13.Database.SQLiteYeuThich;
import com.example.baicuoiky_nhom13.Model.BaiHatYT;
import com.example.baicuoiky_nhom13.R;

import java.util.ArrayList;

public class BaiHatYTAdapter extends ArrayAdapter {
    Activity context;
    int resource;
    ArrayList<BaiHatYT> listBaiHat;
    SQLiteYeuThich sqLiteYeuThich;
    String sql="";
    public BaiHatYTAdapter(Activity context,int resource,ArrayList<BaiHatYT> listBaiHat){
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
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View customView = layoutInflater.inflate(resource,null);

        ImageView imgAnhBaiHat=customView.findViewById(R.id.imgAnhBaiHat);
        TextView tvTenBaiHat=customView.findViewById(R.id.tvTenBaiHat);
        TextView tvTenCaSi=customView.findViewById(R.id.tvTenCaSi);
        ImageView imgXoaYT=customView.findViewById(R.id.imgXoaYT);
        sqLiteYeuThich=new SQLiteYeuThich(context,SQLiteYeuThich.DATABASE_NAME,null,1);

        BaiHatYT baiHat=listBaiHat.get(position);
        tvTenBaiHat.setText(baiHat.getTenBaiHat());
        tvTenCaSi.setText(baiHat.getCaSi());
        if (baiHat.getHinhAnh().trim().length()>0){
            Glide.with(context.getBaseContext()).load(baiHat.getHinhAnh()).into(imgAnhBaiHat);
        }
        imgXoaYT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa bài hát yêu thích");
                builder.setMessage("Bạn thực sự muỗn xóa bài hát này?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            sql = "DELETE FROM BAI_HAT_YT WHERE id_yt='" + baiHat.getId_bh() + "';";
                            sqLiteYeuThich.querySQL(sql);
                            ((ListFavoriteSongsActivity) context).loadBHyt();
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
