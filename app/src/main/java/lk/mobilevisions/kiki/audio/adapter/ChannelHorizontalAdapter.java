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

import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.AudioProgram;

public class ChannelHorizontalAdapter extends
        RecyclerView.Adapter<ChannelHorizontalAdapter.HorizontalViewHolder> {

    private Context mContext;
    private List<AudioProgram> mArrayList;
    OnAudioProgramItemClickListener itemClickListener;
    public ChannelHorizontalAdapter(Context mContext,
                                    List<AudioProgram> mArrayList,OnAudioProgramItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.itemClickListener = itemClickListener;
}

    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_horizontal_list_item, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HorizontalViewHolder holder, int position) {
        final AudioProgram current = mArrayList.get(position);
        holder.programTitleTextView.setText(current.getName());
        holder.programDescriptionTextView.setText(current.getDescription());
        try {
            Picasso.with(mContext).load(URLDecoder.decode(current.getImage(), "UTF-8"))
                    .placeholder(R.drawable.program)
                    .into(holder.programImageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        holder.programImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onAudioProgramItemClick(v, mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class HorizontalViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textView6)
        TextView programTitleTextView;

        @BindView(R.id.textView7)
        TextView programDescriptionTextView;

        @BindView(R.id.image)
        ImageView programImageView;
        public HorizontalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnAudioProgramItemClickListener {
        public void onAudioProgramItemClick(View view, AudioProgram program, int position);
    }
}
