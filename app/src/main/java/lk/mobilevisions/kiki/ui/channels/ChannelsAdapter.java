/**
 * Created by Chatura Dilan Perera on 9/10/2016.
 */
package lk.mobilevisions.kiki.ui.channels;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.modules.api.dto.Channel;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ViewHolder>  {

    List<Channel> channels;
    private Context context;
    OnChannelItemClickListener itemClickListener;


    public  class ViewHolder extends RecyclerView.ViewHolder {
        //public TextView textViewChannelName;
        public ImageView imageViewChannel;
        public View rootView;
        public ViewHolder(View view) {
            super(view);
            rootView = view;
           // textViewChannelName = (TextView) view.findViewById(R.id.textViewChannelName);
            imageViewChannel = (ImageView) view.findViewById(R.id.imageViewChannelImage);
        }
    }

    public ChannelsAdapter(Context context, List<Channel> channels, OnChannelItemClickListener itemClickListener) {
        System.out.println("checking channels 22222  " + channels);
        this.context = context;
        this.channels = channels;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ChannelsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_listitem_channel, parent, false);
        ChannelsAdapter.ViewHolder viewHolder = new ChannelsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ChannelsAdapter.ViewHolder holder, int position) {

        //holder.textViewChannelName.setText(channels.get(position).getName());

        try {
            Picasso.with(context).load(URLDecoder.decode(channels.get(position).getImage(), "UTF-8"))
                    .placeholder(R.drawable.program)
                    .into(holder.imageViewChannel);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onChannelItemClick(view, channels.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public interface OnChannelItemClickListener {
        public void onChannelItemClick(View view, Channel channel, int position);
    }

}
