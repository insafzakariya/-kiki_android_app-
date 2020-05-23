package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.ArtistModel;
import lk.mobilevisions.kiki.audio.model.dto.Song;

public class ArtistAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ArtistModel> mArrayList = new ArrayList<>();
    OnAudioFavouriteArtistItemClickListener onAudioFavouriteArtistItemClickListener;
    public ArtistAdapter(Context mContext, List<ArtistModel> mArrayList,OnAudioFavouriteArtistItemClickListener onAudioFavouriteArtistItemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
        this.onAudioFavouriteArtistItemClickListener = onAudioFavouriteArtistItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_list_item, parent, false);
        return new ArtistViewHolder(view);

    }



    public void setList(List<ArtistModel> mArrayList){
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((ArtistViewHolder)holder, position);





    }
    private void initLayoutOne(ArtistViewHolder holder, int pos) {
//        final Song current = mArrayList.get(pos);
//        holder.titleTextView.setText(current.getTitle());
//        try {
//            Picasso.with(mContext).load(URLDecoder.decode(current.getImageUrl(), "UTF-8"))
//                    .placeholder(R.drawable.program)
//                    .into(holder.imageViewSong);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder {

        public View rootView;
        public ImageView imageViewSong;
        public TextView titleTextView;
        public ArtistViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            imageViewSong = (ImageView) itemView.findViewById(R.id.imageView8);
            titleTextView = (TextView) itemView.findViewById(R.id.textView8);

        }
    }

    public interface OnAudioFavouriteArtistItemClickListener {
        public void onAudioFavouriteArtistItemClick(View view, Song program, int position);
    }
}