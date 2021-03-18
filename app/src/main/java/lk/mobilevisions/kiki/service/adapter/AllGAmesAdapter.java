package lk.mobilevisions.kiki.service.adapter;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.service.dto.ServiceModel;
import lk.mobilevisions.kiki.service.dto.ServiceSub;

public class AllGAmesAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ServiceModel> mArrayList = new ArrayList<>();
    AllGAmesAdapter.OnAllGamesItemClickListener itemClickListener;

    public AllGAmesAdapter(Context mContext, List<ServiceModel> mArrayList, AllGAmesAdapter.OnAllGamesItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;

        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_items_list, parent, false);
        return new AllGAmesAdapter.AllGamesViewHolder(view);

    }



    public void setData(List<ServiceModel> data, List<ServiceSub> subData) {

        this.mArrayList = data;
        notifyDataSetChanged();
    }

    public void addData(List<ServiceModel> data, List<ServiceSub> subData) {

        this.mArrayList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initLayoutOne((AllGAmesAdapter.AllGamesViewHolder) holder, position);


    }

    private void initLayoutOne(final AllGAmesAdapter.AllGamesViewHolder holder, int pos) {

        final ServiceModel current = mArrayList.get(pos);

        try {
            Picasso.with(mContext).load(URLDecoder.decode(current.getImage(), "UTF-8")).fit().centerCrop()
                    .placeholder(R.drawable.program)
                    .into(holder.serviceImageView);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemClickListener.onAllGamesItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class AllGamesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout_click)
        ConstraintLayout rvHorizontal;

        @BindView(R.id.game_icons)
        ImageView serviceImageView;



        public AllGamesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnAllGamesItemClickListener {

        public void onAllGamesItemClick(ServiceModel serviceModel, int position,List<ServiceModel> songs);

    }

}