package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.Song;

public class RecentlyPlayedAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Song> mArrayList = new ArrayList<>();
    OnRecentlyPlayedItemClickListener itemClickListener;

    public RecentlyPlayedAdapter(Context mContext, List<Song> mArrayList, OnRecentlyPlayedItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recently_played_list_item, parent, false);
        return new RecentlyPLayedViewHolder(view);

    }


    public void setList(List<Song> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((RecentlyPLayedViewHolder) holder, position);


    }

    private void initLayoutOne(final RecentlyPLayedViewHolder holder, int pos) {
        final Song current = mArrayList.get(pos);
        holder.songTitleTextview.setText(current.getName());
        try {
            Picasso.with(mContext).load(URLDecoder.decode(current.getUrl(), "UTF-8"))
                    .placeholder(R.drawable.program)
                    .into(holder.programImageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.programImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onRecentlyPlayedItemClick(v, mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class RecentlyPLayedViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.song_title_textview)
        TextView songTitleTextview;

        @BindView(R.id.image)
        ImageView programImageView;

        public RecentlyPLayedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnRecentlyPlayedItemClickListener {
        public void onRecentlyPlayedItemClick(View view, Song song, int position);
    }

}