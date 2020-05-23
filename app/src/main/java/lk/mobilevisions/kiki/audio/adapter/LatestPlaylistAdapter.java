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
import lk.mobilevisions.kiki.audio.model.dto.DailyMix;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;

public class LatestPlaylistAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PlayList> mArrayList = new ArrayList<>();
    OnLatestPlaylistItemClickListener itemClickListener;

    public LatestPlaylistAdapter(Context mContext, List<PlayList> mArrayList, OnLatestPlaylistItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_list_items, parent, false);
        return new LatestPlaylistViewHolder(view);

    }


    public void setList(List<PlayList> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((LatestPlaylistViewHolder) holder, position);


    }

    private void initLayoutOne(final LatestPlaylistViewHolder holder, int pos) {
        final PlayList current = mArrayList.get(pos);
        holder.songTitleTextview.setText(current.getName());
        holder.songCount.setText(current.getSongCount() + " songs");
        holder.artistTextView.setText(current.getArtistName());

        holder.dateTextView.setText(current.getDate().substring(0, 4));





//        String time = "07:02";
//
//        long min = Integer.parseInt(time.substring(0, 2));
//        long sec = Integer.parseInt(time.substring(3));
//
//        long t = (min * 60L) + sec;
//
//        long result = TimeUnit.SECONDS.toMillis(t);

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
                itemClickListener.onLatestPlaylistItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });
        holder.addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemClickListener.onAddPlaylistItemClick(mArrayList.get(holder.getAdapterPosition()));

            }
        });

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

        @BindView(R.id.date_playlist)
        TextView dateTextView;

        @BindView(R.id.playlist_image)
        ImageView playlistImageView;

        @BindView(R.id.song_count)
        TextView songCount;

        @BindView(R.id.addPlaylist)
        TextView addPlaylist;


        public LatestPlaylistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnLatestPlaylistItemClickListener {
        public void onLatestPlaylistItemClick(PlayList song, int position,List<PlayList> songs);

        public void onAddPlaylistItemClick(PlayList song);
    }

}