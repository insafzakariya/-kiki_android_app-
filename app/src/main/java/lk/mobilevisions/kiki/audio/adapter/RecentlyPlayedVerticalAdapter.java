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

public class RecentlyPlayedVerticalAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static Context context;
    private static RecentlyPlayedVerticalAdapter mInstance;
    private RecentlyPlayedItemActionListener recentlyPlayedItemActionListener;
    private Context mContext;
    private List<Song> recentlyPlayedList;
    public RecentlyPlayedVerticalAdapter(Context mContext,
                                         List<Song> recentlyPlayedList) {
        this.mContext = mContext;
        this.recentlyPlayedList = recentlyPlayedList;

    }
    public RecentlyPlayedVerticalAdapter(Context mContext) {
        this.mContext = mContext;


    }
    public static RecentlyPlayedVerticalAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RecentlyPlayedVerticalAdapter(context);
        }
        return mInstance;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_recently_played, parent, false);
            return new RecentlyPlayedRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                initLayoutTwo((RecentlyPlayedRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final RecentlyPlayedRecyclerViewHolder holder, int pos) {
        final Song current = recentlyPlayedList.get(pos);
        System.out.println("ehjhegdfyfh 111 " + current.getName());
        System.out.println("ehjhegdfyfh 222 " + current.getDescription());
        System.out.println("ehjhegdfyfh 333 " + current.getImage());
        holder.titleTextView.setText(current.getName());
        holder.descriptionTextView.setText(current.getDescription());


        if(current.getImage()!=null){
            try {
                Picasso.with(mContext).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                        .placeholder(R.drawable.program)
                        .into(holder.imageView);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentlyPlayedVerticalAdapter adapter = RecentlyPlayedVerticalAdapter.getInstance(context);
                adapter.requestStream(recentlyPlayedList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),recentlyPlayedList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentlyPlayedList.size();
    }


    class RecentlyPlayedRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView6)
        TextView titleTextView;

        @BindView(R.id.textView7)
        TextView descriptionTextView;

        @BindView(R.id.image)
        ImageView imageView;

        public RecentlyPlayedRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    private void requestStream(Song song,int position,List<Song> songs) {
        if(recentlyPlayedItemActionListener!=null){
            recentlyPlayedItemActionListener.onRecentlyPlayedPlayAction(song,position,songs);
        }
    }
    public void setActionListener(RecentlyPlayedItemActionListener actionListener){
        this.recentlyPlayedItemActionListener = actionListener;
    }
    public interface RecentlyPlayedItemActionListener{

        void onRecentlyPlayedPlayAction(Song song,int position,List<Song> songs);
    }


}