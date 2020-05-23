package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.Genre;

public class BrowseAllSongsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Genre> mArrayList = new ArrayList<>();
    OnBrowseAllItemClickListener itemClickListener;

    int[] gradiantList = new int[]{R.drawable.gradiant_one, R.drawable.gradiant_two, R.drawable.gradiant_three, R.drawable.gradiant_four};

    public BrowseAllSongsAdapter(Context mContext, List<Genre> mArrayList, OnBrowseAllItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        System.out.println("checking genres 33333 " + mArrayList.size());
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_genre_tile_item, parent, false);
        return new BrowseAllViewHolder(view);

    }


    public void setList(List<Genre> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        System.out.println("checking genres 4444444 " + this.mArrayList.size());
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        System.out.println("checking genres 5555 " + this.mArrayList.size());
        initLayoutOne((BrowseAllViewHolder) holder, position);
        System.out.println("check bind 56415631");

    }

    private void initLayoutOne(final BrowseAllViewHolder holder, int pos) {
        System.out.println("checking genres 666666 " );
        final Genre current = mArrayList.get(pos);
        System.out.println("checking genres 777777 " + current.getName());
        System.out.println("checking name " + current.getName());
        holder.songTitleTextview.setText(current.getName());
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//        holder.programImageView.setBackgroundColor(currentColor);


        int colors = Color.parseColor(current.getColor());
        holder.programImageView.setBackgroundColor(colors);


        holder.programImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.OnBrowseAllItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        System.out.println("checking list " + mArrayList.size());
        return mArrayList.size();

    }

    class BrowseAllViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.song_title_textview)
        TextView songTitleTextview;

        @BindView(R.id.imageGenres)
        ImageView programImageView;

        public BrowseAllViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnBrowseAllItemClickListener {
        public void OnBrowseAllItemClick(Genre genre, int position);
    }

}