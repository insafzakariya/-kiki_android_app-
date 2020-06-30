package lk.mobilevisions.kiki.audio.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Random;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.activity.AudioPaymentActivity;
import lk.mobilevisions.kiki.audio.adapter.ArtistsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.DailyMixAdapter;
import lk.mobilevisions.kiki.audio.adapter.GenreArtistVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.LatestSongsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.PlaylistVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.PopularSongsVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.RadioChannelVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.RecentlyPlayedVerticalAdapter;
import lk.mobilevisions.kiki.audio.adapter.YouAlsoMightLikeVerticalAdapter;
import lk.mobilevisions.kiki.audio.event.SearchNavigationEvent;
import lk.mobilevisions.kiki.audio.event.UserNavigateBackEvent;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.DailyMix;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.util.SpacesItemDecoration;
import lk.mobilevisions.kiki.databinding.FragmentHomeAudioBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Package;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsManager;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class AudioHomeFragment extends Fragment implements DailyMixAdapter.DailyMixItemActionListener, GenreArtistVerticalAdapter.OnArtistsItemActionListener, PlaylistVerticalAdapter.OnPlaylistItemActionListener {

    @Inject
    TvManager tvManager;

    FragmentHomeAudioBinding binding;
    private int episodesLimit = 200;
    Date selectedDate;
    Context context;
    List<DailyMix> dailyMixArrayList = new ArrayList<>();
    List<PlayList> playlistArrayList = new ArrayList<>();
    List<Song> recentlyPlayedArrayList = new ArrayList<>();
    List<Song> youAlsoMightLikeArrayList = new ArrayList<>();
    List<Song> popularSongsArrayList = new ArrayList<>();
    List<Song> latestSongsArrayList = new ArrayList<>();
    List<Song> radioChannelsArrayList = new ArrayList<>();
    List<Artist> artistsArrayList = new ArrayList<>();
    @Inject
    SubscriptionsManager subscriptionsManager;
    DailyMixAdapter dailyMixAdapter;
    RecentlyPlayedVerticalAdapter recentlyPlayedVerticalAdapter;
    YouAlsoMightLikeVerticalAdapter youAlsoMightLikeVerticalAdapter;
    PopularSongsVerticalAdapter popularSongsVerticalAdapter;
    LatestSongsVerticalAdapter latestSongsVerticalAdapter;
    RadioChannelVerticalAdapter radioChannelVerticalAdapter;
    GenreArtistVerticalAdapter artistsVerticalAdapter;
    PlaylistVerticalAdapter playlistVerticalAdapter;
    private DailyMixAdapter.DailyMixItemActionListener dailyMixItemActionListener;
    private RecentlyPlayedVerticalAdapter.RecentlyPlayedItemActionListener recentlyPlayedItemActionListener;
    private YouAlsoMightLikeVerticalAdapter.OnYouMightAlsoLikeItemActionListener youMightAlsoLikeItemActionListener;
    private PopularSongsVerticalAdapter.OnPopularSongsItemActionListener popularSongsItemActionListener;
    private LatestSongsVerticalAdapter.OnLatestSongsItemActionListener latestSongsItemActionListener;
    private RadioChannelVerticalAdapter.OnRadioChannelItemActionListener radioChannelItemActionListener;
    private ArtistsVerticalAdapter.OnArtistsItemActionListener artistsItemActionListener;
    private PlaylistVerticalAdapter.OnPlaylistItemActionListener playlistItemActionListener;

    private int lastRandomNumber;


    public AudioHomeFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_audio, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);

        checkSubscription();
        setupDailyMix();
        setupRecenltPlayed();
        setupYouMightAlsoLike();
        setupPopularSongs();
        setupLatestSongs();
        setupRadioChannel();
        setupArtists();
        setDataToDailyMix();
