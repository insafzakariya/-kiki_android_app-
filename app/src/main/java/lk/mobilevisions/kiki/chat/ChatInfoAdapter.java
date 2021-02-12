package lk.mobilevisions.kiki.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;

import lk.mobilevisions.kiki.chat.module.dto.ChatMember;

public class ChatInfoAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ChatMember> mArrayList = new ArrayList<>();
    ChatInfoAdapter.OnChatInfoItemClickListener itemClickListener;

    public ChatInfoAdapter(Context mContext, List<ChatMember> mArrayList, ChatInfoAdapter.OnChatInfoItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_info_items, parent, false);
        return new ChatInfoAdapter.ChatInfoViewHolder(view);

    }


    public void setList(List<ChatMember> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }
    public void addData(List<ChatMember> data) {
        this.mArrayList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((ChatInfoAdapter.ChatInfoViewHolder) holder, position);


    }

    private void initLayoutOne(final ChatInfoAdapter.ChatInfoViewHolder holder, int pos) {

        final ChatMember current = mArrayList.get(pos);
        holder.chatTextView.setText(current.getName());
        int color = Color.parseColor(current.getColour());
        holder.chatTextView.setTextColor(color);


        try {
            Picasso.with(mContext).load("https://storage.googleapis.com/kiki_images/live/viewer/1x/" + current.getViewerId() + ".jpeg").fit().centerCrop()
                    .placeholder(R.drawable.program)
                    .into(holder.ChatImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                itemClickListener.onChatInfoItemClick(mArrayList.get(holder.getAdapterPosition()),
//                        holder.getAdapterPosition(),mArrayList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class ChatInfoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.contstrain_artistlist)
        ConstraintLayout rvHorizontal;

        @BindView(R.id.chatinfo_name)
        TextView chatTextView;

        @BindView(R.id.chatinfo_image)
        ImageView ChatImageView;


        public ChatInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnChatInfoItemClickListener {
//        public void onChatInfoItemClick(Song song, int position,List<Song> songs);

    }

}