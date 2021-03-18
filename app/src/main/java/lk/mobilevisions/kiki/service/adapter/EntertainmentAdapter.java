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
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.service.activity.ServiceHomeActivity;
import lk.mobilevisions.kiki.service.dto.ServiceModel;
import lk.mobilevisions.kiki.service.dto.ServiceSub;

public class EntertainmentAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ServiceModel> mArrayList = new ArrayList<>();
    private List<ServiceModel> gamesArrayList = new ArrayList<>();
    EntertainmentAdapter.OnEntertainmentItemClickListener itemClickListener;

    public EntertainmentAdapter(Context mContext, List<ServiceModel> mArrayList, EntertainmentAdapter.OnEntertainmentItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mArrayList = mArrayList;

        this.itemClickListener = itemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_service_list, parent, false);
        return new EntertainmentAdapter.EntertainmentViewHolder(view);

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
        initLayoutOne((EntertainmentAdapter.EntertainmentViewHolder) holder, position);


    }

    private void initLayoutOne(final EntertainmentAdapter.EntertainmentViewHolder holder, int pos) {

        final ServiceModel current = mArrayList.get(pos);

        holder.headerTextView.setText(current.getName());

        holder.descriptionTextView.setText(current.getDescription());

        LayoutTransition lt = new LayoutTransition();
        lt.disableTransitionType(LayoutTransition.CHANGING);
        holder.linearLayoutGame.setLayoutTransition(lt);

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

                if (current.getChildList() != null && current.getChildList().size() > 0 && holder.linearLayoutGame.getVisibility() == View.GONE) {

                    gamesArrayList.clear();
                    Bitmap bitmap = BitmapFactory.decodeResource(v.getContext().getResources(),
                            R.drawable.expand_games);

                    Palette palette = Palette.generate(bitmap);
                    Palette.Swatch swatch = palette.getVibrantSwatch();
                    holder.linearLayoutGame.setBackgroundColor(swatch.getRgb());
                    holder.cardview.setCardBackgroundColor(swatch.getRgb());
                    holder.linearLayoutGame.setVisibility(View.VISIBLE);

                    if (current.getChildList().size() >= 7) {
                        ServiceModel serviceModelX = new ServiceModel();
                        serviceModelX.setId(8);
                        serviceModelX.setName("More");
                        serviceModelX.setImage("");
                        serviceModelX.setLandingUrl("");
                        serviceModelX.setUrl("");
                        serviceModelX.setHasChild(false);
                        serviceModelX.setReferanceCode("");
                        serviceModelX.setDescription("");

                        gamesArrayList.addAll(current.getChildList());
                        gamesArrayList.add(serviceModelX);

                    } else {
                        gamesArrayList.addAll(current.getChildList());
                    }
                    holder.recyclerView.setLayoutManager(new GridLayoutManager(mContext, 5));
                    GamesAdapter mAdapter = new GamesAdapter(mContext, gamesArrayList);
                    holder.recyclerView.setHasFixedSize(true);
                    holder.recyclerView.setItemViewCacheSize(50);
                    holder.recyclerView.setDrawingCacheEnabled(true);
                    holder.recyclerView.setAdapter(mAdapter);

                }else if (current.getChildList() != null && current.getChildList().size() > 0 && holder.linearLayoutGame.getVisibility() == View.VISIBLE){

                    holder.linearLayoutGame.setVisibility(View.GONE);
                }
                itemClickListener.onEntertainmentItemClick(mArrayList.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition(),mArrayList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class EntertainmentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.service_list_linear)
        RelativeLayout rvHorizontal;

        @BindView(R.id.serv_head)
        TextView headerTextView;

        @BindView(R.id.serv_descrip)
        TextView descriptionTextView;

        @BindView(R.id.service_image)
        ImageView serviceImageView;

        @BindView(R.id.sub_recyclerview)
        RecyclerView recyclerView;

        @BindView(R.id.linearLayout_game)
        LinearLayout linearLayoutGame;

        @BindView(R.id.expandable_cardview)
        CardView cardview;



        public EntertainmentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnEntertainmentItemClickListener {

        public void onEntertainmentItemClick(ServiceModel serviceModel, int position,List<ServiceModel> songs);

    }

}