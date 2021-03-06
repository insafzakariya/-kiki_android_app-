/**
 * Created by Chatura Dilan Perera on 27/8/2016.
 */
package lk.mobilevisions.kiki.app;

import javax.inject.Singleton;

import dagger.Component;

import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.activity.AudioNotificationActivity;
import lk.mobilevisions.kiki.audio.activity.AudioPackagesActivity;
import lk.mobilevisions.kiki.audio.activity.AudioPaymentActivity;
import lk.mobilevisions.kiki.audio.activity.AudioPlayerActivity;
import lk.mobilevisions.kiki.audio.activity.AudioProfileActivity;
import lk.mobilevisions.kiki.audio.fragment.ArtistDetailFragment;
import lk.mobilevisions.kiki.audio.fragment.ArtistListFragment;
import lk.mobilevisions.kiki.audio.fragment.ArtistSongsListFragment;
import lk.mobilevisions.kiki.audio.fragment.AudioHomeFragment;
import lk.mobilevisions.kiki.audio.fragment.AudioSongsFragment;
import lk.mobilevisions.kiki.audio.fragment.BrowseAllSongsFrangment;
import lk.mobilevisions.kiki.audio.fragment.ChannelFragment;
import lk.mobilevisions.kiki.audio.fragment.ComingSoonFragment;
import lk.mobilevisions.kiki.audio.fragment.GenreArtistListFragment;
import lk.mobilevisions.kiki.audio.fragment.GenreSongsFragment;
import lk.mobilevisions.kiki.audio.fragment.GenreWiseArtistListFragment;
import lk.mobilevisions.kiki.audio.fragment.GenreWisePlaylistfragment;
import lk.mobilevisions.kiki.audio.fragment.GenreWiseSongsFragment;
import lk.mobilevisions.kiki.audio.fragment.LatestPlaylistFragment;
import lk.mobilevisions.kiki.audio.fragment.LatestSongsFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryArtistDetailFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryArtistListFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryArtistSongsList;
import lk.mobilevisions.kiki.audio.fragment.LibraryFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryHomeArtistDetailFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryHomeArtistListFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryHomeArtistSongsFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryHomePlaylistDetailFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryHomePlaylistListFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryHomeSongListFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryKikiPLaylistListFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryKikiPlaylistDetailFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryPlaylistDetailFragment;
import lk.mobilevisions.kiki.audio.fragment.LibraryPlaylistListFragment;
import lk.mobilevisions.kiki.audio.fragment.LibrarySongListFragment;
import lk.mobilevisions.kiki.audio.fragment.LibrarySongSelectionFragment;
import lk.mobilevisions.kiki.audio.fragment.PlaylistCreationFragment;
import lk.mobilevisions.kiki.audio.fragment.PlaylistDetailFragment;
import lk.mobilevisions.kiki.audio.fragment.PlaylistFragment;
import lk.mobilevisions.kiki.audio.fragment.ProgrammesFragment;
import lk.mobilevisions.kiki.audio.fragment.RecentlyPlayedFragment;
import lk.mobilevisions.kiki.audio.fragment.SeeAllGenreFragment;
import lk.mobilevisions.kiki.audio.fragment.VideosFragment;
import lk.mobilevisions.kiki.audio.fragment.YouMightAlsoLikeFragment;
import lk.mobilevisions.kiki.modules.analytics.AnalyticsModule;
import lk.mobilevisions.kiki.modules.auth.AuthModule;
import lk.mobilevisions.kiki.modules.info.InfoModule;
import lk.mobilevisions.kiki.modules.notifications.NotificationModule;
import lk.mobilevisions.kiki.modules.subscriptions.SubscriptionsModule;
import lk.mobilevisions.kiki.modules.tv.TvModule;

import lk.mobilevisions.kiki.ui.auth.EditMobileNumberActivity;
import lk.mobilevisions.kiki.ui.auth.LoginActivity;
import lk.mobilevisions.kiki.ui.auth.UserFreeTrailFragment;
import lk.mobilevisions.kiki.ui.channels.ChannelsActivity;
import lk.mobilevisions.kiki.ui.episodes.EpisodesActivity;
import lk.mobilevisions.kiki.ui.main.MainActivity;
import lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenActivity;
import lk.mobilevisions.kiki.ui.main.fullscreen.FullScreenEpisodesActivity;
import lk.mobilevisions.kiki.ui.main.home.HomeFragment;
import lk.mobilevisions.kiki.ui.main.videos.HotVideosFragment;
import lk.mobilevisions.kiki.ui.main.videos.LiveVideosFragment;
import lk.mobilevisions.kiki.ui.main.videos.SubscribedVideosFragment;
import lk.mobilevisions.kiki.ui.main.videos.ViewVideosFragment;
import lk.mobilevisions.kiki.ui.navigation.UpdateUserActivity;
import lk.mobilevisions.kiki.ui.notifications.NotificationsActivity;
import lk.mobilevisions.kiki.ui.packages.MyPackagesActivity;
import lk.mobilevisions.kiki.ui.packages.MySubscribePackagesActivity;
import lk.mobilevisions.kiki.ui.packages.PaymentActivity;
import lk.mobilevisions.kiki.ui.splash.SplashActivity;
import lk.mobilevisions.kiki.ui.subscribed.AllSubscribedVideosFragment;
import lk.mobilevisions.kiki.ui.subscribed.SubscriptionActivity;
import lk.mobilevisions.kiki.video.activity.VideoDashboardActivity;
import lk.mobilevisions.kiki.video.activity.VideoEpisodeActivity;
import lk.mobilevisions.kiki.video.activity.VideoProfileActivity;
import lk.mobilevisions.kiki.video.fragment.VideoHomeFragment;
import lk.mobilevisions.kiki.video.fragment.VideoMylistFragment;


