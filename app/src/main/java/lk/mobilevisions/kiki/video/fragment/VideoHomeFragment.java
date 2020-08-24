package lk.mobilevisions.kiki.video.fragment;

import android.content.Context;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.FragmentHomeVideoBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Channel;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.video.activity.VideoChildModeActivity;
import lk.mobilevisions.kiki.video.activity.VideoEpisodeActivity;
import lk.mobilevisions.kiki.video.adapter.ChannelAdapter;
import lk.mobilevisions.kiki.video.adapter.ProgramAdapter;
import lk.mobilevisions.kiki.video.adapter.SlidingImageAdapter;


public class VideoHomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, ProgramAdapter.OnProgramItemClickListener {

    @Inject
    TvManager tvManager;
    List<Program> programs;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    FragmentHomeVideoBinding binding;
    private Date selectedDate;
    private ChannelAdapter channelAdapter;
    private boolean isChannelLoaded;
    private boolean isSlidersLoaded;
    private int channelSize;
    private int currentChannelIndex;
    List<Channel> channelList;
    private boolean isKidsMoodOn;
    private Program selectedProgram;
    private int selectedProgramPosition;
    private FirebaseAnalytics mFirebaseAnalytics;
    Context context;

    public VideoHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_video, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        selectedDate = Utils.DateUtil.getDateOnly(new Date());
        binding.recycleviewChannelsPrograms.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recycleviewChannelsPrograms.setNestedScrollingEnabled(false);
        setupSliderView();
        Application.BUS.register(this);
        loadChannelsAndPrograms();

        binding.indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

