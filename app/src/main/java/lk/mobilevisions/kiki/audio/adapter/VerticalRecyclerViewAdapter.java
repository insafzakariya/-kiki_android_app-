//package lk.mobilevisions.kiki.audio.adapter;
//
//import android.content.Context;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import lk.mobilevisions.kiki.R;
//import lk.mobilevisions.kiki.audio.fragment.HomeFragment;
//import lk.mobilevisions.kiki.audio.model.HorizontalModel;
//import lk.mobilevisions.kiki.audio.model.VerticalModel;
//import lk.mobilevisions.kiki.audio.model.dto.DailyMix;
//import lk.mobilevisions.kiki.audio.model.dto.HomeContent;
//import lk.mobilevisions.kiki.audio.model.dto.Song;
//import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
//
//public class VerticalRecyclerViewAdapter extends
//        RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private Context mContext;
//    private ArrayList<HomeContent> mArrayList = new ArrayList<>();
//    private static final int TYPE_ONE = 1;
//    private static final int TYPE_TWO = 2;
//    private static final int TYPE_THREE = 3;
//    private HomeFragment homeFragment;
//    public VerticalRecyclerViewAdapter(Context mContext, ArrayList<HomeContent> mArrayList, HomeFragment homeFragment) {
//        this.mContext = mContext;
//        this.mArrayList = mArrayList;
//        this.homeFragment = homeFragment;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view;
//        System.out.println("checking recycler view 55555 " +viewType);
//        if (viewType == TYPE_ONE) {
//             view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertical, parent, false);
//            return new DailyMixRecyclerViewHolder(view);
//        } else if (viewType == TYPE_TWO) {
//             view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_item_recently_played, parent, false);
//            return new RecentlyPlayedRecyclerViewHolder(view);
//        } else if (viewType == TYPE_THREE) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.verical_item_you_also_like, parent, false);
//            return new YouMightAlsoLikeRecyclerViewHolder(view);
//        }else{
//            throw new RuntimeException("The type has to be ONE or TWO");
//        }
//    }
//
//
//
//    public void setList(ArrayList<HomeContent> mArrayList){
//        this.mArrayList.addAll(mArrayList);
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        System.out.println("checking recycler view 6666 " +holder.getItemViewType());
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
//
//
//
//
//
//    }
//    private void initLayoutOne(DailyMixRecyclerViewHolder holder, int pos) {
//        final HomeContent current = mArrayList.get(pos);
//        List<DailyMix> singleSectionItems = current.getObject().getDailymix();
//
//        HorizontalRecyclerViewAdapter itemListDataAdapter =
//                new HorizontalRecyclerViewAdapter(mContext, singleSectionItems,true);
//        holder.rvHorizontal.addItemDecoration(new SpacesItemDecoration(15));
//        holder.rvHorizontal.setHasFixedSize(true);
//        holder.rvHorizontal.setLayoutManager(new LinearLayoutManager(mContext,
//                LinearLayoutManager.HORIZONTAL, false));
//        holder.rvHorizontal.setAdapter(itemListDataAdapter);
//
//        holder.rvHorizontal.setNestedScrollingEnabled(false);
//
//    }
//
//    private void initLayoutTwo(RecentlyPlayedRecyclerViewHolder holder, int pos) {
//        final HomeContent current = mArrayList.get(pos);
//       List<Song> singleSectionItems = current.getObject().getRecently().getSongs();
//        HorizontalRecyclerViewAdapter itemListDataAdapter =
//                new HorizontalRecyclerViewAdapter(mContext, singleSectionItems);
//
//        holder.rvHorizontal.addItemDecoration(new SpacesItemDecoration(15));
//        holder.rvHorizontal.setHasFixedSize(true);
//        holder.rvHorizontal.setLayoutManager(new LinearLayoutManager(mContext,
//                LinearLayoutManager.HORIZONTAL, false));
//        holder.rvHorizontal.setAdapter(itemListDataAdapter);
//
//        holder.rvHorizontal.setNestedScrollingEnabled(false);
//        holder.seeAllTextview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                homeFragment.seeAllClicked(0);
//            }
//        });
//    }
//
//    private void initLayoutThree(YouMightAlsoLikeRecyclerViewHolder holder, int pos) {
//        System.out.println("checking recycler view 7777 " +pos);
//        final HomeContent current = mArrayList.get(pos);
//        List<Song> singleSectionItems = current.getObject().getYoumight().getSongs();
//        HorizontalRecyclerViewAdapter itemListDataAdapter =
//                new HorizontalRecyclerViewAdapter(mContext, singleSectionItems,"");
//
//        holder.rvHorizontal.addItemDecoration(new SpacesItemDecoration(15));
//        holder.rvHorizontal.setHasFixedSize(true);
//        holder.rvHorizontal.setLayoutManager(new LinearLayoutManager(mContext,
//                LinearLayoutManager.HORIZONTAL, false));
//        holder.rvHorizontal.setAdapter(itemListDataAdapter);
//
//        holder.rvHorizontal.setNestedScrollingEnabled(false);
//        holder.seeAllTextview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                homeFragment.seeAllClicked(0);
//            }
//        });
//    }
//    @Override
//    public int getItemCount() {
//        return mArrayList.size();
//    }
//    @Override
//    public int getItemViewType(int position) {
//        System.out.println("checking recycler view 2222 " + position);
//        HomeContent item = mArrayList.get(position);
//        System.out.println("checking recycler view 3333 " + item.getType());
//        if (item.getType().equals("Dailymix")) {
//            return TYPE_ONE;
//        } else if (item.getType().equals("recent")) {
//            return TYPE_TWO;
//        } else if (item.getType().equals("youlike")) {
//            System.out.println("checking recycler view 4444 ");
//            return TYPE_THREE;
//        }else{
//            return -1;
//        }
////        if (item.getType() == VerticalModel.ItemType.ONE_ITEM) {
////            return TYPE_ONE;
////        } else if (item.getType() == VerticalModel.ItemType.TWO_ITEM) {
////            return TYPE_TWO;
////        } else if (item.getType() == VerticalModel.ItemType.THREE_ITEM) {
////            System.out.println("checking recycler view 4444 ");
////            return TYPE_THREE;
////        }else{
////            return -1;
////        }
//    }
//    class DailyMixRecyclerViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.rvHorizontal)
//        RecyclerView rvHorizontal;
//
//
//
//        public DailyMixRecyclerViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//    class RecentlyPlayedRecyclerViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.rvHorizontal)
//        RecyclerView rvHorizontal;
//
//
//        @BindView(R.id.tvseeAll)
//        TextView seeAllTextview;
//
//        public RecentlyPlayedRecyclerViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//    class YouMightAlsoLikeRecyclerViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.rvHorizontal)
//        RecyclerView rvHorizontal;
//
//
//        @BindView(R.id.tvseeAll)
//        TextView seeAllTextview;
//
//        public YouMightAlsoLikeRecyclerViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//}