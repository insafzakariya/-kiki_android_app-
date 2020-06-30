package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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

public class LibraryHomeSongListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Song> mArrayList = new ArrayList<>();
    OnLibraryHomeSongItemClickListener itemClickListener;

    public LibraryHomeSongListAdapter(Context mContext, List<Song> mArrayList, OnLibraryHomeSongItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_home_song_list, parent, false);
        return new LibrarySongsViewHolder(view);

    }

    public void setData(List<Song> data) {
        System.out.println("dhdhdhdhdhdh 333  " + data.size());
        this.mArrayList = data;
        notifyDataSetChanged();
    }

    public void addData(List<Song> data) {
        this.mArrayList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((LibrarySongsViewHolder) holder, position);


    }

    private void initLayoutOne(final LibrarySongsViewHolder holder, int pos) {

        final Song current = mArrayList.get(pos);
        holder.songTitleTextview.setText(current.getName());
        holder.descriptionTextView.setText(current.getArtistName());
        holder.songDuration.setText(NavigationUtils.convertMinutesToFormat(current.getDuration()));

        try {
            Picasso.with(mContext).load(URLDecoder.decode(current.getImage(), "UTF-8")).fit().centerCrop()
                    .placeholder(R.drawable.program)
                    .into(holder.programImageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onLibraryHomeSongItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });
        holder.addSongsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onAddHomeSongItemClick(mArrayList.get(holder.getAdapterPosition()));
            }
        });
        holder.removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onRemoveHomeSongItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
//                deleteSong(pos);
            }
        });

    }

    public void deleteSong(int position) {


    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class LibrarySongsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.music_list_relative)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView_head)
        TextView songTitleTextview;

        @BindView(R.id.textView_descrip)
        TextView descriptionTextView;

        @BindView(R.id.artist_image)
        ImageView programImageView;

        @BindView(R.id.removeView)
        ImageView removeImageView;

        @BindView(R.id.song_duration)
        TextView songDuration;

        @BindView(R.id.addSongs)
        TextView addSongsTextView;


        public LibrarySongsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnLibraryHomeSongItemClickListener {

        public void onLibraryHomeSongItemClick(Song song, int position,List<Song> songs);
        public void onAddHomeSongItemClick(Song song);
        public void onRemoveHomeSongItemClick(Song song, int position,List<Song> songs);
    }

}