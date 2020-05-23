//package lk.mobilevisions.kiki.audio.adapter;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//import lk.mobilevisions.kiki.R;
//import lk.mobilevisions.kiki.audio.model.HorizontalModel;
//import lk.mobilevisions.kiki.audio.model.VerticalModel;
//import lk.mobilevisions.kiki.audio.model.dto.DailyMix;
//import lk.mobilevisions.kiki.audio.model.dto.Song;
//
//public class HorizontalRecyclerViewAdapter extends
//        RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private Context mContext;
//    private List<DailyMix> dailyMixListList;
//    private List<Song> recentlyPlayedList;
//    private List<Song> youMightList;
//    private static final int TYPE_ONE = 1;
//    private static final int TYPE_TWO = 2;
//    private static final int TYPE_THREE = 3;
//    int[] gradiantList = new int[]{R.drawable.gradiant_one, R.drawable.gradiant_two,R.drawable.gradiant_three, R.drawable.gradiant_four};
//    public HorizontalRecyclerViewAdapter(Context mContext,
//                                         List<DailyMix> dailyMixListList,boolean is) {
//        this.mContext = mContext;
//        this.dailyMixListList = dailyMixListList;
//    }
//    public HorizontalRecyclerViewAdapter(Context mContext,
//                                         List<Song> recentlyPlayedList) {
//        this.mContext = mContext;
//        this.recentlyPlayedList = recentlyPlayedList;
//    }
//    public HorizontalRecyclerViewAdapter(Context mContext,
//                                         List<Song> youMightList,String txt) {
//        this.mContext = mContext;
//        this.youMightList = youMightList;
//    }
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view;
//        if (viewType == TYPE_ONE) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal, parent, false);
//            return new DailyMixRecyclerViewHolder(view);
//        } else if (viewType == TYPE_TWO) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_recently_played, parent, false);
//            return new RecentlyPlayedRecyclerViewHolder(view);
//        }else if (viewType == TYPE_THREE) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_recently_played, parent, false);
//            return new YouMightAlsoLikeRecyclerViewHolder(view);
//        } else {
//            throw new RuntimeException("The type has to be ONE or TWO");
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        switch (holder.getItemViewType()) {
//            case TYPE_ONE:
//                initLayoutOne((DailyMixRecyclerViewHolder)holder, position);
//                break;
//            case TYPE_TWO:
//                initLayoutTwo((RecentlyPlayedRecyclerViewHolder) holder, position);
//                break;
//            case TYPE_THREE:
//                initLayoutThree((YouMightAlsoLikeRecyclerViewHolder) holder, position);
//                break;
//            default:
//                break;
//        }
//    }
//    private void initLayoutOne(DailyMixRecyclerViewHolder holder, int pos) {
//        final HorizontalModel current = mArrayList.get(pos);
//
//       holder.rvHorizontal.setBackgroundResource(gradiantList[current.getDrawableIndex()]);
//
//    }
//
//    private void initLayoutTwo(RecentlyPlayedRecyclerViewHolder holder, int pos) {
//        final HorizontalModel current = mArrayList.get(pos);
//
//
//    }
//    private void initLayoutThree(YouMightAlsoLikeRecyclerViewHolder holder, int pos) {
//        final HorizontalModel current = mArrayList.get(pos);
//
//
//    }
//    @Override
//    public int getItemCount() {
//        return mArrayList.size();
//    }
//    @Override
//    public int getItemViewType(int position) {
//        HorizontalModel item = mArrayList.get(position);
//        if (item.getType() == HorizontalModel.ItemType.ONE_ITEM) {
//            return TYPE_ONE;
//        } else if (item.getType() == HorizontalModel.ItemType.TWO_ITEM) {
//            return TYPE_TWO;
//        }else if (item.getType() == HorizontalModel.ItemType.THREE_ITEM) {
//            return TYPE_THREE;
//        } else {
//            return -1;
//        }
//    }
//    class DailyMixRecyclerViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.relativeLayoutRoot)
//        RelativeLayout rvHorizontal;
//
//        public DailyMixRecyclerViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this,itemView);
//        }
//    }
//
//    class RecentlyPlayedRecyclerViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.relativeLayoutRoot)
//        RelativeLayout rvHorizontal;
//
//        public RecentlyPlayedRecyclerViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this,itemView);
//        }
//    }
//
//    class YouMightAlsoLikeRecyclerViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.relativeLayoutRoot)
//        RelativeLayout rvHorizontal;
//
//        public YouMightAlsoLikeRecyclerViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this,itemView);
//        }
//    }
//}