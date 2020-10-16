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
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.audio.model.dto.Song;

public class LibraryPlaylistSongAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Song> mArrayList;
    OnLibraryPlaylistItemClickListener itemClickListener;

    public LibraryPlaylistSongAdapter(Context mContext, List<Song> mArrayList, OnLibraryPlaylistItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_playlist_list_items, parent, false);
        return new LibraryPlaylistViewHolder(view);

    }

    public void setData(List<Song> data) {
        this.mArrayList = data;
        notifyDataSetChanged();
    }


    public void setList(List<Song> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((LibraryPlaylistViewHolder) holder, position);


    }

    private void initLayoutOne(final LibraryPlaylistViewHolder holder, int pos) {
        final Song current = mArrayList.get(pos);

        if (Application.getInstance().getSongsAddedToPlaylist().contains(current.getId())) {
            holder.addedSongs.setVisibility(View.VISIBLE);
            holder.addSongs.setVisibility(View.GONE);
            System.out.println("checkcheck 1111");

        } else {
            holder.addedSongs.setVisibility(View.GONE);
            holder.addSongs.setVisibility(View.VISIBLE);
            System.out.println("checkcheck 2222");
        }


        holder.songTitleTextview.setText(current.getName());
        holder.artistTextView.setText(current.getArtistName());

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
                        holder.getAdapterPosition(), mArrayList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class LibraryPlaylistViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.playlist_list_relative)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView_head)
        TextView songTitleTextview;

        @BindView(R.id.artist_text)
        TextView artistTextView;

        @BindView(R.id.playlist_image)
        ImageView playlistImageView;

        @BindView(R.id.addSongsToPlaylist)
        TextView addSongs;

        @BindView(R.id.addedToPlaylist)
        TextView addedSongs;


        public LibraryPlaylistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnLibraryPlaylistItemClickListener {
        public void onPlaylistItemClick(Song song, int position, List<Song> songs);
    }

}