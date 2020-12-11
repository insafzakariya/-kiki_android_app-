package lk.mobilevisions.kiki.audio.adapter;

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
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;

public class RadioDramaVerticalAdapter extends
        RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private static Context context;
    private static PopularSongsVerticalAdapter mInstance;
    private List<PlayList> playlistList;
    private RadioDramaVerticalAdapter.OnRadioDramaItemActionListener onRadioDramaItemActionListener;

    public RadioDramaVerticalAdapter (Context mContext,
                                    List<PlayList> playlistList, OnRadioDramaItemActionListener onPlaylistItemActionListener) {

        this.context = mContext;
        this.playlistList = playlistList;
        this.onRadioDramaItemActionListener = onPlaylistItemActionListener;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal, parent, false);
        return new RadioDramaVerticalAdapter.RadioDramaRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((RadioDramaRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final RadioDramaRecyclerViewHolder holder, int pos) {
        final PlayList current = playlistList.get(pos);

        holder.titleTextView.setText(current.getName());
        if(current.getImage()!=null && !current.getImage().isEmpty()){
            try {
                Picasso.with(context).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                        .placeholder(R.drawable.program)
                        .into(holder.imageView);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {

            Picasso.with(context).load(R.drawable.program)
                    .placeholder(R.drawable.program)
                    .into(holder.imageView);

        }
        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onRadioDramaItemActionListener.onRadioDramaAction(playlistList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),playlistList);

            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public void setData(List<PlayList> songs) {
        this.playlistList = songs;
        notifyDataSetChanged();
    }

    public void addData(List<PlayList> songs) {
        this.playlistList.addAll(songs);
        notifyDataSetChanged();
    }


    class RadioDramaRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView2)
        TextView titleTextView;

        @BindView(R.id.image)
        ImageView imageView;

        public RadioDramaRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnRadioDramaItemActionListener{

        void onRadioDramaAction(PlayList playList,int position,List<PlayList> playLists);
    }
}