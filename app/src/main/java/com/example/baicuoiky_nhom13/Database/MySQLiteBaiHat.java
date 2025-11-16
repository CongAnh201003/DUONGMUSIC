package com.example.baicuoiky_nhom13.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;

import java.util.ArrayList;

public class MySQLiteBaiHat extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="baihat.sql";

    public MySQLiteBaiHat(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        /*String sql1="DROP TABLE BAI_HAT;";
        querySQL(sql1);*/
        String sql="CREATE TABLE IF NOT EXISTS "+
                "BAI_HAT ("+
                "id integer primary key autoincrement, "+
                "tenBH varchar(255), "+
                "CaSi varchar(255), " +
                "hinhAnh varchar(255), "+
                "linkBH varchar(255)); ";

        querySQL(sql);
        String link="android.resource://"+R.class.getPackage().getName()+"/";
        String linkBH="android.resource://"+ R.class.getPackage().getName()+"/";

        sql="SELECT COUNT(id) FROM BAI_HAT;";
        int id=0;
        Cursor cursor=getDataFromSQL(sql);
        while (cursor.moveToNext()){
            id=cursor.getInt(0);
        }
        if (id<=0){
            String query1="INSERT INTO BAI_HAT(tenBH,CaSi,hinhAnh,linkBH) VALUES ('Đi giữa trời rực rỡ','Lan Hương','"+"https://avatar-ex-swe.nixcdn.com/song/2024/08/15/n/m/F/k/1723689056060_640.jpg"+"', '"+
                    linkBH+ R.raw.digiuatroirucro+"');";
            querySQL(query1);
            String query="INSERT INTO BAI_HAT(tenBH,CaSi,hinhAnh,linkBH) VALUES ('TALAGI','DANGRANGTO and KIDSAI','"+"https://i.ytimg.com/vi/x6Ueczc9_I0/maxresdefault.jpg"+"', '"+
                    linkBH+R.raw.talagi+"');";
            querySQL(query);
            String query3="INSERT INTO BAI_HAT(tenBH,CaSi,hinhAnh,linkBH) VALUES ('Tại vì sao','MCK','"+"https://avatar-ex-swe.nixcdn.com/song/2023/03/03/e/d/0/3/1677809278739_640.jpg"+"', '"+
                    linkBH+R.raw.taivisao+"');";
            querySQL(query3);
            sql="INSERT INTO BAI_HAT(tenBH,CaSi,hinhAnh,linkBH) VALUES ('Thao Thức','Dangrangto & North & Vxllish','"+"https://photo-resize-zmp3.zadn.vn/w600_r1x1_jpeg/cover/7/8/4/9/7849e102b98422764aec6991e69402a0.jpg"+"', '"+
                    linkBH+R.raw.thaothuc+"');";
            querySQL(sql);
            sql="INSERT INTO BAI_HAT(tenBH,CaSi,hinhAnh,linkBH) VALUES ('Love Is','DANGRANGTO','"+"https://photo-resize-zmp3.zadn.vn/w600_r1x1_jpeg/cover/2/9/1/d/291de57211876765ae2443d20a1b770c.jpg"+"', '"+
                    linkBH+R.raw.loveis+"');";
            querySQL(sql);
            sql="INSERT INTO BAI_HAT(tenBH,CaSi,hinhAnh,linkBH) VALUES ('Shhhh','Wean & Tlinh','"+"https://avatar-ex-swe.nixcdn.com/song/2023/10/07/6/a/d/0/1696643028822_640.jpg"+"', '"+
                    linkBH+R.raw.shhhh+"');";
            querySQL(sql);
            sql="INSERT INTO BAI_HAT(tenBH,CaSi,hinhAnh,linkBH) VALUES ('Nơi này có anh','Sơn Tùng M-TP','"+"https://upload.wikimedia.org/wikipedia/vi/1/1d/N%C6%A1i_n%C3%A0y_c%C3%B3_anh_-_Single_Cover.jpg"+"', '"+
                    linkBH+R.raw.noinaycoanh+"');";
            querySQL(sql);
            sql="INSERT INTO BAI_HAT(tenBH,CaSi,hinhAnh,linkBH) VALUES ('Phóng zìn zìn','LowG & Tlinh','"+"https://i1.sndcdn.com/artworks-iJs8dmvYRRZeROhT-5yobHQ-t500x500.jpg"+"', '"+
                    linkBH+R.raw.phongzinzin+"');";
            querySQL(sql);
            sql="INSERT INTO BAI_HAT(tenBH,CaSi,hinhAnh,linkBH) VALUES ('Phòng số 12','TeuYoungBoy & Dangrangto','"+"https://i1.sndcdn.com/artworks-OUGIVImjTuQS-0-t500x500.jpg"+"', '"+
                    linkBH+R.raw.phongso12+"');";
            querySQL(sql);
            sql="INSERT INTO BAI_HAT(tenBH,CaSi,hinhAnh,linkBH) VALUES ('Bao nhiêu loài hoa','KayC & Tlinh','"+"https://avatar-ex-swe.nixcdn.com/song/2024/06/13/0/c/6/0/1718261834302_640.jpg"+"', '"+
                    linkBH+R.raw.baonhieuloaihoa+"');";
            querySQL(sql);
        }
    }
    public void querySQL(String sql){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        sqLiteDatabase.execSQL(sql);
    }
    // truy vấn trả về kết quả
    public Cursor getDataFromSQL(String sql){
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(sql,null);
        return cursor;
    }

    public ArrayList<BaiHat> DocDuLieu(String sql){
        ArrayList<BaiHat> listBH=new ArrayList<>();
        Cursor cursor=getDataFromSQL(sql);
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String tenBH=cursor.getString(1);
            String caSi=cursor.getString(2);
            String linkAnh=cursor.getString(3);
            String linkBH=cursor.getString(4);
            BaiHat baiHat=new BaiHat(id,tenBH,caSi,linkAnh,linkBH);
            listBH.add(baiHat);
        }
        return listBH;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