        binding.playLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (programs != null && programs.isEmpty() != true) {
                    Program program = programs.get(binding.pager.getCurrentItem());
                    Intent intentEpisodes = new Intent(getActivity(), VideoEpisodeActivity.class);
                    intentEpisodes.putExtra("program", program);
                    startActivity(intentEpisodes);
                    JSONObject props = new JSONObject();
                    try {
                        props.put("ProgramName", program.getName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ((Application) getActivity().getApplication()).getMixpanelAPI().track("Program Selected", props);

                }
            }
        });
        binding.recycleviewChannelsPrograms.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate) {
                return false;
            }
        });

        String programId = getActivity().getIntent().getStringExtra("programId");
        int programid = 0;
        try {
            programid = Integer.parseInt(programId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        System.out.println("afsdfsdfsdv " + programid);

        String programType = getActivity().getIntent().getStringExtra("programType");

        if (programType != null){
            if (programType.equals("0")){

                tvManager.getProgramWithID(programid, new APIListener <Program>() {
                    @Override
                    public void onSuccess(Program program, List<Object> params) {

                        Intent intentEpisodes = new Intent(getActivity(), VideoEpisodeActivity.class);
                        intentEpisodes.putExtra("program", program);
                        startActivity(intentEpisodes);

                        JSONObject props = new JSONObject();
                        try {
                            props.put("ProgramName", program.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ((Application) getActivity().getApplication()).getMixpanelAPI().track("Program Selected", props);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Utils.Error.onServiceCallFail(getActivity(), t);
                    }
                });
            }
        }

        Bundle params = new Bundle();
        params.putString("user_actions", "Video Home");
        mFirebaseAnalytics.logEvent("VideoTab", params);

        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupSliderView() {
        String kidsModePassword = Utils.SharedPrefUtil.getStringFromSharedPref(getActivity(), "kids_mode_password", "");
        boolean isKidsModeEnabled = false;
        if (kidsModePassword != null && !"".equals(kidsModePassword)) {
            isKidsModeEnabled = true;
            isKidsMoodOn = true;
        } else {
            isKidsModeEnabled = false;
            isKidsMoodOn = false;
        }

        tvManager.getProgramsSliderImages(new APIListener<List<Program>>() {
            @Override
            public void onSuccess(List<Program> sliders, List<Object> params) {

                if (sliders != null) {
                    programs = sliders;

                    binding.pager.setAdapter(new SlidingImageAdapter(getActivity(), programs));
                    binding.indicator.setViewPager(binding.pager);

                    final float density = getResources().getDisplayMetrics().density;
                    binding.indicator.setRadius(5 * density);

                    NUM_PAGES = programs.size();
                    if(!isSlidersLoaded){
                        setUpTimer();
                        isSlidersLoaded = true;
                    }


                }

            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        }, isKidsModeEnabled);

    }

    private void setUpTimer() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                binding.pager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void loadChannelsAndPrograms() {
        String kidsModePassword = Utils.SharedPrefUtil.getStringFromSharedPref(getActivity(), "kids_mode_password", "");
        boolean isKidsModeEnabled = false;
        if (kidsModePassword != null && !"".equals(kidsModePassword)) {
            isKidsModeEnabled = true;
        } else {
            isKidsModeEnabled = false;
        }

        System.out.println("jdjdjdjddjdjdjdj 111 " + isKidsModeEnabled);
        channelAdapter = new ChannelAdapter(getActivity(), VideoHomeFragment.this);
        tvManager.getAllChannels(new APIListener<List<Channel>>() {
            @Override
            public void onSuccess(final List<Channel> channels, List<Object> params) {
                System.out.println("jdjdjdjddjdjdjdj 222 " + channels.size());
                binding.recycleviewChannelsPrograms.setAdapter(channelAdapter);
                if (selectedDate == null) {
                    selectedDate = Utils.DateUtil.getDateOnly(new Date());
                }
                Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
                if (dateToSend == null) {
                    dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
                }
                if (channels != null && channels.size() > 0) {
                    channelList = channels;
                    channelSize = channels.size();
                    if(isKidsMoodOn){
                        fetchKisdProgramsByChannelId(channelList.get(0));
                    }else{
                        fetchProgramsByChannelId(channelList.get(0));
                    }


                }


            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        }, isKidsModeEnabled);
    }


    @Subscribe
    public void onChildModeActivated(VideoChildModeActivity.ChildModeOnEvent event) {
        isKidsMoodOn = true;
        isChannelLoaded = false;
        currentChannelIndex = 0;
        System.out.println("checking kidsmood 111111 ");
        loadChannelsAndPrograms();
        setupSliderView();
    }

    @Subscribe
    public void onChildModeDeactivated(VideoChildModeActivity.ChildModeOffEvent event) {
        isKidsMoodOn = false;
        isChannelLoaded = false;
        currentChannelIndex = 0;
        loadChannelsAndPrograms();
        setupSliderView();
    }

    @Override
    public void onDestroy() {
        Application.BUS.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onProgramItemClick(View view, Program program, int position) {
        selectedProgram = program;
        selectedProgramPosition = position;
        System.out.println("checking subscriped 4444 " + program.isSubscribed());

        Intent intentEpisodes = new Intent(getActivity(), VideoEpisodeActivity.class);
        intentEpisodes.putExtra("program", program);
        startActivity(intentEpisodes);
        JSONObject props = new JSONObject();
        try {
            props.put("ProgramName", program.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((Application) getActivity().getApplication()).getMixpanelAPI().track("Program Selected", props);

    }

    private void fetchProgramsByChannelId(final Channel channel) {

        if (selectedDate == null) {
            selectedDate = Utils.DateUtil.getDateOnly(new Date());
        }
        Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        if (dateToSend == null) {
            dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        }

        tvManager.getPrograms(channel.getId(), dateToSend, dateToSend, new APIListener<List<Program>>() {
            @Override
            public void onSuccess(List<Program> programs, List<Object> params) {

                setProgramsToAdapter(programs,channel);


            }

            @Override
            public void onFailure(Throwable t) {
                if (isAdded())
                    Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });

    }

    private void setProgramsToAdapter(List<Program> programs,Channel channel){
        if (isAdded()) {
            if (programs.size() > 0) {
                channel.setProgramList(programs);
                channelAdapter.addChannel(channel);
                if (!isChannelLoaded) {
                    System.out.println("fkbcfbfjkcbkfbb 1111 ");
                    binding.aviProgressRecycler.setVisibility(View.GONE);
                    binding.recycleviewChannelsPrograms.setAdapter(channelAdapter);
//                            ViewCompat.setNestedScrollingEnabled(binding.recycleviewChannelsPrograms, false);
                    isChannelLoaded = true;

                } else {
                    System.out.println("fkbcfbfjkcbkfbb 2222 " + currentChannelIndex);


                }
            }
            if (!isKidsMoodOn) {
                currentChannelIndex = currentChannelIndex + 1;
                if (currentChannelIndex < channelSize) {
                    fetchProgramsByChannelId(channelList.get(currentChannelIndex));
                }
            }


        }
    }
    private void fetchKisdProgramsByChannelId(final Channel channel) {

        if (selectedDate == null) {
            selectedDate = Utils.DateUtil.getDateOnly(new Date());
        }
        Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        if (dateToSend == null) {
            dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        }

        tvManager.getPrograms(channel.getId(), dateToSend, dateToSend, new APIListener<List<Program>>() {
            @Override
            public void onSuccess(List<Program> programs, List<Object> params) {


                if (isAdded()) {
                    if (programs.size() > 0) {
                        channelAdapter.removeChannels();
                        channel.setProgramList(programs);
                        channelAdapter.addChannel(channel);

                            binding.aviProgressRecycler.setVisibility(View.GONE);
                            binding.recycleviewChannelsPrograms.setAdapter(channelAdapter);
                            ViewCompat.setNestedScrollingEnabled(binding.recycleviewChannelsPrograms, false);
                            channelAdapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onFailure(Throwable t) {
                if (isAdded())
                    Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });

    }

    @Subscribe
    public void onChildModeActivated(VideoEpisodeActivity.SubscribeEvent event) {
        selectedProgram.setSubscribed(event.getSubcribed());
        channelAdapter.notifyDataSetChanged();
//        channelAdapter.notifyItemChanged(selectedProgramPosition,selectedProgram);
    }
}