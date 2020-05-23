package lk.mobilevisions.kiki.audio.fragment;

import androidx.databinding.DataBindingUtil;

import lk.mobilevisions.kiki.databinding.FragmentPopularSongsBinding;

public class PopularSongsFragment
//        extends Fragment implements PopularSongsVerticalAdapter.OnPopularSongsItemActionListener
{

//    @Inject
//    TvManager tvManager;
//
//
    FragmentPopularSongsBinding binding;
//
//    YouMightAlsoLikeAdapter mAdapter;
//    List<Song> youMightAlsoLikeList = new ArrayList<>();
//    private Animation animShow, animHide;
//    GridLayoutManager channelsLayoutManager;
//
//    public YouMightAlsoLikeFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_you_might_also_like, container, false);
//        ((Application) getActivity().getApplication()).getInjector().inject(this);
//
////        AudioDashboardActivity audioDashboardActivity = (AudioDashboardActivity) getActivity();
////        audioDashboardActivity.changeToolbarName("Recently Played");
//
//
//        channelsLayoutManager = new GridLayoutManager(getActivity(), 3);
//        binding.youMightRecyclerview.setLayoutManager(channelsLayoutManager);
//        mAdapter = new YouMightAlsoLikeAdapter(getActivity(), youMightAlsoLikeList, YouMightAlsoLikeFragment.this);
//        binding.youMightRecyclerview.setHasFixedSize(true);
//        binding.youMightRecyclerview.setItemViewCacheSize(50);
//        binding.youMightRecyclerview.setDrawingCacheEnabled(true);
//        binding.youMightRecyclerview.setAdapter(mAdapter);
//        getYouMightLikeSongs();
//
//        binding.backImageview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
//                hhh.onBackPressed();
//            }
//        });
//
//        return binding.getRoot();
//
//    }
//
//
//    private void getYouMightLikeSongs() {
//
//
//        tvManager.getAudioYouLike(30, new APIListener<List<Song>>() {
//            @Override
//            public void onSuccess(List<Song> result, List<Object> params) {
//                youMightAlsoLikeList = result;
//                binding.youMightRecyclerview.setAdapter(new YouMightAlsoLikeAdapter(getContext(),
//                        youMightAlsoLikeList, YouMightAlsoLikeFragment.this));
//                if (result.size() <= 0) {
//                    binding.youMightRecyclerview.setVisibility(View.GONE);
//                    binding.aviProgress.setVisibility(View.GONE);
//
//                } else {
//
//                    binding.youMightRecyclerview.setVisibility(View.VISIBLE);
//                    binding.aviProgress.setVisibility(View.GONE);
//                }
//                mAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//
//                Utils.Error.onServiceCallFail(getContext(), t);
//            }
//        });
//
//
//    }
//
//
//    @Override
//    public void onYouMightLikeItemClick(Song song, int position, List<Song> songs) {
//        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
//        hhh.youAlsoLikeFragmentSongClickedEvent(song, position, songs);
//
//    }
//
//    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
//
//        private int spanCount;
//        private int spacing;
//        private boolean includeEdge;
//
//        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
//            this.spanCount = spanCount;
//            this.spacing = spacing;
//            this.includeEdge = includeEdge;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            int position = parent.getChildAdapterPosition(view); // item position
//            int column = position % spanCount; // item column
//
//            if (includeEdge) {
//                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
//                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
//
//                if (position < spanCount) { // top edge
//                    outRect.top = spacing;
//                }
//                outRect.bottom = spacing; // item bottom
//            } else  {
//                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
//                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
//                if (position >= spanCount) {
//                    outRect.top = spacing; // item top
//                }
//            }
//        }
//    }
//

}
