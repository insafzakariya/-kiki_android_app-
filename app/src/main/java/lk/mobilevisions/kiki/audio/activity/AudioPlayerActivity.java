package lk.mobilevisions.kiki.audio.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;

import lk.mobilevisions.kiki.audio.model.dto.Song;

import lk.mobilevisions.kiki.databinding.ActivityAudioPlayerBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.tv.TvManager;

public class AudioPlayerActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener,
        View.OnClickListener {

    @Inject
    TvManager tvManager;

    ActivityAudioPlayerBinding binding;

    private List<Song> songList;
    private Song song;
    private SongAdapter infiniteAdapter;
    boolean flag = false;
    boolean favFlag = false;
    boolean turnedFlag = false;

    private int programId;
    private int songId;
    private int dailyMixId;
    private String programName;
    private String dailyMixName;
    private String songName;
    private String type;
    Date selectedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_player);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.dialogBackground));
        }
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
// get data via the key
        type = extras.getString("type");
        if (type != null) {
            if (type.equals("channel")) {
                programId = extras.getInt("programId");
                programName = extras.getString("programName");
                getAudioProgramSongs();
            } else if (type.equals("dailymix")) {
                dailyMixId = extras.getInt("dailyMixId");
                dailyMixName = extras.getString("dailyMixName");
                getAudioDailyMixSongs();
            } else if (type.equals("song")) {
                songId = extras.getInt("songId");
                songName = extras.getString("songName");
                getAudioSong();
            }
        }


        binding.songPicker.setOrientation(DSVOrientation.HORIZONTAL);
        binding.songPicker.addOnItemChangedListener(this);
        binding.songPicker.setItemTransitionTimeMillis(600);
        binding.songPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());


        binding.imageView22.setBackgroundResource(R.drawable.ic_audio_thumb_outlined);
// when you click this demo button
        binding.imageView22.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (favFlag) {
                    binding.imageView22.setBackgroundResource(R.drawable.ic_audio_thumb_outlined);
                    favFlag = false;
                } else {
                    binding.imageView22.setBackgroundResource(R.drawable.ic_audio_thumb_filled);
                    favFlag = true;
                }
            }
        });


        binding.imageView25.setBackgroundResource(R.drawable.audio_turned_in_not);
// when you click this demo button
        binding.imageView25.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (turnedFlag) {
                    binding.imageView25.setBackgroundResource(R.drawable.audio_turned_in_not);
                    turnedFlag = false;
                } else {
                    binding.imageView25.setBackgroundResource(R.drawable.audio_turned_in);
                    turnedFlag = true;
                }
            }
        });

        binding.imageView24.setBackgroundResource(R.drawable.audio_play);
// when you click this demo button
        binding.imageView24.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!flag) {
                    binding.imageView24.setBackgroundResource(R.drawable.audio_pause);
                    flag = true;
                } else {
                    binding.imageView24.setBackgroundResource(R.drawable.audio_play);
                    flag = false;
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void onItemChanged(Song item) {
        changeRateButtonState(item);
    }

    private void changeRateButtonState(Song item) {

        binding.audioName.setText(item.getName());
        binding.audioDes.setText(item.getDescription());

    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int position) {
        onItemChanged(songList.get(position));
    }

    public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

        private List<Song> data;

        public SongAdapter(List<Song> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.audio_player_list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Song current = data.get(position);
            try {
                Picasso.with(AudioPlayerActivity.this).load(URLDecoder.decode(current.getUrl(), "UTF-8"))
                        .placeholder(R.drawable.program)
                        .into(holder.image);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
            }
        }
    }

    private void getAudioProgramSongs() {
        if (selectedDate == null) {
            selectedDate = Utils.DateUtil.getDateOnly(new Date());
        }
        Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        if (dateToSend == null) {
            dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        }
        tvManager.getAudioProgramsSongs(programId, dateToSend, dateToSend, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {
                songList = songs;
                binding.songPicker.setAdapter(new SongAdapter(songList));
                onItemChanged(songList.get(2));
                if (songs.size() <= 0) {
                    binding.songPicker.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {

                    binding.songPicker.setVisibility(View.VISIBLE);
                    binding.aviProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(AudioPlayerActivity.this, t);
            }
        });
    }

    private void getAudioSong() {
        if (selectedDate == null) {
            selectedDate = Utils.DateUtil.getDateOnly(new Date());
        }
        Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        if (dateToSend == null) {
            dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        }
        tvManager.getAudioSong(songId, dateToSend, dateToSend, new APIListener<Song>() {
            @Override
            public void onSuccess(Song songs, List<Object> params) {
                song = songs;
                songList = new ArrayList<Song>();
                songList.add(songs);
                binding.songPicker.setAdapter(new SongAdapter(songList));
                onItemChanged(song);
                if (songs == null) {
                    binding.songPicker.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {

                    binding.songPicker.setVisibility(View.VISIBLE);
                    binding.aviProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(AudioPlayerActivity.this, t);
            }
        });
    }

    private void getAudioDailyMixSongs() {
        if (selectedDate == null) {
            selectedDate = Utils.DateUtil.getDateOnly(new Date());
        }
        Date dateToSend = selectedDate.before(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        if (dateToSend == null) {
            dateToSend = selectedDate.after(Utils.DateUtil.getDateOnly(new Date())) ? selectedDate : null;
        }
        tvManager.getAudioDailymixSongs(dailyMixId, dateToSend, dateToSend, new APIListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> songs, List<Object> params) {
                songList = songs;
                binding.songPicker.setAdapter(new SongAdapter(songList));
                onItemChanged(songList.get(0));
                if (songs.size() <= 0) {
                    binding.songPicker.setVisibility(View.GONE);
                    binding.aviProgress.setVisibility(View.GONE);

                } else {

                    binding.songPicker.setVisibility(View.VISIBLE);
                    binding.aviProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(AudioPlayerActivity.this, t);
            }
        });
    }
}
