package lk.mobilevisions.kiki.video.activity;

import android.app.AlertDialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.endless.Endless;
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
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.activity.AudioTrialActivationActivity;
import lk.mobilevisions.kiki.audio.adapter.LibrarySongsListAdapter;
import lk.mobilevisions.kiki.audio.fragment.LibrarySongListFragment;
import lk.mobilevisions.kiki.databinding.ActivityVideoEpisodeBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Episode;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.api.dto.PackageV2;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity;
import lk.mobilevisions.kiki.ui.widgets.DividerItemDecoration;
import lk.mobilevisions.kiki.ui.widgets.EndlessRecyclerViewScrollListener;
import lk.mobilevisions.kiki.ui.widgets.KikiToast;
import lk.mobilevisions.kiki.video.adapter.EpisodeAdapter;
import lk.mobilevisions.kiki.video.adapter.ProgramEpisodeAdapter;

public class VideoEpisodeActivity extends AppCompatActivity implements View.OnClickListener, EpisodeAdapter.OnEpisodesItemClickListener, ProgramEpisodeAdapter.OnEpisodeItemClickListener {
    @Inject
    TvManager tvManager;
    @Inject
    SubscriptionsManager subscriptionsManager;
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
    ProgramEpisodeAdapter programEpisodeAdapter;
    LinearLayoutManager episodesLayoutManager;
    private Endless endless;

