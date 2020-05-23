package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.model.FavouriteVideosModel;

public class FavouriteVideosAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<FavouriteVideosModel> mArrayList = new ArrayList<>();

    public FavouriteVideosAdapter(Context mContext, ArrayList<FavouriteVideosModel> mArrayList) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_list_item, parent, false);
        return new FavouriteVideosViewHolder(view);

    }



    public void setList(ArrayList<FavouriteVideosModel> mArrayList){
        this.mArrayList.addAll(mArrayList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((FavouriteVideosViewHolder)holder, position);





    }
    private void initLayoutOne(FavouriteVideosViewHolder holder, int pos) {
        final FavouriteVideosModel current = mArrayList.get(pos);


    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class FavouriteVideosViewHolder extends RecyclerView.ViewHolder {



        public FavouriteVideosViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}