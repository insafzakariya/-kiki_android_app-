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

public class LibraryArtistDetailSongsAdapter extends
        RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private static Context context;
    private static PopularSongsVerticalAdapter mInstance;
    private List<Song> artistSongsList = new ArrayList<>();
    private LibraryArtistDetailSongsAdapter.OnArtistSongsItemActionListener onArtistSongsItemActionListener;

    public LibraryArtistDetailSongsAdapter (Context mContext,OnArtistSongsItemActionListener onGenreSongsItemActionListener) {

        this.context = mContext;
        this.onArtistSongsItemActionListener = onGenreSongsItemActionListener;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_recently_played, parent, false);
        return new LibraryArtistSongsRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((LibraryArtistSongsRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final LibraryArtistSongsRecyclerViewHolder holder, int pos) {
        final Song current = artistSongsList.get(pos);
        System.out.println("dhhdhdhdhd 333 " + current.getName());

        if(Application.getInstance().getSongsAddedToPlaylist().contains(current.getId())){
            holder.overlayImageView.setVisibility(View.VISIBLE);
            holder.overlayTextView.setVisibility(View.VISIBLE);
        }else{
            holder.overlayImageView.setVisibility(View.GONE);
            holder.overlayTextView.setVisibility(View.GONE);
        }

        holder.titleTextView.setText(current.getName());
        holder.descriptionTextView.setText(current.getArtistName());
        try {
            Picasso.with(context).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                    .placeholder(R.drawable.program)
                    .into(holder.imageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onArtistSongsItemActionListener.onArtistSongsPlayAction(artistSongsList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),artistSongsList);

            }
        });
    }

    @Override
    public int getItemCount() {
        return artistSongsList.size();
    }

    public void setData(List<Song> songs) {
        System.out.println("dhhdhdhdhd 222 " + songs.size());
        this.artistSongsList = songs;
        notifyDataSetChanged();
    }

    public void addData(List<Song> songs) {
        this.artistSongsList.addAll(songs);
        notifyDataSetChanged();
    }


    class LibraryArtistSongsRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView6)
        TextView titleTextView;

        @BindView(R.id.textView7)
        TextView descriptionTextView;

        @BindView(R.id.overLayText)
        TextView overlayTextView;

        @BindView(R.id.image)
        ImageView imageView;

        @BindView(R.id.overlay)
        ImageView overlayImageView;

        public LibraryArtistSongsRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnArtistSongsItemActionListener{

        void onArtistSongsPlayAction(Song song,int position,List<Song> songs);
    }
}