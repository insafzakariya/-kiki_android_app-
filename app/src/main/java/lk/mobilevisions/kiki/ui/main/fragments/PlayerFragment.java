package lk.mobilevisions.kiki.ui.main.fragments;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.CaptioningManager;
import android.webkit.URLUtil;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.metadata.id3.ApicFrame;
import com.google.android.exoplayer.metadata.id3.GeobFrame;
import com.google.android.exoplayer.metadata.id3.Id3Frame;
import com.google.android.exoplayer.metadata.id3.PrivFrame;
import com.google.android.exoplayer.metadata.id3.TextInformationFrame;
import com.google.android.exoplayer.metadata.id3.TxxxFrame;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.SubtitleLayout;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.Util;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Settings;
import lk.mobilevisions.kiki.app.Utils;

import lk.mobilevisions.kiki.databinding.FragmentPlayerBinding;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.api.dto.Content;
import lk.mobilevisions.kiki.modules.auth.AuthOptions;
import lk.mobilevisions.kiki.modules.player.EventLogger;
import lk.mobilevisions.kiki.modules.player.KeyCompatibleMediaController;
import lk.mobilevisions.kiki.modules.player.MediaControllerListener;
import lk.mobilevisions.kiki.modules.player.MediaControllerView;
import lk.mobilevisions.kiki.modules.player.PlayerBackNavigationListner;
import lk.mobilevisions.kiki.modules.player.PlayerTitleView;
import lk.mobilevisions.kiki.modules.player.exo.ExoPlayer;
import lk.mobilevisions.kiki.modules.tv.PlayerListener;
import lk.mobilevisions.kiki.ui.packages.PaymentActivity;
import timber.log.Timber;

