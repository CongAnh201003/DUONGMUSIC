package com.example.baicuoiky_nhom13.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.baicuoiky_nhom13.Model.NguoiDung;

import java.util.ArrayList;

public class MySQLite extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="nguoidung.sql";
    public MySQLite(Context context, String dbName, SQLiteDatabase.CursorFactory cursorFactory, int version){
        super(context, dbName, cursorFactory, version);
        String sql="CREATE TABLE IF NOT EXISTS "+
                "NGUOI_DUNG ( "+
                "id integer primary key autoincrement, "+
                "ten_dang_nhap varchar(255), " +
                "mat_khau varchar(255), " +
                "ho_ten varchar(255), " +
                "email varchar(255), " +
                "anh_dai_dien TEXT, " +
                "vai_tro integer );"; // 1 là admin , 2 là nguoidung
        querySQL(sql);
        // thêm tài khoản admin
        sql="SELECT COUNT(id) FROM NGUOI_DUNG WHERE ten_dang_nhap = 'admin';";
        int id=0;
        Cursor cursor=getDataFromSQL(sql);
        while (cursor.moveToNext()){
            id=cursor.getInt(0);
        }
        if (id<=0){
            sql="INSERT INTO NGUOI_DUNG "+
                    "VALUES (null, 'admin', '123456', 'Nguyen Lan', 'nlan1022004@gmail.com','',1)";
            querySQL(sql);
        }
        // thêm tài khoản user
        sql="SELECT id FROM NGUOI_DUNG WHERE ten_dang_nhap = 'user1';";
        id=0;
        cursor=getDataFromSQL(sql);
        while (cursor.moveToNext()){
            id=cursor.getInt(0);
        }
        if (id<=0){
            sql="INSERT INTO NGUOI_DUNG "+
                    "VALUES (null, 'user1', '123456', 'ABCD', 'user1@gmail.com','',2)";
            querySQL(sql);
        }
    }
    // truy vấn không trả về kết quả: CREATE, UPDATE, INSERT, DELETE...
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
    // doc du lieu
    public ArrayList<NguoiDung> docDuLieu(String sql){
        ArrayList<NguoiDung> listNguoiDung=new ArrayList<>();
        Cursor cursor=getDataFromSQL(sql);
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String tenDN=cursor.getString(1);
            String mk=cursor.getString(2);
            String ht=cursor.getString(3);
            String email=cursor.getString(4);
            int vt=cursor.getInt(6);
            NguoiDung nguoiDung=new NguoiDung(id,tenDN,mk, ht, email,"",vt);
            listNguoiDung.add(nguoiDung);
        }
        return listNguoiDung;
    }

    public NguoiDung kiemTraDangNhap(String tenDN,String matKhau){
        String sql="SELECT * FROM NGUOI_DUNG WHERE ten_dang_nhap= '"+tenDN+"' and mat_khau = '"+matKhau+"'";
        Cursor cursor=getDataFromSQL(sql);
        NguoiDung nguoiDung=new NguoiDung();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String tenDangNhap=cursor.getString(1);
            String mk=cursor.getString(2);
            String ht=cursor.getString(3);
            String email=cursor.getString(4);
            int vt=cursor.getInt(6);
            nguoiDung=new NguoiDung(id,tenDangNhap,mk, ht, email,"",vt);
        }
        return nguoiDung;
    }
    public NguoiDung loadNguoiDung(int idND){
        String sql="SELECT * FROM NGUOI_DUNG WHERE id= "+idND+";";
        Cursor cursor=getDataFromSQL(sql);
        NguoiDung nguoiDung=new NguoiDung();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String tenDangNhap=cursor.getString(1);
            String mk=cursor.getString(2);
            String ht=cursor.getString(3);
            String email=cursor.getString(4);
            int vt=cursor.getInt(6);
            nguoiDung=new NguoiDung(id,tenDangNhap,mk, ht, email,"",vt);
        }
        return nguoiDung;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
    // Phương thức insert() để chèn một người dùng vào cơ sở dữ liệu
    public long insert(String tableName, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase(); // Mở cơ sở dữ liệu để ghi
        return db.insert(tableName, null, values); // Chèn dữ liệu vào bảng
    }
    // Phương thức cập nhật vai trò người dùng
    public void capNhatVaiTroNguoiDung(int userId, int newRole) {
        SQLiteDatabase db = this.getWritableDatabase(); // Mở cơ sở dữ liệu để ghi
        String sql = "UPDATE NGUOI_DUNG SET vai_tro = ? WHERE id = ?"; // Câu lệnh SQL
        db.execSQL(sql, new Object[]{newRole, userId}); // Thực thi câu lệnh với tham số
        db.close(); // Đóng cơ sở dữ liệu
    }
}
