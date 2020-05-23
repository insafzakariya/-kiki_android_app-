package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.fragment.ChannelFragment;
import lk.mobilevisions.kiki.audio.model.dto.AudioChannel;
import lk.mobilevisions.kiki.audio.model.dto.AudioProgram;
import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;

public class ChannelVerticalAdapter extends
        RecyclerView.Adapter<ChannelVerticalAdapter.VerticalRecyclerViewHolder> {

    private Context mContext;
    private List<AudioChannel> mArrayList = new ArrayList<>();
    private ChannelFragment channelFragment;
    OnAudioChannelItemClickListener itemClickListener;
    ChannelHorizontalAdapter.OnAudioProgramItemClickListener onAudioProgramItemClickListener;

    public ChannelVerticalAdapter(Context mContext, List<AudioChannel> mArrayList, ChannelFragment channelFragment,OnAudioChannelItemClickListener itemClickListener,ChannelHorizontalAdapter.OnAudioProgramItemClickListener onAudioProgramItemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.channelFragment = channelFragment;
        this.itemClickListener = itemClickListener;
        this.onAudioProgramItemClickListener = onAudioProgramItemClickListener;
        System.out.println("checking getAllAudioChannels 55555  " + mArrayList.size());

    }

    @Override
    public VerticalRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_vertical_list_item, parent, false);
        return new VerticalRecyclerViewHolder(view);
    }

    public void setList(ArrayList<AudioChannel> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final VerticalRecyclerViewHolder holder, int position) {
        System.out.println("checking getAllAudioChannels 8888  " + position);
        final AudioChannel current = mArrayList.get(position);

        final String channelName = current.getName();
        System.out.println("checking getAllAudioChannels 66666  " + channelName);
        holder.titleTextView.setText(channelName);

        List<AudioProgram> audioPrograms = current.getPrograms();


        ChannelHorizontalAdapter itemListDataAdapter =
                new ChannelHorizontalAdapter(mContext, audioPrograms,onAudioProgramItemClickListener);
        holder.rvHorizontal.addItemDecoration(new SpacesItemDecoration(15));
        holder.rvHorizontal.setHasFixedSize(true);
        holder.rvHorizontal.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false));
        holder.rvHorizontal.setAdapter(itemListDataAdapter);

        holder.rvHorizontal.setNestedScrollingEnabled(false);
        holder.seeAllTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onAudioChannelItemClick(v, mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition());
            }
        });


    }

    @Override
    public int getItemCount() {
        System.out.println("checking getAllAudioChannels 77777  " + mArrayList.size());
        return mArrayList.size();
    }

    class VerticalRecyclerViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.rvHorizontal)
        RecyclerView rvHorizontal;

        @BindView(R.id.tvseeAll)
        TextView seeAllTextview;

        @BindView(R.id.tvTitle)
        TextView titleTextView;

        public VerticalRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnAudioChannelItemClickListener {
        public void onAudioChannelItemClick(View view, AudioChannel channel, int position);
    }
}
