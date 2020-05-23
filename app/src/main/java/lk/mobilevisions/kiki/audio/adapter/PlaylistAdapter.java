package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innodroid.expandablerecycler.ExpandableRecyclerAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;

public class PlaylistAdapter extends
        RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private static Context context;
    private static PopularSongsVerticalAdapter mInstance;
    private List<PlayList> playlistList;
    private OnUserPlaylistItemActionListener onPlaylistItemActionListener;

    public PlaylistAdapter (Context mContext,
                                           List<PlayList> playlistList,OnUserPlaylistItemActionListener onPlaylistItemActionListener) {

        this.context = mContext;
        this.playlistList = playlistList;
        this.onPlaylistItemActionListener = onPlaylistItemActionListener;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal, parent, false);
        return new PlaylistRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((PlaylistRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final PlaylistRecyclerViewHolder holder, int pos) {
        final PlayList current = playlistList.get(pos);

        if(pos==0){
            holder.plusTextView.setVisibility(View.VISIBLE);
            holder.titleTextView.setVisibility(View.GONE);
            Picasso.with(context).load(R.drawable.playlist_plus_backgroud_drawable)
                    .placeholder(R.drawable.playlist_plus_backgroud_drawable)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(holder.imageView);
        } else{
            holder.plusTextView.setVisibility(View.GONE);
            holder.titleTextView.setVisibility(View.VISIBLE);
            holder.titleTextView.setText(current.getName());
            if(current.getImage()!=null && !current.getImage().isEmpty()){
                try {
                    Picasso.with(context).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                            .placeholder(R.drawable.program)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(holder.imageView);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else {

                    Picasso.with(context).load(R.drawable.program)
                            .placeholder(R.drawable.program)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(holder.imageView);

            }

        }

        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onPlaylistItemActionListener.onUserPlaylistPlayAction(playlistList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),playlistList);

            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public void setData(List<PlayList> songs) {
        this.playlistList = songs;
        notifyDataSetChanged();
    }

    public void addData(List<PlayList> songs) {
        this.playlistList.addAll(songs);
        notifyDataSetChanged();
    }


    class PlaylistRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView2)
        TextView titleTextView;

        @BindView(R.id.plus_textview)
        TextView plusTextView;

        @BindView(R.id.image)
        ImageView imageView;

        public PlaylistRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnUserPlaylistItemActionListener{

        void onUserPlaylistPlayAction(PlayList playList,int position,List<PlayList> playLists);
    }
}
