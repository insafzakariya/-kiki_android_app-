package lk.mobilevisions.kiki.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.chat.module.dto.Avatar;

public class ChatUserImageAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static Context context;
    private List<Avatar> avatarList;
    private ChatUserImageAdapter.OnImageItemActionListener onPlaylistItemActionListener;

    public ChatUserImageAdapter(Context mContext,
                                List<Avatar> playlistList, ChatUserImageAdapter.OnImageItemActionListener onPlaylistItemActionListener) {

        this.context = mContext;
        this.avatarList = playlistList;
        this.onPlaylistItemActionListener = onPlaylistItemActionListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.avatar_vertical_items, parent, false);
        return new ChatUserImageAdapter.UserImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((ChatUserImageAdapter.UserImageViewHolder) holder, position);

    }


    private void initLayoutTwo(final ChatUserImageAdapter.UserImageViewHolder holder, int pos) {
        final Avatar current = avatarList.get(pos);

        if (pos == 0) {
            holder.plusTextView.setVisibility(View.VISIBLE);
            Picasso.with(context).load(R.drawable.playlist_plus_backgroud_drawable)
                    .placeholder(R.drawable.playlist_plus_backgroud_drawable)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(holder.imageView);
        } else {
            holder.plusTextView.setVisibility(View.GONE);
            if (current.getUrl() != null && !current.getUrl().isEmpty()) {
                try {
                    Picasso.with(context).load(URLDecoder.decode(current.getUrl(), "UTF-8"))
                            .placeholder(R.drawable.program)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(holder.imageView);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {

                Picasso.with(context).load(R.drawable.program)
                        .placeholder(R.drawable.program)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(holder.imageView);

            }

        }

        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onPlaylistItemActionListener.onImagePlayAction(avatarList.get(holder.getAdapterPosition()), holder.getAdapterPosition(), avatarList);

            }
        });
    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }

    public void setData(List<Avatar> avatars) {
        this.avatarList = avatars;
        notifyDataSetChanged();
    }

    public void addData(List<Avatar> avatars) {
        this.avatarList.addAll(avatars);
        notifyDataSetChanged();
    }


    class UserImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar_layout_root)
        RelativeLayout rvHorizontal;

        @BindView(R.id.plus_textview)
        TextView plusTextView;

        @BindView(R.id.userImage)
        ImageView imageView;

        @BindView(R.id.overlay_drawable)
        ImageView overlay;

        public UserImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnImageItemActionListener {

        void onImagePlayAction(Avatar avatar, int position, List<Avatar> avatars);
    }
}