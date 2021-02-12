package lk.mobilevisions.kiki.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twilio.chat.Member;
import com.twilio.chat.Message;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.chat.messages.DateFormatter;
import lk.mobilevisions.kiki.chat.module.dto.ChatMember;


public class ChatAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Message> mArrayList = new ArrayList<>();
    private List<ChatMember> memberArrayList;
    ChatAdapter.OnChatItemClickListener itemClickListener;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    String viewerId;

    public ChatAdapter(Context mContext, List<Message> mArrayList, List<ChatMember> memberArrayList, ChatAdapter.OnChatItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.memberArrayList = memberArrayList;
        this.itemClickListener = itemClickListener;

    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mArrayList.get(position);
        System.out.println("hghghgh " + message.getAuthor());
        System.out.println("hghghgh 111 " + Application.getInstance().getAuthUser().getId());

        if (message.getAuthor().equals(String.valueOf(Application.getInstance().getAuthUser().getId()))) {
            System.out.println("hghghgh 22" + message.getAuthor());
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            System.out.println("hghghgh 333 ");
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item_right, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.messages_item_left, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;

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
        final Message message = mArrayList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
//        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                itemClickListener.onChatItemClick(mArrayList.get(holder.getAdapterPosition()),
//                        holder.getAdapterPosition(),mArrayList);
//            }
//        });


    }


    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_me);
        }

        void bind(Message message) {
            messageText.setText(message.getMessageBody());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(DateFormatter.getFormattedDateFromISOString(message.getDateCreated()));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        CardView chatBoxColor;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_other);
            timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_other);
            nameText = (TextView) itemView.findViewById(R.id.text_gchat_user_other);
            chatBoxColor = (CardView) itemView.findViewById(R.id.card_gchat_message_other);
            profileImage = (ImageView) itemView.findViewById(R.id.image_gchat_profile_other);
        }

        void bind(Message message) {
            viewerId = message.getAuthor();

            messageText.setText(message.getMessageBody());
            timeText.setText(DateFormatter.getFormattedDateFromISOString(message.getDateCreated()));
            nameText.setText(getName(message.getAuthor()));
            chatBoxColor.setCardBackgroundColor(Integer.parseInt(getColor()));
            nameText.setTextColor(Integer.parseInt(getColor()));

            try {
                Picasso.with(mContext).load("https://storage.googleapis.com/kiki_images/live/viewer/1x/" + viewerId + ".jpeg").fit().centerCrop()
                        .placeholder(R.drawable.ic_user_avatar)
                        .into(profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public interface OnChatItemClickListener {
        public void onChatItemClick(Message message, int position, List<Message> messageList);

    }

    private String getColor() {
        int color = Color.parseColor("#009688");
        for (ChatMember member : memberArrayList) {
            if (String.valueOf(member.getViewerId()).equals(viewerId)) {
                color = Color.parseColor(member.getColour());
            }
        }
        return String.valueOf(color);
    }

    private String getName(String viewerId) {
        String name = viewerId;
        for (ChatMember member : memberArrayList) {
            if (String.valueOf(member.getViewerId()).equals(viewerId)) {
                name = member.getName();
            }
        }
        return name;
    }

}