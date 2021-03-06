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
import lk.mobilevisions.kiki.audio.model.dto.Song;

public class GenreSongsVerticalAdapter extends
        RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private static Context context;
    private static PopularSongsVerticalAdapter mInstance;
    private List<Song> popularSongsList;
    private OnGenreSongsItemActionListener onGenreSongsItemActionListener;

    public GenreSongsVerticalAdapter (Context mContext,
                                        List<Song> popularSongsList,OnGenreSongsItemActionListener onGenreSongsItemActionListener) {

        this.context = mContext;
        this.popularSongsList = popularSongsList;
        this.onGenreSongsItemActionListener = onGenreSongsItemActionListener;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_recently_played, parent, false);
        return new GenreSongsRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((GenreSongsRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final GenreSongsRecyclerViewHolder holder, int pos) {
        final Song current = popularSongsList.get(pos);

        holder.titleTextView.setText(current.getName());
        holder.descriptionTextView.setText(current.getDescription());
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

                onGenreSongsItemActionListener.onGenreSongsPlayAction(popularSongsList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),popularSongsList);

            }
        });
    }

    @Override
    public int getItemCount() {
        return popularSongsList.size();
    }

    public void setData(List<Song> songs) {
        this.popularSongsList = songs;
        notifyDataSetChanged();
    }

    public void addData(List<Song> songs) {
        this.popularSongsList.addAll(songs);
        notifyDataSetChanged();
    }


    class GenreSongsRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView6)
        TextView titleTextView;

        @BindView(R.id.textView7)
        TextView descriptionTextView;

        @BindView(R.id.image)
        ImageView imageView;
        public GenreSongsRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnGenreSongsItemActionListener{

        void onGenreSongsPlayAction(Song song,int position,List<Song> songs);
    }
}
