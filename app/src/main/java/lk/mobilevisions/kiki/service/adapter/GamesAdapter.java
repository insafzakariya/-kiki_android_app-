package lk.mobilevisions.kiki.service.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.service.dto.ServiceModel;
import lk.mobilevisions.kiki.service.dto.ServiceSub;

public class GamesAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ServiceModel> mArrayList = new ArrayList<>();
    GamesAdapter itemClickListener;

    public GamesAdapter(Context mContext, List<ServiceModel> mArrayList) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;



    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_items_list, parent, false);
        return new GamesAdapter.EntertainmentViewHolder(view);

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
        initLayoutOne((GamesAdapter.EntertainmentViewHolder) holder, position);


    }

    private void initLayoutOne(final GamesAdapter.EntertainmentViewHolder holder, int pos) {

        final ServiceModel current = mArrayList.get(pos);

//        holder.headerTextView.setText(current.getName());

//        holder.descriptionTextView.setText(current.getDescription());
        System.out.println("hdjfgdhg " + current.getName());


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

            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class EntertainmentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout_click)
        ConstraintLayout rvHorizontal;


//        @BindView(R.id.serv_descrip)
//        TextView descriptionTextView;

        @BindView(R.id.game_icons)
        ImageView serviceImageView;



        public EntertainmentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnEntertainmentItemClickListener {

        public void onEntertainmentItemClick(ServiceModel serviceModel, int position,List<ServiceModel> songs);

    }

}