package lk.mobilevisions.kiki.video.adapter;

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

import lk.mobilevisions.kiki.audio.util.NavigationUtils;
import lk.mobilevisions.kiki.modules.api.dto.Episode;

public class ProgramEpisodeAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Episode> episodeArrayList = new ArrayList<>();
    ProgramEpisodeAdapter.OnEpisodeItemClickListener itemClickListener;

    public ProgramEpisodeAdapter(Context mContext, List<Episode> episodeArrayList, ProgramEpisodeAdapter.OnEpisodeItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.episodeArrayList = episodeArrayList;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem_episodes, parent, false);
        return new ProgramEpisodeAdapter.EpisodesViewHolder(view);

    }

    public void setData(List<Episode> data) {
        System.out.println("dhdhdhdhdhdh 333  " + data.size());
        this.episodeArrayList = data;
        notifyDataSetChanged();
    }

    public void addData(List<Episode> data) {
        this.episodeArrayList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((ProgramEpisodeAdapter.EpisodesViewHolder) holder, position);


    }

    private void initLayoutOne(final ProgramEpisodeAdapter.EpisodesViewHolder holder, int pos) {

        final Episode current = episodeArrayList.get(pos);

        holder.textViewEpisodeName.setText(current.getName() );
        holder.textViewVideoDes.setText(current.getDescription());



        try {
            Picasso.with(mContext).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                    .placeholder(R.drawable.program)
                    .into(holder.imageViewEpisodeImage);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onEpisodeSongsItemClick(episodeArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),episodeArrayList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return episodeArrayList.size();
    }

    class EpisodesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.episode_relative)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textViewEpisodeName)
        TextView textViewEpisodeName;

        @BindView(R.id.textViewVideoDes)
        TextView textViewVideoDes;

        @BindView(R.id.imageViewEpisodeImage)
        ImageView imageViewEpisodeImage;


        public EpisodesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnEpisodeItemClickListener {
        public void onEpisodeSongsItemClick(Episode song, int position,List<Episode> episodes);
    }

}