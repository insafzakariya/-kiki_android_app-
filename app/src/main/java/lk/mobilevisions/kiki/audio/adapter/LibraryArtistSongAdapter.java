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

public class LibraryArtistSongAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Song> mArrayList = new ArrayList<>();
    OnLibraryArtistItemClickListener itemClickListener;

    public LibraryArtistSongAdapter(Context mContext, List<Song> mArrayList, OnLibraryArtistItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_home_song_list, parent, false);
        return new LibraryArtistViewHolder(view);

    }


    public void setList(List<Song> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }
    public void addData(List<Song> data) {
        this.mArrayList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((LibraryArtistViewHolder) holder, position);


    }

    private void initLayoutOne(final LibraryArtistViewHolder holder, int pos) {

        final Song current = mArrayList.get(pos);
        holder.songTitleTextview.setText(current.getName());
        holder.descriptionTextView.setText(current.getDescription());

        int duration = Integer.parseInt(current.getDuration());
        holder.songDuration.setText(timeConversion(duration));

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
                itemClickListener.onLibraryArtistItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });
        holder.addSongTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onAddSongLibraryItemClick(mArrayList.get(holder.getAdapterPosition()));
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

    class LibraryArtistViewHolder extends RecyclerView.ViewHolder {

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

        @BindView(R.id.addSongs)
        TextView addSongTextView;


        public LibraryArtistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnLibraryArtistItemClickListener {
        public void onLibraryArtistItemClick(Song song, int position,List<Song> songs);
        public void onAddSongLibraryItemClick(Song song);
    }

}