package lk.mobilevisions.kiki.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.chat.module.ChatManager;
import lk.mobilevisions.kiki.chat.module.dto.ChatMember;
import lk.mobilevisions.kiki.databinding.ActivityChatInfoBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;

public class ChatInfoActivity extends AppCompatActivity implements View.OnClickListener, ChatInfoAdapter.OnChatInfoItemClickListener {

    ActivityChatInfoBinding binding;
    @Inject
    ChatManager chatManager;
    ChatInfoAdapter chatInfoAdapter;
    LinearLayoutManager channelsLayoutManager;
    List<ChatMember> chatMemberArrayList = new ArrayList<>();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_info);
        ((Application) getApplication()).getInjector().inject(this);

        channelsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        binding.onlineArtistRecycleview.setLayoutManager(channelsLayoutManager);
        chatInfoAdapter = new ChatInfoAdapter(this, chatMemberArrayList, ChatInfoActivity.this);
        binding.onlineArtistRecycleview.setHasFixedSize(true);
        binding.onlineArtistRecycleview.setItemViewCacheSize(50);
        binding.onlineArtistRecycleview.setDrawingCacheEnabled(true);
        binding.onlineArtistRecycleview.setAdapter(chatInfoAdapter);

        binding.includedToggle.infoToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
        }
        });

        int chatId = getIntent().getIntExtra("chatId",0);

        setChatMembers(chatId);
    }

    private void setChatMembers(int chatID) {


        chatManager.getChatMembers(chatID, "channel_admin", new APIListener<List<ChatMember>>() {
            @Override
            public void onSuccess(List<ChatMember> result, List<Object> params) {
                chatMemberArrayList = result;
                chatInfoAdapter.setList(chatMemberArrayList);

                binding.memberCount.setText("Online Artist " + result.size());


            }

            @Override
            public void onFailure(Throwable t) {


            }
        });


    }


    @Override
    public void onClick(View v) {

    }
}
