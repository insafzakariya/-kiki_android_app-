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

public class YouAlsoMightLikeVerticalAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static Context context;
    private static YouAlsoMightLikeVerticalAdapter mInstance;
    private List<Song> youMightAlsoLikeList;
    private OnYouMightAlsoLikeItemActionListener onYouMightAlsoLikeItemActionListener;

    public YouAlsoMightLikeVerticalAdapter(Context mContext,
                                           List<Song> youMightAlsoLikeList) {
        this.context = mContext;
        this.youMightAlsoLikeList = youMightAlsoLikeList;

    }
    public YouAlsoMightLikeVerticalAdapter(Context mContext) {
        this.context = mContext;


    }
    public static YouAlsoMightLikeVerticalAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new YouAlsoMightLikeVerticalAdapter(context);
        }
        return mInstance;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_popular_artists, parent, false);
        return new YouMightAlsoLikeRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((YouMightAlsoLikeRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final YouMightAlsoLikeRecyclerViewHolder holder, int pos) {
        final Song current = youMightAlsoLikeList.get(pos);

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
                YouAlsoMightLikeVerticalAdapter adapter = YouAlsoMightLikeVerticalAdapter.getInstance(context);
                adapter.requestStream(youMightAlsoLikeList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),youMightAlsoLikeList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return youMightAlsoLikeList.size();
    }


    class YouMightAlsoLikeRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView6)
        TextView titleTextView;

        @BindView(R.id.textView7)
        TextView descriptionTextView;

        @BindView(R.id.image)
        ImageView imageView;
        public YouMightAlsoLikeRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    private void requestStream(Song song,int position,List<Song> songs) {
        if(onYouMightAlsoLikeItemActionListener!=null){
            onYouMightAlsoLikeItemActionListener.onYouMightAlsoLikePlayAction(song,position,songs);
        }
    }
    public void setYouMightAlsoLikeItemActionListenerActionListener(OnYouMightAlsoLikeItemActionListener actionListener){
        this.onYouMightAlsoLikeItemActionListener = actionListener;
    }
    public interface OnYouMightAlsoLikeItemActionListener{

        void onYouMightAlsoLikePlayAction(Song song,int position,List<Song> songs);
    }
}