/**
 * Created by Chatura Dilan Perera on 19/1/2017.
 */
package lk.mobilevisions.kiki.ui.main.home;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import timber.log.Timber;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.modules.api.dto.Program;

public class ProgramsAdapter extends RecyclerView.Adapter<ProgramsAdapter.ViewHolder> {

    List<Program> programs;
    private Context context;
    ProgramsAdapter.OnProgramItemClickListener itemClickListener;

    private boolean disableEpisodeButton;
    private boolean disableSubscribedVideoButton;
    private boolean disablePlayIndicator;
    private boolean displayProgramImage;

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView imageViewProgram;
        public ImageButton imageButtonEpisodesList;
        public ImageButton imageButtonSubscribe;
        public ImageView imageViewPlaying;
        public TextView textViewSubscribe;
        public LinearLayout layoutEpisodeButton;
        public LinearLayout layoutSubscribedVideoButton;
        public View rootView;
        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.textViewVideoName);
            rootView = view;
            imageViewProgram = (ImageView) view.findViewById(R.id.imageViewProgramImage);
            imageButtonEpisodesList = (ImageButton) view.findViewById(R.id.imageButtonEpisodesList);
            imageButtonSubscribe = (ImageButton)view.findViewById(R.id.imageButtonSubscribe);
            imageViewPlaying = (ImageView) view.findViewById(R.id.imageViewPlaying);
            textViewSubscribe = (TextView) view.findViewById(R.id.textViewSubscribe);
            layoutEpisodeButton = (LinearLayout) view.findViewById(R.id.layoutEpisodesButton);
            layoutSubscribedVideoButton = (LinearLayout) view.findViewById(R.id.layoutSubscribeButton);
        }
    }

    public ProgramsAdapter(Context context, List<Program> programs, boolean displayProgramImage, ProgramsAdapter.OnProgramItemClickListener itemClickListener) {
        this.context = context;
        this.programs = programs;
        this.displayProgramImage = displayProgramImage;
        this.itemClickListener = itemClickListener;
    }

    public ProgramsAdapter(Context context, List<Program> programs, ProgramsAdapter.OnProgramItemClickListener itemClickListener,
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
    public ProgramsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_listitem_programs, parent, false);
        ProgramsAdapter.ViewHolder viewHolder = new ProgramsAdapter.ViewHolder(view);
        if(disableEpisodeButton){
            viewHolder.layoutEpisodeButton.setVisibility(View.GONE);
        }
        if(disableSubscribedVideoButton){
            viewHolder.layoutSubscribedVideoButton.setVisibility(View.GONE);
        }
        if(disablePlayIndicator){
            viewHolder.imageViewPlaying.setVisibility(View.GONE);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ProgramsAdapter.ViewHolder holder, final int position) {
        if(Integer.parseInt(programs.get(position).getEpisode().getId()) == Application.PLAYER.getCurrentEpisode()){
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

        if(programs.get(position).isSubscribed()){
            ((ImageButton)holder.imageButtonSubscribe).setImageResource(R.drawable.ic_yes_subscribe);
            holder.textViewSubscribe.setText("Subscribed");
        }else{
            ((ImageButton)holder.imageButtonSubscribe).setImageResource(R.drawable.ic_no_subscribe);
            holder.textViewSubscribe.setText("Subscribe");
        }

        holder.imageButtonEpisodesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getAdapterPosition()>=0) {
                    itemClickListener.onProgramItemClick(view, programs.get(position),
                            position, new ProgramsAdapter.ProgramItemClickCallback() {
                                @Override
                                public void onSubscribe(boolean isSubscribed) {

                                }

                                @Override
                                public void onUnSubscribe(boolean isUnSubscribed) {

                                }
                            });
                }
            }
        });
        holder.imageButtonSubscribe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view) {
                if(holder.getAdapterPosition()>=0) {
                    itemClickListener.onProgramItemClick(view, programs.get(position),
                            position, new ProgramsAdapter.ProgramItemClickCallback() {
                                @Override
                                public void onSubscribe(boolean isSubscribed) {
                                    if (isSubscribed) {
                                        programs.get(position).setSubscribed(true);
                                        ((ImageButton) view).setImageResource(R.drawable.ic_yes_subscribe);
                                        holder.textViewSubscribe.setText("Subscribed");
                                    }
                                }

                                @Override
                                public void onUnSubscribe(boolean isUnSubscribed) {
                                    if (isUnSubscribed) {
                                        programs.get(position).setSubscribed(false);
                                        ((ImageButton) view).setImageResource(R.drawable.ic_no_subscribe);
                                        holder.textViewSubscribe.setText("Subscribe");
                                    }
                                }
                            });
                }
            }
        });

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getAdapterPosition()>=0) {
                    itemClickListener.onProgramItemClick(view, programs.get(position),
                            position, new ProgramsAdapter.ProgramItemClickCallback() {
                                @Override
                                public void onSubscribe(boolean isSubscribed) {

                                }

                                @Override
                                public void onUnSubscribe(boolean isUnSubscribed) {

                                }
                            });
                }
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