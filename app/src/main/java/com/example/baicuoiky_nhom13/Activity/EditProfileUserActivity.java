package com.example.baicuoiky_nhom13.Activity;


import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.API.CloudinaryResponse;
import com.example.baicuoiky_nhom13.API.CloudinaryService;
import com.example.baicuoiky_nhom13.API.RetrofitClient;
import com.example.baicuoiky_nhom13.Database.MySQLite;
import com.example.baicuoiky_nhom13.Model.NguoiDung;
import com.example.baicuoiky_nhom13.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;

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

    EditText edtTenDangNhap, edtTenNguoiDung, edtEmail, edtMatKhau;
    Button btnLuu;
    ImageView imgBack,imgAnhDaiDien;
    MySQLite mySQLite;
    private Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        imgAnhDaiDien = findViewById(R.id.imgAnhDaiDien);
        imgBack = findViewById(R.id.imgBack);
        edtTenDangNhap = findViewById(R.id.edtTenDangNhap);
        edtTenNguoiDung = findViewById(R.id.edtTenNguoiDung);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        btnLuu = findViewById(R.id.btnLuu);

        // Khoi tao MySQLite
        mySQLite = new MySQLite(this, MySQLite.DATABASE_NAME, null, 1);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent=getIntent();
        Bundle data=intent.getExtras();
        NguoiDung nguoiDung= (NguoiDung) data.get("nguoi_dung");
        edtTenNguoiDung.setText(nguoiDung.getHoTen());
        edtEmail.setText(nguoiDung.getEmail());
        edtMatKhau.setText(nguoiDung.getMatKhau());
        edtTenDangNhap.setText(nguoiDung.getTenDN());


        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgAnhDaiDien.setImageURI(selectedImageUri);
                    }
                }
        );

        imgAnhDaiDien.setOnClickListener(v -> {
            Intent intentImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intentImage);
        });




        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lay du lieu moi tu EditText
                String tenDangNhapMoi = edtTenDangNhap.getText().toString();
                String hoTenMoi = edtTenNguoiDung.getText().toString();
                String emailMoi = edtEmail.getText().toString();
                String matKhauMoi = edtMatKhau.getText().toString();

                // Cap nhat du lieu vao CSDL
                String updateSQL = "UPDATE NGUOI_DUNG SET " +
                        "ten_dang_nhap = '" + tenDangNhapMoi + "', " +
                        "ho_ten = '" + hoTenMoi + "', " +
                        "email = '" + emailMoi + "', " +
                        "mat_khau = '" + matKhauMoi + "' " +
                        "WHERE id = '" + nguoiDung.getId() + "'";
                NguoiDung nd=new NguoiDung(nguoiDung.getId(),tenDangNhapMoi,matKhauMoi,hoTenMoi,emailMoi,"",2);
                mySQLite.querySQL(updateSQL);
                // Thong bao cap nhat thanh cong
                Toast.makeText(EditProfileUserActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                // Tra ve du lieu moi
                Intent resultIntent = new Intent();
                resultIntent.putExtra("tenDangNhap", tenDangNhapMoi);
                resultIntent.putExtra("hoTen", hoTenMoi);
                resultIntent.putExtra("email", emailMoi);
                resultIntent.putExtra("matKhau", matKhauMoi);
                setResult(RESULT_OK,resultIntent);
                finish();

            }
        });

        btnLuu.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                try {
                    String localPath = saveImageToLocal(selectedImageUri);
                    uploadImageToCloudinary(localPath);
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi lưu ảnh: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi lưu ảnh!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Lưu ảnh vào bộ nhớ trong
    private String saveImageToLocal(Uri imageUri) throws Exception {
        ContentResolver resolver = getContentResolver();
        InputStream inputStream = resolver.openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        String fileName = "profile_picture_" + System.currentTimeMillis() + ".jpg";
        File file = new File(getFilesDir(), fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        outputStream.flush();
        outputStream.close();
        return file.getAbsolutePath();
    }

    // Tạo signature bằng SHA-1
    private String generateSignature(String timestamp) {
        try {
            String params = "timestamp=" + timestamp + "&upload_preset=" + CLOUDINARY_UPLOAD_PRESET;
            String input = params + CLOUDINARY_API_SECRET;

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo SHA-1 hash", e);
        }
    }

    // Upload ảnh lên Cloudinary
    private void uploadImageToCloudinary(String imagePath) {
        File imageFile = new File(imagePath);

        // Tạo các tham số
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
                    Log.d("Upload", "Ảnh đã tải lên thành công: " + imageUrl);
                    Toast.makeText(EditProfileUserActivity.this, "Ảnh đã được lưu ở: " + imageUrl, Toast.LENGTH_LONG).show();

                    // Trả URL ảnh về ProfileUserActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("anhDaiDien", imageUrl); // URL ảnh đại diện mới
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Kết thúc EditProfileUserActivity
                } else {
                    Log.e("Upload", "Lỗi từ Cloudinary: " + response.message());
                    Toast.makeText(EditProfileUserActivity.this, "Lỗi khi tải ảnh lên Cloudinary!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CloudinaryResponse> call, Throwable t) {
                Log.e("Upload", "Lỗi kết nối Cloudinary: " + t.getMessage());
                Toast.makeText(EditProfileUserActivity.this, "Lỗi kết nối Cloudinary!", Toast.LENGTH_SHORT).show();
            }
        });


    }
}