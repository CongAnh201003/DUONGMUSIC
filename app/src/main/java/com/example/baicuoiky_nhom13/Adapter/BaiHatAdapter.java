package com.example.baicuoiky_nhom13.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.baicuoiky_nhom13.Database.SQLiteYeuThich;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.example.baicuoiky_nhom13.TrangChuActivity;

import java.util.ArrayList;

public class BaiHatAdapter extends ArrayAdapter {
    Activity context;
    int resource;
    ArrayList<BaiHat> listBaiHat,listBHBackup, listBHFilter;;
    SQLiteYeuThich sqLiteYeuThich;
    String sql="";
    public BaiHatAdapter(Activity context,int resource,ArrayList<BaiHat> listBaiHat){
        super(context,resource);
        this.context=context;
        this.resource=resource;
        this.listBaiHat=listBaiHat;
        this.listBHBackup = new ArrayList<>(listBaiHat);  // Lưu danh sách bài hát gốc để tìm kiếm
        this.listBHFilter = new ArrayList<>(listBaiHat);  // Danh sách bài hát đã lọc

    }

    @Override
    public int getCount() {
        return this.listBaiHat.size();
    }
    @Override
    public BaiHat getItem(int position) {
        return listBaiHat.get(position);
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View customView = layoutInflater.inflate(resource,null);

        ImageView imgAnhBaiHat=customView.findViewById(R.id.imgAnhBaiHat);
        TextView tvTenBaiHat=customView.findViewById(R.id.tvTenBaiHat);
        TextView tvTenCaSi=customView.findViewById(R.id.tvTenCaSi);
        ImageView imgAdd=customView.findViewById(R.id.imgAdd);
        ImageView imgXoaYT=customView.findViewById(R.id.imgXoaYT);
        sqLiteYeuThich=new SQLiteYeuThich(context,SQLiteYeuThich.DATABASE_NAME,null,1);

        BaiHat baiHat=listBaiHat.get(position);
        tvTenBaiHat.setText(baiHat.getTenBaiHat());
        tvTenCaSi.setText(baiHat.getCaSi());
        if (baiHat.getHinhAnh().trim().length()>0){
            Glide.with(context.getBaseContext()).load(baiHat.getHinhAnh()).into(imgAnhBaiHat);
        }
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenBH=baiHat.getTenBaiHat();
                String casi=baiHat.getCaSi();
                String linkAnh=baiHat.getHinhAnh();
                String linkBH=baiHat.getLinkBaiHat();
                int id=((TrangChuActivity) context).idND();
                int dem=sqLiteYeuThich.loadBH(tenBH,id);
                if (dem<=0){
                    sql="INSERT INTO BAI_HAT_YT (id_nd,tenBH_yt,CaSi_yt,hinhAnh_yt,linkBH_yt) VALUES ( "+
                            id+", '"+
                            tenBH+"', '"+
                            casi+"', '"+
                            linkAnh+"', '"+
                            linkBH+"');";
                    sqLiteYeuThich.querySQL(sql);
                    Toast.makeText(context,"Đã thêm vào danh sách yêu thích",Toast.LENGTH_SHORT).show();
                    imgAdd.setImageResource(R.drawable.baseline_expand_circle_down_24);
                }
                else {
                    Toast.makeText(context,"Đã có trong danh sách yêu thích",Toast.LENGTH_SHORT).show();
                }



            }
        });

        return customView;
    }
    // Tìm kiếm bài hát theo tên và ca sĩ
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                String query = constraint.toString().trim().toLowerCase();

                // Nếu không có từ khóa tìm kiếm, hiển thị tất cả bài hát
                if (query.length() < 1) {
                    listBHFilter = listBHBackup;
                } else {
                    listBHFilter = new ArrayList<>();
                    // Lọc bài hát theo tên bài hát và ca sĩ
                    for (BaiHat baiHat : listBHBackup) {
                        if (baiHat.getTenBaiHat().toLowerCase().contains(query) ||
                                baiHat.getCaSi().toLowerCase().contains(query)) {
                            listBHFilter.add(baiHat);
                        }
                    }
                }

                filterResults.values = listBHFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // Cập nhật danh sách đã lọc và thông báo cho Adapter để hiển thị lại
                listBHFilter = (ArrayList<BaiHat>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
