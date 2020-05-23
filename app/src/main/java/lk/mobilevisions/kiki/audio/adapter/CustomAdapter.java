package lk.mobilevisions.kiki.audio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.audio.activity.AudioDashboardActivity;
import lk.mobilevisions.kiki.audio.model.dto.PlayList;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.audio.player.AudioStreamingManager;
import lk.mobilevisions.kiki.audio.util.SharedPrefrenceUtils;
import lk.mobilevisions.kiki.audio.widgets.AnimatedExpandableListView;
import lk.mobilevisions.kiki.modules.api.dto.Channel;

public class CustomAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private Context context;
    private List<PlayList> playLists;
    OnAudioPlaylistItemClickListener onAudioPlaylistItemClickListener;
    private SharedPrefrenceUtils utils;
    private AudioStreamingManager streamingManager;
    public CustomAdapter(Context context, OnAudioPlaylistItemClickListener onAudioPlaylistItemClickListener) {
        this.context = context;
        streamingManager = AudioStreamingManager.getInstance(context);
        utils = SharedPrefrenceUtils.getInstance(context);
        playLists = new ArrayList<>();
        this.onAudioPlaylistItemClickListener = onAudioPlaylistItemClickListener;
    }


    public void addPlayList(PlayList playList) {
        playLists.add(playList);
        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<Song> productList = playLists.get(groupPosition).getSongsList();
        return productList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Song song = (Song) getChild(groupPosition, childPosition);
        System.out.println("efbhkefbhiefbbf ****  " + song.getUrl());
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.play_list_child_item, null);
        }
        TextView songNameTextview = (TextView) convertView.findViewById(R.id.textView8);
        TextView songDesTextview = (TextView) convertView.findViewById(R.id.textView9);
        ImageView songImageview = (ImageView) convertView.findViewById(R.id.imageView8);
        final ImageView pauseImageView = (ImageView) convertView.findViewById(R.id.pause_imageview);
        final ImageView playButton = (ImageView) convertView.findViewById(R.id.imageView11);
//        if(utils.getCurrentSongId() == song.getId()){
//
//            if(streamingManager!=null && streamingManager.isPlaying()){
//                pauseImageView.setVisibility(View.VISIBLE);
//                playButton.setVisibility(View.GONE);
//            }
//
//
//
//
//        }else{
//            pauseImageView.setVisibility(View.GONE);
//            playButton.setVisibility(View.VISIBLE);
//        }
        songNameTextview.setText(song.getName().trim());
        songDesTextview.setText(song.getDescription().trim());
        try {
            Picasso.with(context).load(URLDecoder.decode(song.getImage(), "UTF-8"))
                    .placeholder(R.drawable.header_logo)
                    .into(songImageview);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                if(utils.getCurrentSongId() == song.getId()){
//
//                        onAudioPlaylistItemClickListener.onAudioSongsPauseSongItemClick(song,
//                                groupPosition);
//
//                }else{
//                    utils.setCurrentSongId(song.getId());
                    onAudioPlaylistItemClickListener.onAudioSongsPlaySongItemClick(song,
                            groupPosition);
//                }
//
//
//
//                pauseImageView.setVisibility(View.VISIBLE);
//                playButton.setVisibility(View.GONE);

            }
        });
//        pauseImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onAudioPlaylistItemClickListener.onAudioSongsPauseSongItemClick(song,
//                        groupPosition);
//                utils.setCurrentSongId(0);
//                pauseImageView.setVisibility(View.GONE);
//                playButton.setVisibility(View.VISIBLE);
//
//            }
//        });

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {

        List<Song> songsList = playLists.get(groupPosition).getSongsList();
        return songsList.size();
    }


    @Override
    public Object getGroup(int groupPosition) {
        return playLists.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return playLists.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        PlayList playList = (PlayList) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.play_list_header_item, null);
        }
        ImageView indicatorIcon = (ImageView) view.findViewById(R.id.imageView9);
        if (isLastChild) {
            indicatorIcon.setImageResource(R.drawable.ic_collapse_arrow);
        } else {
            indicatorIcon.setImageResource(R.drawable.ic_expand_arrow);
        }
        TextView playlistName = (TextView) view.findViewById(R.id.textView8);
        ImageView playButton = (ImageView) view.findViewById(R.id.imageView11);
        playlistName.setText(playList.getName().trim());
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAudioPlaylistItemClickListener.onAudioSongsPlayListItemClick(playLists.get(groupPosition),
                        groupPosition);

            }
        });


        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public interface OnAudioPlaylistItemClickListener {

        public void onAudioSongsPlayListItemClick(PlayList playList, int position);

        public void onAudioSongsPlaySongItemClick(Song song, int position);
        public void onAudioSongsPauseSongItemClick(Song song, int position);
    }
}