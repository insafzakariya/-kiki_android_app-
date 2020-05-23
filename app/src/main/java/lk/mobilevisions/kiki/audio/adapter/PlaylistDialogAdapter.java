package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;

public class PlaylistDialogAdapter extends RecyclerView.Adapter<PlaylistDialogAdapter.MyHolder> {

    Context context;
    List<PlayList> playLists;
    OnAudioPlaylistItemClickListener onAudioPlaylistItemClickListener;

    public PlaylistDialogAdapter(Context context, List<PlayList> playLists, OnAudioPlaylistItemClickListener onAudioPlaylistItemClickListener) {
        this.context = context;
        this.playLists = playLists;
        this.onAudioPlaylistItemClickListener = onAudioPlaylistItemClickListener;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model, parent, false);
        MyHolder holder = new MyHolder(v);
        return holder;
    }

    //BIND DATA
    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {

        final PlayList playList = playLists.get(position);
        holder.nameTxt.setText(playList.getName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAudioPlaylistItemClickListener.onAudioSongsPlayListItemClick(playLists.get(position),
                        position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playLists.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView nameTxt;
        CardView cardView;

        public MyHolder(View itemView) {
            super(itemView);
            nameTxt = (TextView) itemView.findViewById(R.id.nameTxt);
            cardView = (CardView) itemView.findViewById(R.id.mCard);
        }
    }

    public interface OnAudioPlaylistItemClickListener {

        public void onAudioSongsPlayListItemClick(PlayList playList, int position);

    }
}
