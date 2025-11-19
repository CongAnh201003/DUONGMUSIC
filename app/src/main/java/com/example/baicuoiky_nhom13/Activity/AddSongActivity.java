package com.example.baicuoiky_nhom13.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class AddSongActivity extends AppCompatActivity {
    private static final String TAG = "AddSongActivity";

    // UI Components
    private EditText edtTenBaiHat, edtTenCaSi, edtHinhAnh, edtLinkBH, edtTheLoai;
    private Button btnThem;
    private ImageView imgBack;

    // Firebase
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song); // Sử dụng layout activity_add_song

        initViews();
        firestore = FirebaseFirestore.getInstance();

        imgBack.setOnClickListener(v -> finish());
        btnThem.setOnClickListener(v -> addNewSong());
    }

    private void addNewSong() {
        String tenBH = edtTenBaiHat.getText().toString().trim();
        String tenCaSi = edtTenCaSi.getText().toString().trim();
        String hinhAnh = edtHinhAnh.getText().toString().trim();
        String linkBH = edtLinkBH.getText().toString().trim();
        String theLoai = edtTheLoai.getText().toString().trim();

        if (tenBH.isEmpty() || tenCaSi.isEmpty() || linkBH.isEmpty()) {
            Toast.makeText(this, "Tên bài hát, ca sĩ và link nhạc không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        BaiHat newSong = new BaiHat();
        newSong.setTenBH(tenBH);
        newSong.setTenCaSi(tenCaSi);
        newSong.setHinhAnh(hinhAnh);
        newSong.setLinkBH(linkBH);
        newSong.setTheLoai(theLoai);
        newSong.setTenBH_lowercase(tenBH.toLowerCase(Locale.ROOT));

        // === ĐÃ SỬA: Thêm vào collection "BaiHat" ở cấp cao nhất ===
        firestore.collection("BaiHat")
                .add(newSong)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Thêm bài hát thành công với ID: " + documentReference.getId());
                    Toast.makeText(this, "Thêm bài hát thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi thêm bài hát: ", e);
                    Toast.makeText(this, "Thêm bài hát thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void initViews() {
        edtTenBaiHat = findViewById(R.id.edtTenBaiHat);
        edtTenCaSi = findViewById(R.id.edtTenCaSi);
        edtHinhAnh = findViewById(R.id.edtHinhAnh);
        edtLinkBH = findViewById(R.id.edtLinkBH);
        edtTheLoai = findViewById(R.id.edtTheLoai);
        btnThem = findViewById(R.id.btnThem);
        imgBack = findViewById(R.id.imgBack);
    }
}
