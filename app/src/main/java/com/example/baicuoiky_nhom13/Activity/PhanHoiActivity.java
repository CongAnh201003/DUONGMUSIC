package com.example.baicuoiky_nhom13.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.baicuoiky_nhom13.R;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PhanHoiActivity extends AppCompatActivity {
    EditText edtEmailTo, edtSubject, edtContent;
    Button btnSend;
    ImageView imgBack;

    // Cấu hình email cố định
    // Đây là tài khoản trung gian (Bot gửi thư)
    final String FROM_EMAIL = "duongnvt2k5@gmail.com"; // email là email trung gian
    final String FROM_PASSWORD = "lgzm nsba ipdi keky";

    // Đây là tài khoản nhận thư (Admin)
    final String TO_EMAIL = "stu735105023@hnue.edu.vn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phan_hoi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ
        edtEmailTo = findViewById(R.id.edtEmailTo);
        edtSubject = findViewById(R.id.edtSubject);
        edtContent = findViewById(R.id.edtContent);
        btnSend = findViewById(R.id.btnSend);
        imgBack = findViewById(R.id.imgBack);

        // Thiết lập giao diện: Hiển thị nơi gửi đến nhưng không cho sửa
        edtEmailTo.setText(TO_EMAIL);
        edtEmailTo.setEnabled(false); // Khóa ô nhập liệu này lại

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy dữ liệu người dùng nhập
                String subject = edtSubject.getText().toString().trim();
                String content = edtContent.getText().toString().trim();

                // Kiểm tra dữ liệu rỗng
                if (subject.isEmpty() || content.isEmpty()) {
                    Toast.makeText(PhanHoiActivity.this, "Vui lòng nhập chủ đề và nội dung!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Bắt đầu gửi email
                sendEmailBackground(subject, content);
            }
        });
    }

    private void sendEmailBackground(String subject, String content) {
        // Hiển thị thông báo đang gửi (để người dùng biết app đang chạy)
        Toast.makeText(PhanHoiActivity.this, "Đang gửi phản hồi...", Toast.LENGTH_SHORT).show();

        Thread emailThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Properties properties = new Properties();
                    properties.put("mail.smtp.host", "smtp.gmail.com");
                    properties.put("mail.smtp.port", "465");
                    properties.put("mail.smtp.ssl.enable", "true");
                    properties.put("mail.smtp.auth", "true");

                    Session session = Session.getInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
                        }
                    });

                    MimeMessage mimeMessage = new MimeMessage(session);
                    // Người nhận cố định là stu735105023@gmail.com
                    mimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAIL));
                    mimeMessage.setSubject(subject);
                    mimeMessage.setText(content);

                    // Thực hiện gửi
                    Transport.send(mimeMessage);

                    // Gửi thành công -> Cập nhật giao diện (Phải dùng runOnUiThread)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PhanHoiActivity.this, "Phản hồi đã được gửi thành công!", Toast.LENGTH_LONG).show();
                            edtSubject.setText("");
                            edtContent.setText("");
                        }
                    });

                } catch (Exception e) {
                    Log.e("Lỗi gửi email", e.toString());
                    // Gửi thất bại -> Cập nhật giao diện
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PhanHoiActivity.this, "Gửi thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        emailThread.start();
    }
}
