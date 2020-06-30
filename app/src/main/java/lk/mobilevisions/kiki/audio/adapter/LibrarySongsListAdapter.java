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
import lk.mobilevisions.kiki.audio.util.NavigationUtils;

public class LibrarySongsListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Song> mArrayList = new ArrayList<>();
    OnLibrarySongsItemClickListener itemClickListener;

    public LibrarySongsListAdapter(Context mContext, List<Song> mArrayList, OnLibrarySongsItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_song_list_item, parent, false);
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

        if(Application.getInstance().getSongsAddedToPlaylist().contains(current.getId())){
            holder.addedSongText.setVisibility(View.VISIBLE);

        }else if(current.isAvailable()){
            holder.addedSongText.setVisibility(View.VISIBLE);
        }else{
            holder.addedSongText.setVisibility(View.GONE);
        }

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
                itemClickListener.onLibrarySongsItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });

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

        @BindView(R.id.song_duration)
        TextView songDuration;

        @BindView(R.id.addedSongs)
        TextView addedSongText;


        public LibrarySongsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnLibrarySongsItemClickListener {
        public void onLibrarySongsItemClick(Song song, int position,List<Song> songs);
    }

}