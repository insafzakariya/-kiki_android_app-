package lk.mobilevisions.kiki.audio.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.NavigationUtils;

public class YouMightAlsoLikeAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Song> mArrayList = new ArrayList<>();
    OnYouMightLikeItemClickListener itemClickListener;

    public YouMightAlsoLikeAdapter(Context mContext, List<Song> mArrayList, OnYouMightLikeItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_item, parent, false);
        return new YouMightLikeViewHolder(view);

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
        initLayoutOne((YouMightLikeViewHolder) holder, position);


    }

    private void initLayoutOne(final YouMightLikeViewHolder holder, int pos) {

        final Song current = mArrayList.get(pos);
        holder.songTitleTextview.setText(current.getName());
        holder.descriptionTextView.setText(current.getDescription());
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
                itemClickListener.onYouMightLikeItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });
        holder.addSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemClickListener.onAddSongsItemClick(mArrayList.get(holder.getAdapterPosition()));
//                final AlertDialog.Builder dialog = new AlertDialog.Builder (mContext); // Context, this, etc.
//                dialog.setView(R.layout.audio_alertdialog_view);
//                dialog.create();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class YouMightLikeViewHolder extends RecyclerView.ViewHolder {

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
        TextView addSongs;

        public YouMightLikeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnYouMightLikeItemClickListener {
        public void onYouMightLikeItemClick(Song song, int position,List<Song> songs);

        public void onAddSongsItemClick(Song song);
    }

}