package lk.mobilevisions.kiki.audio.adapter;

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

public class LibraryPlaylistVerticalAdapter extends
        RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private static Context context;
    private static PopularSongsVerticalAdapter mInstance;
    private List<PlayList> playlistList;
    private OnPlaylistItemActionListener onPlaylistItemActionListener;

    public LibraryPlaylistVerticalAdapter (Context mContext,
                                    List<PlayList> playlistList,OnPlaylistItemActionListener onPlaylistItemActionListener) {

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

        holder.titleTextView.setText(current.getName());
        try {
            Picasso.with(context).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                    .placeholder(R.drawable.program)
                    .into(holder.imageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onPlaylistItemActionListener.onPlaylistPlayAction(playlistList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),playlistList);

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

        @BindView(R.id.image)
        ImageView imageView;
        public PlaylistRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnPlaylistItemActionListener{

        void onPlaylistPlayAction(PlayList playList,int position,List<PlayList> playLists);
    }
}
