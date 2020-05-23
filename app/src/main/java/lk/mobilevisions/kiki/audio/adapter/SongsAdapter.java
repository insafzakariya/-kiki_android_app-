package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
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

import lk.mobilevisions.kiki.R;

import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.NavigationUtils;

public class SongsAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Song> mArrayList = new ArrayList<>();
    OnAudioSongsItemClickListener onAudioItemClickListener;

    public SongsAdapter(Context mContext, OnAudioSongsItemClickListener onAudioItemClickListener) {
        this.mContext = mContext;
        this.onAudioItemClickListener = onAudioItemClickListener;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_list_item, parent, false);
        return new SongsViewHolder(view);

    }


    public void setList(List<Song> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((SongsViewHolder) holder, position);


    }

    private void initLayoutOne(final SongsViewHolder holder, int pos) {
        final Song current = mArrayList.get(pos);
        holder.titleTextView.setText(current.getName());
        holder.desTextView.setText(current.getDescription());
        holder.songDuration.setText(NavigationUtils.convertMinutesToFormat(current.getDuration()));
        if(current.getImage()!=null){
            try {
                Picasso.with(mContext).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                        .placeholder(R.drawable.program)
                        .into(holder.imageViewSong);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


//        holder.addToPlayListImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onAudioItemClickListener.onAudioSongsMoreItemClick(v, mArrayList.get(holder.getAdapterPosition()),
//                        holder.getAdapterPosition());
//
//            }
//        });

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                holder.imageViewPlay.setVisibility(View.GONE);
//                holder.imageViewPause.setVisibility(View.VISIBLE);

                onAudioItemClickListener.onAudioSongsPlayItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition());

            }
        });
        holder.addTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onAudioItemClickListener.onAddSongsItemClick(mArrayList.get(holder.getAdapterPosition()));

            }
        });


    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class SongsViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public ImageView imageViewSong;
        public ImageView imageViewPlay;
        public ImageView imageViewPause;
//        public ImageView addToPlayListImageView;
        public TextView titleTextView;
        public TextView desTextView;
        public TextView addTextView;
        public TextView songDuration;


        public SongsViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            imageViewPlay = (ImageView) itemView.findViewById(R.id.song_thumb_imageview);
            imageViewPause = (ImageView) itemView.findViewById(R.id.pause_imageview);
            imageViewSong = (ImageView) itemView.findViewById(R.id.song_thumb_imageview);
//            addToPlayListImageView = (ImageView) itemView.findViewById(R.id.more_imageview);
            titleTextView = (TextView) itemView.findViewById(R.id.song_name_textview);
            songDuration = (TextView) itemView.findViewById(R.id.song_duration_search);
            addTextView = (TextView) itemView.findViewById(R.id.addSongs);
            desTextView = (TextView) itemView.findViewById(R.id.song_artist_textview);
        }
    }

    public interface OnAudioSongsItemClickListener {

        public void onAudioSongsMoreItemClick(View view, Song song, int position);

        public void onAudioSongsPlayItemClick(Song song, int position);

        public void onAddSongsItemClick(Song song);
    }

}