package lk.mobilevisions.kiki.audio.adapter;

import android.app.AlertDialog;
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

public class AddSongToPlaylistDialogAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PlayList> mArrayList = new ArrayList<>();
    OnPlaylistDialogItemClickListener itemClickListener;

    public AddSongToPlaylistDialogAdapter(Context mContext, List<PlayList> mArrayList, OnPlaylistDialogItemClickListener itemClickListener ) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alertdialog_playlist_list, parent, false);
        return new PlaylistDialogViewHolder(view);

    }


    public void setList(List<PlayList> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((PlaylistDialogViewHolder) holder, position);


    }

    private void initLayoutOne(final PlaylistDialogViewHolder holder, int pos) {
        final PlayList current = mArrayList.get(pos);
        holder.songTitleTextview.setText(current.getName());
//        holder.songCount.setText(current.getSongCount() + " songs");
        holder.artistTextView.setText(current.getArtistName());

        if(current.getImage()!=null && !current.getImage().isEmpty()) {
            try {
                Picasso.with(mContext).load(URLDecoder.decode(current.getImage(), "UTF-8")).fit().centerCrop()
                        .placeholder(R.drawable.program)
                        .into(holder.playlistImageView);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {

            Picasso.with(mContext).load(R.drawable.program)
                    .placeholder(R.drawable.program)
                    .into(holder.playlistImageView);

        }

        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPlaylistDialogItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class PlaylistDialogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.playlistListLayout)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView_head)
        TextView songTitleTextview;

        @BindView(R.id.artist_text)
        TextView artistTextView;

        @BindView(R.id.playlist_image)
        ImageView playlistImageView;

        public PlaylistDialogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnPlaylistDialogItemClickListener {
        public void onPlaylistDialogItemClick(PlayList song, int position,List<PlayList> songs);
    }

}