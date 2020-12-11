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
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.NavigationUtils;

public class PlaylistDetailAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Song> mArrayList = new ArrayList<>();
    OnPlaylistDetailItemClickListener itemClickListener;

    public PlaylistDetailAdapter(Context mContext, List<Song> mArrayList, OnPlaylistDetailItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_detail_list_items, parent, false);
        return new LatestPlaylistViewHolder(view);

    }


    public void setList(List<Song> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((LatestPlaylistViewHolder) holder, position);


    }

    private void initLayoutOne(final LatestPlaylistViewHolder holder, int pos) {
        final Song current = mArrayList.get(pos);
        holder.songTitleTextview.setText(current.getName());
        holder.artistTextView.setText(current.getArtistName());

        int duration = Integer.parseInt(current.getDuration());
        holder.songDuration.setText(timeConversion(duration));

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
                itemClickListener.onPlaylistItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });
        holder.addSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemClickListener.onAddSongsItemClick(mArrayList.get(holder.getAdapterPosition()));
            }
        });

    }

    private static String timeConversion(int totalSeconds) {

        int hr = totalSeconds/3600;
        int rem = totalSeconds%3600;
        int mn = rem/60;
        int sec = rem%60;
        String hrStr = (hr<10 ? "0" : "")+hr;
        String mnStr = (mn<10 ? "0" : "")+mn;
        String secStr = (sec<10 ? "0" : "")+sec;

        return mnStr + " : " + secStr;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class LatestPlaylistViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.playlistListLayout)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView_head)
        TextView songTitleTextview;

        @BindView(R.id.artist_text)
        TextView artistTextView;

        @BindView(R.id.playlist_image)
        ImageView playlistImageView;

        @BindView(R.id.addSongs)
        TextView addSongs;

        @BindView(R.id.plalist_song_duration)
        TextView songDuration;


        public LatestPlaylistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnPlaylistDetailItemClickListener {
        public void onPlaylistItemClick(Song song, int position,List<Song> songs);
        public void onAddSongsItemClick (Song song);
    }

}