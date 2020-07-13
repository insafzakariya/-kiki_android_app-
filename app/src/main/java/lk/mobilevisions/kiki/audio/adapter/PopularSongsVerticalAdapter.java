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
import lk.mobilevisions.kiki.audio.model.dto.Song;

public class PopularSongsVerticalAdapter extends
        RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private static Context context;
    private static PopularSongsVerticalAdapter mInstance;
    private List<Song> popularSongsList;
    private OnPopularSongsItemActionListener onPopularSongsItemActionListener;

    public PopularSongsVerticalAdapter (Context mContext,
                                           List<Song> popularSongsList) {
        this.context = mContext;
        this.popularSongsList = popularSongsList;


    }
    public PopularSongsVerticalAdapter (Context mContext) {
        this.context = mContext;


    }
    public static PopularSongsVerticalAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PopularSongsVerticalAdapter(context);
        }
        return mInstance;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_recently_played, parent, false);
        return new PopularSongsRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((PopularSongsRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final PopularSongsRecyclerViewHolder holder, int pos) {
        final Song current = popularSongsList.get(pos);

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
                PopularSongsVerticalAdapter adapter = PopularSongsVerticalAdapter.getInstance(context);
                adapter.requestStream(popularSongsList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),popularSongsList);
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


    class PopularSongsRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView6)
        TextView titleTextView;

        @BindView(R.id.textView7)
        TextView descriptionTextView;

        @BindView(R.id.image)
        ImageView imageView;
        public PopularSongsRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    private void requestStream(Song song,int position,List<Song> songs) {
        if(onPopularSongsItemActionListener!=null){

            onPopularSongsItemActionListener.onPopularSongsPlayAction(song,position,songs);
        }
    }
    public void setOnPopularSongsItemActionListener (OnPopularSongsItemActionListener actionListener) {

        this.onPopularSongsItemActionListener = actionListener;
    }
    public interface OnPopularSongsItemActionListener{

        void onPopularSongsPlayAction(Song song,int position,List<Song> songs);
    }
}
