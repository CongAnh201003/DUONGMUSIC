package com.example.baicuoiky_nhom13.Adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.baicuoiky_nhom13.Model.Message;
import com.example.baicuoiky_nhom13.R;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng một TextView đơn giản bọc trong LinearLayout
        LinearLayout layout = new LinearLayout(parent.getContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(parent.getContext());
        textView.setPadding(20, 20, 20, 20);
        textView.setTextSize(16);
        textView.setTextColor(0xFFFFFFFF); // Màu trắng

        layout.addView(textView);
        return new ViewHolder(layout, textView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.textView.setText(message.getContent());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.textView.getLayoutParams();
        if (message.isUser()) {
            // Tin nhắn User: Căn phải, màu nền xanh
            holder.layout.setGravity(Gravity.END);
            holder.textView.setBackgroundColor(0xFF2196F3);
        } else {
            // Tin nhắn AI: Căn trái, màu nền xám
            holder.layout.setGravity(Gravity.START);
            holder.textView.setBackgroundColor(0xFF424242);
        }
        holder.textView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() { return messageList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView textView;
        public ViewHolder(View itemView, TextView textView) {
            super(itemView);
            this.layout = (LinearLayout) itemView;
            this.textView = textView;
        }
    }
}
