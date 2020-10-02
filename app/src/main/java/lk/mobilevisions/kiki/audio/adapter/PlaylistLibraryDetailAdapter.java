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
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.NavigationUtils;

public class PlaylistLibraryDetailAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Song> mArrayList = new ArrayList<>();
    OnPlaylistLibraryItemClickListener itemClickListener;

    public PlaylistLibraryDetailAdapter(Context mContext, List<Song> mArrayList, OnPlaylistLibraryItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_library_detail_list_items, parent, false);
        return new PlaylistLibraryViewHolder(view);

    }


    public void setList(List<Song> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((PlaylistLibraryViewHolder) holder, position);


    }

    private void initLayoutOne(final PlaylistLibraryViewHolder holder, int pos) {
        final Song current = mArrayList.get(pos);
        holder.songTitleTextview.setText(current.getName());

        holder.artistTextView.setText(current.getArtistName());
        holder.songDuration.setText(NavigationUtils.convertMinutesToFormat(current.getDuration()));

        try {
            Picasso.with(mContext).load(URLDecoder.decode(current.getImage(), "UTF-8")).fit().centerCrop()
                    .placeholder(R.drawable.program)
                    .into(holder.playlistImageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPlaylistLibraryItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class PlaylistLibraryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.playlistListLayout)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView_head)
        TextView songTitleTextview;

        @BindView(R.id.artist_text)
        TextView artistTextView;

        @BindView(R.id.playlist_song_duration)
        TextView songDuration;

        @BindView(R.id.playlist_image)
        ImageView playlistImageView;


        public PlaylistLibraryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnPlaylistLibraryItemClickListener {
        public void onPlaylistLibraryItemClick(Song song, int position,List<Song> songs);

    }

}