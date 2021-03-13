package lk.mobilevisions.kiki.chatweb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.modules.chat.ChatWebDTO;

public class ChatVerticalAdapter extends
        RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private static Context context;
    private static ChatVerticalAdapter mInstance;
    private List<ChatWebDTO> chatList;
    private ChatVerticalAdapter.OnChatItemActionListener onChannelItemActionListener;

    public ChatVerticalAdapter (Context mContext,
                                   List<ChatWebDTO> chatList, ChatVerticalAdapter.OnChatItemActionListener onChannelItemActionListener) {

        this.context = mContext;
        this.chatList = chatList;
        this.onChannelItemActionListener = onChannelItemActionListener;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_channel_items, parent, false);
        return new ChatRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((ChatRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final ChatRecyclerViewHolder holder, int pos) {
        final ChatWebDTO current = chatList.get(pos);

        if(current.getImageURL()!=null && !current.getImageURL().isEmpty()){
            try {
                Picasso.with(context).load(URLDecoder.decode(current.getImageURL(), "UTF-8"))
                        .placeholder(R.drawable.program)
                        .into(holder.imageView);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {

            Picasso.with(context).load(R.drawable.program)
                    .placeholder(R.drawable.program)
                    .into(holder.imageView);

        }
        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onChannelItemActionListener.onChatAction(chatList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),chatList);

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void setData(List<ChatWebDTO> songs) {
        this.chatList = songs;
        notifyDataSetChanged();
    }

    public void addData(List<ChatWebDTO> songs) {
        this.chatList.addAll(songs);
        notifyDataSetChanged();
    }


    class ChatRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.channel_relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.channel_image)
        ImageView imageView;

        public ChatRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnChatItemActionListener{

        void onChatAction(ChatWebDTO chatlist, int position, List<ChatWebDTO> chatWebDTOList);
    }
}