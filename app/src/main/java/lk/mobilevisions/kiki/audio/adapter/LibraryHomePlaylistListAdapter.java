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

public class LibraryHomePlaylistListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PlayList> mArrayList = new ArrayList<>();
    OnPlaylistItemClickListener itemClickListener;

    public LibraryHomePlaylistListAdapter(Context mContext, List<PlayList> mArrayList, OnPlaylistItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_home_playlist_items, parent, false);
        return new PlaylistViewHolder(view);

    }


    public void setList(List<PlayList> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((PlaylistViewHolder) holder, position);


    }

    private void initLayoutOne(final PlaylistViewHolder holder, int pos) {
        final PlayList current = mArrayList.get(pos);

        if(pos==0){
            holder.plusTextview.setVisibility(View.VISIBLE);
            holder.songTitleTextview.setVisibility(View.GONE);
            holder.removeImageView.setVisibility(View.GONE);
            holder.plusDesText.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(R.drawable.playlist_plus_backgroud_drawable)
                    .placeholder(R.drawable.playlist_plus_backgroud_drawable)
                    .into(holder.playlistImageView);
        }else {
            holder.plusTextview.setVisibility(View.GONE);
            holder.plusDesText.setVisibility(View.GONE);
            holder.songTitleTextview.setVisibility(View.VISIBLE);
            holder.removeImageView.setVisibility(View.VISIBLE);
            holder.songTitleTextview.setText(current.getName());
            holder.songCount.setText(current.getSongCount() + " songs");
            holder.artistTextView.setText(current.getArtistName());
            holder.dateTextView.setText(current.getDate().substring(0, 4));

            if (current.getImage() != null && !current.getImage().isEmpty()) {
                try {
                    Picasso.with(mContext).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                            .placeholder(R.drawable.program)
                            .into(holder.playlistImageView);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {

                Picasso.with(mContext).load(R.drawable.program)
                        .placeholder(R.drawable.program)
                        .into(holder.playlistImageView);

            }
        }

        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPlaylistItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });
        holder.removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onRemovePlaylistItemClick(mArrayList.get(holder.getAdapterPosition()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder {

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

        @BindView(R.id.removeView)
        ImageView removeImageView;

        @BindView(R.id.song_count)
        TextView songCount;

        @BindView(R.id.plus_textview)
        TextView plusTextview;

        @BindView(R.id.plus_descrip_text)
        TextView plusDesText;


        public PlaylistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnPlaylistItemClickListener {
        public void onPlaylistItemClick(PlayList song, int position,List<PlayList> songs);
        public void onRemovePlaylistItemClick(PlayList playList);
    }

}