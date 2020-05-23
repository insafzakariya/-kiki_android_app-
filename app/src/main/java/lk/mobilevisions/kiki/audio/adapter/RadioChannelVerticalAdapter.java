package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.Song;

public class RadioChannelVerticalAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static Context context;
    private static RadioChannelVerticalAdapter mInstance;
    private List<Song> radioChannelList;
    private OnRadioChannelItemActionListener onRadioChannelItemActionListener;

    public RadioChannelVerticalAdapter(Context mContext,
                                       List<Song> radioChannelList) {
        this.context = mContext;
        this.radioChannelList = radioChannelList;

    }

    public RadioChannelVerticalAdapter(Context mContext) {
        this.context = mContext;


    }

    public static RadioChannelVerticalAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RadioChannelVerticalAdapter(context);
        }
        return mInstance;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_recently_played, parent, false);
        return new RadioChannelVerticalAdapter.RadioChannelRecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        initLayoutTwo((RadioChannelVerticalAdapter.RadioChannelRecyclerViewHolder) holder, position);

    }


    private void initLayoutTwo(final RadioChannelVerticalAdapter.RadioChannelRecyclerViewHolder holder, int pos) {
        final Song current = radioChannelList.get(pos);

        holder.titleTextView.setText(current.getName());
        holder.descriptionTextView.setText(current.getDescription());
        try {
            Picasso.with(context).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                    .placeholder(R.drawable.program)
                    .into(holder.imageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioChannelVerticalAdapter adapter = RadioChannelVerticalAdapter.getInstance(context);
                adapter.requestStream(radioChannelList.get(holder.getAdapterPosition()), holder.getAdapterPosition(), radioChannelList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return radioChannelList.size();
    }


    class RadioChannelRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView6)
        TextView titleTextView;

        @BindView(R.id.textView7)
        TextView descriptionTextView;

        @BindView(R.id.image)
        ImageView imageView;

        public RadioChannelRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void requestStream(Song song, int position, List<Song> songs) {
        if (onRadioChannelItemActionListener != null) {
            onRadioChannelItemActionListener.onRadioChannelPlayAction(song, position, songs);
        }
    }

    public void setRadioChannelItemActionListener(RadioChannelVerticalAdapter.OnRadioChannelItemActionListener actionListener) {
        this.onRadioChannelItemActionListener = actionListener;
    }

    public interface OnRadioChannelItemActionListener {

        void onRadioChannelPlayAction(Song song, int position, List<Song> songs);
    }
}
