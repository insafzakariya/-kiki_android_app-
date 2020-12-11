/**
 * Created by Chatura Dilan Perera on 28/8/2016.
 */
package lk.mobilevisions.kiki.modules.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.mobilevisions.kiki.audio.model.PlaylistModel;
import lk.mobilevisions.kiki.audio.model.dto.Artist;
import lk.mobilevisions.kiki.audio.model.dto.AudioChannel;
import lk.mobilevisions.kiki.audio.model.dto.AudioProgram;
import lk.mobilevisions.kiki.audio.model.dto.DailyMix;
import lk.mobilevisions.kiki.audio.model.dto.Genre;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.SearchResponse;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.modules.api.dto.AuthUser;
import lk.mobilevisions.kiki.modules.api.dto.Channel;
import lk.mobilevisions.kiki.modules.api.dto.Content;
import lk.mobilevisions.kiki.modules.api.dto.Episode;
import lk.mobilevisions.kiki.modules.api.dto.ForgotPasswordUser;
import lk.mobilevisions.kiki.modules.api.dto.Notification;
import lk.mobilevisions.kiki.modules.api.dto.NotificationCountResponse;
import lk.mobilevisions.kiki.modules.api.dto.Package;
import lk.mobilevisions.kiki.modules.api.dto.PackageToken;
import lk.mobilevisions.kiki.modules.api.dto.PackageV2;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.api.dto.Slider;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_TOKEN_AUTHENTICATION = "Bearer-Access-Token";


    @POST("service/login")
    @Headers("Content-Type: application/json")
    Call<AuthUser> authLogin(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                             @Body HashMap<String, Object> request);


    @GET("auth/token")
    @Headers("Content-Type: application/json")
    Call<AuthUser> authLoginWithAccessToken(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                            @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken);

    @POST("service/register")
    Call<AuthUser> authRegister(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                @Body HashMap<String, Object> request);


    @POST("auth/viewer")
    Call<AuthUser> updateCurrentUser(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                     @Body HashMap<String, Object> request);

    @GET("auth/viewer")
    Call<AuthUser> getCurrentUser(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                  @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken);

    @GET("content/programs")
    Call<List<Program>> getPrograms(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                    @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                    @Query("channel") int channelId,
                                    @Query("start_date") String startDate,
                                    @Query("end_date") String endDate,
                                    @Query("offset") int offset,
                                    @Query("limit") int limit,
                                    @Query("sort") String sort);

    @GET("content/programs")
    Call<Program> getProgramWithID(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                   @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                   @Query("searchId") int programID);

    @GET("artist/searchbyid")
    Call<Artist> getArtistWithID(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                 @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                 @Query("artist_id") int artistID);

    @GET("playlist/playlistdata/{id}")
    Call<PlayList> getPlaylistWithID(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                     @Path("id") int playlistID,
                                     @Query("g") boolean g);

    @GET("audio/songsbyid")
    Call<Song> getSongWithID(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                             @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                             @Query("song_id") int songID);

    @GET("content/programs")
    Call<List<Program>> getChannelTrailers(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                           @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                           @Query("channel") int channelId,
                                           @Query("start_date") String startDate,
                                           @Query("end_date") String endDate,
                                           @Query("offset") int offset,
                                           @Query("limit") int limit,
                                           @Query("t") boolean trailer,
                                           @Query("sort") String sort);


    @GET("content/programs/{id}/allepisodes")
    Call<List<Episode>> getProgramTrailers(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                           @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                           @Path("id") int programId,
                                           @Query("start_date") String startDate,
                                           @Query("end_date") String endDate,
                                           @Query("offset") int offset,
                                           @Query("limit") int limit,
                                           @Query("t") boolean trailer,
                                           @Query("sort") String order);

    @GET("content/programs/trailer/v2/{id}")
    Call<List<Program>> getTrailer(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                   @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                   @Path("id") int programId);


    @GET("content/list/programs")
    Call<List<Program>> getProgramsImages(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                          @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                          @Query("offset") int offset,
                                          @Query("limit") int limit);

    @GET("ads/slider")
    Call<List<Program>> getProgramsSliderImages(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                                @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                                @Query("offset") int offset,
                                                @Query("limit") int limit,
                                                @Query("k") boolean kidsMode);

    @GET("content/programs/subscribed")
    Call<List<Program>> getSubscribedPrograms(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                              @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                              @Query("channel") int channelId,
                                              @Query("offset") int offset,
                                              @Query("limit") int limit,
                                              @Query("k") boolean kidsMode);

    @GET("content/programs/subscribed")
    Call<List<Program>> getSubscribedPrograms(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                              @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                              @Query("offset") int offset,
                                              @Query("limit") int limit,
                                              @Query("k") boolean kidsMode);

    @GET("content/programs/{id}/allepisodes")
    Call<List<Episode>> getEpisodes(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                    @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                    @Path("id") int programId,
                                    @Query("start_date") String startDate,
                                    @Query("end_date") String endDate,
                                    @Query("offset") int offset,
                                    @Query("limit") int limit,
                                    @Query("sort") String order);


    @GET("content/programs/{id}/episodes")
    Call<List<Episode>> getEpisodesNew(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                       @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                       @Path("id") int id,
                                       @Query("program_id") int programId,
                                       @Query("start_date") String startDate,
                                       @Query("end_date") String endDate,
                                       @Query("offset") int offset,
                                       @Query("limit") int limit,
                                       @Query("sort") String order);


    @GET("content/programs")
    Call<List<Program>> getProgramList(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                       @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                       @Query("start_date") String startDate,
                                       @Query("end_date") String endDate,
                                       @Query("limit") int limit,
                                       @Query("offset") int offset,
                                       @Query("channel") int channelId);


    @GET("content/programs/hot")
    Call<List<Program>> getHotVideos(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                     @Query("channel") int channelId);

    @GET("content/programs/live")
    Call<List<Program>> getLiveVideos(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                      @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                      @Query("channel") int channelId);


    @GET("content/action/like/program/{id}")
    Call<Void> likeProgram(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                           @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                           @Path("id") int programId);

    @GET("content/action/unlike/program/{id}")
    Call<Void> unlikeProgram(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                             @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                             @Path("id") int programId);

    @GET("ads/content")
    Call<List<Content>> getAdvertisements(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                          @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                          @Query("content_id") int episodeId);

    @GET("content/channels")
    Call<List<Channel>> getAllChannels(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                       @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                       @Query("k") boolean kidsMode);

    @GET("content/action/2/{id}/{actionid}")
    Call<Void> likeEpisode(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                           @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                           @Path("id") int episodeId,
                           @Path("actionid") int actionId);

    @GET("content/action/unlike/episode/{id}")
    Call<Void> unlikeEpisode(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                             @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                             @Path("id") int episodeId);

    @GET("content/action/2/{id}/{actionid}")
    Call<Void> subscribeProgram(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                @Path("id") int episodeId,
                                @Path("actionid") int actionId);

    @GET("content/action/2/{id}/{actionid}")
    Call<Void> unSubscribeProgram(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                  @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                  @Path("id") int episodeId,
                                  @Path("actionid") int actionId);

