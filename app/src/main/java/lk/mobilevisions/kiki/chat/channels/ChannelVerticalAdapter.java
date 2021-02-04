package lk.mobilevisions.kiki.chat.channels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.chat.module.dto.Channels;

public class ChannelVerticalAdapter extends
        RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private static Context context;
    private static ChannelVerticalAdapter mInstance;
    private List<Channels> playlistList;
    private ChannelVerticalAdapter.OnChannelItemActionListener onChannelItemActionListener;

    public ChannelVerticalAdapter (Context mContext,
                                      List<Channels> playlistList, ChannelVerticalAdapter.OnChannelItemActionListener onChannelItemActionListener) {

        this.context = mContext;
        this.playlistList = playlistList;
        this.onChannelItemActionListener = onChannelItemActionListener;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_channel_items, parent, false);
        return new ChannelVerticalAdapter.ChannelRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((ChannelVerticalAdapter.ChannelRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final ChannelVerticalAdapter.ChannelRecyclerViewHolder holder, int pos) {
        final Channels current = playlistList.get(pos);

        if(current.getImagePath()!=null && !current.getImagePath().isEmpty()){
            try {
                Picasso.with(context).load(URLDecoder.decode(current.getImagePath(), "UTF-8"))
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

                onChannelItemActionListener.onChannelAction(playlistList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),playlistList);

            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public void setData(List<Channels> songs) {
        this.playlistList = songs;
        notifyDataSetChanged();
    }

    public void addData(List<Channels> songs) {
        this.playlistList.addAll(songs);
        notifyDataSetChanged();
    }


    class ChannelRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.channel_relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.channel_image)
        ImageView imageView;

        public ChannelRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnChannelItemActionListener{

        void onChannelAction(Channels playList,int position,List<Channels> playLists);
    }
}