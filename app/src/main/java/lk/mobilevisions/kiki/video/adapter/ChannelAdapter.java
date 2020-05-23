package lk.mobilevisions.kiki.video.adapter;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.modules.api.dto.Channel;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

    public static final int HORIZONTAL = 1;
    private Context context;
    private List<Channel> channels;
    private ProgramAdapter programAdapter;
    private LinearLayoutManager linearLayoutManager;
    ProgramAdapter.OnProgramItemClickListener itemClickListener;

    public ChannelAdapter(Context context, ProgramAdapter.OnProgramItemClickListener itemClickListener) {
        this.context = context;
        channels = new ArrayList<>();
        this.itemClickListener = itemClickListener;
    }


    public void addChannel(Channel channel) {
        channels.add(channel);
        notifyItemInserted(channels.size()-1);

    }
    public void removeChannels() {
        channels.clear();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_channel_header_item, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Channel channel = channels.get(position);
        holder.snapTextView.setText(channel.getName());

        linearLayoutManager = new LinearLayoutManager(holder.recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setOnFlingListener(null);
        new LinearSnapHelper().attachToRecyclerView(holder.recyclerView);
        programAdapter = new ProgramAdapter(context, channel.getProgramList(),itemClickListener);
        holder.recyclerView.setAdapter(programAdapter);

    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView snapTextView;
        public RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            snapTextView = (TextView) itemView.findViewById(R.id.channel_name_textview);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);

        }

    }
}

