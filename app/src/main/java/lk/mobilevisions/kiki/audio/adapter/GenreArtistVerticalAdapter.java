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
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.Artist;

public class GenreArtistVerticalAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static Context context;
    private static ArtistsVerticalAdapter mInstance;
    private List<Artist> artistsList;
    private OnArtistsItemActionListener onArtistsItemActionListener;

    public GenreArtistVerticalAdapter(Context mContext,
                                  List<Artist> artistsList,OnArtistsItemActionListener onArtistsItemActionListener) {
        this.context = mContext;
        this.artistsList = artistsList;
        this.onArtistsItemActionListener = onArtistsItemActionListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_popular_artists, parent, false);
        return new ArtistesRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((ArtistesRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final ArtistesRecyclerViewHolder holder, int pos) {
        final Artist current = artistsList.get(pos);

        holder.titleTextView.setText(current.getName());

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
                onArtistsItemActionListener.onArtistsPlayAction(artistsList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),artistsList);


            }
        });
    }

    @Override
    public int getItemCount() {
        return artistsList.size();
    }


    class ArtistesRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView6)
        TextView titleTextView;

        @BindView(R.id.textView7)
        TextView descriptionTextView;

        @BindView(R.id.image)
        ImageView imageView;
        public ArtistesRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnArtistsItemActionListener{

        void onArtistsPlayAction(Artist artist,int position,List<Artist> artistList);
    }
}