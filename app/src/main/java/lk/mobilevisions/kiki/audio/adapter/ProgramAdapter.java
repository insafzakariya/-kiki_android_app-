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
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.fragment.ProgrammesFragment;

import lk.mobilevisions.kiki.audio.model.dto.AudioProgram;

public class ProgramAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<AudioProgram> mArrayList = new ArrayList<>();
    private ProgrammesFragment programmesFragment;
    OnAudioProgramItemClickListener itemClickListener;

    public ProgramAdapter(Context mContext, List<AudioProgram> mArrayList, ProgrammesFragment programmesFragment, OnAudioProgramItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.programmesFragment = programmesFragment;
        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.programes_list_item, parent, false);
        return new ProgramViewHolder(view);

    }


    public void setList(ArrayList<AudioProgram> mArrayList) {
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((ProgramViewHolder) holder, position);


    }

    private void initLayoutOne(final ProgramViewHolder holder, int pos) {
        final AudioProgram current = mArrayList.get(pos);
        holder.programTitleTextView.setText(current.getName());
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

    class ProgramViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView6)
        TextView programTitleTextView;

        @BindView(R.id.image)
        ImageView programImageView;

        public ProgramViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnAudioProgramItemClickListener {
        public void onAudioProgramItemClick(View view, AudioProgram program, int position);
    }

}