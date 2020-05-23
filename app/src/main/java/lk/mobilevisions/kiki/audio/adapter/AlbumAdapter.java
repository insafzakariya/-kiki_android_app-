package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.AlbumModel;

public class AlbumAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<AlbumModel> mArrayList = new ArrayList<>();

    public AlbumAdapter(Context mContext, ArrayList<AlbumModel> mArrayList) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_list_item, parent, false);
        return new AlbumViewHolder(view);

    }



    public void setList(ArrayList<AlbumModel> mArrayList){
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((AlbumViewHolder)holder, position);





    }
    private void initLayoutOne(AlbumViewHolder holder, int pos) {
        final AlbumModel current = mArrayList.get(pos);


    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {



        public AlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}