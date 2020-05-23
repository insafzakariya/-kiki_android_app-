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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.Song;

public class ArtistListAdapter extends
        RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private static Context mcontext;
    private static ArtistListAdapter mInstance;
    private List<Artist> artistList;
    OnArtistListItemActionListener onArtistListItemActionListener;

    public ArtistListAdapter(Context mContext, List<Artist> artistList, OnArtistListItemActionListener onArtistListItemActionListener) {
        this.mcontext = mContext;
        this.artistList = artistList;
        this.onArtistListItemActionListener = onArtistListItemActionListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_artist_list, parent, false);
        return new ArtistListRecyclerViewHolder(view);

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

        initLayoutTwo((ArtistListRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final ArtistListRecyclerViewHolder holder, int pos) {

        final Artist current = artistList.get(pos);
        holder.titleTextView.setText(current.getName());
        holder.songCountTextView.setText(current.getSongsCount() + " Songs");

        try {
            Picasso.with(mcontext).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                    .placeholder(R.drawable.program)
                    .into(holder.imageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onArtistListItemActionListener.onArtistListItemClick(artistList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),artistList);
            }
        });
        holder.addArtistTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onArtistListItemActionListener.onAddArtistItemClick(artistList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }



    class ArtistListRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.artist_list_relative)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView_head)
        TextView titleTextView;

        @BindView(R.id.songCount)
        TextView songCountTextView;

        @BindView(R.id.addArtist)
        TextView addArtistTextView;

        @BindView(R.id.artist_image)
        ImageView imageView;

        public ArtistListRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnArtistListItemActionListener {

        public void onArtistListItemClick(Artist artist, int position,List<Artist> artistList);
        public void onAddArtistItemClick(Artist artist);
    }
}