package com.example.baicuoiky_nhom13.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.baicuoiky_nhom13.Adapter.MessageAdapter;
import com.example.baicuoiky_nhom13.Model.Message;
import com.example.baicuoiky_nhom13.R;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatAIActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private EditText edtMessage;
    private ImageView btnSendChat, imgBackChat;
    private MessageAdapter adapter;
    private List<Message> messageList;

    // 🔥 KEY CỦA BẠN (Hãy đảm bảo bạn đã ENABLE API cho dự án chứa key này)
    private static final String GEMINI_API_KEY = "AIzaSyBRfROo7bYhWN_QE7rbIIE57BPuCrSsMZ4";

    // 🔥 DÙNG URL NÀY (Chính xác 100% cho bản miễn phí)
    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + GEMINI_API_KEY;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ai);

        initViews();

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(adapter);

        addMessage("Xin chào! Mình là Gemini Flash. Bạn cần giúp gì không?", false);

        btnSendChat.setOnClickListener(v -> {
            String question = edtMessage.getText().toString().trim();
            if (!question.isEmpty()) {
                addMessage(question, true);
                edtMessage.setText("");
                callGeminiAI(question);
            }
        });

        imgBackChat.setOnClickListener(v -> finish());
    }

    private void initViews() {
        recyclerChat = findViewById(R.id.recyclerChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSendChat = findViewById(R.id.btnSendChat);
        imgBackChat = findViewById(R.id.imgBackChat);
    }

    private void addMessage(String text, boolean isUser) {
        messageList.add(new Message(text, isUser));
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerChat.smoothScrollToPosition(messageList.size() - 1);
    }

    private void callGeminiAI(String question) {
        JSONObject jsonBody = new JSONObject();
        try {
            JSONArray partsArr = new JSONArray();
            JSONObject partObj = new JSONObject();
            partObj.put("text", question);
            partsArr.put(partObj);

            JSONObject contentsObj = new JSONObject();
            contentsObj.put("parts", partsArr);

            JSONArray contentsArr = new JSONArray();
            contentsArr.put(contentsObj);

            jsonBody.put("contents", contentsArr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ChatAI", "Lỗi mạng: " + e.getMessage());
                runOnUiThread(() -> addMessage("Lỗi kết nối: " + e.getMessage(), false));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("ChatAI_Response", responseBody);

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        if (jsonResponse.has("candidates")) {
                            JSONArray candidates = jsonResponse.getJSONArray("candidates");
                            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                            JSONArray parts = content.getJSONArray("parts");
                            String result = parts.getJSONObject(0).getString("text");

                            runOnUiThread(() -> addMessage(result.trim(), false));
                        } else {
                            runOnUiThread(() -> addMessage("AI không trả lời được.", false));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> addMessage("Lỗi xử lý dữ liệu.", false));
                    }
                } else {
                    // NẾU VẪN 404: CHẮC CHẮN DO CHƯA ENABLE API Ở BƯỚC 1
                    final String errorMsg = "Lỗi API (" + response.code() + "): " + responseBody;
                    runOnUiThread(() -> addMessage(errorMsg, false));
                }
            }
        });
    }
}
