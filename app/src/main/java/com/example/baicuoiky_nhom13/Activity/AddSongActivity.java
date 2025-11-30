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

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.baicuoiky_nhom13.Model.BaiHat;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddSongActivity extends AppCompatActivity {
    private static final String TAG = "AddSongActivity";

    // Khai báo View
    private EditText edtTenBaiHat, edtTenCaSi, edtLinkBH, edtTheLoai;
    private ImageView imgBack, imgPreview;
    private Button btnThem, btnChonAnh;

    // Biến xử lý
    private Uri selectedImageUri = null; // Uri ảnh được chọn từ máy
    private FirebaseFirestore firestore;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        // 1. Khởi tạo Cloudinary (Chỉ cần chạy 1 lần, nhưng để đây cho chắc chắn)
        configCloudinary();

        // 2. Khởi tạo View và Firebase
        initViews();
        firestore = FirebaseFirestore.getInstance();

        // 3. Cài đặt bộ chọn ảnh
        setupImagePicker();

        // 4. Sự kiện Click
        imgBack.setOnClickListener(v -> finish());
        btnChonAnh.setOnClickListener(v -> openGallery());
        btnThem.setOnClickListener(v -> handleAddSong());
    }

    // --- Cấu hình Cloudinary ---
    private void configCloudinary() {
        try {
            Map config = new HashMap();
            // TODO: THAY ĐỔI THÔNG TIN CỦA BẠN VÀO ĐÂY
            config.put("cloud_name", "dhdfyfzej");
            config.put("api_key", "933946823114623");
            config.put("api_secret", "TDeurhfnQZ-06m7MBBtMeMM-ArE");

            MediaManager.init(this, config);
        } catch (Exception e) {
            // Nếu đã init rồi thì bỏ qua để tránh crash
            Log.d(TAG, "Cloudinary already initialized");
        }
    }

    // --- Khởi tạo View ---
    private void initViews() {
        edtTenBaiHat = findViewById(R.id.edtTenBaiHat);
        edtTenCaSi = findViewById(R.id.edtTenCaSi);
        edtLinkBH = findViewById(R.id.edtLinkBH);
        edtTheLoai = findViewById(R.id.edtTheLoai);
        imgPreview = findViewById(R.id.imgPreview);
        btnChonAnh = findViewById(R.id.btnChonAnh);
        btnThem = findViewById(R.id.btnThem);
        imgBack = findViewById(R.id.imgBack);
    }

    // --- Bộ chọn ảnh ---
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        // Hiển thị ảnh vừa chọn lên ImageView
                        imgPreview.setImageURI(selectedImageUri);
                    }
                }
        );
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // --- Xử lý logic Thêm bài hát ---
    private void handleAddSong() {
        String tenBH = edtTenBaiHat.getText().toString().trim();
        String tenCaSi = edtTenCaSi.getText().toString().trim();
        String linkBH = edtLinkBH.getText().toString().trim();

        // Validate cơ bản
        if (tenBH.isEmpty() || tenCaSi.isEmpty() || linkBH.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Tên bài, Ca sĩ và Link nhạc!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh bìa!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bắt đầu upload ảnh lên Cloudinary
        uploadImageToCloudinary();
    }

    private void uploadImageToCloudinary() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải ảnh lên...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Upload Unsigned (Không cần server xác thực - phù hợp demo/đồ án)
        // TODO: Đảm bảo bạn đã tạo Upload Preset dạng "Unsigned" trên Dashboard Cloudinary
        String uploadPreset = "android_upload"; // Ví dụ: "my_app_preset"

        MediaManager.get().upload(selectedImageUri)
                .unsigned(uploadPreset)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) { }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) { }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // Lấy URL ảnh thành công
                        String imageUrl = (String) resultData.get("secure_url");
                        Log.d(TAG, "Upload ảnh thành công: " + imageUrl);

                        // Lưu thông tin vào Firestore
                        saveToFirestore(imageUrl, progressDialog);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo errorInfo) {
                        progressDialog.dismiss();
                        Toast.makeText(AddSongActivity.this, "Lỗi Upload Ảnh: " + errorInfo.getDescription(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Cloudinary Error: " + errorInfo.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo errorInfo) { }
                })
                .dispatch();
    }

    // --- Lưu vào Firestore ---
    private void saveToFirestore(String imageUrl, ProgressDialog progressDialog) {
        String tenBH = edtTenBaiHat.getText().toString().trim();
        String tenCaSi = edtTenCaSi.getText().toString().trim();
        String linkBH = edtLinkBH.getText().toString().trim();
        String theLoai = edtTheLoai.getText().toString().trim();

        // Tạo Document Reference mới để lấy ID
        DocumentReference newRef = firestore.collection("BAI_HAT").document();

        BaiHat newSong = new BaiHat();
        newSong.setIdBH(newRef.getId());
        newSong.setTenBH(tenBH);
        newSong.setTenCaSi(tenCaSi);
        newSong.setHinhAnh(imageUrl); // URL lấy từ Cloudinary
        newSong.setLinkBH(linkBH);
        newSong.setTheLoai(theLoai);
        newSong.setTenBH_lowercase(tenBH.toLowerCase(Locale.ROOT));

        newRef.set(newSong)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Thêm bài hát thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Lỗi lưu Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
