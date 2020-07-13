package lk.mobilevisions.kiki.video.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.ActivityVideoEpisodeBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Episode;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity;
import lk.mobilevisions.kiki.ui.widgets.DividerItemDecoration;
import lk.mobilevisions.kiki.ui.widgets.EndlessRecyclerViewScrollListener;
import lk.mobilevisions.kiki.ui.widgets.KikiToast;
import lk.mobilevisions.kiki.video.adapter.EpisodeAdapter;


public class VideoEpisodeActivity extends AppCompatActivity implements View.OnClickListener, EpisodeAdapter.OnEpisodesItemClickListener {
    @Inject
    TvManager tvManager;
    Program program;
    private List<Episode> episodeList = new ArrayList<>();
    private List<Episode> trailerList = new ArrayList<>();
    private int episodesLimit = 10;
    Date selectedDate;
    boolean sortAscending;
    String sortOrder;
    Episode currentTrailer;
    ActivityVideoEpisodeBinding binding;
    RecyclerView.Adapter episodeAdapter;
    LinearLayoutManager episodesLayoutManager;
    EndlessRecyclerViewScrollListener recyclerViewScrollListener;
    public static final String TV_PLAYER_EPISODE_POSITION = "TV_PLAYER_EPISODE_POSITION";
    public static final String TV_PLAYER_EPISODE_TRAILER_POSITION = "TV_PLAYER_EPISODE_TRAILER_POSITION";
    public static final String TV_PLAYER_EPISODE = "TV_PLAYER_EPISODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_episode);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundColor));
        }
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "VideoEpisodeActivity", null);
        program = (Program) getIntent().getSerializableExtra("program");
        selectedDate = (Date) getIntent().getSerializableExtra("selectedDate");
        sortAscending = false;
        episodesLayoutManager = new LinearLayoutManager(this);
        binding.recycleviewProgramEpisodes.setLayoutManager(episodesLayoutManager);
        binding.recycleviewProgramEpisodes.setHasFixedSize(true);
        binding.recycleviewProgramEpisodes.setNestedScrollingEnabled(false);
        binding.btnBack.setOnClickListener(this);
        binding.thumbImageview.setOnClickListener(this);
        binding.addImageview.setOnClickListener(this);
        binding.removeImageview.setOnClickListener(this);
        binding.playTrailerLayout.setOnClickListener(this);
        binding.playNowLayout.setOnClickListener(this);

        if (program != null) {

            binding.programeNameTextview.setText(program.getName());
            binding.programeDescriptionTextview.setText(program.getDescription());


        }
        loadProgramTrailer();
        loadEpisodes();
        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {

                        int visibleItemCount = episodesLayoutManager.getChildCount();
                        int totalItemCount = episodesLayoutManager.getItemCount();
                        int pastVisiblesItems = episodesLayoutManager.findFirstVisibleItemPosition();


                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            System.out.println("fnfnfnfnfnfn 1111 ");
                            if (selectedDate == null) {
                                selectedDate = Utils.DateUtil.getDateOnly(new Date());
                            }

                            Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
                            if (dateToSend == null) {
                                dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
                            }
                            tvManager.getEpisodes(Integer.valueOf(program.getId()), episodeList.size(), episodesLimit, dateToSend, dateToSend, sortOrder, new APIListener<List<Episode>>() {
                                @Override
                                public void onSuccess(List<Episode> episodes, List<Object> params) {
                                    if (episodes != null && episodes.size() > 0) {
                                        System.out.println("fnfnfnfnfnfn 2222 " + episodes.size());
                                        episodeList.addAll(episodes);


//                                            episodeAdapter = new EpisodeAdapter(VideoEpisodeActivity.this, episodeList, VideoEpisodeActivity.this);
                                        episodeAdapter.notifyDataSetChanged();

                                    } else {

                                    }
                                }

                                @Override
                                public void onFailure(Throwable t) {

                                    Utils.Error.onServiceCallFail(VideoEpisodeActivity.this, t);
                                }
                            });
                        }
                    }

                }
            }

        });
    }


    private void loadProgramTrailer() {
        binding.aviProgress.setVisibility(View.VISIBLE);
        binding.playNowLayout.setClickable(false);
        if (selectedDate == null) {
            selectedDate = Utils.DateUtil.getDateOnly(new Date());
        }

        Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        if (dateToSend == null) {
            dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        }
        tvManager.getTrailer(Integer.valueOf(program.getId()), new APIListener<List<Program>>() {
            @Override
            public void onSuccess(List<Program> episodes, List<Object> params) {
                trailerList.add(episodes.get(0).getEpisode());
                System.out.println("checking episode trailer size " + episodes.size());
                Application.PLAYER.setEpisodes(trailerList);
                System.out.println("checking episode trailer 11111 " + trailerList.get(0).getName());
                System.out.println("checking episode trailer 2222222 " + trailerList.get(0).getVideoLink());
                Application.PLAYER.setCurrentVideo(0);
                Application.PLAYER.setCurrentVideoPosition(0);
                binding.aviProgress.setVisibility(View.GONE);
                binding.playNowLayout.setClickable(true);
                if (episodes != null && episodes.size() > 0) {

                    try {
                        Picasso.with(VideoEpisodeActivity.this).load(URLDecoder.decode(episodes.get(0).getEpisode().getImage(), "UTF-8"))
                                .placeholder(R.drawable.program)
                                .into(binding.trailerImageview);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    currentTrailer = episodes.get(0).getEpisode();
                    if (episodes.get(0).isIsliked()) {
                        binding.thumbImageview.setBackgroundResource(R.drawable.ic_thumb_up_filled);
                        binding.thumbTextview.setText("Already liked");
                    } else {
                        binding.thumbImageview.setBackgroundResource(R.drawable.ic_thumup_outlined);
                        binding.thumbTextview.setText(R.string.like_movie);
                    }

                    if (episodes.get(0).isSubscribed()) {
                        binding.addImageview.setVisibility(View.GONE);
                        binding.removeImageview.setVisibility(View.VISIBLE);
                        binding.addTextview.setText("Remove from my list");

                    } else {
                        binding.removeImageview.setVisibility(View.GONE);
                        binding.addImageview.setVisibility(View.VISIBLE);
                        binding.addTextview.setText(R.string.add_to_my_list);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                binding.aviProgress.setVisibility(View.GONE);
                binding.playNowLayout.setClickable(true);
                Utils.Error.onServiceCallFail(VideoEpisodeActivity.this, t);
            }
        });
    }

    private void loadEpisodes() {
        binding.aviProgressRecycler.setVisibility(View.VISIBLE);
        binding.playNowLayout.setClickable(false);
        if (selectedDate == null) {
            selectedDate = Utils.DateUtil.getDateOnly(new Date());
        }

        Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        if (dateToSend == null) {
            dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        }
        tvManager.getEpisodes(Integer.valueOf(program.getId()), episodeList.size(), episodesLimit, dateToSend, dateToSend, sortOrder, new APIListener<List<Episode>>() {
            @Override
            public void onSuccess(List<Episode> episodes, List<Object> params) {
                binding.aviProgress.setVisibility(View.GONE);
                binding.playNowLayout.setClickable(true);
                if (episodes != null && episodes.size() > 0) {

                    episodeList = episodes;

                    binding.recycleviewProgramEpisodes.setVisibility(View.VISIBLE);
                    binding.noMoviesTextview.setVisibility(View.GONE);
                    binding.aviProgressRecycler.setVisibility(View.GONE);
                    binding.playNowLayout.setClickable(true);
                    binding.moreMoviesTextview.setVisibility(View.VISIBLE);
                    episodeAdapter = new EpisodeAdapter(VideoEpisodeActivity.this, episodeList, VideoEpisodeActivity.this);
                    binding.recycleviewProgramEpisodes.setAdapter(episodeAdapter);
                    binding.recycleviewProgramEpisodes.addItemDecoration(new DividerItemDecoration(VideoEpisodeActivity.this, R.drawable.episode_line_divider));


                } else {
                    binding.recycleviewProgramEpisodes.setVisibility(View.GONE);
                    binding.noMoviesTextview.setVisibility(View.VISIBLE);
                    binding.aviProgressRecycler.setVisibility(View.GONE);
                    binding.playNowLayout.setClickable(true);
                    binding.moreMoviesTextview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                binding.aviProgress.setVisibility(View.GONE);
                binding.playNowLayout.setClickable(true);
                Utils.Error.onServiceCallFail(VideoEpisodeActivity.this, t);
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.thumb_imageview:
                if (binding.thumbTextview.getText().toString().trim().equals(getResources().getString(R.string.like_movie))) {
                    likeTrailer();
                } else {
                    unLikeTrailer();
                }

                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.add_imageview:
                addToMyList();
                break;
            case R.id.remove_imageview:
                removeFromMyList();
                break;
            case R.id.play_trailer_layout:
                Intent intent = new Intent(this, FullScreenActivity.class);
                Application.PLAYER.setEpisodes(trailerList);
                Application.PLAYER.setCurrentVideo(0);
                this.startActivityForResult(intent, 1004);
                break;
            case R.id.play_now_layout:
                if (episodeList.size()>0){
                    if (!episodeList.get(0).isTrailerOnly()) {
                        Application.PLAYER.setEpisodes(episodeList);
                        Application.PLAYER.setCurrentVideoPosition(0);
                        Intent intent1 = new Intent(this, FullScreenActivity.class);
                        Application.PLAYER.setCurrentVideo(0);
                        this.startActivityForResult(intent1, 1004);
                    } else {
                        Intent intentEpisodes = new Intent(this, SubscriptionAlertActivity.class);
                        intentEpisodes.putExtra("Episode", episodeList.get(0));
                        startActivity(intentEpisodes);
                    }
                }


                break;
            default:
        }
    }

    @Override
    public void onEpisodeItemClick(View view, Episode episode, int position) {

        if (!episode.isTrailerOnly()) {
            Application.PLAYER.setEpisodes(episodeList);
            Application.PLAYER.setCurrentVideoPosition(0);
            Application.PLAYER.setCurrentVideo(position);
            Intent intent = new Intent(this, FullScreenActivity.class);
            this.startActivityForResult(intent, 1004);
            JSONObject props = new JSONObject();
            try {
                props.put("EpisodeName", episode.getName());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ((Application) getApplication()).getMixpanelAPI().track("Episode Selected", props);
        } else {
            Intent intentEpisodes = new Intent(this, SubscriptionAlertActivity.class);
            intentEpisodes.putExtra("Episode", episode);
            startActivity(intentEpisodes);
        }


    }

    private void likeTrailer() {
        System.out.println("uvggvgh");
        binding.thumbImageview.setBackgroundResource(R.drawable.ic_thumb_up_filled);
        binding.thumbTextview.setText("Already liked");

        tvManager.likeEpisode(Integer.parseInt(program.getId()), 1, new APIListener<List<Void>>() {
            @Override
            public void onSuccess(List<Void> result, List<Object> params) {

            }

            @Override
            public void onFailure(Throwable t) {
                KikiToast.show(VideoEpisodeActivity.this, getString(R.string.user_already_liked), false);
                binding.thumbImageview.setBackgroundResource(R.drawable.ic_thumb_up_filled);

            }
        });
    }

    private void unLikeTrailer() {
        binding.thumbImageview.setBackgroundResource(R.drawable.ic_thumup_outlined);
        binding.thumbTextview.setText(R.string.like_movie);
        tvManager.likeEpisode(Integer.parseInt(program.getId()), 1,new APIListener<List<Void>>() {
            @Override
            public void onSuccess(List<Void> result, List<Object> params) {


            }

            @Override
            public void onFailure(Throwable t) {


            }
        });
    }

    private void addToMyList() {
//        System.out.println("test addd to list");
//        new MaterialDialog.Builder(VideoEpisodeActivity.this)
//                .title(getString(R.string.app_name))
//                .content("Are you sure you want to subscribe this program?")
//                .positiveText("Yes")
//                .negativeText("No")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        binding.addImageview.setVisibility(View.GONE);
                        binding.addTextview.setVisibility(View.GONE);
                        binding.addAviProgress.setVisibility(View.VISIBLE);
                        binding.playNowLayout.setClickable(false);
                        tvManager.subscribeProgram(Integer.valueOf(program.getId()),2, new APIListener<Void>() {
                            @Override
                            public void onSuccess(Void result, List<Object> params) {
                                program.setSubscribed(true);
                                binding.addAviProgress.setVisibility(View.GONE);
                                binding.playNowLayout.setClickable(true);
                                binding.addTextview.setVisibility(View.VISIBLE);
                                binding.removeImageview.setVisibility(View.VISIBLE);
                                binding.addTextview.setText("Remove from my list");
                                Application.BUS.post(new SubscribeEvent(true));
                                JSONObject props = new JSONObject();
                                try {
                                    props.put("ProgramName", program.getName());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ((Application) getApplication()).getMixpanelAPI().track("Added to my list", props);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Utils.Error.onServiceCallFail(VideoEpisodeActivity.this, t);
                                binding.addImageview.setVisibility(View.VISIBLE);
                                binding.addTextview.setVisibility(View.VISIBLE);
                                binding.addAviProgress.setVisibility(View.GONE);
                                binding.playNowLayout.setClickable(true);
                                binding.removeImageview.setVisibility(View.GONE);
                            }
                        });
                    }


    private void removeFromMyList() {
//        new MaterialDialog.Builder(VideoEpisodeActivity.this)
//                .title(getString(R.string.app_name))
//                .content("Are you sure you want to unsubscribe from this program?")
//                .positiveText("Yes")
//                .negativeText("No")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        binding.removeImageview.setVisibility(View.GONE);
                        binding.addTextview.setVisibility(View.GONE);
                        binding.addAviProgress.setVisibility(View.VISIBLE);
                        binding.playNowLayout.setClickable(false);
                        tvManager.unSubscribeProgram(Integer.valueOf(program.getId()), 2,new APIListener<Void>() {
                            @Override
                            public void onSuccess(Void result, List<Object> params) {
                                program.setSubscribed(false);
                                binding.addAviProgress.setVisibility(View.GONE);
                                binding.playNowLayout.setClickable(true);
                                binding.addTextview.setVisibility(View.VISIBLE);
                                binding.addImageview.setVisibility(View.VISIBLE);
                                binding.addTextview.setText(R.string.add_to_my_list);
                                Application.BUS.post(new SubscribeEvent(false));
                                JSONObject props = new JSONObject();
                                try {
                                    props.put("ProgramName", program.getName());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ((Application) getApplication()).getMixpanelAPI().track("Removed from my list", props);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Utils.Error.onServiceCallFail(VideoEpisodeActivity.this, t);
                                binding.removeImageview.setVisibility(View.VISIBLE);
                                binding.addTextview.setVisibility(View.VISIBLE);
                                binding.addAviProgress.setVisibility(View.GONE);
                                binding.playNowLayout.setClickable(true);
                                binding.addImageview.setVisibility(View.GONE);
                            }
                        });
                    }



    public class SubscribeEvent {
        private boolean isSubcribed;

        public SubscribeEvent(boolean isSubcribed) {
            this.isSubcribed = isSubcribed;
        }

        public boolean getSubcribed() {
            return isSubcribed;
        }
    }
}
