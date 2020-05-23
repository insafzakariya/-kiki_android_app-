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

public class LatestSongsVerticalAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static Context context;
    private static LatestSongsVerticalAdapter mInstance;
    private List<Song> latestSongsList;
    private OnLatestSongsItemActionListener onLatestSongsItemActionListener;

    public LatestSongsVerticalAdapter(Context mContext,
                                           List<Song> latestSongsList) {
        this.context = mContext;
        this.latestSongsList = latestSongsList;

    }
    public LatestSongsVerticalAdapter(Context mContext) {
        this.context = mContext;


    }
    public static LatestSongsVerticalAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LatestSongsVerticalAdapter(context);
        }
        return mInstance;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_recently_played, parent, false);
        return new LatestSongsVerticalAdapter.LatestSongsRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((LatestSongsVerticalAdapter.LatestSongsRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final LatestSongsVerticalAdapter.LatestSongsRecyclerViewHolder holder, int pos) {
        final Song current = latestSongsList.get(pos);

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
                LatestSongsVerticalAdapter adapter = LatestSongsVerticalAdapter.getInstance(context);
                adapter.requestStream(latestSongsList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),latestSongsList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return latestSongsList.size();
    }


    class LatestSongsRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView6)
        TextView titleTextView;

        @BindView(R.id.textView7)
        TextView descriptionTextView;

        @BindView(R.id.image)
        ImageView imageView;
        public LatestSongsRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    private void requestStream(Song song,int position,List<Song> songs) {
        if(onLatestSongsItemActionListener!=null){
            onLatestSongsItemActionListener.onLatestSongsPlayAction(song,position,songs);
        }
    }
    public void setLatestSongsrActionListener(LatestSongsVerticalAdapter.OnLatestSongsItemActionListener actionListener){
        this.onLatestSongsItemActionListener = actionListener;
    }
    public interface OnLatestSongsItemActionListener{

        void onLatestSongsPlayAction(Song song,int position,List<Song> songs);
    }
}