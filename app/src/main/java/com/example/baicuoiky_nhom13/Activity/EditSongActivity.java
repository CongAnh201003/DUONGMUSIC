package com.example.baicuoiky_nhom13.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditSongActivity extends AppCompatActivity {

    private static final String TAG = "EditSongActivity";

    // UI Components
    private EditText edtTenBH, edtTenCaSi, edtLinkBH, edtTheLoai;
    private ImageView imgBack, imgPreview;
    private Button btnChonAnh, btnLuu;

    // Data
    private BaiHat currentSong; // Bài hát hiện tại đang sửa
    private Uri newImageUri = null; // Uri ảnh mới nếu người dùng chọn
    private FirebaseFirestore firestore;

    // Cấu hình Cloudinary (QUAN TRỌNG: Phải trùng tên preset Unsigned bạn đã tạo trên web)
    private static final String CLOUDINARY_UPLOAD_PRESET = "android_upload";

    // Launcher chọn ảnh
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);

        firestore = FirebaseFirestore.getInstance();

        // 1. Ánh xạ view
        initViews();

        // 2. Cài đặt bộ chọn ảnh
        setupImagePicker();

        // 3. Nhận dữ liệu từ Intent (từ màn hình danh sách gửi sang)
        currentSong = (BaiHat) getIntent().getSerializableExtra("SONG_DATA");

        if (currentSong != null) {
            fillDataToViews(); // Đổ dữ liệu cũ lên màn hình
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy dữ liệu bài hát!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 4. Bắt sự kiện click
        imgBack.setOnClickListener(v -> finish());

        btnChonAnh.setOnClickListener(v -> openGallery());

        btnLuu.setOnClickListener(v -> handleUpdateSong());
    }

    private void initViews() {
        edtTenBH = findViewById(R.id.edtTenBaiHatEdit);
        edtTenCaSi = findViewById(R.id.edtTenCaSiEdit);
        edtLinkBH = findViewById(R.id.edtLinkBHEdit);
        edtTheLoai = findViewById(R.id.edtTheLoaiEdit);

        imgPreview = findViewById(R.id.imgPreviewEdit);
        btnChonAnh = findViewById(R.id.btnChonAnhEdit);
        btnLuu = findViewById(R.id.btnLuuThayDoi);
        imgBack = findViewById(R.id.imgBackEdit);
    }

    private void fillDataToViews() {
        edtTenBH.setText(currentSong.getTenBH());
        edtTenCaSi.setText(currentSong.getTenCaSi());
        edtLinkBH.setText(currentSong.getLinkBH());
        edtTheLoai.setText(currentSong.getTheLoai());

        // Load ảnh cũ bằng Glide
        if (currentSong.getHinhAnh() != null && !currentSong.getHinhAnh().isEmpty()) {
            Glide.with(this)
                    .load(currentSong.getHinhAnh())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgPreview);
        }
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        newImageUri = result.getData().getData();
                        imgPreview.setImageURI(newImageUri); // Hiển thị ảnh mới chọn
                    }
                }
        );
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleUpdateSong() {
        String tenMoi = edtTenBH.getText().toString().trim();
        String caSiMoi = edtTenCaSi.getText().toString().trim();
        String linkMoi = edtLinkBH.getText().toString().trim();

        if (tenMoi.isEmpty() || caSiMoi.isEmpty() || linkMoi.isEmpty()) {
            Toast.makeText(this, "Vui lòng không để trống tên, ca sĩ và link nhạc!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Logic xử lý cập nhật
        if (newImageUri != null) {
            // Trường hợp 1: Có chọn ảnh mới -> Upload Cloudinary -> Update Firestore
            uploadImageToCloudinaryAndSave();
        } else {
            // Trường hợp 2: Giữ nguyên ảnh cũ -> Update Firestore luôn
            updateFirestore(currentSong.getHinhAnh());
        }
    }

    private void uploadImageToCloudinaryAndSave() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tải ảnh mới lên...");
        dialog.setCancelable(false);
        dialog.show();

        MediaManager.get().upload(newImageUri)
                .unsigned(CLOUDINARY_UPLOAD_PRESET) // Đảm bảo preset này đúng là Unsigned
                .callback(new UploadCallback() {
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        dialog.dismiss();
                        String newUrl = (String) resultData.get("secure_url");
                        Log.d(TAG, "Upload ảnh mới thành công: " + newUrl);

                        // Có link mới rồi thì cập nhật Firestore
                        updateFirestore(newUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo errorInfo) {
                        dialog.dismiss();
                        Log.e(TAG, "Lỗi Upload: " + errorInfo.getDescription());
                        Toast.makeText(EditSongActivity.this, "Lỗi upload ảnh: " + errorInfo.getDescription(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStart(String requestId) {}
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}
                    @Override
                    public void onReschedule(String requestId, ErrorInfo errorInfo) {}
                }).dispatch();
    }

    private void updateFirestore(String imageUrl) {
        String tenMoi = edtTenBH.getText().toString().trim();
        String caSiMoi = edtTenCaSi.getText().toString().trim();
        String linkMoi = edtLinkBH.getText().toString().trim();
        String theLoaiMoi = edtTheLoai.getText().toString().trim();

        // Tạo Map dữ liệu cần update
        Map<String, Object> updates = new HashMap<>();
        updates.put("tenBH", tenMoi);
        updates.put("tenCaSi", caSiMoi);
        updates.put("linkBH", linkMoi);
        updates.put("theLoai", theLoaiMoi);
        updates.put("hinhAnh", imageUrl); // Link ảnh (mới hoặc cũ)
        updates.put("tenBH_lowercase", tenMoi.toLowerCase(Locale.ROOT));

        // Gọi lệnh update
        firestore.collection("BAI_HAT").document(currentSong.getIdBH())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    // Gửi kết quả OK về Activity trước để reload list
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi Update Firestore: ", e);
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
