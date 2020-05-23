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
import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.ui.main.home.ProgramsAdapter;
import timber.log.Timber;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    List<Program> programs;
    private Context context;
    VideosAdapter.OnProgramItemClickListener itemClickListener;

    private boolean disableEpisodeButton;
    private boolean disableSubscribedVideoButton;
    private boolean disablePlayIndicator;
    private boolean displayProgramImage;

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView imageViewProgram;
        public ImageView imageViewPlaying;

        public View rootView;
        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.textViewVideoName);
            rootView = view;
            imageViewProgram = (ImageView) view.findViewById(R.id.imageViewProgramImage);
            imageViewPlaying = (ImageView) view.findViewById(R.id.imageViewPlaying);

        }
    }

    public VideosAdapter(Context context, List<Program> programs, boolean displayProgramImage, VideosAdapter.OnProgramItemClickListener itemClickListener) {
        this.context = context;
        this.programs = programs;
        this.displayProgramImage = displayProgramImage;
        this.itemClickListener = itemClickListener;
    }

    public VideosAdapter(Context context, List<Program> programs, VideosAdapter.OnProgramItemClickListener itemClickListener,
                           boolean disableEpisodeButton, boolean disableSubscribedVideoButton, boolean disablePlayIndicator,
                           boolean displayProgramImage) {
        this.context = context;
        this.programs = programs;
        this.itemClickListener = itemClickListener;
        this.disableEpisodeButton = disableEpisodeButton;
        this.disableSubscribedVideoButton = disableSubscribedVideoButton;
        this.displayProgramImage = displayProgramImage;
        this.disablePlayIndicator = disablePlayIndicator;
    }

    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.videos_list_item, parent, false);
        VideosAdapter.ViewHolder viewHolder = new VideosAdapter.ViewHolder(view);

        if(disablePlayIndicator){
            viewHolder.imageViewPlaying.setVisibility(View.GONE);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VideosAdapter.ViewHolder holder, int position) {
        if(Integer.parseInt(programs.get(position).getEpisode().getId()) ==
                Application.PLAYER.getCurrentEpisode()){
            //holder.imageViewPlaying.setImageResource(R.drawable.anim_blink);
            //AnimationDrawable frameAnimation = (AnimationDrawable) holder.imageViewPlaying.getDrawable();
            //frameAnimation.start();
            if(!disablePlayIndicator){
                holder.imageViewPlaying.setVisibility(View.VISIBLE);
            }
        }else{
            holder.imageViewPlaying.setVisibility(View.INVISIBLE);
        }
        holder.mTextView.setText(programs.get(position).getName());
        try {
            if(displayProgramImage){
                Picasso.with(context).load(URLDecoder.decode(programs.get(position).getImage(), "UTF-8"))
                        .resize(720, 340)
                        .placeholder(R.drawable.program)
                        .into(holder.imageViewProgram);
            }else{
                Picasso.with(context).load(URLDecoder.decode(programs.get(position).getEpisode().getImage(), "UTF-8"))
                        .resize(720, 340)
                        .placeholder(R.drawable.program)
                        .into(holder.imageViewProgram);
            }

        } catch (UnsupportedEncodingException e) {
            Timber.e(e);
        }





        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onProgramItemClick(view, programs.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(), new ProgramsAdapter.ProgramItemClickCallback() {
                            @Override
                            public void onSubscribe(boolean isSubscribed) {

                            }

                            @Override
                            public void onUnSubscribe(boolean isUnSubscribed) {

                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }


    public interface OnProgramItemClickListener {
        public void onProgramItemClick(View view, Program program, int position, ProgramsAdapter.ProgramItemClickCallback clickCallback);
    }

    public interface ProgramItemClickCallback{
        public void onSubscribe(boolean isSubscribed);
        public void onUnSubscribe(boolean isUnSubscribed);
    }

}