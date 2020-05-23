/**
 * Created by Chatura Dilan Perera on 9/10/2016.
 */
package lk.mobilevisions.kiki.ui.main.home;

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

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder>  {

    List<Episode> episodes;
    private Context context;
    EpisodesAdapter.OnEpisodesItemClickListener itemClickListener;
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

    public EpisodesAdapter(Context context, List<Episode> episodes, Program program, EpisodesAdapter.OnEpisodesItemClickListener itemClickListener) {
        this.context = context;
        this.episodes = episodes;
        this.itemClickListener = itemClickListener;
        this.program = program;
    }

    public EpisodesAdapter(Context context, List<Episode> episodes, EpisodesAdapter.OnEpisodesItemClickListener itemClickListener) {
        this.context = context;
        this.episodes = episodes;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public EpisodesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_listitem_episodes, parent, false);
        EpisodesAdapter.ViewHolder viewHolder = new EpisodesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final EpisodesAdapter.ViewHolder holder, int position) {


            holder.textViewEpisodeName.setText(episodes.get(position).getName() );
            holder.textViewEpisodeDes.setText(episodes.get(position).getDescription() );

        System.out.println("checking url**** " + episodes.get(position).getName());


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
