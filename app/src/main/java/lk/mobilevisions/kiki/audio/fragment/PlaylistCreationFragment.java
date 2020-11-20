package lk.mobilevisions.kiki.audio.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.adapter.PlaylistCreationAdapter;
import lk.mobilevisions.kiki.audio.event.UserNavigateBackEvent;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.databinding.NewPlaylistCreationBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;
import lk.mobilevisions.kiki.ui.auth.UserLoginFragment;

import static android.app.Activity.RESULT_OK;

public class PlaylistCreationFragment extends Fragment implements PlaylistCreationAdapter.OnPlaylistCreationItemClickListener {

    NewPlaylistCreationBinding binding;
    PlaylistCreationAdapter playlistCreationAdapter;
    @Inject
    TvManager tvManager;
    LinearLayoutManager channelsLayoutManager;
    private List<Integer> songIdList = new ArrayList<>();
    private List<Song> playlistSongsList = new ArrayList<>();
    private int playlistId;
    private Uri selectedImageUri;
    private String encodedImage = "";
    private boolean editPlaylist;
    private int editPlaylistID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.new_playlist_creation, container, false);
        ((Application) getActivity().getApplication()).getInjector().inject(this);
        Application.BUS.register(this);
        Application.getInstance().addSessionId(null);
        Application.getInstance().clearPlayList();
        Application.getInstance().clearPlayListIds();

        binding.addSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LibrarySongSelectionFragment librarySongSelectionFragment = new LibrarySongSelectionFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.playlist_creation_container, librarySongSelectionFragment, "librarySongSelection")
                        .addToBackStack(null)
                        .commit();
            }
        });


        editPlaylist = getArguments().getBoolean("editPlaylist");
        editPlaylistID = getArguments().getInt("editPlaylistID");
        String editPlaylistName = getArguments().getString("editPlaylistName");
        String editPlaylistImage = getArguments().getString("editPlaylistImage");

        if (editPlaylist) {

            loadPlaylistTempTable(editPlaylistID);

        } else {
            String sessionId;
            sessionId = Application.getInstance().getSessionId();
            getTempPlaylistSongs(sessionId);
        }

        if (editPlaylist) {

            binding.editPlaylistName.setText(editPlaylistName);
            if (editPlaylistImage != null) {
                try {
                    Picasso.with(getActivity()).load(URLDecoder.decode(editPlaylistImage, "UTF-8"))
                            .placeholder(R.drawable.ic_image_camera)
                            .fit()
                            .into(binding.customPlaylistImage);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        }

        channelsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.addedSongsRecycle.setLayoutManager(channelsLayoutManager);
//        playlistCreationAdapter = new PlaylistCreationAdapter(getActivity(), playlistSongsList, PlaylistCreationFragment.this);
        binding.addedSongsRecycle.setHasFixedSize(true);
        binding.addedSongsRecycle.setItemViewCacheSize(50);
        binding.addedSongsRecycle.setDrawingCacheEnabled(true);
        binding.addedSongsRecycle.setAdapter(playlistCreationAdapter);

        binding.editTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.editPlaylistName.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.editPlaylistName, InputMethodManager.SHOW_IMPLICIT);

            }
        });


        binding.cancelTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();
            }
        });

        binding.customPlaylistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });


        binding.confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPlaylist) {
                    binding.confirmText.setEnabled(true);

                    String name = binding.editPlaylistName.getText().toString();

                    if (selectedImageUri != null && !name.isEmpty()) {

                        saveEditedPlaylist(name, encodedImage);

                    } else if (selectedImageUri == null && !name.isEmpty()) {

                        saveEditedPlaylist(name, "data:image/png;base64,null");

                    } else if (selectedImageUri != null && name.isEmpty()) {

                        saveEditedPlaylist("New playlist", encodedImage);

                    } else {

                        saveEditedPlaylist("New playlist", "data:image/png;base64,null");
                    }
                    binding.confirmText.setEnabled(false);
                } else {
                    System.out.println("hgfjghfjghfjgh 1111111 " );
                    binding.confirmText.setEnabled(true);
                    binding.aviPlaylist.setVisibility(View.VISIBLE);

                    String name = binding.editPlaylistName.getText().toString();

                    if (selectedImageUri != null && !name.isEmpty()) {

                        createPlayList(name, encodedImage);

                    } else if (selectedImageUri == null && !name.isEmpty()) {

                        createPlayList(name, "data:image/png;base64,null");

                    } else if (selectedImageUri != null && name.isEmpty()) {

                        createPlayList("New playlist", encodedImage);

                    } else {

                        createPlayList("New playlist", "data:image/png;base64,null");
                    }
                    binding.confirmText.setEnabled(false);
                }
            }
        });


        return binding.getRoot();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedImageUri = imageReturnedIntent.getData();
                    binding.customPlaylistImage.setImageURI(selectedImageUri);
                    final InputStream imageStream;
                    try {
                        imageStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        encodedImage = encodeImage(selectedImage);
                        encodedImage = "data:image/png;base64," + encodedImage;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    private void getTempPlaylistSongs(String sessionId) {
//        binding.aviPlaylist.setVisibility(View.VISIBLE);
        binding.confirmText.setEnabled(false);
        binding.disableConfirmText.setVisibility(View.VISIBLE);
        songIdList.clear();
        tvManager.getSongsOfTempPlayList(sessionId, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {
                for (Song song : songs) {
                    songIdList.add(song.getId());
                }
                System.out.println("songcount get " + songIdList.size());
                playlistSongsList = songs;
                if (editPlaylist) {
                    for (Song song : playlistSongsList) {
                        if (!Application.getInstance().getSongsAddedToPlaylist().contains(song.getId())) {
                            Application.getInstance().addSongToPlayList(song.getId());
                        }
                    }
                }
                binding.addedSongsRecycle.setAdapter(new PlaylistCreationAdapter(getContext(),
                        playlistSongsList, PlaylistCreationFragment.this));
                if (songs.size() <= 0) {
                    binding.addedSongsRecycle.setVisibility(View.GONE);

                } else {
                    binding.addedSongsRecycle.setVisibility(View.VISIBLE);
                }
                binding.aviPlaylist.setVisibility(View.GONE);
                binding.confirmText.setEnabled(true);
                binding.disableConfirmText.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.editPlaylistName.getWindowToken(), 0);
    }


    private void createPlayList(String name, String imageUrl) {

        tvManager.createPlaylist(name, imageUrl, new APIListener<PlayList>() {

            @Override
            public void onSuccess(PlayList playList, List<Object> params) {
                playlistId = playList.getId();
                addSongsToPlaylist(playlistId, songIdList);
                Toast.makeText(getActivity(), getResources().getString(R.string.playlist_created), Toast.LENGTH_SHORT).show();
                hideKeyboard();

                binding.aviPlaylist.setVisibility(View.GONE);

                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void addSongsToPlaylist(int playlistId, List<Integer> songIdList) {
        System.out.println("songcount " + songIdList.size());
        tvManager.addSongsToPlaylist(playlistId, songIdList, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {


            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void saveEditedPlaylist(String playlistName, String playlistImage) {

        tvManager.saveEditedPlaylist(playlistName, editPlaylistID, songIdList, playlistImage, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {
                songIdList.size();
                Toast.makeText(getActivity(), "Your playlist edited successfully.", Toast.LENGTH_SHORT).show();
                AudioDashboardActivity hhh = (AudioDashboardActivity) getActivity();
                hhh.onBackPressed();

            }

            @Override
            public void onFailure(Throwable t) {

                Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPlaylistTempTable(int editPlaylistID) {

        String sessionId = null;
        if (Application.getInstance().getSessionId() != null) {
            sessionId = Application.getInstance().getSessionId();
        } else {
            sessionId = UUID.randomUUID().toString();
            Application.getInstance().addSessionId(sessionId);
        }
        tvManager.loadPlaylistTempTable(sessionId, editPlaylistID, new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {
                getTempPlaylistSongs(Application.getInstance().getSessionId());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    @Override
    public void onPlaylistCreationItemClick(Song song, int position, List<Song> songs) {

        songIdList.remove(position);
        songIdList.size();
        Toast.makeText(getActivity(),  getResources().getString(R.string.audio_removed), Toast.LENGTH_SHORT).show();

    }

    private void addSongsToTempPlaylist(int songId) {
        String sessionId = null;
        if (Application.getInstance().getSessionId() != null) {
            sessionId = Application.getInstance().getSessionId();
        } else {
            sessionId = UUID.randomUUID().toString();
            Application.getInstance().addSessionId(sessionId);
        }
        tvManager.addSongsToTempTable(sessionId, songId, "S", new APIListener<Void>() {
            @Override
            public void onSuccess(Void result, List<Object> params) {


            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(getActivity(), t);
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Application.BUS.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Subscribe
    public void onUserNavigateBack(UserNavigateBackEvent event) {

        if (Application.getInstance().getSessionId() != null) {
            getTempPlaylistSongs(Application.getInstance().getSessionId());
            System.out.println("sdjfnsdjfndjfjdfjvn 111 ");
            binding.aviPlaylist.setVisibility(View.VISIBLE);
        }

    }

}
