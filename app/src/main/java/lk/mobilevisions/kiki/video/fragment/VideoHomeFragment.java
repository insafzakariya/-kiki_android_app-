package lk.mobilevisions.kiki.video.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.StatusListener;
import com.twilio.chat.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
import lk.mobilevisions.kiki.chat.ChatActivity;
import lk.mobilevisions.kiki.chat.ChatClientManager;

import lk.mobilevisions.kiki.chat.ChatProfileActivity;
import lk.mobilevisions.kiki.chat.channels.ChannelManager;
import lk.mobilevisions.kiki.chat.channels.ChannelVerticalAdapter;
import lk.mobilevisions.kiki.chat.channels.LoadChannelListener;
import lk.mobilevisions.kiki.chat.listeners.TaskCompletionListener;
import lk.mobilevisions.kiki.chat.module.ChatManager;
import lk.mobilevisions.kiki.chat.module.dto.ChannelDto;
import lk.mobilevisions.kiki.chat.module.dto.ChatMember;
import lk.mobilevisions.kiki.chat.module.dto.ChatToken;
import lk.mobilevisions.kiki.databinding.FragmentHomeVideoBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Channel;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.api.dto.PackageV2;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.video.activity.VideoChildModeActivity;
import lk.mobilevisions.kiki.video.activity.VideoEpisodeActivity;
import lk.mobilevisions.kiki.video.activity.VideoTrialActivationActivity;
import lk.mobilevisions.kiki.video.adapter.ChannelAdapter;
import lk.mobilevisions.kiki.video.adapter.ProgramAdapter;
import lk.mobilevisions.kiki.video.adapter.SlidingImageAdapter;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread;