@Singleton
@Component(modules = {AuthModule.class, TvModule.class, SubscriptionsModule.class,
        NotificationModule.class, AnalyticsModule.class, InfoModule.class})
public interface Injector {
    void inject(LoginActivity activity);
    void inject(ChannelsActivity activity);
    void inject(VideoDashboardActivity activity);
    void inject(SplashActivity activity);
    void inject(MainActivity activity);
    void inject(SubscriptionActivity activity);
    void inject(MyPackagesActivity activity);
    void inject(MySubscribePackagesActivity activity);
    void inject(AudioPackagesActivity activity);
    void inject(UpdateUserActivity activity);
    void inject(FullScreenActivity activity);
    void inject(EpisodesActivity activity);
    void inject(NotificationsActivity activity);
    void inject(AudioNotificationActivity activity);
    void inject(HomeFragment fragment);
    void inject(VideosFragment fragment);
    void inject(HotVideosFragment fragment);
    void inject(LiveVideosFragment fragment);
    void inject(SubscribedVideosFragment fragment);
    void inject(AllSubscribedVideosFragment activity);
    void inject(ViewVideosFragment fragment);
    void inject(ChannelFragment fragment);
    void inject(ProgrammesFragment fragment);
    void inject(RecentlyPlayedFragment fragment);
    void inject(YouMightAlsoLikeFragment fragment);
    void inject(LatestSongsFragment fragment);
    void inject(ComingSoonFragment fragment);
    void inject(AudioHomeFragment fragment);
    void inject(LatestPlaylistFragment fragment);
    void inject(AudioSongsFragment fragment);
    void inject(BrowseAllSongsFrangment fragment);
    void inject(GenreSongsFragment fragment);
    void inject(ArtistDetailFragment fragment);
    void inject(ArtistListFragment fragment);
    void inject(LibraryFragment fragment);
    void inject(ArtistSongsListFragment fragment);
    void inject(PlaylistDetailFragment fragment);
    void inject(GenreArtistListFragment fragment);
    void inject(GenreWiseArtistListFragment fragment);
    void inject(GenreWiseSongsFragment fragment);
    void inject(SeeAllGenreFragment fragment);
    void inject(LibraryHomeSongListFragment fragment);
    void inject(LibraryHomePlaylistListFragment fragment);
    void inject(LibraryHomeArtistListFragment fragment);
    void inject(LibraryHomePlaylistDetailFragment fragment);
    void inject(LibraryKikiPlaylistDetailFragment fragment);
    void inject(LibraryKikiPLaylistListFragment fragment);
    void inject(LibraryHomeArtistDetailFragment fragment);
    void inject(LibraryHomeArtistSongsFragment fragment);
    void inject(GenreWisePlaylistfragment fragment);
    void inject(LibrarySongSelectionFragment fragment);
    void inject(LibrarySongListFragment fragment);
    void inject(LibraryPlaylistListFragment fragment);
    void inject(LibraryArtistListFragment fragment);
    void inject(PlaylistCreationFragment fragment);
    void inject(LibraryPlaylistDetailFragment fragment);
    void inject(LibraryArtistDetailFragment fragment);
    void inject(LibraryArtistSongsList fragment);
    void inject(AudioPlayerActivity activity);
    void inject(AudioDashboardActivity activity);
    void inject(FullScreenEpisodesActivity activity);
    void inject(PlaylistFragment fragment);
    void inject(UserFreeTrailFragment fragment);
    void inject(VideoHomeFragment fragment);
    void inject(VideoMylistFragment fragment);
    void inject(VideoProfileActivity activity);
    void inject(AudioProfileActivity activity);
    void inject(EditMobileNumberActivity activity);
    void inject(VideoEpisodeActivity activity);
    void inject(PaymentActivity activity);
    void inject(AudioPaymentActivity activity);


}