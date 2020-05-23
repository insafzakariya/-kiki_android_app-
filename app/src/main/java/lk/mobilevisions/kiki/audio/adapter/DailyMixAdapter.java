package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lk.mobilevisions.kiki.R;

import lk.mobilevisions.kiki.audio.model.dto.DailyMix;

public class DailyMixAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static Context context;
    private Context mContext;
    private static DailyMixAdapter mInstance;
    private DailyMixItemActionListener dailyMixItemActionListener;
    private List<DailyMix> dailyMixList;
    private boolean isFirstTime;

//    int[] gradiantList = new int[]{R.drawable.gradiant_one, R.drawable.gradiant_two, R.drawable.gradiant_three, R.drawable.gradiant_four};


    public DailyMixAdapter(Context mContext, List<DailyMix> mArrayList,DailyMixItemActionListener dailyMixItemActionListener) {
        this.mContext = mContext;
        this.dailyMixList = mArrayList;
        this.dailyMixItemActionListener = dailyMixItemActionListener;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        System.out.println("bksdksdkgdg 3333 ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal, parent, false);
        return new DailyMixRecyclerViewHolder(view);

    }


    public void setList(List<DailyMix> mArrayList) {
        System.out.println("cnncncncncn 3333  " + dailyMixList.size());
        this.dailyMixList.clear();
        this.dailyMixList.addAll(mArrayList);

                this.notifyDataSetChanged();


    }
//    public void removeItem(int position) {
//        dailyMixList.remove(position);
//        notifyItemRemoved(position);
//    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        System.out.println("bksdksdkgdg 5555 ");

        initLayoutOne((DailyMixRecyclerViewHolder) holder, position);


    }

    private void initLayoutOne(final DailyMixRecyclerViewHolder holder, final int pos) {

        final DailyMix current = dailyMixList.get(pos);
        System.out.println("cnncncncncn 444  " + current.isOpened());
        if(current.isOpened()){
            holder.avLoadingIndicatorView.setVisibility(View.VISIBLE);
        }else {
            holder.avLoadingIndicatorView.setVisibility(View.GONE);
        }
        holder.titleTextView.setText(current.getName());

//            holder.rvHorizontal.setBackgroundResource(gradiantList[current.getIndex()]);


        holder.rvHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current.setOpened(true);
//                notifyDataSetChanged();
                notifyItemChanged(pos);
                dailyMixItemActionListener.onDailyMixPlayAction(dailyMixList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),dailyMixList);


            }
        });
    }

    @Override
    public int getItemCount() {
        return dailyMixList.size();
    }


    class DailyMixRecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.aviProgress)
        AVLoadingIndicatorView avLoadingIndicatorView;

        @BindView(R.id.relativeLayoutRoot)
        RelativeLayout rvHorizontal;

        @BindView(R.id.textView2)
        TextView titleTextView;


        public DailyMixRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    private void requestStream(DailyMix dailyMix,int position,List<DailyMix> dailyMixes) {
        if(dailyMixItemActionListener!=null){
            dailyMixItemActionListener.onDailyMixPlayAction(dailyMix,position,dailyMixes);
        }
    }
    public void setActionListener(DailyMixItemActionListener actionListener){
        this.dailyMixItemActionListener = actionListener;
    }
    public interface DailyMixItemActionListener{

        public void onDailyMixPlayAction(DailyMix dailyMix,int position,List<DailyMix> dailyMixes);
    }

    public void UpdateData(int position,DailyMix userData){
        isFirstTime = true;
        dailyMixList.remove(position);
        dailyMixList.add(position,userData);
        notifyItemChanged(position);
//        notifyDataSetChanged();
    }
}