package lk.mobilevisions.kiki.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.Member;
import com.twilio.chat.Message;
import com.twilio.chat.Messages;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.chat.module.dto.ChatMember;
import lk.mobilevisions.kiki.databinding.ActivityChatMainBinding;


public class ChatActivity extends AppCompatActivity implements ChannelListener, ChatAdapter.OnChatItemClickListener {

    ActivityChatMainBinding binding;

    Context context;
    ChatAdapter chatAdapter;
    List<Message> messageArrayList = new ArrayList<>();
    List<ChatMember> chatMemberArrayList;
    LinearLayoutManager linearLayoutManager;
    Channel currentChannel;
    Messages messagesObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_main);

        setMessageInputEnabled(false);
        setUpListeners();
        setCurrentChannel();

        String chatImage = getIntent().getStringExtra("chatImage");
        binding.includedChatSub.onlineCountSize.setText(getIntent().getStringExtra("memberCount"));

        if (chatImage != null) {
            try {
                Picasso.with(getApplication()).load(URLDecoder.decode(chatImage, "UTF-8"))
                        .placeholder(R.drawable.program)
                        .into(binding.includedChatSub.inboxImageview);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        binding.includedChatSub.infoInboxToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ChatInfoActivity.class);
                startActivity(intent);
            }
        });
        binding.includedChatSub.mainChatBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.includedChatSub.backImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setCurrentChannel() {
        currentChannel = Application.getInstance().getCurrentChannel();
        setupMessages();
        if (currentChannel != null) {
            currentChannel.addListener(this);
            loadMessages();
        }

    }

    private void setupMessages() {

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        binding.listViewMessages.setLayoutManager(linearLayoutManager);
        binding.listViewMessages.setHasFixedSize(true);
//        binding.listViewMessages.(true);
        binding.listViewMessages.setItemViewCacheSize(1000);
        binding.listViewMessages.setDrawingCacheEnabled(true);
        chatAdapter = new ChatAdapter(this, messageArrayList, chatMemberArrayList = (ArrayList<ChatMember>) getIntent().getSerializableExtra("memberList"), ChatActivity.this);
        binding.listViewMessages.setAdapter(chatAdapter);
    }

    @Override
    public void onMessageAdded(Message message) {
        System.out.println("checking ss 212121 ");
        this.chatAdapter.addMessage(message);
        if (message.getAuthor().equals(String.valueOf(Application.getInstance().getAuthUser().getId()))) {
            System.out.println("hghghgh 22" + message.getAuthor());
            // If the current user is the sender of the message
            binding.listViewMessages.smoothScrollToPosition(chatAdapter.getItemCount());
        }

    }

    @Override
    public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {

    }

    @Override
    public void onMessageDeleted(Message message) {

    }

    @Override
    public void onMemberAdded(Member member) {

    }

    @Override
    public void onMemberUpdated(Member member, Member.UpdateReason updateReason) {

    }

    @Override
    public void onMemberDeleted(Member member) {

    }

    @Override
    public void onTypingStarted(Channel channel, Member member) {

    }

    @Override
    public void onTypingEnded(Channel channel, Member member) {

    }

    @Override
    public void onSynchronizationChanged(Channel channel) {
//        channel.getMembers().getMembersList().size();

    }

    private void setUpListeners() {
        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        binding.editTextMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
    }

    private void sendMessage() {
        String messageText = getTextInput();
        if (messageText.length() == 0) {
            return;
        }
        Message.Options messageOptions = Message.options().withBody(messageText);
        this.messagesObject.sendMessage(messageOptions, null);
        clearTextInput();
    }

    private void setMessageInputEnabled(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.buttonSend.setEnabled(enabled);
                binding.editTextMessage.setEnabled(enabled);
            }
        });
    }

    private String getTextInput() {
        return binding.editTextMessage.getText().toString();
    }

    private void clearTextInput() {
        binding.editTextMessage.setText("");
    }

    private void loadMessages() {

        this.messagesObject = this.currentChannel.getMessages();

        if (messagesObject != null) {
            setMessageInputEnabled(true);
//            binding.editTextMessage.requestFocus();
            System.out.println("checking ss 1919191 ");
            messagesObject.getLastMessages(150, new CallbackListener<List<Message>>() {
                @Override
                public void onSuccess(List<Message> messageList) {
                    System.out.println("checking ss 20202020 " + messageList.size());
                    messageArrayList = messageList;
                    chatAdapter.setList(messageArrayList);
                    binding.listViewMessages.smoothScrollToPosition(chatAdapter.getItemCount());
                }
            });
        }
//        ChatClientManager chatClientManager = Application.getInstance().getChatClientManager();
//        if(chatClientManager!=null){
//            System.out.println("checking ss 16161616 " );
//            com.twilio.chat.Channels channels = chatClientManager.getChatClient().getChannels();
//            if(channels!=null){
//                System.out.println("checking ss 17171717 " );
//                channels.getChannel(ssId, new CallbackListener<Channel>() {
//                    @Override
//                    public void onSuccess(com.twilio.chat.Channel channel) {
//                        System.out.println("checking ss 18181818 " );
//                        channel.addListener(ChatActivity.this);
//                        Messages messages = channel.getMessages();
//                        messagesObject = messages;
//                        if(messages!=null){
//                            System.out.println("checking ss 1919191 " );
//                            setMessageInputEnabled(true);
//                            binding.editTextMessage.requestFocus();
//                            messages.getLastMessages(100, new CallbackListener<List<Message>>() {
//                                @Override
//                                public void onSuccess(List<Message> messages) {
//                                    System.out.println("checking ss 20202020 " + messages.size());
//                                    messageArrayList = messages;
//                                    chatAdapter.setList(messageArrayList);
//                                }
//                            });
//                        }
//
//                    }
//                });
//            }
//        }
    }

    @Override
    public void onChatItemClick(Message message, int position, List<Message> messageList) {

    }
}
