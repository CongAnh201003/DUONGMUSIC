package com.example.baicuoiky_nhom13.Activity;import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TrinhNgheNhacActivity extends AppCompatActivity {
    private static final String TAG = "TrinhNgheNhacActivity";

    // --- UI Components ---
    private ImageView imgQuayLai, albumArt, imgLapLai, imgTuaLai, imgPhatHoacDung, imgTuaToi;
    private TextView songTitle, artistName, currentTime, totalTime;
    private SeekBar seekBar;

    // --- Media Player ---
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();

    // --- Data ---
    private ArrayList<BaiHat> playlist;
    private int currentSongIndex = -1;

    // --- Trạng thái ---
    private boolean isRepeating = false;
    private boolean isUserSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trinh_nghe_nhac);
        applyWindowInsets();

        initViews();

        if (!loadDataFromIntent()) {
            Toast.makeText(this, "Lỗi: Không nhận được dữ liệu bài hát.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        playSong(currentSongIndex);
        setupListeners();
    }

    private boolean loadDataFromIntent() {
        Intent intent = getIntent();
        playlist = (ArrayList<BaiHat>) intent.getSerializableExtra("danh_sach_bai_hat");
        currentSongIndex = intent.getIntExtra("vi_tri", -1);
        return playlist != null && !playlist.isEmpty() && currentSongIndex != -1;
    }

    private void playSong(int index) {
        if (index < 0 || index >= playlist.size()) return;

        currentSongIndex = index;
        BaiHat baiHat = playlist.get(currentSongIndex);

        // === CÁC LỖI ĐÃ SỬA: Dùng đúng tên phương thức từ Model ===
        songTitle.setText(baiHat.getTenBH());
        artistName.setText(baiHat.getTenCaSi());
        Glide.with(this).load(baiHat.getHinhAnh()).placeholder(R.drawable.album).into(albumArt);

        String linkBH = baiHat.getLinkBH();
        if (linkBH == null || linkBH.isEmpty()) {
            Toast.makeText(this, "Lỗi: Link bài hát không hợp lệ.", Toast.LENGTH_SHORT).show();
            playNextSong(); // Tự động chuyển bài nếu lỗi
            return;
        }

        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(linkBH);
            mediaPlayer.prepareAsync(); // Chuẩn bị bất đồng bộ

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                updateUIForPlaying();
                updateSeekBar();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                if (isRepeating) {
                    mp.seekTo(0);
                    mp.start();
                } else {
                    playNextSong();
                }
            });

        } catch (IOException e) {
            Log.e(TAG, "Lỗi khi chuẩn bị MediaPlayer", e);
            Toast.makeText(this, "Lỗi khi tải bài hát", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        imgQuayLai.setOnClickListener(v -> finish());
        imgPhatHoacDung.setOnClickListener(v -> togglePlayPause());
        imgTuaToi.setOnClickListener(v -> playNextSong());
        imgTuaLai.setOnClickListener(v -> playPreviousSong());
        imgLapLai.setOnClickListener(v -> toggleRepeat());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
            }
        });
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                updateUIForPaused();
            } else {
                mediaPlayer.start();
                updateUIForPlaying();
            }
        }
    }

    private void toggleRepeat() {
        isRepeating = !isRepeating;
        if (isRepeating) {
            imgLapLai.setImageResource(R.drawable.baseline_replay_circle_filled_24);
            Toast.makeText(this, "Lặp lại bài hát này", Toast.LENGTH_SHORT).show();
        } else {
            imgLapLai.setImageResource(R.drawable.baseline_replay_24);
            Toast.makeText(this, "Phát tuần tự", Toast.LENGTH_SHORT).show();
        }
    }

    private void playNextSong() {
        currentSongIndex = (currentSongIndex + 1) % playlist.size();
        playSong(currentSongIndex);
    }

    private void playPreviousSong() {
        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
        playSong(currentSongIndex);
    }

    private void updateUIForPlaying() {
        //imgPhatHoacDung.setImageResource(R.drawable.baseline_pause_24);
    }

    private void updateUIForPaused() {
        imgPhatHoacDung.setImageResource(R.drawable.baseline_play_arrow_24);
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            seekBar.setMax(mediaPlayer.getDuration());

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && !isUserSeeking) {
                        try {
                            int currentPosition = mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(currentPosition);

                            String currentTimeStr = String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                                    TimeUnit.MILLISECONDS.toSeconds(currentPosition) % 60);
                            currentTime.setText(currentTimeStr);

                            int duration = mediaPlayer.getDuration();
                            String totalTimeStr = String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(duration),
                                    TimeUnit.MILLISECONDS.toSeconds(duration) % 60);
                            totalTime.setText(totalTimeStr);

                            handler.postDelayed(this, 1000);
                        } catch (IllegalStateException e) {
                            // MediaPlayer có thể đã được giải phóng
                        }
                    }
                }
            }, 1000);
        }
    }

    private void initViews() {
        imgQuayLai = findViewById(R.id.imgQuayLai);
        albumArt = findViewById(R.id.albumArt);
        imgLapLai = findViewById(R.id.imgLapLai);
        imgTuaLai = findViewById(R.id.imgTuaLai);
        imgPhatHoacDung = findViewById(R.id.imgPhatHoacDung);
        imgTuaToi = findViewById(R.id.imgTuaToi);
        songTitle = findViewById(R.id.songTitle);
        artistName = findViewById(R.id.artistName);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        seekBar = findViewById(R.id.seekBar);
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}
