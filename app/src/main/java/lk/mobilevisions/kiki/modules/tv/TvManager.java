/**
 * Created by Chatura Dilan Perera on 23/9/2016.
 */
package lk.mobilevisions.kiki.modules.tv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import lk.mobilevisions.kiki.modules.api.dto.Channel;
import lk.mobilevisions.kiki.modules.api.dto.Content;
import lk.mobilevisions.kiki.modules.api.dto.Slider;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Settings;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.modules.api.API;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Episode;
import lk.mobilevisions.kiki.modules.api.dto.Program;
import lk.mobilevisions.kiki.modules.api.exceptions.ApplicationException;
import lk.mobilevisions.kiki.modules.api.exceptions.ErrorResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.InvalidResponseException;
import lk.mobilevisions.kiki.modules.api.exceptions.NoInternetConnectionException;
import lk.mobilevisions.kiki.modules.api.exceptions.RemoteServerException;
import lk.mobilevisions.kiki.modules.auth.exceptions.AuthenticationFailedWithAccessTokenException;

import static lk.mobilevisions.kiki.app.Constants.PROGRAM_LIST_LIMIT;

public class TvManager {

    API api;
    Application application;

    public TvManager(Application application, API api) {
        this.application = application;
        this.api = api;
    }

    public void getPrograms(int channelId, Date startDate, Date endDate, final APIListener<List<Program>> listener) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);

        String startDateSting = startDate != null ? dateFormat.format(startDate) : null;
        String endDateSting = endDate != null ? dateFormat.format(endDate) : null;

        api.getPrograms(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), channelId, startDateSting,
                endDateSting, 0, PROGRAM_LIST_LIMIT, "latest").enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getProgramWithID(int programID, final APIListener<Program> listener) {
        api.getProgramWithID(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), programID  ).enqueue(new Callback<Program>() {
            @Override
            public void onResponse(Call<Program> call, Response<Program> response) {

                System.out.println("checking getAllAudioChannel 3333  " + response.code());

                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Program> call, Throwable t) {

            }
        });
    }

    public void getArtistWithID(int artistID, final APIListener<Artist> listener) {
        api.getArtistWithID(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), artistID  ).enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(Call<Artist> call, Response<Artist> response) {

                System.out.println("checking getAllAudioChannel 3333  " + response.code());

                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Artist> call, Throwable t) {

            }
        });
    }

    public void getPlaylistWithID(int playlistID, final APIListener<PlayList> listener) {
        api.getPlaylistWithID(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), playlistID, true  ).enqueue(new Callback<PlayList>() {
            @Override
            public void onResponse(Call<PlayList> call, Response<PlayList> response) {

                System.out.println("checking getAllAudioChannel 3333  " + response.code());

                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<PlayList> call, Throwable t) {

            }
        });
    }

    public void getSongWithID(int songID, final APIListener<Song> listener) {
        api.getSongWithID(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), songID ).enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {

                System.out.println("checking getAllAudioChannel 3333  " + response.code());

                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Song> call, Throwable t) {

            }
        });
    }


    public void getChannelTrailers(int channelId, Date startDate, Date endDate, final APIListener<List<Program>> listener) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);

        String startDateSting = startDate != null ? dateFormat.format(startDate) : null;
        String endDateSting = endDate != null ? dateFormat.format(endDate) : null;
        System.out.println("fnfnfnc 1111  " + startDateSting);
        System.out.println("fnfnfnc 2222  " + endDateSting);
        api.getChannelTrailers(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), channelId, startDateSting,
                endDateSting, 0, PROGRAM_LIST_LIMIT, true, "random").enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getProgramTrailers(int programId, Date startDate, Date endDate, final APIListener<List<Episode>> listener) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);

        String startDateSting = startDate != null ? dateFormat.format(startDate) : null;
        String endDateSting = endDate != null ? dateFormat.format(endDate) : null;

        api.getProgramTrailers(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), programId, startDateSting,
                endDateSting, 0, PROGRAM_LIST_LIMIT, true, "random").enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getTrailer(int programId,  final APIListener<List<Program>> listener) {


        api.getTrailer(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), programId).enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }
    public void getProgramsImages(final APIListener<List<Program>> listener) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);


        api.getProgramsImages(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), 0, 10).enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getProgramsSliderImages(final APIListener<List<Program>> listener, boolean isKidsModeEnabled) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);


        api.getProgramsSliderImages(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), 0, 10, isKidsModeEnabled).enqueue(new Callback<List<Program>>() {
            @Override
            public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Program>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getSubscribedPrograms(int channelId, int offset, int limit, final APIListener<List<Program>> listener, boolean isKidsModeEnabled) {

        if (channelId > 0) {
            System.out.println("checking subscription 5555 " + isKidsModeEnabled);
            api.getSubscribedPrograms(Utils.Auth.getBasicAuthToken(application),
                    Utils.Auth.getBearerToken(application), channelId, offset, limit, isKidsModeEnabled).enqueue(new Callback<List<Program>>() {
                @Override
                public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                    System.out.println("checking subscription 6666 " + response);
                    System.out.println("checking subscription 777 " + response.body());
                    switch (response.code()) {
                        case 200:
                            if (response.body() != null) {
                                listener.onSuccess(response.body(), null);
                            } else {
                                listener.onFailure(new InvalidResponseException());
                            }
                            break;
                        case 401:
                            try {
                                listener.onFailure(Utils.Error.
                                        getServerError(application, response,
                                                AuthenticationFailedWithAccessTokenException.class));
                            } catch (ErrorResponseException e) {
                                listener.onFailure(e);
                            }
                            break;
                        case 400:
                            try {
                                listener.onFailure(Utils.Error.
                                        getServerError(application, response,
                                                ApplicationException.class));
                            } catch (ErrorResponseException e) {
                                listener.onFailure(e);
                            }
                            break;
                        default:
                            listener.onFailure(new RemoteServerException());
                            break;
                    }
                }

                @Override
                public void onFailure(Call<List<Program>> call, Throwable t) {
                    Timber.e(t);
                    listener.onFailure(t);
                }
            });
        } else {
            System.out.println("checking subscription 9999 " + isKidsModeEnabled);
            api.getSubscribedPrograms(Utils.Auth.getBasicAuthToken(application),
                    Utils.Auth.getBearerToken(application), offset, limit, isKidsModeEnabled).enqueue(new Callback<List<Program>>() {
                @Override
                public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                    switch (response.code()) {
                        case 200:
                            if (response.body() != null) {
                                listener.onSuccess(response.body(), null);
                            } else {
                                listener.onFailure(new InvalidResponseException());
                            }
                            break;
                        case 401:
                            try {
                                listener.onFailure(Utils.Error.
                                        getServerError(application, response,
                                                AuthenticationFailedWithAccessTokenException.class));
                            } catch (ErrorResponseException e) {
                                listener.onFailure(e);
                            }
                            break;
                        case 400:
                            try {
                                listener.onFailure(Utils.Error.
                                        getServerError(application, response,
                                                ApplicationException.class));
                            } catch (ErrorResponseException e) {
                                listener.onFailure(e);
                            }
                            break;
                        default:
                            listener.onFailure(new RemoteServerException());
                            break;
                    }
                }

                @Override
                public void onFailure(Call<List<Program>> call, Throwable t) {
                    Timber.e(t);
                    listener.onFailure(t);
                }
            });
        }

    }


    public void getAllChannels(final APIListener<List<Channel>> listener, boolean isKidsModeEnabled) {
        api.getAllChannels(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), isKidsModeEnabled).enqueue(new Callback<List<Channel>>() {
            @Override
            public void onResponse(Call<List<Channel>> call, Response<List<Channel>> response) {


                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Channel>> call, Throwable t) {

            }
        });
    }

    public void getEpisodes(final int programId, final int offset, final int limit, Date startDate, Date endDate,
                            String order, final APIListener<List<Episode>> listener) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);

        String startDateSting = startDate != null ? dateFormat.format(startDate) : null;
        String endDateSting = endDate != null ? dateFormat.format(endDate) : null;

        api.getEpisodes(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)
                , programId, startDateSting, endDateSting, offset, limit, order).enqueue(new Callback<List<Episode>>() {

            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getProgramList(final int programId, final int offset, final int limit, Date startDate, Date endDate,
                            String order, final APIListener<List<Episode>> listener) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);

        String startDateSting = startDate != null ? dateFormat.format(startDate) : null;
        String endDateSting = endDate != null ? dateFormat.format(endDate) : null;

        api.getEpisodes(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)
                , programId, startDateSting, endDateSting, offset, limit, order).enqueue(new Callback<List<Episode>>() {

            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }


    public void getEpisodesNew(final int programId, final int offset, final int limit, Date startDate, Date endDate,
                            String order, final APIListener<List<Episode>> listener) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);

        String startDateSting = startDate != null ? dateFormat.format(startDate) : null;
        String endDateSting = endDate != null ? dateFormat.format(endDate) : null;

        api.getEpisodesNew(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)
                , programId, programId,startDateSting, endDateSting, offset, limit, order).enqueue(new Callback<List<Episode>>() {

            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getAdvertisement(int episodeId, final APIListener<List<Content>> listener) {

        api.getAdvertisements(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), episodeId)
                .enqueue(new Callback<List<Content>>() {
                    @Override
                    public void onResponse(Call<List<Content>> call, Response<List<Content>> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    listener.onSuccess(response.body(), null);
                                } else {
                                    listener.onFailure(new InvalidResponseException());
                                }
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Content>> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }

    public void getHotVideos(int channelId, final APIListener<List<Program>> listener) {
        api.getHotVideos(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), channelId)
                .enqueue(new Callback<List<Program>>() {

                    @Override
                    public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    listener.onSuccess(response.body(), null);
                                } else {
                                    listener.onFailure(new InvalidResponseException());
                                }
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Program>> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }

    public void getLiveVideos(int channelId, final APIListener<List<Program>> listener) {
        api.getLiveVideos(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), channelId)
                .enqueue(new Callback<List<Program>>() {

                    @Override
                    public void onResponse(Call<List<Program>> call, Response<List<Program>> response) {
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    listener.onSuccess(response.body(), null);
                                } else {
                                    listener.onFailure(new InvalidResponseException());
                                }
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application,
                                            response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application,
                                            response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Program>> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }


    public void likeProgram(final int programId, final APIListener<List<Episode>> listener) {
        api.likeProgram(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), programId).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        listener.onSuccess(null, null);
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void unlikeProgram(final int programId, final APIListener<List<Episode>> listener) {
        api.unlikeProgram(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), programId).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        listener.onSuccess(null, null);
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void likeEpisode(final int episodeId,final int actionId,  final APIListener<List<Void>> listener) {
        api.likeEpisode(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), episodeId, actionId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        listener.onSuccess(null, null);
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void unlikeEpisode(final int episodeId, final APIListener<List<Void>> listener) {
        api.unlikeEpisode(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), episodeId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        listener.onSuccess(null, null);
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void subscribeProgram(final int episodeId,final int actionId,  final APIListener<Void> listener) {
        api.subscribeProgram(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), episodeId, actionId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        listener.onSuccess(null, null);
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }


    public void unSubscribeProgram(final int episodeId,final int actionId,  final APIListener<Void> listener) {
        api.unSubscribeProgram(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), episodeId, actionId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        listener.onSuccess(null, null);
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }


    public void getAllAudioChannels(final APIListener<List<AudioChannel>> listener) {
        api.getAllAudioChannels(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<List<AudioChannel>>() {
            @Override
            public void onResponse(Call<List<AudioChannel>> call, Response<List<AudioChannel>> response) {

                System.out.println("checking getAllAudioChannels 3333  " + response.code());

                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<AudioChannel>> call, Throwable t) {

            }
        });
    }

    public void getAudioPrograms(int channelId, Date startDate, Date endDate, final APIListener<List<AudioProgram>> listener) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);

        String startDateSting = startDate != null ? dateFormat.format(startDate) : null;
        String endDateSting = endDate != null ? dateFormat.format(endDate) : null;

        api.getAudioPrograms(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), channelId).enqueue(new Callback<List<AudioProgram>>() {
            @Override
            public void onResponse(Call<List<AudioProgram>> call, Response<List<AudioProgram>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<AudioProgram>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getAudioProgramsSongs(int programId, Date startDate, Date endDate, final APIListener<List<Song>> listener) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);

        String startDateSting = startDate != null ? dateFormat.format(startDate) : null;
        String endDateSting = endDate != null ? dateFormat.format(endDate) : null;

        api.getAudioProgramsSongs(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), programId).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getDailyMix(final APIListener<List<DailyMix>> listener) {
        api.getAllDailymix(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), 0, 30, true).enqueue(new Callback<List<DailyMix>>() {
            @Override
            public void onResponse(Call<List<DailyMix>> call, Response<List<DailyMix>> response) {

                System.out.println("checking getAllAudioChannels 3333  " + response.code());

                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<DailyMix>> call, Throwable t) {

            }
        });
    }

    public void getDailyMixNew(int offset, int limit,final APIListener<List<PlayList>> listener) {
        api.getAllDailymixNew(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), offset, limit, true).enqueue(new Callback<List<PlayList>>() {
            @Override
            public void onResponse(Call<List<PlayList>> call, Response<List<PlayList>> response) {

                System.out.println("checking getAllAudioChannels 3333  " + response.code());

                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<PlayList>> call, Throwable t) {

            }
        });
    }


    public void getAudioRecentlyPlayed(final APIListener<List<Song>> listener) {

        api.getAudioRecentlyPlayed(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), 0, 30).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                System.out.println("jfbjkbckbjckbjc 111 " + response.code());
                switch (response.code()) {

                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getAudioYouLike(int limit, final APIListener<List<Song>> listener) {

        api.getAllSongs(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), 0, limit).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                System.out.println("sfhbasfbbfbf 111 " + t.toString());
                System.out.println("sfhbasfbbfbf 222 " + t.getMessage());
                System.out.println("sfhbasfbbfbf 333 " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getPopularSongs( int offset, int limit,final APIListener<List<Song>> listener) {

        api.getPopularSongs(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application),offset,limit,true).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                System.out.println("sfhbasfbbfbf 111 " + t.toString());
                System.out.println("sfhbasfbbfbf 222 " + t.getMessage());
                System.out.println("sfhbasfbbfbf 333 " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getLibrarySongs( int limit, int offset,final APIListener<List<Song>> listener) {

        api.getLibrarySongs(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application),limit,offset).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                System.out.println("sfhbasfbbfbf 111 " + t.toString());
                System.out.println("sfhbasfbbfbf 222 " + t.getMessage());
                System.out.println("sfhbasfbbfbf 333 " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getLibraryArtists( int limit, int offset,final APIListener<List<Artist>> listener) {

        api.getLibraryArtists(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application),limit,offset).enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                System.out.println("sfhbasfbbfbf 111 " + t.toString());
                System.out.println("sfhbasfbbfbf 222 " + t.getMessage());
                System.out.println("sfhbasfbbfbf 333 " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getLibraryPlaylists( int limit, int offset,final APIListener<List<PlayList>> listener) {

        api.getLibraryPlaylists(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application),limit,offset).enqueue(new Callback<List<PlayList>>() {
            @Override
            public void onResponse(Call<List<PlayList>> call, Response<List<PlayList>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<PlayList>> call, Throwable t) {
                System.out.println("sfhbasfbbfbf 111 " + t.toString());
                System.out.println("sfhbasfbbfbf 222 " + t.getMessage());
                System.out.println("sfhbasfbbfbf 333 " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }


    public void getLatestSongs( final APIListener<List<Song>> listener) {

        api.getLatestSongs(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application),true).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                System.out.println("sfhbasfbbfbf 111 " + t.toString());
                System.out.println("sfhbasfbbfbf 222 " + t.getMessage());
                System.out.println("sfhbasfbbfbf 333 " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getPopularArtists( final APIListener<List<Artist>> listener) {

        api.getPopularArtists(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application),true).enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                System.out.println("sfhbasfbbfbf 111 " + t.toString());
                System.out.println("sfhbasfbbfbf 222 " + t.getMessage());
                System.out.println("sfhbasfbbfbf 333 " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getGenreArtists(final int artistID, final APIListener<List<Artist>> listener) {

        api.getGenreArtists(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), artistID).enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                System.out.println("check artist response " + response.code());
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                System.out.println("check artist 111 " + t.toString());
                System.out.println("check artist 222 " + t.getMessage());
                System.out.println("check artist 333 " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getAllArtists(int offset, int limit, final APIListener<List<Artist>> listener) {

        api.getAllArtists(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application),offset,limit).enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                System.out.println("check artist response " + response.code());
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                System.out.println("check artist 111 " + t.toString());
                System.out.println("check artist 222 " + t.getMessage());
                System.out.println("check artist 333 " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }


    public void getArtistSongs(final int artistID, final APIListener<List<Song>> listener) {

        api.getArtistSongs(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), artistID).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                System.out.println("check artist response " + response.code());
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                System.out.println("check artist 111 " + t.toString());
                System.out.println("check artist 222 " + t.getMessage());
                System.out.println("check artist 333 " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

//    public void getGenrePlaylistNew(final int id, final APIListener<List<PlayList>> listener) {
//
//        api.getGenrePlaylists(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), id).enqueue(new Callback<List<PlayList>>() {
//            @Override
//            public void onResponse(Call<List<PlayList>> call, Response<List<PlayList>> response) {
//                System.out.println("check artist response " + response.code());
//                switch (response.code()) {
//                    case 200:
//                        if (response.body() != null) {
//                            listener.onSuccess(response.body(), null);
//                        } else {
//                            listener.onFailure(new InvalidResponseException());
//                        }
//                        break;
//                    case 401:
//                        try {
//                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
//                        } catch (ErrorResponseException e) {
//                            listener.onFailure(e);
//                        }
//                        break;
//                    case 400:
//                        try {
//                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
//                        } catch (ErrorResponseException e) {
//                            listener.onFailure(e);
//                        }
//                        break;
//                    default:
//                        listener.onFailure(new RemoteServerException());
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<PlayList>> call, Throwable t) {
//                System.out.println("check artist 111 " + t.toString());
//                System.out.println("check artist 222 " + t.getMessage());
//                System.out.println("check artist 333 " + t.getLocalizedMessage());
//                Timber.e(t);
//                listener.onFailure(t);
//            }
//        });
//    }



    public void getRadioChannels( final APIListener<List<Song>> listener) {

        api.getRadioChannel(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application)).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                System.out.println("sfhbasfbbfbf 111 " + t.toString());
                System.out.println("sfhbasfbbfbf 222 " + t.getMessage());
                System.out.println("sfhbasfbbfbf 333 " + t.getLocalizedMessage());
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }


    public void getAudioSong(int songId, Date startDate, Date endDate, final APIListener<Song> listener) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);

        String startDateSting = startDate != null ? dateFormat.format(startDate) : null;
        String endDateSting = endDate != null ? dateFormat.format(endDate) : null;

        api.getAudioSong(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), songId).enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<Song> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }

    public void getAudioDailymixSongs(int dailymixId, Date startDate, Date endDate, final APIListener<List<Song>> listener) {

        DateFormat dateFormat = new SimpleDateFormat(Settings.Date.FORMAT);

        String startDateSting = startDate != null ? dateFormat.format(startDate) : null;
        String endDateSting = endDate != null ? dateFormat.format(endDate) : null;

        api.getAudioDailymixSongs(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), dailymixId).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Timber.e(t);
                listener.onFailure(t);
            }
        });
    }


    public void getAllAudioGenre(int limit,final APIListener<List<Genre>> listener) {
        api.getAllGenre(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), limit,0).enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {

            }
        });
    }

    public void getSearchedSongs(int offset, int limit, final String text, final APIListener<List<Song>> listener) {
        api.getSearchedSongs(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), text, offset, limit).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                listener.onFailure(new RemoteServerException());
            }
        });
    }

    public void getSearchedAll(final String text, final APIListener<SearchResponse> listener) {
        api.getSearchedAll(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), text ).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {

                System.out.println("checking getAllAudioChannels 3333  " + response.code());

                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {

            }
        });
    }

    public void getSearchSongsbyType(int offset, int limit, final String text, final APIListener<List<Song>> listener) {
        api.getSearchSongsbyType(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), text, offset,limit, "song").enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                listener.onFailure(new RemoteServerException());
            }
        });
    }

    public void getSearchArtistbyType(final String text,int offset, int limit, final APIListener<List<Artist>> listener) {
        api.getSearchArtistbyType(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), text, offset,limit, "artist").enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                listener.onFailure(new RemoteServerException());
            }
        });
    }

    public void getSearchPlaylistbyType(String text, int offset, int limit, final APIListener<List<PlayList>> listener) {
        api.getSearchPlaylistbyType(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), text, offset,limit, "playlist").enqueue(new Callback<List<PlayList>>() {
            @Override
            public void onResponse(Call<List<PlayList>> call, Response<List<PlayList>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            System.out.println("skdcbsjhdadasda 2222" + response.body());
                            listener.onSuccess(response.body(), null);
                        } else {
                            System.out.println("skdcbsjhdadasda 3333" );
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<PlayList>> call, Throwable t) {
                System.out.println("skdcbsjhdadasda 11111" );
                listener.onFailure(new RemoteServerException());
            }
        });
    }


    public void getAllSongs(int offset, int limit, final APIListener<List<Song>> listener) {
        api.getAllSongs(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), offset, limit).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                System.out.println("wtgaegrfg 5555 " + response.code());
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                listener.onFailure(new RemoteServerException());
                System.out.println("wtgaegrfg 6666 " + t.toString());
                System.out.println("wtgaegrfg 7777 " + t.getMessage());
                System.out.println("wtgaegrfg 8888 " + t.getLocalizedMessage());
            }
        });
    }

    public void getGenreSongs(int offset, int limit, final String genre, final APIListener<List<Song>> listener) {
        api.getGenreSongs(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), genre, offset, limit).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                listener.onFailure(new RemoteServerException());
            }
        });
    }

    public void getByGenrePlaylists(final int id, final APIListener<List<PlayList>> listener) {
        api.getGenrePlaylists(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), id).enqueue(new Callback<List<PlayList>>() {
            @Override
            public void onResponse(Call<List<PlayList>> call, Response<List<PlayList>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<PlayList>> call, Throwable t) {
                listener.onFailure(new RemoteServerException());
            }
        });
    }


    public void getSongsOfDailymix(final int dailyMixId, final APIListener<List<Song>> listener) {
        api.getSongsOfDailymix(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), dailyMixId, 1, 100, true).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });
    }

    public void getSongsOfUserPlaylist(final int dailyMixId, final APIListener<List<Song>> listener) {
        api.getSongsOfUserPlaylist(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), dailyMixId, 1, 100).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });
    }


    public void getSongsOfPlaylist(final int playlistId, final APIListener<List<Song>> listener) {
        api.getSongsOfPlaylist(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), playlistId, 0, 30).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });
    }

    public void addDataToLibrary(PlaylistModel playlistModel, final APIListener<Void> listener) {
        api.addDataToLibrary(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), playlistModel)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("add songs to playlist 333 " + response.code());
                        System.out.println("add songs to playlist 444 " + response.body());
                        switch (response.code()) {
                            case 201:
                                listener.onSuccess(null, null);
                                break;
                            case 200:
                                listener.onSuccess(null, null);
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }

        public void addSongsToPlaylist(int id, List<Integer> songsIdList, final APIListener<Void> listener) {
            HashMap<String, Object> request = new HashMap<>();
            request.put("playlist_id",id);
            request.put("songs",songsIdList);
        api.addSongsToPlaylist(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("add songs to playlist 333 " + response.code());
                        System.out.println("add songs to playlist 444 " + response.body());
                        switch (response.code()) {
                            case 201:
                                listener.onSuccess(null, null);
                                break;
                            case 200:
                                listener.onSuccess(null, null);
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }

    public void addSongsToTempTable(String sessionId, int id, String type, final APIListener<Void> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("sessionId",sessionId);
        request.put("refId",id);
        request.put("type",type);
        api.addSongsToTempTable(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("add songs to playlist 333 " + response.code());
                        System.out.println("add songs to playlist 444 " + response.body());
                        switch (response.code()) {
                            case 201:
                                listener.onSuccess(null, null);
                                break;
                            case 200:
                                listener.onSuccess(null, null);
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }

    public void loadPlaylistTempTable(String sessionId, int playlistId, final APIListener<Void> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("sessionId",sessionId);
        request.put("playlistId",playlistId);
        api.loadPlaylistTempTable(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("add songs to playlist 333 " + response.code());
                        System.out.println("add songs to playlist 444 " + response.body());
                        switch (response.code()) {
                            case 201:
                                listener.onSuccess(null, null);
                                break;
                            case 200:
                                listener.onSuccess(null, null);
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }

    public void saveEditedPlaylist(String playlistName, int playlistId, List<Integer> songsIdList, String playlistImage, final APIListener<Void> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("name",playlistName);
        request.put("playlist_id",playlistId);
        request.put("songs",songsIdList);
        request.put("image_base64",playlistImage);

        api.saveEditedPlaylist(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("add songs to playlist 333 " + response.code());
                        System.out.println("add songs to playlist 444 " + response.body());
                        switch (response.code()) {
                            case 201:
                                listener.onSuccess(null, null);
                                break;
                            case 200:
                                listener.onSuccess(null, null);
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }

    public void addDataToLibraryHash(String type,List<Integer> id,  final APIListener<Void> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("type",type);
        request.put("ids",id);
        api.addDataToLibraryHash(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("add songs to playlist 333 " + response.code());
                        System.out.println("add songs to playlist 444 " + response.body());
                        switch (response.code()) {
                            case 201:
                                listener.onSuccess(null, null);
                                break;
                            case 200:
                                listener.onSuccess(null, null);
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }

    public void removeDatafromLibrary(String type,List<Integer> id,  final APIListener<Void> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("type",type);
        request.put("ids",id);
        api.removeDatafromLibrary(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("add songs to playlist 333 " + response.code());
                        System.out.println("add songs to playlist 444 " + response.body());
                        switch (response.code()) {
                            case 201:
                                listener.onSuccess(null, null);
                                break;
                            case 200:
                                listener.onSuccess(null, null);
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }


    public void getAllPlaylist(final APIListener<List<PlayList>> listener) {
        api.getAllPlaylist(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), 0, 100).enqueue(new Callback<List<PlayList>>() {
            @Override
            public void onResponse(Call<List<PlayList>> call, Response<List<PlayList>> response) {

                System.out.println("checking getAllAudioChannels 3333  " + response.code());

                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<PlayList>> call, Throwable t) {

            }
        });
    }

    public void getPlaylistData(int id, final APIListener<PlayList> listener) {
        api.getPlaylistData(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), id , false ).enqueue(new Callback<PlayList>() {
            @Override
            public void onResponse(Call<PlayList> call, Response<PlayList> response) {

                System.out.println("checking getAllAudioChannels 3333  " + response.code());

                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<PlayList> call, Throwable t) {

            }
        });
    }


    public void createPlaylist(String name,String imageUrl, final APIListener<PlayList> listener) {

        HashMap<String, Object> request = new HashMap<>();
        request.put("name",name);
        request.put("image_base64",imageUrl);

        api.audioPlaylistCreate(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), "application/json", request)
                .enqueue(new Callback<PlayList>() {

                    @Override
                    public void onResponse(Call<PlayList> call, Response<PlayList> response) {
                        System.out.println("sjdjkdjdjdj 3333  " + response.code());
                        switch (response.code()) {
                            case 200:
                                if (response.body() != null) {
                                    listener.onSuccess(response.body(), null);
                                } else {
                                    listener.onFailure(new InvalidResponseException());
                                }

                                break;
                            case 201:
                                if (response.body() != null) {
                                    listener.onSuccess(response.body(), null);
                                } else {
                                    listener.onFailure(new InvalidResponseException());
                                }
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<PlayList> call, Throwable t) {
                        System.out.println("sjdjkdjdjdj 444  " + t.getLocalizedMessage());
                        System.out.println("sjdjkdjdjdj 555  " + t.getMessage());
                        System.out.println("sjdjkdjdjdj 666  " + t.toString());
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }

    public void removePlaylist(String id, final APIListener<Void> listener) {
        HashMap<String, Object> request = new HashMap<>();
        request.put("id",id);
        api.audioPlaylistRemove(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application),"application/json", request)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("add songs to playlist 333 " + response.code());
                        System.out.println("add songs to playlist 444 " + response.body());
                        switch (response.code()) {
                            case 201:
                                listener.onSuccess(null, null);
                                break;
                            case 200:
                                listener.onSuccess(null, null);
                                break;
                            case 401:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            case 400:
                                try {
                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
                                } catch (ErrorResponseException e) {
                                    listener.onFailure(e);
                                }
                                break;
                            default:
                                listener.onFailure(new RemoteServerException());
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Timber.e(t);
                        listener.onFailure(t);
                    }
                });
    }


//    public void addSongToPlaylist(int playlistId, ArrayList<Integer> songIds, final APIListener<Void> listener) {
//        api.addSongsToPlaylist(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), "application/json", playlistId, songIds)
//                .enqueue(new Callback<Void>() {
//
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//                        System.out.println("add songs to playlist 333 " + response.code());
//                        System.out.println("add songs to playlist 444 " + response.body());
//                        switch (response.code()) {
//                            case 201:
//                                listener.onSuccess(null, null);
//                                break;
//                            case 200:
//                                listener.onSuccess(null, null);
//                                break;
//                            case 401:
//                                try {
//                                    listener.onFailure(Utils.Error.getServerError(application, response, AuthenticationFailedWithAccessTokenException.class));
//                                } catch (ErrorResponseException e) {
//                                    listener.onFailure(e);
//                                }
//                                break;
//                            case 400:
//                                try {
//                                    listener.onFailure(Utils.Error.getServerError(application, response, ApplicationException.class));
//                                } catch (ErrorResponseException e) {
//                                    listener.onFailure(e);
//                                }
//                                break;
//                            default:
//                                listener.onFailure(new RemoteServerException());
//                                break;
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//                        Timber.e(t);
//                        listener.onFailure(t);
//                    }
//                });
//    }

    public void getSongsOfTempPlayList(final String sessionId, final APIListener<List<Song>> listener) {
        api.getSongsFromPlaylistTable(Utils.Auth.getBasicAuthToken(application), Utils.Auth.getBearerToken(application), sessionId).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                switch (response.code()) {
                    case 200:
                        if (response.body() != null) {
                            listener.onSuccess(response.body(), null);
                        } else {
                            listener.onFailure(new InvalidResponseException());
                        }
                        break;
                    case 401:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            AuthenticationFailedWithAccessTokenException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    case 400:
                        try {
                            listener.onFailure(Utils.Error.
                                    getServerError(application, response,
                                            ApplicationException.class));
                        } catch (ErrorResponseException e) {
                            listener.onFailure(e);
                        }
                        break;
                    default:
                        listener.onFailure(new RemoteServerException());
                        break;
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });
    }


}

