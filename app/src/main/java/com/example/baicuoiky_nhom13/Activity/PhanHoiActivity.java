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
    EditText edtEmailTo,edtSubject,edtContent;
    Button btnSend;
    ImageView imgBack;
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
        // ánh xạ
        edtEmailTo=findViewById(R.id.edtEmailTo);
        edtSubject=findViewById(R.id.edtSubject);
        edtContent=findViewById(R.id.edtContent);
        btnSend=findViewById(R.id.btnSend);
        imgBack=findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String fromEmail="duongnvt2k5@gmail.com";
                    String passWord="zyvewfenubsweskp";
                    String toEmail=edtEmailTo.getText().toString().trim();
                    String subject=edtSubject.getText().toString().trim();
                    String content=edtContent.getText().toString().trim();
                    String host="smtp.gmail.com";
                    Properties properties=System.getProperties();
                    properties.put("mail.smtp.host",host);
                    properties.put("mail.smtp.port","465");
                    properties.put("mail.smtp.ssl.enable",true);
                    properties.put("mail.smtp.auth",true);
                    Session session=Session.getInstance(properties, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(fromEmail,passWord);
                        }
                    });
                    MimeMessage mimeMessage=new MimeMessage(session);
                    mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(toEmail)));
                    mimeMessage.setSubject(subject);
                    mimeMessage.setText(content);
                    Thread emailThread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Transport.send(mimeMessage);
                            }catch (Exception e){
                                Log.d("Lỗi thread email", e.toString());
                                Toast.makeText(PhanHoiActivity.this,
                                        e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    emailThread.start();
                    edtEmailTo.setText("");
                    edtContent.setText("");
                    edtSubject.setText("");
                    Toast.makeText(PhanHoiActivity.this,"Phản hồi đã được gửi, cảm ơn bạn. ",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.d("Lỗi gửi email", e.toString());
                    Toast.makeText(PhanHoiActivity.this,
                            e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}