//        setDataToRecentlyPlayed();
//        setDataToYouAlsoMightLike();
        setDataToPopularSongs();
        setDataToLatestSongs();
        setDataToRadioChannel();
        setDataToArtists();

        Application.BUS.register(this);

        binding.seeAllRecentlyPlayed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecentlyPlayedFragment recentlyPlayedFragment = new RecentlyPlayedFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_recently_played, recentlyPlayedFragment, "recently played")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllYouMightLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YouMightAlsoLikeFragment youMightAlsoLikeFragment = new YouMightAlsoLikeFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_you_might_like, youMightAlsoLikeFragment, "you might like")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllPopularSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YouMightAlsoLikeFragment youMightAlsoLikeFragment = new YouMightAlsoLikeFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_popular_songs, youMightAlsoLikeFragment, "popular songs")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllLatestSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatestSongsFragment latestSongsFragment = new LatestSongsFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_latest_songs, latestSongsFragment, "latest songs")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllArtists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtistListFragment artistListFragment = new ArtistListFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_artists, artistListFragment, "Artists")
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.seeAllLatestPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatestPlaylistFragment latestPlaylistFragment = new LatestPlaylistFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_container_latest_playlist, latestPlaylistFragment, "Latest Playlist")
                        .addToBackStack(null)
                        .commit();
            }
        });


        recentlyPlayedVerticalAdapter = RecentlyPlayedVerticalAdapter.getInstance(context);
        recentlyPlayedVerticalAdapter.setActionListener(recentlyPlayedItemActionListener);
        youAlsoMightLikeVerticalAdapter = YouAlsoMightLikeVerticalAdapter.getInstance(context);
        youAlsoMightLikeVerticalAdapter.setYouMightAlsoLikeItemActionListenerActionListener(youMightAlsoLikeItemActionListener);
        popularSongsVerticalAdapter = PopularSongsVerticalAdapter.getInstance(context);
        popularSongsVerticalAdapter.setOnPopularSongsItemActionListener(popularSongsItemActionListener);
        latestSongsVerticalAdapter = LatestSongsVerticalAdapter.getInstance(context);
        latestSongsVerticalAdapter.setLatestSongsrActionListener(latestSongsItemActionListener);
        radioChannelVerticalAdapter = RadioChannelVerticalAdapter.getInstance(context);
        radioChannelVerticalAdapter.setRadioChannelItemActionListener(radioChannelItemActionListener);


        String programType = getActivity().getIntent().getStringExtra("programType");
        String programid = getActivity().getIntent().getStringExtra("programId");
        int contentId = 0;
        try {
            contentId = Integer.parseInt(programid);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String contentType = getActivity().getIntent().getStringExtra("contentType");

        if (programType != null && contentType !=null){
            if (programType.equals("1") && contentType.equals("playlistid")){

                tvManager.getPlaylistWithID(contentId, new APIListener <PlayList>() {
                    @Override
                    public void onSuccess(PlayList playList, List<Object> params) {

                        Bundle bundle=new Bundle();
                        bundle.putInt("playlistID", playList.getId());
                        bundle.putString("playlistName", playList.getName());
                        bundle.putString("songCount", playList.getSongCount());
                        bundle.putString("playlistImage", playList.getImage());
                        bundle.putString("playlistYear", playList.getDate());

                        PlaylistDetailFragment playlistDetailFragment = new PlaylistDetailFragment();
                        playlistDetailFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container_home_playlist_detail, playlistDetailFragment, "Notify to PlaylistDetail")
                                .addToBackStack(null)
                                .commit();
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Utils.Error.onServiceCallFail(getActivity(), t);
                    }
                });
            } else if (programType.equals("1") && contentType.equals("artistid")){

                tvManager.getArtistWithID(contentId, new APIListener <Artist>() {
                    @Override
                    public void onSuccess(Artist artist, List<Object> params) {

                        Bundle bundle=new Bundle();
                        bundle.putInt("artistID", artist.getId());
                        bundle.putString("artistName", artist.getName());
                        bundle.putString("artistImage", artist.getImage());
                        bundle.putString("songCount", artist.getSongsCount());

                        ArtistDetailFragment artistDetailFragment = new ArtistDetailFragment();
                        artistDetailFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.frame_container_artist_detail_playlist, artistDetailFragment, "Notify to ArtistDetail")
                                .addToBackStack(null)
                                .commit();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Utils.Error.onServiceCallFail(getActivity(), t);
                    }
                });

            }else if (programType.equals("1") && contentType.equals("songid")){

                tvManager.getSongWithID(contentId, new APIListener <Song>() {
                    @Override
                    public void onSuccess(Song song, List<Object> params) {

                        System.out.println("sjnfsjkbfv 1111" + song.getId());
                        List<Song> songsList = new ArrayList<>();
                        songsList.add(song);

                        AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                        hhh.setNotifcationSong(song , 0,songsList);

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Utils.Error.onServiceCallFail(getActivity(), t);
                    }
                });

            }

        }


        return binding.getRoot();
    }


    private void setupDailyMix() {
        binding.dailyMixRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.dailyMixRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.dailyMixRecyclerview.setHasFixedSize(true);
        binding.dailyMixRecyclerview.setNestedScrollingEnabled(false);


    }

    private void setupRecenltPlayed() {

        binding.recentlyPlayedRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.recentlyPlayedRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.recentlyPlayedRecyclerview.setHasFixedSize(true);
        binding.recentlyPlayedRecyclerview.setNestedScrollingEnabled(false);
    }

    private void setupYouMightAlsoLike() {
        binding.youMightRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.youMightRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.youMightRecyclerview.setHasFixedSize(true);
        binding.youMightRecyclerview.setNestedScrollingEnabled(false);

    }

    private void setupPopularSongs() {
        binding.popularSongsRecycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.popularSongsRecycleview.addItemDecoration(new SpacesItemDecoration(15));
        binding.popularSongsRecycleview.setHasFixedSize(true);
        binding.popularSongsRecycleview.setNestedScrollingEnabled(false);

    }

    private void setupLatestSongs() {
        binding.latestSongsRecycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.latestSongsRecycleview.addItemDecoration(new SpacesItemDecoration(15));
        binding.latestSongsRecycleview.setHasFixedSize(true);
        binding.latestSongsRecycleview.setNestedScrollingEnabled(false);

    }

    private void setupRadioChannel() {
        binding.radioChannelRecycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.radioChannelRecycleview.addItemDecoration(new SpacesItemDecoration(15));
        binding.radioChannelRecycleview.setHasFixedSize(true);
        binding.radioChannelRecycleview.setNestedScrollingEnabled(false);

    }

    private void setupArtists() {
        binding.artistRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.artistRecyclerview.addItemDecoration(new SpacesItemDecoration(15));
        binding.artistRecyclerview.setHasFixedSize(true);
        binding.artistRecyclerview.setNestedScrollingEnabled(false);

    }

    private void setDataToDailyMix() {
        tvManager.getDailyMixNew(0,10,new APIListener<List<PlayList>>() {
            @Override
            public void onSuccess(List<PlayList> result, List<Object> params) {
                System.out.println("Check playlist  " + result.size());
                if (result.size() == 0) {
                    binding.dailyMixLayout.setVisibility(View.GONE);

                } else {
                    binding.dailyMixLayout.setVisibility(View.VISIBLE);
                    playlistArrayList = result;
                    playlistVerticalAdapter = new PlaylistVerticalAdapter(getActivity(), playlistArrayList,AudioHomeFragment.this);
                    binding.dailyMixRecyclerview.setAdapter(playlistVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });


    }

//    private void setDataToRecentlyPlayed() {
//
//
//        tvManager.getAudioRecentlyPlayed(new APIListener<List<Song>>() {
//            @Override
//            public void onSuccess(List<Song> result, List<Object> params) {
//                if (result.size() == 0) {
//
//                    binding.recentlyPlayedLayout.setVisibility(View.GONE);
//                } else {
//                    binding.recentlyPlayedLayout.setVisibility(View.VISIBLE);
//                    recentlyPlayedArrayList = result;
//                    recentlyPlayedVerticalAdapter = new RecentlyPlayedVerticalAdapter(getActivity(), recentlyPlayedArrayList);
//                    binding.recentlyPlayedRecyclerview.setAdapter(recentlyPlayedVerticalAdapter);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//
//
//            }
//        });
//
//
//    }

    private int generateRandomIndex(int size) {
        while (true) {
            Random random = new Random();
            int randomNumber = random.nextInt(3);
            if (randomNumber != lastRandomNumber) {
                lastRandomNumber = randomNumber;
                return randomNumber;
            }
        }
    }

    private void setDataToYouAlsoMightLike() {

        tvManager.getAudioYouLike(10, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result, List<Object> params) {
                if (result.size() == 0) {
                    binding.youMightLikeLayout.setVisibility(View.GONE);

                } else {
                    binding.youMightLikeLayout.setVisibility(View.VISIBLE);
                    youAlsoMightLikeArrayList = result;
                    youAlsoMightLikeVerticalAdapter = new YouAlsoMightLikeVerticalAdapter(getActivity(), youAlsoMightLikeArrayList);
                    binding.youMightRecyclerview.setAdapter(youAlsoMightLikeVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });

    }

    private void setDataToPopularSongs() {

        tvManager.getPopularSongs( 0,12,new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result, List<Object> params) {
                if (result.size() == 0) {
                    binding.popularSongsLayout.setVisibility(View.GONE);

                } else {
                    binding.popularSongsLayout.setVisibility(View.VISIBLE);
                    popularSongsArrayList = result;
                    popularSongsVerticalAdapter = new PopularSongsVerticalAdapter(getActivity(), popularSongsArrayList);
                    binding.popularSongsRecycleview.setAdapter(popularSongsVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });

    }

    private void setDataToLatestSongs() {

        tvManager.getLatestSongs( new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result, List<Object> params) {
                if (result.size() == 0) {
                    binding.latestSongsLayout.setVisibility(View.GONE);

                } else {
                    binding.latestSongsLayout.setVisibility(View.VISIBLE);
                    latestSongsArrayList = result;
                    popularSongsVerticalAdapter = new PopularSongsVerticalAdapter(getActivity(), latestSongsArrayList);
                    binding.latestSongsRecycleview.setAdapter(popularSongsVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });

    }

    private void setDataToRadioChannel() {

        tvManager.getRadioChannels( new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result, List<Object> params) {
                if (result.size() == 0) {
                    binding.radioChannelLayout.setVisibility(View.GONE);

                } else {
                    binding.radioChannelLayout.setVisibility(View.VISIBLE);
                    for(Song song : result){
                        Song song1 = new Song();
                        try {
                            song1.setUrl(URLDecoder.decode(song.getUrl(), "UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        song1.setBlocked(song.isBlocked());
                        song1.setDate(song.getDate());
                        song1.setDescription(song.getDescription());
                        song1.setDuration(song.getDuration());
                        song1.setId(song.getId());
                        song1.setImage(song.getImage());
                        song1.setName(song.getName());
                        radioChannelsArrayList.add(song1);
                    }

                    popularSongsVerticalAdapter = new PopularSongsVerticalAdapter(getActivity(), radioChannelsArrayList);
                    binding.radioChannelRecycleview.setAdapter(popularSongsVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });

    }

    private void setDataToArtists() {

        tvManager.getPopularArtists( new APIListener<List<Artist>>() {
            @Override
            public void onSuccess(List<Artist> result, List<Object> params) {
                if (result.size() == 0) {
                    binding.artistLayout.setVisibility(View.GONE);

                } else {
                    binding.artistLayout.setVisibility(View.VISIBLE);
                    artistsArrayList = result;
                    artistsVerticalAdapter = new GenreArtistVerticalAdapter(getActivity(), artistsArrayList,AudioHomeFragment.this);
                    binding.artistRecyclerview.setAdapter(artistsVerticalAdapter);
                }
                binding.aviProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {


            }
        });

    }




    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.context = context;

    }

//    public void setDailyMixActionListener(DailyMixAdapter.DailyMixItemActionListener actionListener) {
//        this.dailyMixItemActionListener = actionListener;
//    }

    public void setActionListener(RecentlyPlayedVerticalAdapter.RecentlyPlayedItemActionListener actionListener) {
        this.recentlyPlayedItemActionListener = actionListener;
    }

    public void setYouMightAlsoLikeItemActionListenerActionListener(YouAlsoMightLikeVerticalAdapter.OnYouMightAlsoLikeItemActionListener actionListener) {
        this.youMightAlsoLikeItemActionListener = actionListener;
    }

    public void setOnPopularOnSongsItemActionListener(PopularSongsVerticalAdapter.OnPopularSongsItemActionListener actionListener) {
        this.popularSongsItemActionListener = actionListener;
    }

    public void setLatestSongsrActionListener(LatestSongsVerticalAdapter.OnLatestSongsItemActionListener actionListener) {
        this.latestSongsItemActionListener = actionListener;
    }

    public void setRadioChannelItemActionListener(RadioChannelVerticalAdapter.OnRadioChannelItemActionListener actionListener) {
        this.radioChannelItemActionListener = actionListener;
    }

    public void setArtistsItemActionListener(ArtistsVerticalAdapter.OnArtistsItemActionListener actionListener) {
        this.artistsItemActionListener = actionListener;
    }



    @Override
    public void onDailyMixPlayAction(final DailyMix dailyMix, final int position, final List<DailyMix> dailyMixes) {
//        tvManager.getSongsOfDailymix(dailyMix.getId(), new APIListener<List<Song>>() {
//            @Override
//            public void onSuccess(List<Song> songs, List<Object> params) {
//                updateDatar(position, dailyMix);
//
//                if (songs.size() == 0) {
//                    Utils.Dialog.showOKDialog(getActivity(), "No Songs Available");
//                } else {
//                    AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
//                    hhh.dailymixSongs(songs);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                updateDatar(position, dailyMix);
//                Utils.Error.onServiceCallFail(getActivity(), t);
//            }
//        });
//    }
//
//    private void updateDatar(int position, DailyMix dailyMix) {
//        DailyMix userData = new DailyMix();
//
//        userData.setName(dailyMix.getName());
//        userData.setId(dailyMix.getId());
//        userData.setOpened(false);
//        userData.setDate(dailyMix.getDate());
//        userData.setIndex(dailyMix.getIndex());
//
//        dailyMixAdapter.UpdateData(position, userData);
    }

    private void subscriptionDialog() {
        LayoutInflater myLayout = LayoutInflater.from(getActivity());
        final View dialogView = myLayout.inflate(R.layout.alert_dialog_subscription, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


        RelativeLayout createTextView = (RelativeLayout) alertDialog.findViewById(R.id.subscribe_now_layout);
        TextView cancelTextView = (TextView) alertDialog.findViewById(R.id.go_back_textview);


        createTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intentPackages = new Intent(getActivity(), AudioPaymentActivity.class);
                startActivity(intentPackages);


            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                alertDialog.dismiss();


            }
        });
    }

    private void checkSubscription(){
        subscriptionsManager.generateSubscriptionToken((int) Utils.App.getConfig(getActivity().getApplication()).getPaidPackageId(), new APIListener<PackageToken>() {
            @Override
            public void onSuccess(final PackageToken packageToken, List<Object> params) {

                subscriptionsManager.getActivatedPackage(new APIListener<Package>() {
                    @Override
                    public void onSuccess(Package thePackage, List<Object> params) {
                        binding.aviProgress.setVisibility(View.GONE);
                        System.out.println("Check sub status1 " + thePackage.getId());


                        if (thePackage.getId() == 46 || thePackage.getId() == 81 || thePackage.getId() == 101 || thePackage.getId() == 106) {


                        } else {
                            try {
                                subscriptionDialog();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

//                        if (thePackage.getId() !=1){
//                            subscriptionDialog();
//
//                        }



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
    public void onArtistsPlayAction(Artist artist, int position, List<Artist> artistList) {
        Bundle bundle=new Bundle();
        bundle.putInt("artistID", artist.getId());
        bundle.putString("artistName", artist.getName());
        bundle.putString("artistImage", artist.getImage());
        bundle.putString("songCount", artist.getSongsCount());

        ArtistDetailFragment artistDetailFragment = new ArtistDetailFragment();
        artistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_container_artist_detail_playlist, artistDetailFragment, "Home to ArtistDetail")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPlaylistPlayAction(PlayList playList, int position, List<PlayList> playLists) {

        Bundle bundle=new Bundle();
        bundle.putInt("playlistID", playList.getId());
        bundle.putString("playlistName", playList.getName());
        bundle.putString("songCount", playList.getSongCount());
        bundle.putString("playlistImage", playList.getImage());
        bundle.putString("playlistYear", playList.getDate());

        PlaylistDetailFragment playlistDetailFragment = new PlaylistDetailFragment();
        playlistDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_container_home_playlist_detail, playlistDetailFragment, "Home to PlaylistDetail")
                .addToBackStack(null)
                .commit();

    }
    @Subscribe
    public void onSearchNavigateEvent(SearchNavigationEvent event) {

        String searchKeyWord = event.getmSearchKey();
        String searchType = event.getMtype();

        if (searchType.equals("seeAllSongs")) {

            Bundle bundle = new Bundle();
            bundle.putString("searchKey", searchKeyWord);
            System.out.println("ksjhbsdjhkb " + searchKeyWord);

            SearchedSongsFragment searchedSongsFragment = new SearchedSongsFragment();
            searchedSongsFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container_search_toSeeAllSongs, searchedSongsFragment, "Search to ViewAllSongs")
                    .addToBackStack(null)
                    .commit();
        }
        if (searchType.equals("seeAllArtists")) {

            Bundle bundle=new Bundle();
            bundle.putString("searchKey", searchKeyWord);

            SearchedArtistFragment searchedArtistFragment = new SearchedArtistFragment();
            searchedArtistFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container_search_toSeeAllArtist, searchedArtistFragment, "Search to ViewAllArtists")
                    .addToBackStack(null)
                    .commit();
        }
        if (searchType.equals("seeAllPlaylists")) {

            Bundle bundle=new Bundle();
            bundle.putString("searchKey", searchKeyWord);
            System.out.println("sdjkbfsdjhbv " + searchKeyWord);
            SearchedPlaylistFragment searchedPlaylistFragment = new SearchedPlaylistFragment();
            searchedPlaylistFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_container_search_toSeeAllPlaylist, searchedPlaylistFragment, "Search to ViewAllPlaylists")
                    .addToBackStack(null)
                    .commit();
        }


    }
    }