//    @GET("content/action/subscribe/{id}")
//    Call<Void> subscribeProgram(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
//                                @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
//                                @Path("id") int programId);

//    @GET("content/action/unsubscribe/{id}")
//    Call<Void> unSubscribeProgram(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
//                                  @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
//                                  @Path("id") int programId);

    @GET("notification/clear/{id}")
    Call<Void> clearNotification(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                 @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                 @Path("id") int notificationId);

    @GET("notification/clear")
    Call<Void> clearAllNotifications(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken);

    @GET("subscription/promocode/{id}")
    Call<Package> subscribePromotion(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                     @Path("id") String scratchNumber);

    @GET("subscription/current")
    Call<Package> getActivatedPackage(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                      @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken);

    @GET("subscription/current/v2")
    Call<PackageV2> getTrialStatus(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                   @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken);

    @GET("subscription/packages")
    Call<List<Package>> getAllSubscriptionPackages(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                                   @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken);

    @GET("subscription/packages/subscribe/{id}")
    Call<PackageToken> generateSubscriptionToken(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                                 @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                                 @Path("id") int packageId);


    @GET("analytics/player/{action}/{id}")
    Call<Void> publishAnalytics(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                @Path("action") String action,
                                @Path("id") String id,
                                @Query("action_time") String actionTime);

    @POST("auth/verify")
    Call<Void> verifyUserFromSMS(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                 @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                 @Body HashMap<String, Object> request);


    @GET("notification/received")
    Call<List<Notification>> getNotifications(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                              @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                              @Query("limit") int limit);


    @GET("notification/received/unread/count")
    Call<NotificationCountResponse> getNotificationCount(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                                         @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken);

    @POST("notification/received/markread")
    Call<Void> markNotificationsAsRead(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                       @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                       @Body ArrayList<Integer> request);

    @GET("content/programs")
    Call<List<Program>> getHomeContents(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                        @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                        @Query("channel") int channelId,
                                        @Query("start_date") String startDate,
                                        @Query("end_date") String endDate,
                                        @Query("offset") int offset,
                                        @Query("limit") int limit);


    @GET("audio/channels")
    Call<List<AudioChannel>> getAllAudioChannels(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                                 @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken);

    @GET("audio/channel/program")
    Call<List<AudioProgram>> getAudioPrograms(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                              @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                              @Query("channel") int channelId);

    @GET("audio/program/songs")
    Call<List<Song>> getAudioProgramsSongs(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                           @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                           @Query("program") int programId);


    @GET("audio/songs/recent")
    Call<List<Song>> getAudioRecentlyPlayed(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                            @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                            @Query("offset") int offset,
                                            @Query("limit") int limit);


    @GET("audio/songs/suggestions")
    Call<List<Song>> getAudioYouLike(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                     @Query("offset") int offset,
                                     @Query("limit") int limit);

    @GET("audio/getSong")
    Call<Song> getAudioSong(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                            @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                            @Query("songs") int songId);

    @GET("audio/dailymix/songs")
    Call<List<Song>> getAudioDailymixSongs(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                           @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                           @Query("dailymix") int songId);

    @GET("audio/favourite/getSongs")
    Call<List<Song>> getAudioFavouriteSongs(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                            @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken);

    @GET("audio/songs/attr/genre")
    Call<List<Genre>> getAllGenre(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                  @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                  @Query("limit") int limit,
                                  @Query("offset") int offset);

    @GET("audio/songs")
    Call<List<Song>> getAllSongs(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                 @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                 @Query("offset") int offset,
                                 @Query("limit") int limit);


    @GET("audio/playlist/songs")
    Call<List<Song>> getPopularSongs(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                     @Query("offset") int offset,
                                     @Query("limit") int limit,
                                     @Query("ps") boolean tru);

    @GET("library/songs")
    Call<List<Song>> getLibrarySongs(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                     @Query("limit") int limit,
                                     @Query("offset") int offset);

    @GET("library/artists")
    Call<List<Artist>> getLibraryArtists(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                         @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                         @Query("limit") int limit,
                                         @Query("offset") int offset);

    @GET("library/playlist")
    Call<List<PlayList>> getLibraryPlaylists(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                             @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                             @Query("limit") int limit,
                                             @Query("offset") int offset);


    @GET("audio/playlist/songs")
    Call<List<Song>> getLatestSongs(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                    @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                    @Query("ls") boolean tru);

    @GET("audio/artist/popular")
    Call<List<Artist>> getPopularArtists(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                         @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                         @Query("pa") boolean tru);

    @GET("genre/{id}/artists")
    Call<List<Artist>> getGenreArtists(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                       @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                       @Path("id") int artistID);

    @GET("artist/list")
    Call<List<Artist>> getAllArtists(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                     @Query("offset") int offset,
                                     @Query("limit") int limit);

    @GET("artist/{id}/songs")
    Call<List<Song>> getArtistSongs(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                    @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                    @Path("id") int artistID);


    @GET("radio/channel")
    Call<List<Song>> getRadioChannel(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken);


    @GET("audio/songs/genre/{genre}")
    Call<List<Song>> getGenreSongs(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                   @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                   @Path("genre") String genre,
                                   @Query("offset") int offset,
                                   @Query("limit") int limit);

    @GET("genre/{id}/playlists")
    Call<List<PlayList>> getGenrePlaylists(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                           @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                           @Path("id") int id);


    @GET("audio/songs/search/{text}")
    Call<List<Song>> getSearchedSongs(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                      @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                      @Path("text") String text,
                                      @Query("offset") int offset,
                                      @Query("limit") int limit);

    @GET("audio/search-all")
    Call<SearchResponse> getSearchedAll(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                        @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                        @Query("query_text") String text);

    @GET("audio/search-by-type")
    Call<List<Song>> getSearchSongsbyType(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                          @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                          @Query("query_text") String text,
                                          @Query("offset") int offset,
                                          @Query("limit") int limit,
                                          @Query("type") String type);

    @GET("audio/search-by-type")
    Call<List<Artist>> getSearchArtistbyType(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                             @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                             @Query("query_text") String text,
                                             @Query("offset") int offset,
                                             @Query("limit") int limit,
                                             @Query("type") String type);

    @GET("audio/search-by-type")
    Call<List<PlayList>> getSearchPlaylistbyType(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                                 @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                                 @Query("query_text") String text,
                                                 @Query("offset") int offset,
                                                 @Query("limit") int limit,
                                                 @Query("type") String type);

    @GET("audio/search-history")
    Call<List<String>> getSearchSuggestions(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                            @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                            @Query("limit") int limit);

    @GET("audio/songs/suggession/genre")
    Call<List<Song>> getAudioSuggestionList(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                            @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                            @Query("offset") int offset,
                                            @Query("limit") int limit,
                                            @Query("song_id") int songID);

    @GET("audio/playlist")
    Call<List<DailyMix>> getAllDailymix(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                        @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                        @Query("offset") int offset,
                                        @Query("limit") int limit,
                                        @Query("g") boolean globle);

    @GET("audio/playlist")
    Call<List<PlayList>> getAllDailymixNew(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                           @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                           @Query("offset") int offset,
                                           @Query("limit") int limit,
                                           @Query("g") boolean globle);

    @GET("audio/playlist")
    Call<List<PlayList>> getRadioDramasData(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                           @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                           @Query("offset") int offset,
                                           @Query("limit") int limit,
                                           @Query("rd") boolean rd);

    @GET("audio/playlist")
    Call<List<PlayList>> getAllPlaylist(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                        @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                        @Query("offset") int offset,
                                        @Query("limit") int limit);

    @GET("playlist/playlistdata/{id}")
    Call<PlayList> getPlaylistData(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                   @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                   @Path("id") int id,
                                   @Query("g") boolean data);


    @GET("audio/playlist/songs/{id}")
    Call<List<Song>> getSongsOfDailymix(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                        @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                        @Path("id") int id,
                                        @Query("offset") int offset,
                                        @Query("limit") int limit,
                                        @Query("g") boolean globle);

    @GET("audio/playlist/songs/{id}")
    Call<List<Song>> getSongsOfPlaylistInLibrary(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                                 @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                                 @Path("id") int id,
                                                 @Query("offset") int offset,
                                                 @Query("limit") int limit,
                                                 @Query("sessionid") String sessionid,
                                                 @Query("g") boolean globle);


    @GET("audio/playlist/songs/{id}")
    Call<List<Song>> getSongsOfUserPlaylist(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                            @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                            @Path("id") int id,
                                            @Query("offset") int offset,
                                            @Query("limit") int limit);


    @GET("audio/playlist/songs/{id}")
    Call<List<Song>> getSongsOfPlaylist(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                        @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                        @Path("id") int id,
                                        @Query("offset") int offset,
                                        @Query("limit") int limit);

    @POST("audio/playlist/create")
    Call<PlayList> audioPlaylistCreate(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                       @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                       @Header("Content-Type") String content_type,
                                       @Body HashMap<String, Object> request);

    @POST("audio/playlist/remove")
    Call<Void> audioPlaylistRemove(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                   @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                   @Header("Content-Type") String content_type,
                                   @Body HashMap<String, Object> request);

    @POST("audio/playlist/create")
    Call<PlayList> audioPlaylistCreateLibrary(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                              @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                              @Header("Content-Type") String content_type,
                                              @Body ArrayList<String> strings);


    @POST("playlist/songs/add")
    Call<Void> addSongsToPlaylist(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                  @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                  @Body HashMap<String, Object> request);                   //Body created with Hashmap

    @POST("library/add")
    Call<Void> addDataToLibrary(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                @Body PlaylistModel request);                              //Body created with Model

    @POST("library/add")
    Call<Void> addDataToLibraryHash(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                    @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                    @Body HashMap<String, Object> request);                    //Body created with HASHMAP

    @POST("library/remove")
    Call<Void> removeDatafromLibrary(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                     @Body HashMap<String, Object> request);                    //Body created with HASHMAP


    @POST("audio/playlist/addtotemp")
    Call<Void> addSongsToTempTable(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                   @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                   @Body HashMap<String, Object> request);                   //Add n Add all songs to temp table

    @POST("audio/playlist/temp/reload")
    Call<Void> loadPlaylistTempTable(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                     @Body HashMap<String, Object> request);                   //Add n Add all songs to temp table

    @POST("playlist/songs/editsongs")
    Call<Void> saveEditedPlaylist(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                  @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                  @Body HashMap<String, Object> request);                   //Add n Add all songs to temp table

    @GET("audio/playlist/temp/all")
    Call<List<Song>> getSongsFromPlaylistTable(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                               @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                               @Query("session_id") String sessionId);


    @POST("audio/playlist/addsongs/{id}")
    Call<Void> addSongsToPlaylist(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                  @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                  @Header("Content-Type") String content_type,
                                  @Path("id") int id,
                                  @Body ArrayList<Integer> request);

    @POST("password/request")
    Call<ForgotPasswordUser> forgotPasswordMobileNumberRequest(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                                               @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                                               @Query("mobile_no") String mobileNumber);

    @POST("password/request/verify")
    Call<ForgotPasswordUser> forgotPasswordOtpRequest(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                                      @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                                      @Query("viwer_id") int viwerId,
                                                      @Query("otp") String otp);

    @POST("password/reset")
    Call<ForgotPasswordUser> forgotNewPasswordUpdate(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                                     @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                                     @Query("viwer_id") int viwerId,
                                                     @Query("password") String password);

    @POST("deviceid/update")
    Call<Void> sendDeviceId(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                            @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                            @Body HashMap<String, Object> request);

    @GET("analytics/screen/{Content_Id}/{Screen_Id}/{Screen_Action_Id}/{Screen_Time}")
    Call<Void> sendActionAnalytics(@Header(HEADER_AUTHORIZATION) String basicAuthToken,
                                   @Header(HEADER_TOKEN_AUTHENTICATION) String accessToken,
                                   @Path("Content_Id") int contentId,
                                   @Path("Screen_Id") int screenId,
                                   @Path("Screen_Action_Id") int actionId,
                                   @Path("Screen_Time") String screenTime);


}