    private String alertTitle;
    private String messageBody;
    private boolean trialStatus;
//    EndlessRecyclerViewScrollListener recyclerViewScrollListener;
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
        episodesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        binding.recycleviewProgramEpisodes.setLayoutManager(episodesLayoutManager);
        binding.recycleviewProgramEpisodes.setHasFixedSize(true);
        binding.recycleviewProgramEpisodes.setItemViewCacheSize(50);
//        binding.recycleviewProgramEpisodes.setNestedScrollingEnabled(false);
        programEpisodeAdapter = new ProgramEpisodeAdapter(this, episodeList, VideoEpisodeActivity.this);

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
        View loadingView = View.inflate(this, R.layout.layout_loading, null);
        loadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        endless = Endless.applyTo(binding.recycleviewProgramEpisodes,
                loadingView
        );
        endless.setAdapter(programEpisodeAdapter);
        endless.setLoadMoreListener(new Endless.LoadMoreListener() {
            @Override
            public void onLoadMore(int page) {
                binding.spinKit.setVisibility(View.VISIBLE);
                System.out.println("acsc 222 " + page);
                loadEpisodes(page);

            }
        });
        binding.recycleviewProgramEpisodes.setAdapter(programEpisodeAdapter);
        loadEpisodes(0);
    }


    private void loadProgramTrailer() {
        binding.aviProgress.setVisibility(View.VISIBLE);
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
                        binding.thumbTextview.setText(R.string.unlike_movie);
                    } else {
                        binding.thumbImageview.setBackgroundResource(R.drawable.ic_thumup_outlined);
                        binding.thumbTextview.setText(R.string.like_movie);
                    }

                    if (episodes.get(0).isSubscribed()) {
                        binding.addImageview.setVisibility(View.GONE);
                        binding.removeImageview.setVisibility(View.VISIBLE);
                        binding.addTextview.setText(R.string.remove_from_my_list);

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
                Utils.Error.onServiceCallFail(VideoEpisodeActivity.this, t);
            }
        });
    }


    private void loadEpisodes(final int page) {
        System.out.println("acsc 11 " + page);
        binding.aviProgressRecycler.setVisibility(View.GONE);
        if (selectedDate == null) {
            selectedDate = Utils.DateUtil.getDateOnly(new Date());
        }

        int offset;
        if (page == 0) {
            offset = 0;
        } else {
            offset = episodeList.size();
        }

        Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        if (dateToSend == null) {
            dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        }
        tvManager.getEpisodes(Integer.parseInt(program.getId()), offset, 10, dateToSend, dateToSend, sortOrder, new APIListener<List<Episode>>() {
            @Override
            public void onSuccess(List<Episode> episodes, List<Object> params) {
                binding.aviProgress.setVisibility(View.GONE);
                episodeList.addAll(episodes);
                if (page == 0) {
                    programEpisodeAdapter.setData(episodes);
                    if(episodes.size()>0){
                        binding.playNowLayout.setClickable(true);
                    }
                } else {
                    programEpisodeAdapter.addData(episodes);
                    endless.loadMoreComplete();
                    binding.spinKit.setVisibility(View.GONE);

                }
                if(episodes.size() < 10){
                    endless.setLoadMoreAvailable(false);
                }

                binding.spinKit.setVisibility(View.GONE);
                binding.aviProgressRecycler.setVisibility(View.GONE);

//                if (episodes != null && episodes.size() > 0) {
//
////                    binding.recycleviewProgramEpisodes.setVisibility(View.VISIBLE);
//                    binding.noMoviesTextview.setVisibility(View.GONE);
//                    binding.aviProgressRecycler.setVisibility(View.GONE);
//                    binding.playNowLayout.setClickable(true);
//                    binding.moreMoviesTextview.setVisibility(View.VISIBLE);
////                    episodeAdapter = new EpisodeAdapter(VideoEpisodeActivity.this, episodeList, VideoEpisodeActivity.this);
////                    binding.recycleviewProgramEpisodes.setAdapter(episodeAdapter);
//                    binding.recycleviewProgramEpisodes.addItemDecoration(new DividerItemDecoration(VideoEpisodeActivity.this, R.drawable.episode_line_divider));
//
//
//                } else {
//                    binding.recycleviewProgramEpisodes.setVisibility(View.GONE);
//                    binding.noMoviesTextview.setVisibility(View.VISIBLE);
//                    binding.aviProgressRecycler.setVisibility(View.GONE);
//                    binding.playNowLayout.setClickable(true);
//                    binding.moreMoviesTextview.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onFailure(Throwable t) {
                binding.aviProgress.setVisibility(View.GONE);
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
        binding.thumbTextview.setText(R.string.unlike_movie);

        tvManager.likeEpisode(Integer.parseInt(program.getId()), 1, new APIListener<List<Void>>() {
            @Override
            public void onSuccess(List<Void> result, List<Object> params) {

            }

            @Override
            public void onFailure(Throwable t) {
                KikiToast.show(VideoEpisodeActivity.this, getString(R.string.unlike_movie), false);
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
                        tvManager.subscribeProgram(Integer.valueOf(program.getId()),2, new APIListener<Void>() {
                            @Override
                            public void onSuccess(Void result, List<Object> params) {
                                program.setSubscribed(true);
                                binding.addAviProgress.setVisibility(View.GONE);
                                binding.addTextview.setVisibility(View.VISIBLE);
                                binding.removeImageview.setVisibility(View.VISIBLE);
                                binding.addTextview.setText(R.string.remove_from_my_list);
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
                        tvManager.unSubscribeProgram(Integer.valueOf(program.getId()), 2,new APIListener<Void>() {
                            @Override
                            public void onSuccess(Void result, List<Object> params) {
                                program.setSubscribed(false);
                                binding.addAviProgress.setVisibility(View.GONE);
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
                                binding.addImageview.setVisibility(View.GONE);
                            }
                        });
                    }

    @Override
    public void onEpisodeSongsItemClick(Episode episode, int position, List<Episode> episodes) {

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
            checkTrialStatus(episode);

        }
    }

    private void checkTrialStatus(Episode episode){
        subscriptionsManager.generateSubscriptionToken((int) Utils.App.getConfig(this.getApplication()).getPaidPackageId(),
                new APIListener<PackageToken>() {
                    @Override
                    public void onSuccess(final PackageToken packageToken, List<Object> params) {

                        subscriptionsManager.getTrialStatus(new APIListener<PackageV2>() {
                            @Override
                            public void onSuccess(PackageV2 thePackage, List<Object> params) {
//                                binding.aviProgress.setVisibility(View.GONE);
                                alertTitle = thePackage.getTitleText();
                                messageBody = thePackage.getMessageBody();
                                trialStatus = thePackage.isTrialStatus();
                                System.out.println("hdfhdfhdh " + alertTitle);
                                System.out.println("hdfhdfhdh 111  " + messageBody);
//                        if (thePackage.getPackageId() == 46 || thePackage.getPackageId() == 81 ||
//                                thePackage.getPackageId() == 101 || thePackage.getPackageId() == 106) {
//
//                        } else {
//                            try {
//                                subscriptionDialog();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }

                                if (thePackage.isTrialStatus() && !thePackage.isSubStatus()) {

                                    try {
                                        trialSubscriptionDialog();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Intent intentEpisodes = new Intent(VideoEpisodeActivity.this, SubscriptionAlertActivity.class);
                                    intentEpisodes.putExtra("Episode", episode);
                                    startActivity(intentEpisodes);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {


                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {


                    }
                });
    }

    private void trialSubscriptionDialog() {
        LayoutInflater myLayout = LayoutInflater.from(this);
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_trial_subscription, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(dialogView);
        dialogView.setClipToOutline(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        RelativeLayout tryTextview = (RelativeLayout) alertDialog.findViewById(R.id.try_now_layout);
        ImageView closeImageView = (ImageView) alertDialog.findViewById(R.id.close_imageview);
        TextView title = (TextView) alertDialog.findViewById(R.id.alert_title);
        title.setText(alertTitle);
        TextView body = (TextView) alertDialog.findViewById(R.id.message_body_text);
        body.setText(messageBody);


        tryTextview.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intentPackages = new Intent(VideoEpisodeActivity.this, AudioTrialActivationActivity.class);
                startActivity(intentPackages);


            }
        });
        closeImageView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();


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
