package com.example.baicuoiky_nhom13.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.Model.BaiHatYT;

import java.util.ArrayList;

public class SQLiteYeuThich extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="baihatYT.sql";

    public SQLiteYeuThich(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        String sql="CREATE TABLE IF NOT EXISTS "+
                "BAI_HAT_YT ("+
                "id_yt integer primary key autoincrement, "+
                "id_nd integer, "+
                "tenBH_yt varchar(255), "+
                "CaSi_yt varchar(255), " +
                "hinhAnh_yt varchar(255), "+
                "linkBH_yt varchar(255)); ";

        querySQL(sql);

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
    public ArrayList<BaiHatYT> DocDuLieuYT(String sql){
        ArrayList<BaiHatYT> listBH=new ArrayList<>();
        Cursor cursor=getDataFromSQL(sql);
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            int idnd=cursor.getInt(1);
            String tenBH=cursor.getString(2);
            String caSi=cursor.getString(3);
            String linkAnh=cursor.getString(4);
            String linkBH=cursor.getString(5);
            BaiHatYT baiHatYT=new BaiHatYT(id,idnd,tenBH,caSi,linkAnh,linkBH);
            listBH.add(baiHatYT);
        }
        return listBH;
    }
    public int loadBH(String tenBaihat,int idnd){
        String sql="SELECT COUNT(id_yt) FROM BAI_HAT_YT WHERE tenBH_yt = '"+tenBaihat+"' and id_nd = "+idnd+";";
        int id=0;
        Cursor cursor=getDataFromSQL(sql);
        while (cursor.moveToNext()){
            id=cursor.getInt(0);
        }
        return id;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
