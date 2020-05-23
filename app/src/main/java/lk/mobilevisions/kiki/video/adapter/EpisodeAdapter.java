package lk.mobilevisions.kiki.video.adapter;

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
import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.modules.api.dto.Episode;
import lk.mobilevisions.kiki.modules.api.dto.Program;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder>  {

    List<Episode> episodes;
    private Context context;
    OnEpisodesItemClickListener itemClickListener;
    Program program;
    private boolean disablePlayIndicator;


    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewEpisodeName;
        public TextView textViewEpisodeDes;
        public ImageView imageViewEpisode;

        public View rootView;
        public ViewHolder(View view) {
            super(view);
            rootView = view;
            textViewEpisodeName = (TextView) view.findViewById(R.id.textViewEpisodeName);
            textViewEpisodeDes = (TextView) view.findViewById(R.id.textViewVideoDes);
            imageViewEpisode = (ImageView) view.findViewById(R.id.imageViewEpisodeImage);
        }
    }



    public EpisodeAdapter(Context context, List<Episode> episodes, OnEpisodesItemClickListener itemClickListener) {
        this.context = context;
        this.episodes = episodes;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_listitem_episodes, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        holder.textViewEpisodeName.setText(episodes.get(position).getName() );
        holder.textViewEpisodeDes.setText(episodes.get(position).getDescription());



        try {
            Picasso.with(context).load(URLDecoder.decode(episodes.get(position).getImage(), "UTF-8"))
                    .placeholder(R.drawable.program)
                    .into(holder.imageViewEpisode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onEpisodeItemClick(view, episodes.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public interface OnEpisodesItemClickListener {
        public void onEpisodeItemClick(View view, Episode episode, int position);
    }

}
