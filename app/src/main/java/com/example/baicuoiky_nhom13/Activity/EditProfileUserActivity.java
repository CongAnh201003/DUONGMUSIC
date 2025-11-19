package com.example.baicuoiky_nhom13.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.baicuoiky_nhom13.API.CloudinaryResponse;
import com.example.baicuoiky_nhom13.API.CloudinaryService;
import com.example.baicuoiky_nhom13.API.RetrofitClient;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileUserActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileUser";
    private static final String CLOUDINARY_API_KEY = "824931231479934";
    private static final String CLOUDINARY_API_SECRET = "CQiPu_nEy-DLjM77lr-STNReODU";
    private static final String CLOUDINARY_UPLOAD_PRESET = "ml_default";

    // Bỏ edtTenDangNhap
    private EditText edtTenNguoiDung, edtEmail, edtMatKhau;
    private Button btnLuu;
    private ImageView imgBack, imgAnhDaiDien;

    // Khai báo Firestore
    private FirebaseFirestore firestore;
    private NguoiDung nguoiDungHienTai;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_user);

        initViews();
        firestore = FirebaseFirestore.getInstance();

        // Lấy dữ liệu người dùng được truyền từ Activity trước
        if (getIntent().getExtras() != null) {
            nguoiDungHienTai = (NguoiDung) getIntent().getExtras().get("nguoi_dung");
            if (nguoiDungHienTai != null) {
                displayUserData();
            }
        }

        setupImagePicker();
        setupClickListeners();
    }

    /**
     * Hiển thị dữ liệu người dùng lên giao diện.
     */
    private void displayUserData() {
        edtTenNguoiDung.setText(nguoiDungHienTai.getHoTen());
        edtEmail.setText(nguoiDungHienTai.getEmail());
        edtMatKhau.setText(nguoiDungHienTai.getMatKhau());
        // Không còn TenDN nên không setText cho edtTenDangNhap nữa
    }

    /**
     * Khởi tạo ActivityResultLauncher để chọn ảnh.
     */
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgAnhDaiDien.setImageURI(selectedImageUri);
                    }
                }
        );
    }

    /**
     * Thiết lập các sự kiện click.
     */
    private void setupClickListeners() {
        imgBack.setOnClickListener(v -> finish());

        imgAnhDaiDien.setOnClickListener(v -> {
            Intent intentImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intentImage);
        });

        // Hợp nhất logic vào một OnClickListener duy nhất
        btnLuu.setOnClickListener(v -> saveChanges());
    }

    /**
     * Hàm chính để lưu tất cả thay đổi (cả text và ảnh).
     */
    private void saveChanges() {
        // Luôn cập nhật thông tin text trước
        updateTextDataInFirestore(() -> {
            // Sau khi cập nhật text thành công, kiểm tra xem có ảnh mới không
            if (selectedImageUri != null) {
                // Nếu có, upload ảnh
                try {
                    String localPath = saveImageToLocal(selectedImageUri);
                    uploadImageToCloudinary(localPath);
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi lưu ảnh cục bộ: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi xử lý ảnh!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Nếu không có ảnh mới, chỉ cần cập nhật text là đủ, kết thúc Activity
                Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // Báo hiệu cho Activity trước cập nhật
                finish();
            }
        });
    }

    /**
     * Cập nhật các trường dữ liệu text lên Firestore.
     * Sử dụng một callback để thực hiện hành động tiếp theo sau khi cập nhật thành công.
     */
    private void updateTextDataInFirestore(Runnable onComplete) {
        String hoTenMoi = edtTenNguoiDung.getText().toString().trim();
        String matKhauMoi = edtMatKhau.getText().toString().trim();

        if (hoTenMoi.isEmpty() || matKhauMoi.isEmpty()) {
            Toast.makeText(this, "Họ tên và mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        String documentId = nguoiDungHienTai.getId();
        Map<String, Object> updates = new HashMap<>();
        updates.put("hoTen", hoTenMoi);
        updates.put("matKhau", matKhauMoi);

        firestore.collection("NGUOI_DUNG").document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cập nhật dữ liệu text thành công.");
                    // Cập nhật đối tượng người dùng hiện tại
                    nguoiDungHienTai.setHoTen(hoTenMoi);
                    nguoiDungHienTai.setMatKhau(matKhauMoi);
                    // Gọi callback để tiếp tục xử lý (ví dụ: upload ảnh)
                    if (onComplete != null) {
                        onComplete.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi cập nhật dữ liệu text", e);
                    Toast.makeText(this, "Lỗi khi cập nhật thông tin!", Toast.LENGTH_LONG).show();
                });
    }


    // --- CÁC HÀM XỬ LÝ ẢNH (GIỮ NGUYÊN) ---

    private void uploadImageToCloudinary(String imagePath) {
        File imageFile = new File(imagePath);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String signature = generateSignature(timestamp);

        RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), CLOUDINARY_API_KEY);
        RequestBody uploadPreset = RequestBody.create(MediaType.parse("text/plain"), CLOUDINARY_UPLOAD_PRESET);
        RequestBody timestampBody = RequestBody.create(MediaType.parse("text/plain"), timestamp);
        RequestBody signatureBody = RequestBody.create(MediaType.parse("text/plain"), signature);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        CloudinaryService cloudinaryService = RetrofitClient.getClient().create(CloudinaryService.class);
        Call<CloudinaryResponse> call = cloudinaryService.uploadImage(apiKey, uploadPreset, timestampBody, signatureBody, body);

        call.enqueue(new Callback<CloudinaryResponse>() {
            @Override
            public void onResponse(Call<CloudinaryResponse> call, Response<CloudinaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getSecure_url();
                    Log.d(TAG, "Ảnh đã tải lên thành công: " + imageUrl);
                    updateImageUrlInFirestore(imageUrl);
                } else {
                    Log.e(TAG, "Lỗi từ Cloudinary: " + response.message());
                    Toast.makeText(EditProfileUserActivity.this, "Lỗi khi tải ảnh lên!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CloudinaryResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối Cloudinary: " + t.getMessage());
                Toast.makeText(EditProfileUserActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cập nhật trường 'anhDaiDien' trong Firestore sau khi upload ảnh thành công.
     */
    private void updateImageUrlInFirestore(String imageUrl) {
        String documentId = nguoiDungHienTai.getId();
        firestore.collection("NGUOI_DUNG").document(documentId)
                .update("anhDaiDien", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileUserActivity.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK); // Báo hiệu cho Activity trước cập nhật
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi cập nhật URL ảnh", e);
                    Toast.makeText(EditProfileUserActivity.this, "Lỗi khi lưu URL ảnh!", Toast.LENGTH_SHORT).show();
                });
    }

    private String saveImageToLocal(Uri imageUri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        String fileName = "profile_picture_" + System.currentTimeMillis() + ".jpg";
        File file = new File(getFilesDir(), fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        outputStream.close();
        return file.getAbsolutePath();
    }

    private String generateSignature(String timestamp) {
        try {
            String toSign = "timestamp=" + timestamp + "&upload_preset=" + CLOUDINARY_UPLOAD_PRESET + CLOUDINARY_API_SECRET;
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(toSign.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo signature", e);
        }
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        imgAnhDaiDien = findViewById(R.id.imgAnhDaiDien);
        // edtTenDangNhap không còn trong layout nữa, bạn cần xóa nó khỏi file XML
        // edtTenDangNhap = findViewById(R.id.edtTenDangNhap);
        edtTenNguoiDung = findViewById(R.id.edtTenNguoiDung);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        btnLuu = findViewById(R.id.btnLuu);
    }
}
