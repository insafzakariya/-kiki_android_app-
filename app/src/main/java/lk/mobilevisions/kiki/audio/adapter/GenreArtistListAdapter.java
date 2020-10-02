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
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.NavigationUtils;

public class GenreArtistListAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Artist> artistList;
    OnArtistItemClickListener itemClickListener;
    public boolean enabled = true;

    public GenreArtistListAdapter(Context mContext, List<Artist> artistList, OnArtistItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.artistList = artistList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_artist_list, parent, false);
        return new YouMightLikeViewHolder(view);

    }

    public void setEnbaled(boolean isEnable){
        this.enabled = isEnable;
    }

    public void setList(List<Artist> artistList) {
        this.artistList.addAll(artistList);
        notifyDataSetChanged();
    }
    public void addData(List<Artist> data) {
        this.artistList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((YouMightLikeViewHolder) holder, position);


    }

    private void initLayoutOne(final YouMightLikeViewHolder holder, int pos) {

        final Artist current = artistList.get(pos);
        holder.songTitleTextview.setText(current.getName());
        holder.songCountTextview.setText(current.getSongsCount() + " Songs");
        System.out.println("sdfsdfvsdv " + current.getSongsCount());
//        holder.songDuration.setText(NavigationUtils.convertMinutesToFormat(current.getDuration()));

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
                    itemClickListener.onArtistItemClick(artistList.get(holder.getAdapterPosition()),
                            holder.getAdapterPosition(),artistList);
            }
        });
        holder.addArtistTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enabled){
                    itemClickListener.onAddArtistItemClick(artistList.get(holder.getAdapterPosition()));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    class YouMightLikeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.artist_list_relative)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView_head)
        TextView songTitleTextview;

        @BindView(R.id.songCount)
        TextView songCountTextview;

        @BindView(R.id.addArtist)
        TextView addArtistTextview;

        @BindView(R.id.artist_image)
        ImageView programImageView;


        public YouMightLikeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnArtistItemClickListener {

        public void onArtistItemClick(Artist artist, int position,List<Artist> artistList);
        public void onAddArtistItemClick(Artist artist);
    }

}