import static lk.mobilevisions.kiki.app.Application.PLAYER;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment implements SurfaceHolder.Callback, View.OnClickListener,
        ExoPlayer.Listener, ExoPlayer.CaptionListener, ExoPlayer.Id3MetadataListener,
        AudioCapabilitiesReceiver.Listener, MediaControllerListener,PlayerBackNavigationListner {

    private static final int MENU_GROUP_TRACKS = 1;
    private static final int ID_OFFSET = 2;
    private static final int MEDIA_CONTROLLER_REMOVE_DELAY = 5000;
    private static final CookieManager defaultCookieManager;
    private static ExoPlayer player;

    static {
        defaultCookieManager = new CookieManager();
        defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }


    PopupMenu popupSubtitles;
    PopupMenu popupResolutions;
    Handler mediaControllerRemoveHandler;
    Runnable mediaControllerRemoveThread;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private DebugTextViewHelper debugViewHelper;
    private boolean playerNeedsPrepare;
    private MediaControllerView mediaController;
    private PlayerTitleView playerTitleView;
    private EventLogger eventLogger;
    private boolean enableBackgroundAudio;
    private boolean enableFullScreen;
    private boolean isPausedForAds = false;
    private boolean isTopRightAdVisible = false;
    private boolean isTopLeftAdVisible = false;
    private boolean isBottomAdVisible = false;
    private PlayerListener playerListener;
    private MediaControllerListener mediaControllerListener;
    private PlayerBackNavigationListner playerBackNavigationListner;
    private int videoResolution = 0;
    private Handler adHandler;
    private Runnable adThread;
    private List<Content> adContents;


    FragmentPlayerBinding binding;


    private BroadcastReceiver phoneStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PlayerFragment.this.onPhoneStateChange(intent);
        }
    };

    public PlayerFragment() {
        // Required empty public constructor
    }

    private static String buildTrackName(MediaFormat format) {

        if (format.adaptive) {
            return "auto";
        }
        String trackName;
        if (MimeTypes.isVideo(format.mimeType)) {
            trackName = buildResolutionString(format);
        } else if (MimeTypes.isAudio(format.mimeType)) {
            trackName = joinWithSeparator(joinWithSeparator(joinWithSeparator(buildLanguageString(format),
                    buildAudioPropertyString(format)), buildBitrateString(format)),
                    buildTrackIdString(format));
        } else {
            trackName = joinWithSeparator(joinWithSeparator(buildLanguageString(format),
                    buildBitrateString(format)), buildTrackIdString(format));
        }
        return trackName.length() == 0 ? "On" : trackName;
    }

    private static String joinWithSeparator(String first, String second) {
        return first.length() == 0 ? second : (second.length() == 0 ? first : first + ", " + second);
    }

    private static String buildTrackIdString(MediaFormat format) {
        return format.trackId == null ? "" : " (" + format.trackId + ")";
    }

    private static String buildResolutionString(MediaFormat format) {
        return format.width == MediaFormat.NO_VALUE || format.height == MediaFormat.NO_VALUE
                // ? "" : format.width + " x " + format.height;
                ? "" : format.height + "p";
    }

    private static String buildAudioPropertyString(MediaFormat format) {
        return format.channelCount == MediaFormat.NO_VALUE || format.sampleRate == MediaFormat.NO_VALUE
                ? "" : format.channelCount + "ch, " + format.sampleRate + "Hz";
    }

    private static String buildLanguageString(MediaFormat format) {
        return TextUtils.isEmpty(format.language) || "und".equals(format.language) ? ""
                : format.language;
    }

    private static String buildBitrateString(MediaFormat format) {
        return format.bitrate == MediaFormat.NO_VALUE ? ""
                : String.format(Locale.US, "%.2fMbit", format.bitrate / 1000000f);
    }

    public void setMediaControllerListener(MediaControllerListener listener) {
        mediaControllerListener = listener;
    }
    public void setNavigationBackListener(PlayerBackNavigationListner listener) {
        playerBackNavigationListner = listener;
    }

    public void setPlayer(ExoPlayer exoPlayer) {
        player = exoPlayer;
    }

    public void doPauseResume() {
        mediaController.doPauseResume();
    }

    public void doPause() {
        mediaController.doPause();
        if (player != null) {
            PLAYER.setReleasedVideoPosition(player.getCurrentPosition());
        }
    }

    public void doStart() {
        mediaController.doStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (Utils.App.getConfig((Application) getActivity().getApplication()).isScreenCaptureDisabled()
                && Settings.DISABLE_SCREEN_CAPTURING) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false);
        CookieHandler currentHandler = CookieHandler.getDefault();
        if (currentHandler != defaultCookieManager) {
            CookieHandler.setDefault(defaultCookieManager);
        }

        mediaController = new KeyCompatibleMediaController(getActivity(), mediaControllerListener, this);
        if (enableFullScreen) {
            mediaController.setEnableFullScreen();
        }

        mediaController.setAnchorView((ViewGroup) binding.mediaControllerFullScreen);
        playerTitleView = new PlayerTitleView(getActivity(),playerBackNavigationListner);
        playerTitleView.setAnchorView((ViewGroup) binding.playerTitleView);
        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getActivity(), this);
        audioCapabilitiesReceiver.register();

        binding.getRoot().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });
        binding.getRoot().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE
                        || keyCode == KeyEvent.KEYCODE_MENU) {
                    return false;
                }
                return mediaController.dispatchKeyEvent(event) && playerTitleView.dispatchKeyEvent(event);
            }
        });

        binding.layoutProgressVideo.setAlpha(0.5f);

        videoResolution = (int) Utils.App.getConfig((Application) getActivity().getApplicationContext())
                .getDefaultVideoResolution();


        //Setting language of the user
        AuthUser authUser = ((Application) getActivity().getApplicationContext()).getAuthUser();
        if (authUser != null) {
            if (AuthOptions.Language.SINHALA.toString().equals(authUser.getLanguage().toUpperCase())) {
                Utils.App.getConfig((Application) getActivity().getApplicationContext())
                        .setDefaultSubtitle(1);
            } else if (AuthOptions.Language.TAMIL.toString().equals(authUser.getLanguage().toUpperCase())) {
                Utils.App.getConfig((Application) getActivity().getApplicationContext())
                        .setDefaultSubtitle(2);
            } else if (AuthOptions.Language.ENGLISH.toString().equals(authUser.getLanguage().toUpperCase())) {
                Utils.App.getConfig((Application) getActivity().getApplicationContext())
                        .setDefaultSubtitle(0);
            }
        }

        ((Application) getContext().getApplicationContext()).PLAYER.setSubtitles((int) Utils.App.getConfig((Application) getActivity().getApplicationContext())
                .getDefaultSubtitle());

        return binding.getRoot();


    }


    public void loadAdvertisement(final List<Content> adContents) {
        this.adContents = adContents;

        adHandler = new Handler();

        long delayCount = 0;

        adThread = new Runnable() {
            public void run() {
                if (adContents != null && adContents.size() > 0) {
                    for (final Content content : adContents) {
                        long playerPosition = getPlayerPosition() / 1000;
                        if ((playerPosition) > content.getStartTime()
                                && (playerPosition) < (content.getStartTime() + content.getDuration())
                                && binding.layoutProgressVideo.getVisibility() == View.GONE) {
                            try {
                                if ("full_screen".equals(content.getPosition())) {
                                    if (!isPausedForAds) {
                                        doPause();
                                        isPausedForAds = true;
                                        binding.imageAdvertisementFullScreenView.setVisibility(View.VISIBLE);
                                        if ((content.getWebURL().startsWith("app://") || URLUtil.isValidUrl(content.getWebURL()))) {
                                            binding.imageAdvertisementFullScreenView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intentPackages = new Intent(getActivity(), PaymentActivity.class);
                                                    startActivity(intentPackages);
                                                    getActivity().finish();
                                                }
                                            });
                                        }
                                        Picasso.with(getActivity()).load(URLDecoder.decode(content.getImageURL(), "UTF-8"))
                                                .into(binding.imageAdvertisementFullScreenView);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (isPausedForAds && !content.isStopMainContent()) {
                                                    doStart();
                                                    binding.imageAdvertisementFullScreenView.setVisibility(View.GONE);
                                                }
                                            }
                                        }, content.getDuration() * 1000);
                                    }


                                } else if ("top_right".equals(content.getPosition())) {
                                    binding.imageAdvertisementLeftScreenView.setVisibility(View.VISIBLE);
                                    Picasso.with(getActivity()).load(URLDecoder.decode(content.getImageURL(), "UTF-8"))
                                            .into(binding.imageAdvertisementLeftScreenView);

                                    if (!isTopRightAdVisible && (content.getWebURL().startsWith("app://") || URLUtil.isValidUrl(content.getWebURL()))) {
                                        binding.imageAdvertisementLeftScreenView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intentPackages = new Intent(getActivity(), PaymentActivity.class);
                                                startActivity(intentPackages);
                                                getActivity().finish();
                                            }
                                        });
                                        isTopLeftAdVisible = true;
                                    }

                                } else if ("top_left".equals(content.getPosition())) {
                                    binding.imageAdvertisementRightScreenView.setVisibility(View.VISIBLE);
                                    Picasso.with(getActivity()).load(URLDecoder.decode(content.getImageURL(), "UTF-8"))
                                            .into(binding.imageAdvertisementRightScreenView);

                                    if (!isTopLeftAdVisible && (content.getWebURL().startsWith("app://") || URLUtil.isValidUrl(content.getWebURL()))) {
                                        binding.imageAdvertisementRightScreenView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intentPackages = new Intent(getActivity(), PaymentActivity.class);
                                                startActivity(intentPackages);
                                                getActivity().finish();
                                            }
                                        });
                                        isTopLeftAdVisible = true;
                                    }

                                } else if ("scroll".equals(content.getPosition())) {
                                    binding.imageAdvertisementBottomScreenView.setVisibility(View.VISIBLE);
                                    Picasso.with(getActivity()).load(URLDecoder.decode(content.getImageURL(), "UTF-8"))
                                            .into(binding.imageAdvertisementBottomScreenView);

                                    if (!isBottomAdVisible && (content.getWebURL().startsWith("app://") || URLUtil.isValidUrl(content.getWebURL()))) {
                                        binding.imageAdvertisementBottomScreenView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intentPackages = new Intent(getActivity(), PaymentActivity.class);
                                                startActivity(intentPackages);
                                                getActivity().finish();
                                            }
                                        });
                                        isBottomAdVisible = true;
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if ("full_screen".equals(content.getPosition())) {
                                if ((playerPosition) < content.getStartTime() || (playerPosition) > (content.getStartTime() + content.getDuration())) {
                                    binding.imageAdvertisementFullScreenView.setVisibility(View.GONE);
                                    if (isPausedForAds && !content.isStopMainContent()) {
                                        doStart();
                                    }
                                    isPausedForAds = false;
                                }
                            } else if ("top_right".equals(content.getPosition())) {
                                binding.imageAdvertisementLeftScreenView.setVisibility(View.GONE);
                                isTopLeftAdVisible = false;
                            } else if ("top_left".equals(content.getPosition())) {
                                binding.imageAdvertisementRightScreenView.setVisibility(View.GONE);
                                isTopRightAdVisible = false;
                            } else if ("scroll".equals(content.getPosition())) {
                                binding.imageAdvertisementBottomScreenView.setVisibility(View.GONE);
                                isBottomAdVisible = false;
                            }
                        }
                    }

                }

                adHandler.postDelayed(this, 1000);
            }
        };
        adHandler.postDelayed(adThread, 0);
    }

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    public void setEnableFullScreen() {
        enableFullScreen = true;
    }

    public void startVideo() {
        onHidden();
        onShown();
        hideMediaController();
    }

    public String getCurrentVideoStreamingPosition(){
        return mediaController.getCurrentVideoStreamingPosition();
    }

    private void hideMediaController(){

        if(popupSubtitles!=null){
            popupSubtitles.dismiss();
        }

        if(popupResolutions!=null) {
            popupResolutions.dismiss();
        }
        mediaController.hide();
        playerTitleView.hide();
    }
    public void setNoProgramsAvailable(boolean isNoProgramsAvailable) {
        if (binding.imageViewNoPrograms != null) {
            if (isNoProgramsAvailable) {
                binding.imageViewNoPrograms.setVisibility(View.VISIBLE);
            } else {
                binding.imageViewNoPrograms.setVisibility(View.GONE);
            }
        }
    }

    public long getPlayerPosition() {
        if (player != null) {
            return player.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }

    @Override
    public void onCues(List<Cue> cues) {
        binding.subtitles.setCues(cues);
    }

    @Override
    public void onId3Metadata(List<Id3Frame> id3Frames) {
        for (Id3Frame id3Frame : id3Frames) {
            if (id3Frame instanceof TxxxFrame) {
                TxxxFrame txxxFrame = (TxxxFrame) id3Frame;
                Timber.i("ID3 TimedMetadata %s: description=%s, value=%s", txxxFrame.id,
                        txxxFrame.description, txxxFrame.value);
            } else if (id3Frame instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) id3Frame;
                Timber.i("ID3 TimedMetadata %s: owner=%s", privFrame.id, privFrame.owner);
            } else if (id3Frame instanceof GeobFrame) {
                GeobFrame geobFrame = (GeobFrame) id3Frame;
                Timber.i("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
                        geobFrame.id, geobFrame.mimeType, geobFrame.filename, geobFrame.description);
            } else if (id3Frame instanceof ApicFrame) {
                ApicFrame apicFrame = (ApicFrame) id3Frame;
                Timber.i("ID3 TimedMetadata %s: mimeType=%s, description=%s",
                        apicFrame.id, apicFrame.mimeType, apicFrame.description);
            } else if (id3Frame instanceof TextInformationFrame) {
                TextInformationFrame textInformationFrame = (TextInformationFrame) id3Frame;
                Timber.i("ID3 TimedMetadata %s: description=%s", textInformationFrame.id,
                        textInformationFrame.description);
            } else {
                Timber.i("ID3 TimedMetadata %s", id3Frame.id);
            }
        }
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == com.google.android.exoplayer.ExoPlayer.STATE_ENDED) {
            showControls();
        }
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        if (!playWhenReady) {
            showProgressDialog();
        }
        switch (playbackState) {
            case com.google.android.exoplayer.ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                showProgressDialog();
                break;
            case com.google.android.exoplayer.ExoPlayer.STATE_ENDED:
                text += "ended";
                playerListener.onVideoEnded();
                hideMediaController();
                break;
            case com.google.android.exoplayer.ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case com.google.android.exoplayer.ExoPlayer.STATE_PREPARING:
                showProgressDialog();
                text += "preparing";
                break;
            case com.google.android.exoplayer.ExoPlayer.STATE_READY:
                text += "ready";
                dismissProgressDialog();
                break;
            default:
                text += "unknown";
                break;
        }
        Timber.d("Player State logs %s", text);
        updateButtonVisibilities();
    }

    @Override
    public void onError(Exception e) {

        String errorString = null;
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            errorString = getString(Util.SDK_INT < 18 ? R.string.error_drm_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown);
        } else if (e instanceof ExoPlaybackException
                && e.getCause() instanceof MediaCodecTrackRenderer.DecoderInitializationException) {
            // Special case for decoder initialization failures.
            MediaCodecTrackRenderer.DecoderInitializationException decoderInitializationException =
                    (MediaCodecTrackRenderer.DecoderInitializationException) e.getCause();
            if (decoderInitializationException.decoderName == null) {
                if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                    errorString = getString(R.string.error_querying_decoders);
                } else if (decoderInitializationException.secureDecoderRequired) {
                    errorString = getString(R.string.error_no_secure_decoder) +
                            decoderInitializationException.mimeType;
                } else {
                    errorString = getString(R.string.error_no_decoder) +
                            decoderInitializationException.mimeType;
                }
            } else {
                errorString = getString(R.string.error_instantiating_decoder) +
                        decoderInitializationException.decoderName;
            }
        }
        if (errorString != null) {
            Toast.makeText(getActivity().getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
        }
        playerNeedsPrepare = true;
        //updateButtonVisibilities();
        showControls();
    }

    private void configurePopupWithTracks(final PopupMenu popup,
                                          final PopupMenu.OnMenuItemClickListener customActionClickListener,
                                          final int trackType) {
        if (player == null) {
            return;
        }
        int trackCount = player.getTrackCount(trackType);
        if (trackCount == 0) {
            return;
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                hideMediaController();
                return (customActionClickListener != null
                        && customActionClickListener.onMenuItemClick(item))
                        || onTrackItemClick(item, trackType);
            }
        });
        Menu menu = popup.getMenu();
        // ID_OFFSET ensures we avoid clashing with Menu.NONE (which equals 0).
        if (trackType == ExoPlayer.TYPE_TEXT) {
            menu.add(MENU_GROUP_TRACKS, ExoPlayer.TRACK_DISABLED + ID_OFFSET, Menu.NONE, R.string.off);
        }

        if (player != null && player.getFormat() != null && player.getFormat().height >= 0) {
            if (mediaController != null) {
                mediaController.setResolution(player.getFormat().height + "p");
            }
        }

        for (int i = 0; i < trackCount; i++) {
            menu.add(MENU_GROUP_TRACKS, i + ID_OFFSET, Menu.NONE,
                    buildTrackName(player.getTrackFormat(trackType, i)));
        }
        menu.setGroupCheckable(MENU_GROUP_TRACKS, true, true);
        menu.findItem(player.getSelectedTrack(trackType) + ID_OFFSET).setChecked(true);

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        binding.viewShutter.setVisibility(View.GONE);
        dismissProgressDialog();
        binding.videoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthHeightRatio) / height);
        if (mediaController != null && player != null && player.getFormat() != null) {
            mediaController.setResolution(player.getFormat().height + "p");
        }
    }


    public void updatePlayPause(){
        if(mediaController!=null)
        mediaController.updatePausePlay();
    }
    // OnClickListener methods

    private void dismissProgressDialog() {
        binding.layoutProgressVideo.animate()
                .alpha(0.0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.layoutProgressVideo.setVisibility(View.GONE);
                        binding.layoutProgressVideo.setAlpha(0.9f);
                    }
                });
    }

    private void showProgressDialog() {
        binding.layoutProgressVideo.setAlpha(0.9f);
        binding.layoutProgressVideo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        boolean backgrounded = player.getBackgrounded();
        boolean playWhenReady = player.getPlayWhenReady();
        releasePlayer();
        preparePlayer(playWhenReady);
        player.setBackgrounded(backgrounded);
    }

    private void onShown() {
        configureSubtitleView();
        if (player == null) {
            if (!maybeRequestPermission()) {
                preparePlayer(true);
            }
        } else {
            player.setBackgrounded(false);
        }

        if (Application.PLAYER.isPlayerPaused()) {
            doPause();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(phoneStateReceiver);
        if (Util.SDK_INT <= 23) {
            onHidden();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            onHidden();
        }
        if (adHandler != null) {
            adHandler.removeCallbacks(adThread);
        }
        isTopRightAdVisible = false;
        isTopLeftAdVisible = false;
        isBottomAdVisible = false;
    }

    private void onHidden() {
        if (!enableBackgroundAudio) {
            releasePlayer();
        } else {
            player.setBackgrounded(true);
        }
        if (binding.viewShutter != null) {
            binding.viewShutter.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    @Override
    public void onClick(View view) {
        if (view == binding.retryButton) {
            preparePlayer(true);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            if (debugViewHelper != null) {
                debugViewHelper.stop();
            }
            debugViewHelper = null;
            PLAYER.setReleasedVideoPosition(player.getCurrentPosition());
            player.release();
            player = null;
            if (eventLogger != null) {
                eventLogger.endSession();
            }
            eventLogger = null;
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        if (player == null) {
            player = PLAYER.getNewExoPlayer();
            player.addListener(this);
            player.setCaptionListener(this);
            player.setMetadataListener(this);
            player.seekTo(PLAYER.getCurrentVideoPosition());
            playerNeedsPrepare = true;
            mediaController.setMediaPlayer(player.getPlayerControl());
            mediaController.setEnabled(true);
            eventLogger = new EventLogger();
            eventLogger.startSession();
            player.addListener(eventLogger);
            player.setInfoListener(eventLogger);
            player.setInternalErrorListener(eventLogger);
            debugViewHelper = new DebugTextViewHelper(player, binding.textViewDebug);
            debugViewHelper.start();
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
            updateButtonVisibilities();
        }
        player.setSurface(binding.surfaceViewVideo.getHolder().getSurface());
        player.setPlayWhenReady(playWhenReady);
        showControls();
    }

    private void configureSubtitleView() {
        CaptionStyleCompat style;
        float fontScale;
        if (Util.SDK_INT >= 19) {
            // style = getUserCaptionStyleV19();
            style = new CaptionStyleCompat(Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_OUTLINE, Color.BLACK, Typeface.DEFAULT);

            fontScale = getUserCaptionFontScaleV19();
        } else {
            style = CaptionStyleCompat.DEFAULT;
            fontScale = 1.0f;
        }
        binding.subtitles.setStyle(style);
        binding.subtitles.setApplyEmbeddedStyles(true);
        binding.subtitles.setFractionalTextSize(SubtitleLayout.DEFAULT_TEXT_SIZE_FRACTION * fontScale);

    }

    @TargetApi(19)
    private float getUserCaptionFontScaleV19() {
        if (getActivity() != null) {
            CaptioningManager captioningManager =
                    (CaptioningManager) getActivity().getSystemService(Context.CAPTIONING_SERVICE);
            if (captioningManager != null) {
                return captioningManager.getFontScale();
            }
        }
        return 0f;
    }

    @TargetApi(19)
    private CaptionStyleCompat getUserCaptionStyleV19() {
        if (getActivity() != null) {
            CaptioningManager captioningManager =
                    (CaptioningManager) getActivity().getSystemService(Context.CAPTIONING_SERVICE);
            return CaptionStyleCompat.createFromCaptionStyle(captioningManager.getUserStyle());
        }
        return null;
    }

    @TargetApi(23)
    private boolean maybeRequestPermission() {
        if (requiresPermission(PLAYER.getCurrentVideoContentURI())) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return true;
        } else {
            return false;
        }
    }

    @TargetApi(23)
    private boolean requiresPermission(Uri uri) {
        return Util.SDK_INT >= 23
                && Util.isLocalFileUri(uri)
                && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void toggleControlsVisibility() {
        if (mediaController.isShowing()) {
            hideMediaController();
            binding.textViewDebug.setVisibility(View.GONE);
            if (mediaControllerRemoveHandler != null) {
                mediaControllerRemoveHandler.removeCallbacks(mediaControllerRemoveThread);
            }
        } else {
            if (PLAYER.getCurrentVideoContentURI() != null) {
                showControls();
                mediaControllerRemoveHandler = new Handler();
                mediaControllerRemoveThread = new Runnable() {
                    @Override
                    public void run() {
                        hideMediaController();
                        binding.textViewDebug.setVisibility(View.GONE);
                    }
                };
                mediaControllerRemoveHandler.postDelayed(mediaControllerRemoveThread, MEDIA_CONTROLLER_REMOVE_DELAY);
            }
        }

    }

    public void showControls() {
        mediaController.show(0);
        playerTitleView.show(0);
        binding.textViewDebug.setVisibility(View.VISIBLE);
    }

    private void updateButtonVisibilities() {
        binding.retryButton.setVisibility(playerNeedsPrepare ? View.VISIBLE : View.GONE);
        binding.videoControls.setVisibility(haveTracks(ExoPlayer.TYPE_VIDEO) ? View.VISIBLE : View.GONE);
        binding.audioControls.setVisibility(haveTracks(ExoPlayer.TYPE_AUDIO) ? View.VISIBLE : View.GONE);
        binding.textControls.setVisibility(haveTracks(ExoPlayer.TYPE_TEXT) ? View.VISIBLE : View.GONE);
    }

    private boolean haveTracks(int type) {

        if (player != null && type == ExoPlayer.TYPE_TEXT && player.getTrackCount(type) > 0) {
            if (getActivity() != null) {
                popupSubtitles = new PopupMenu(getActivity(), new View(getContext()));
                configurePopupWithTracks(popupSubtitles, null, ExoPlayer.TYPE_TEXT);

                if (getContext() != null) {

                    if (getContext() != null) {
                        int subtitles = ((Application) getContext().getApplicationContext()).PLAYER.getSubtitles();
                        if (subtitles < player.getTrackCount(type)) {
                            onTrackItemClick(popupSubtitles.getMenu()
                                    .getItem(subtitles + 1), ExoPlayer.TYPE_TEXT);
                        } else {
                            onTrackItemClick(popupSubtitles.getMenu()
                                    .getItem(0), ExoPlayer.TYPE_TEXT);
                        }
                    }

                }

            }
        } else if (player != null && type == ExoPlayer.TYPE_VIDEO && player.getTrackCount(type) > 0) {
            if (getActivity() != null) {
                popupResolutions = new PopupMenu(getActivity(), new View(getContext()));
                configurePopupWithTracks(popupResolutions, null, ExoPlayer.TYPE_VIDEO);
                if (getContext() != null) {
                    int resolutionValue = ((Application) getContext().getApplicationContext()).PLAYER.getVideoResolution();
                    if (resolutionValue < player.getTrackCount(type)) {
                        onTrackItemClick(popupResolutions.getMenu()
                                .getItem(resolutionValue), ExoPlayer.TYPE_VIDEO);
                    } else {
                        resolutionValue = videoResolution;
                        ((Application) getContext().getApplicationContext()).PLAYER.setVideoResolution(videoResolution);
                        onTrackItemClick(popupResolutions.getMenu()
                                .getItem(resolutionValue), ExoPlayer.TYPE_VIDEO);
                    }
                }
            }

        }

        return player != null && player.getTrackCount(type) > 0;
    }

    @Override
    public void onMediaControllerFullScreenToggle(boolean isFullScreen) {

    }

    @Override
    public void onMoreOptionsButtonClick(View view) {

        popupResolutions = new PopupMenu(getActivity(), view);
        configurePopupWithTracks(popupResolutions, null, ExoPlayer.TYPE_VIDEO);
        popupResolutions.show();

    }

    @Override
    public void onLikeButtonClick(boolean hasLiked,boolean isLiked) {
        enableAndDisableMediaController();

    }



    @Override
    public void onShareButtonClick() {
        enableAndDisableMediaController();

    }



    @Override
    public void onPlayButtonClick() {
        enableAndDisableMediaController();

    }

    @Override
    public void onPauseButtonClick() {
        enableAndDisableMediaController();

    }

    @Override
    public void onMediaControllerClick() {
        enableAndDisableMediaController();

    }

    @Override
    public void onSubTitlesButtonClick(View view) {
        popupSubtitles = new PopupMenu(getActivity(), view);
        configurePopupWithTracks(popupSubtitles, null, ExoPlayer.TYPE_TEXT);
        popupSubtitles.show();


    }

    @Override
    public void onRotateToggle(boolean isRotationEnabled) {

    }

    public void enableAndDisableMediaController() {
        if (mediaControllerRemoveHandler != null && mediaController.isShowing()) {
            mediaControllerRemoveHandler.removeCallbacks(mediaControllerRemoveThread);
            if (mediaController.isShowing()) {
                mediaControllerRemoveThread = new Runnable() {
                    @Override
                    public void run() {
                        hideMediaController();
                        binding.textViewDebug.setVisibility(View.GONE);
                    }
                };
                mediaControllerRemoveHandler.postDelayed(mediaControllerRemoveThread, MEDIA_CONTROLLER_REMOVE_DELAY);
            }
        }
    }

    private boolean onTrackItemClick(MenuItem item, int type) {
        if (player == null || item.getGroupId() != MENU_GROUP_TRACKS) {
            return false;
        }
        player.setSelectedTrack(type, item.getItemId() - ID_OFFSET);

        int itemValue = (item.getItemId() - ID_OFFSET);

        if (type == ExoPlayer.TYPE_TEXT) {
            ((Application) getContext().getApplicationContext()).PLAYER.setSubtitles(itemValue);
        }
        if (type == ExoPlayer.TYPE_VIDEO) {
            ((Application) getContext().getApplicationContext()).PLAYER.setVideoResolution(itemValue);
        }

        return true;
    }

    public void setVideoLike(boolean isLike) {

    }

    public void setVideoTitle(String title) {
        playerTitleView.setVideoTitle(title);
    }

    public void onPhoneStateChange(Intent intent) {
        PlayerPhoneStateListener phoneListener = new PlayerPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        getActivity().registerReceiver(phoneStateReceiver, intentFilter);
    }

    @Override
    public void onBackButtonClick() {

    }

    public class PlayerPhoneStateListener extends PhoneStateListener {

        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Timber.d("Call State Idle");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Timber.d("Call State Offhook");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Timber.d("Call State Ringing");
                    if (PlayerFragment.this != null) {
                        PlayerFragment.this.doPause();
                        Application.PLAYER.setPlayerPaused(true);
                    }
                    break;
            }
        }

    }

}