public class VideoHomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener,
        ProgramAdapter.OnProgramItemClickListener, ChannelVerticalAdapter.OnChannelItemActionListener, ChatClientListener {

    @Inject
    TvManager tvManager;
    @Inject
    ChatManager chatManager;

    @Inject
    SubscriptionsManager subscriptionsManager;
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

    private String alertTitle;
    private String messageBody;
    private String sSID;
    private String chatImage;

    private ChatClientManager chatClientManager;
    private ProgressDialog progressDialog;
    private ChannelManager channelManager;
    private ChannelVerticalAdapter channelVerticalAdapter;
    List<com.twilio.chat.Channel> chatChannelList;
    List<ChannelDto> channelDtoList = new ArrayList<>();
    List<ChatMember> chatMemberArrayList;
    String memberOnlineCount;
    private boolean isTiwilioChannelLoaded;
    private boolean isUserImageAvailable;
    HashMap<String, com.twilio.chat.Channel> channelHashMap = new HashMap<String, com.twilio.chat.Channel>();

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

        binding.chatChannelsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.chatChannelsRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.chatChannelsRecyclerview.setHasFixedSize(true);
        binding.chatChannelsRecyclerview.setNestedScrollingEnabled(false);
        setupSliderView();
        Application.BUS.register(this);
        loadChannelsAndPrograms();
        checkSubscription();
        channelManager = ChannelManager.getInstance();
        getChatToken();
        getChatMembers();


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

        if (programType != null) {
            if (programType.equals("0")) {
                System.out.println("dhdhdhd 0000 ");
                tvManager.getProgramWithID(programid, new APIListener<Program>() {
                    @Override
                    public void onSuccess(Program program, List<Object> params) {

                        System.out.println("dhdhdhd 1111 ");

                        System.out.println("dhdhdhd " + program);
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
                        System.out.println("dhdhdhd 2222 ");
                        Utils.Error.onServiceCallFail(getActivity(), t);
                    }
                });
            }
        }

        checkUserImageAvailability();

        Bundle params = new Bundle();
        params.putString("user_actions", "videoHome");
        mFirebaseAnalytics.logEvent("video_tab", params);

        return binding.getRoot();
    }

    private void checkUserImageAvailability(){

        try {
            Picasso.with(getActivity()).load("https://storage.googleapis.com/kiki_images/live/viewer/1x/" + Application.getInstance().getAuthUser().getId() + ".jpeg")
            .into(binding.testImageview, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    isUserImageAvailable = true;

                }

                @Override
                public void onError() {
                    isUserImageAvailable = false;

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getChatToken() {
        chatManager.getAccessToken(new APIListener<ChatToken>() {
            @Override
            public void onSuccess(ChatToken chatToken, List<Object> params) {
                System.out.println("dydhdyhdhdhdh  66666 " + chatToken.getDataObject().getToken());
                checkTwilioClient(chatToken.getDataObject().getToken());
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });
    }

    private void checkTwilioClient(String token) {
        chatClientManager = Application.getInstance().getChatClientManager();
        if (chatClientManager.getChatClient() == null) {
            System.out.println("dydhdyhdhdhdh  0000 ");
            initializeClient(token);
        } else {
            System.out.println("dydhdyhdhdhdh  11111 ");
            getChatChannels();
            populateChannels();
        }
    }

    private void initializeClient(String token) {
        chatClientManager.connectClient(token, new TaskCompletionListener<Void, String>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("dydhdyhdhdhdh  33333 ");
                getChatChannels();
                populateChannels();
            }

            @Override
            public void onError(String errorMessage) {
                System.out.println("dydhdyhdhdhdh  4444 " + errorMessage);
            }
        });
    }

    private void stopActivityIndicator() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void getChatChannels() {

        chatManager.getChannels(new APIListener<List<ChannelDto>>() {
            @Override
            public void onSuccess(List<ChannelDto> result, List<Object> params) {
                System.out.println("dydhdyhdhdhdh  55555 " + result.size());
                if (result.size() == 0) {
                    binding.aviProgressRecycler.setVisibility(View.GONE);

                } else {
//                    binding.radioDramaLayout.setVisibility(View.VISIBLE);
                    channelDtoList = result;
                    if (isTiwilioChannelLoaded) {
                        binding.chatChannelsRecyclerview.setVisibility(View.VISIBLE);
                        binding.chatHeader.setVisibility(View.VISIBLE);
                        if (isChannelLoaded) {
                            binding.aviProgressRecycler.setVisibility(View.GONE);
                        }
                        channelVerticalAdapter = new ChannelVerticalAdapter(getActivity(), channelDtoList, VideoHomeFragment.this);
                        binding.chatChannelsRecyclerview.setAdapter(channelVerticalAdapter);
                    }

                }

            }

            @Override
            public void onFailure(Throwable t) {


            }
        });

    }

    private void getChatMembers() {


        chatManager.getChatMembers(8, "all", new APIListener<List<ChatMember>>() {
            @Override
            public void onSuccess(List<ChatMember> result, List<Object> params) {

                chatMemberArrayList = result;
                memberOnlineCount = Integer.toString(result.size());
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });


    }


    private void populateChannels() {
        System.out.println("dydhdyhdhdhdh  010101010101010 ");
        channelManager.setChannelListener(this);
        channelManager.populateChannels(new LoadChannelListener() {
            @Override
            public void onChannelsFinishedLoading(List<com.twilio.chat.Channel> channels) {
                System.out.println("dydhdyhdhdhdh  222222 " + channels.size());
                chatChannelList = channels;
                isTiwilioChannelLoaded = true;
                if (channelDtoList != null && channelDtoList.size() > 0 && channelVerticalAdapter == null) {
                    binding.chatChannelsRecyclerview.setVisibility(View.VISIBLE);
                    binding.chatHeader.setVisibility(View.VISIBLE);
                    if (isChannelLoaded) {
                        binding.aviProgressRecycler.setVisibility(View.GONE);
                    }
                    channelVerticalAdapter = new ChannelVerticalAdapter(getActivity(), channelDtoList, VideoHomeFragment.this);
                    binding.chatChannelsRecyclerview.setAdapter(channelVerticalAdapter);
                }
            }
        });
    }


    private String getStringResource(int id) {
        Resources resources = getResources();
        return resources.getString(id);
    }

    private void trialSubscriptionDialog() {
        LayoutInflater myLayout = LayoutInflater.from(getActivity());
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_trial_subscription, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
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
                Intent intentPackages = new Intent(getActivity(), VideoTrialActivationActivity.class);
                startActivity(intentPackages);


            }
        });
        closeImageView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });
    }

    private void checkSubscription() {
        subscriptionsManager.generateSubscriptionToken((int) Utils.App.getConfig(getActivity().getApplication()).getPaidPackageId(),
                new APIListener<PackageToken>() {
                    @Override
                    public void onSuccess(final PackageToken packageToken, List<Object> params) {

                        subscriptionsManager.getTrialStatus(new APIListener<PackageV2>() {
                            @Override
                            public void onSuccess(PackageV2 thePackage, List<Object> params) {

                                alertTitle = thePackage.getTitleText();
                                messageBody = thePackage.getMessageBody();

                                if (thePackage.isTrialStatus() && !thePackage.isSubStatus()) {

                                    try {
                                        trialSubscriptionDialog();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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
                    if (!isSlidersLoaded) {
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
                    if (isKidsMoodOn) {
                        fetchKisdProgramsByChannelId(channelList.get(0));
                    } else {
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
        System.out.println("dscsd 111 " + program.getId());

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

                setProgramsToAdapter(programs, channel);


            }

            @Override
            public void onFailure(Throwable t) {
                if (isAdded())
                    Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });

    }

    private void setProgramsToAdapter(List<Program> programs, Channel channel) {
        if (isAdded()) {
            if (programs.size() > 0) {
                channel.setProgramList(programs);
                channelAdapter.addChannel(channel);
                if (!isChannelLoaded) {
                    System.out.println("fkbcfbfjkcbkfbb 1111 ");
                    if (isTiwilioChannelLoaded) {
                        binding.aviProgressRecycler.setVisibility(View.GONE);
                    }

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

//            binding.aviProgressRecycler.setVisibility(View.GONE);
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
                        isChannelLoaded = true;
                        if (isTiwilioChannelLoaded) {
                            binding.aviProgressRecycler.setVisibility(View.GONE);
                        }
                        binding.recycleviewChannelsPrograms.setAdapter(channelAdapter);
                        ViewCompat.setNestedScrollingEnabled(binding.recycleviewChannelsPrograms, false);
                        channelAdapter.notifyDataSetChanged();
//                        binding.aviProgressRecycler.setVisibility(View.GONE);
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

    @Override
    public void onChannelJoined(com.twilio.chat.Channel channel) {

    }

    @Override
    public void onChannelInvited(com.twilio.chat.Channel channel) {

    }

    @Override
    public void onChannelAdded(com.twilio.chat.Channel channel) {

    }

    @Override
    public void onChannelUpdated(com.twilio.chat.Channel channel, com.twilio.chat.Channel.UpdateReason updateReason) {

    }

    @Override
    public void onChannelDeleted(com.twilio.chat.Channel channel) {

    }

    @Override
    public void onChannelSynchronizationChange(com.twilio.chat.Channel channel) {

    }

    @Override
    public void onError(ErrorInfo errorInfo) {

    }

    @Override
    public void onUserUpdated(User user, User.UpdateReason updateReason) {

    }

    @Override
    public void onUserSubscribed(User user) {

    }

    @Override
    public void onUserUnsubscribed(User user) {

    }

    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {

    }

    @Override
    public void onNewMessageNotification(String s, String s1, long l) {

    }

    @Override
    public void onAddedToChannelNotification(String s) {

    }

    @Override
    public void onInvitedToChannelNotification(String s) {

    }

    @Override
    public void onRemovedFromChannelNotification(String s) {

    }

    @Override
    public void onNotificationSubscribed() {

    }

    @Override
    public void onNotificationFailed(ErrorInfo errorInfo) {

    }

    @Override
    public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {

    }

    @Override
    public void onTokenExpired() {

    }

    @Override
    public void onTokenAboutToExpire() {

    }

    @Override
    public void onChannelAction(ChannelDto channelDto, int position, List<ChannelDto> playLists) {

        Vibrator vibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibe.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibe.vibrate(50);
        }

        if (!isUserImageAvailable) {
            Intent intent = new Intent(getActivity(), ChatProfileActivity.class);
            intent.putExtra("memberList", (Serializable) chatMemberArrayList);
            intent.putExtra("chatImage", chatImage);
            intent.putExtra("memberCount", memberOnlineCount);
            intent.putExtra("channelDTO", channelDto);
            startActivity(intent);
        }

        chatImage = channelDto.getImagePath();
        if (chatChannelList != null) {
            if (channelDto.isBlock() && !channelDto.isMember()) {
                System.out.println("checking ss 1111111 " + channelDto.getSid());
                com.twilio.chat.Channel selectedChatChannel = null;

                for (com.twilio.chat.Channel channel1 : chatChannelList) {
                    if (channelDto.getSid().equals(channel1.getSid())) {
                        System.out.println("checking ss 33333");
                        selectedChatChannel = channel1;
                    }
                }
                if (selectedChatChannel != null) {
                    System.out.println("checking ss 44444 " + selectedChatChannel.getSid());
                    Application.getInstance().setCurrentChannel(selectedChatChannel);
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("memberList", (Serializable) chatMemberArrayList);
                    intent.putExtra("chatImage", chatImage);
                    intent.putExtra("memberCount", memberOnlineCount);
                    startActivity(intent);

                }


            } else if (!channelDto.isBlock() && !channelDto.isMember()) {

                System.out.println("checking ss 3333 " + channelDto.getSid());
                sSID = channelDto.getSid();
                com.twilio.chat.Channel selectedChatChannel = null;
                for (com.twilio.chat.Channel channel1 : chatChannelList) {

                    if (channelDto.getSid().equals(channel1.getSid())) {
                        System.out.println("checking ss 33333");
                        selectedChatChannel = channel1;
                    }
                }
                if (selectedChatChannel != null) {
                    System.out.println("checking ss 44444");
                    getRoleDetail(selectedChatChannel);
                }
            } else if (!channelDto.isBlock()) {
                System.out.println("checking ss 5555555");
                sSID = channelDto.getSid();
                com.twilio.chat.Channel selectedChatChannel = null;
                for (com.twilio.chat.Channel channel1 : chatChannelList) {

                    if (channelDto.getSid().equals(channel1.getSid())) {
                        System.out.println("checking ss 33333");
                        selectedChatChannel = channel1;
                    }
                }
                if (selectedChatChannel != null) {
                    System.out.println("checking ss 44444");
                    getRoleDetail(selectedChatChannel);
                }
            } else if (channelDto.isBlock()) {

                Toast.makeText(getApplicationContext(), "You're Blocked.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void getRoleDetail(com.twilio.chat.Channel selctedChannel) {
        chatManager.getRoleDetail(new APIListener<ChannelDto>() {
            @Override
            public void onSuccess(ChannelDto channelDto, List<Object> params) {
                System.out.println("checking ss 66666 ");

                createMember(selctedChannel, channelDto.getSid(), channelDto.getAccountSid(), channelDto.getServiceSid(), channelDto.getId());
            }

            @Override
            public void onFailure(Throwable t) {

                System.out.println("checking ss 77777 " + t.getLocalizedMessage());
            }
        });
    }

    private void createMember(com.twilio.chat.Channel channel, String sid, String accountSid, String serviceSid, int roleId) {
        int viewverID = Application.getInstance().getAuthUser().getId();
        String name = Application.getInstance().getAuthUser().getName();
        List<Integer> channelIdList = new ArrayList<>();
        for (ChannelDto channelDto : channelDtoList) {
            channelIdList.add(channelDto.getId());
        }
        chatManager.createChatMember(sid, accountSid, serviceSid, roleId, viewverID, name, channelIdList, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {
                System.out.println("checking ss 888888 ");
                joinChannel(channel);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("checking ss 99999 " + t.getLocalizedMessage());
                joinChannel(channel);
            }
        });
    }

    private void joinChannel(final com.twilio.chat.Channel selectedChannel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selectedChannel.getStatus() == com.twilio.chat.Channel.ChannelStatus.JOINED) {
                    System.out.println("checking ss 1010101 ");
                    Application.getInstance().setCurrentChannel(selectedChannel);
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("memberList", (Serializable) chatMemberArrayList);
                    intent.putExtra("chatImage", chatImage);
                    intent.putExtra("memberCount", memberOnlineCount);
                    startActivity(intent);
                } else {
                    System.out.println("checking ss 12121212 ");
                    selectedChannel.join(new StatusListener() {
                        @Override
                        public void onSuccess() {
                            System.out.println("checking ss 1313131 ");
                            Application.getInstance().setCurrentChannel(selectedChannel);
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("memberList", (Serializable) chatMemberArrayList);
                            intent.putExtra("chatImage", chatImage);
                            intent.putExtra("memberCount", memberOnlineCount);
                            startActivity(intent);
//                            Toast.makeText(getActivity(), "Member Created.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            System.out.println("checking ss 14141414 " + errorInfo.getMessage());
                        }
                    });
                }


            }
        });
    }

}