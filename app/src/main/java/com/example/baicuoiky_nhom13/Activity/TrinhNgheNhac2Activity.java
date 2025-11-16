package com.example.baicuoiky_nhom13.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.Model.BaiHatYT;
import com.example.baicuoiky_nhom13.R;

public class TrinhNgheNhac2Activity extends AppCompatActivity {
    MediaController mediaController;
    ImageView albumArt,imgQuayLai,imgLapLai;
    VideoView videoView;
    TextView artistName, songTitle;
    boolean isImage1 = true;
    TextView tvChedo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trinh_nghe_nhac);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        videoView = findViewById(R.id.videoView);
        songTitle = findViewById(R.id.songTitle);
        artistName = findViewById(R.id.artistName);
        imgQuayLai = findViewById(R.id.imgQuayLai);
        albumArt=findViewById(R.id.albumArt);
        imgLapLai=findViewById(R.id.imgLapLai);

        Intent intent=getIntent();
        Bundle data=intent.getExtras();
        BaiHatYT baiHat= (BaiHatYT) data.get("name");

        Glide.with(getBaseContext()).load(baiHat.getHinhAnh()).into(albumArt);

        songTitle.setText(baiHat.getTenBaiHat());
        artistName.setText(baiHat.getCaSi());
        String linkBH=baiHat.getLinkBaiHat();
        Uri videoURI = Uri.parse(linkBH);

        videoView.setVideoURI(videoURI);
        if (mediaController == null) {
            mediaController = new MediaController(TrinhNgheNhac2Activity.this);
        }
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // hàm phát nhạc lặp lại
        imgLapLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isImage1) {
                    imgLapLai.setImageResource(R.drawable.baseline_replay_circle_filled_24);// Đổi sang ảnh khác
                    videoView.setOnCompletionListener(mp -> videoView.start());
                    tvChedo.setText("Replay");

                } else {
                    imgLapLai.setImageResource(R.drawable.baseline_replay_24); // Trở lại ảnh ban đầu
                    videoView.setOnCompletionListener(mp -> videoView.pause());
                    tvChedo.setText("");
                }
                isImage1 = !isImage1;
            }
        });

        videoView.start();

        imgQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    // hiển thị mediacontroller khi chạm vào màn hình
    public boolean onTouchEvent(android.view.MotionEvent event) {
        mediaController.show();
        return super.onTouchEvent(event);}
}