package lk.mobilevisions.kiki.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twilio.chat.Message;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.adapter.YouMightAlsoLikeAdapter;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.chat.messages.DateFormatter;
import lk.mobilevisions.kiki.chat.messages.UserMessage;


public class ChatAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Message> mArrayList = new ArrayList<>();
    ChatAdapter.OnChatItemClickListener itemClickListener;

    public ChatAdapter(Context mContext, List<Message> mArrayList, ChatAdapter.OnChatItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_list, parent, false);
        return new ChatAdapter.ChatViewHolder(view);

    }


    public void setList(List<Message> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }
    public void addData(List<Message> data) {
        this.mArrayList.addAll(data);
        notifyDataSetChanged();
    }

//    public void setMessages(List<Message> messages) {
//        this.messages = convertTwilioMessages(messages);
//        this.statusMessageSet.clear();
//        notifyDataSetChanged();
//    }

    public void addMessage(Message message) {
        this.mArrayList.add(message);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((ChatAdapter.ChatViewHolder) holder, position);


    }

    private void initLayoutOne(final ChatAdapter.ChatViewHolder holder, int pos) {

        final Message current = mArrayList.get(pos);

        holder.textViewMessage.setText(current.getMessageBody());
        holder.textViewAuthor.setText(current.getAuthor());
        holder.textViewDate.setText(DateFormatter.getFormattedDateFromISOString(current.getDateCreated()));

        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onChatItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.chat_layout)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textViewMessage)
        TextView textViewMessage;

        @BindView(R.id.textViewAuthor)
        TextView textViewAuthor;

        @BindView(R.id.textViewDate)
        TextView textViewDate;

        public ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnChatItemClickListener {
        public void onChatItemClick(Message message, int position,List<Message> messageList);

    }

}