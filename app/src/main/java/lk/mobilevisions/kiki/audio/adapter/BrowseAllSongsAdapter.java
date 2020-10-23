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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.Genre;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

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

        final Genre current = mArrayList.get(pos);
//        holder.songTitleTextview.setText(current.getName());
//        Random rnd = new Random();
//        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//        holder.programImageView.setBackgroundColor(currentColor);

//        DrawableCrossFadeFactory factory =
//                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        try {
            Picasso.with(mContext).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                    .placeholder(R.color.md_black_999)
                    .into(holder.programImageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        int colors = Color.parseColor(current.getColor());
//        holder.programImageView.setBackgroundColor(colors);


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