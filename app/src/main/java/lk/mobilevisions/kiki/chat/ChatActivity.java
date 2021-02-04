package lk.mobilevisions.kiki.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.Member;
import com.twilio.chat.Message;
import com.twilio.chat.Messages;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.audio.adapter.SearchSuggestionAdapter;
import lk.mobilevisions.kiki.audio.adapter.YouMightAlsoLikeAdapter;
import lk.mobilevisions.kiki.audio.fragment.YouMightAlsoLikeFragment;
import lk.mobilevisions.kiki.audio.model.dto.Song;
import lk.mobilevisions.kiki.chat.messages.JoinedStatusMessage;
import lk.mobilevisions.kiki.chat.messages.LeftStatusMessage;
import lk.mobilevisions.kiki.chat.messages.MessageAdapter;
import lk.mobilevisions.kiki.chat.messages.StatusMessage;
import lk.mobilevisions.kiki.databinding.ActivityChatMainBinding;

import static com.androidquery.util.AQUtility.getContext;

public class ChatActivity extends AppCompatActivity implements ChannelListener, ChatAdapter.OnChatItemClickListener {

    ActivityChatMainBinding binding;

    Context context;



//    MessageAdapter messageAdapter;
    ChatAdapter chatAdapter;
    List<Message> messageArrayList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    Channel currentChannel;
    Messages messagesObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_main);


//        messageAdapter = new MessageAdapter(mainActivity);
//        messagesListView.setAdapter(messageAdapter);
        setupMessages();
        setUpListeners();
        setMessageInputEnabled(false);
        String sSID = getIntent().getStringExtra("SSID");
        System.out.println("checking ss 1515151 " + sSID);
        loadMessages(sSID);

    }

    private void setupMessages(){

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.listViewMessages.setLayoutManager(linearLayoutManager);
        binding.listViewMessages.setHasFixedSize(true);
//        binding.listViewMessages.(true);
        binding.listViewMessages.setItemViewCacheSize(1000);
        binding.listViewMessages.setDrawingCacheEnabled(true);
        chatAdapter = new ChatAdapter(this, messageArrayList, ChatActivity.this);
        binding.listViewMessages.setAdapter(chatAdapter);
    }

    @Override
    public void onMessageAdded(Message message) {
        System.out.println("checking ss 212121 " );
        this.chatAdapter.addMessage(message);
    }

    @Override
    public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {

    }

    @Override
    public void onMessageDeleted(Message message) {

    }

    @Override
    public void onMemberAdded(Member member) {
        System.out.println("checking ss 2323232 " );
//        StatusMessage statusMessage = new JoinedStatusMessage(member.getIdentity());
//        this.chatAdapter.addMessage(statusMessage);
    }

    @Override
    public void onMemberUpdated(Member member, Member.UpdateReason updateReason) {
//        StatusMessage statusMessage = new LeftStatusMessage(member.getIdentity());
//        this.messageAdapter.addStatusMessage(statusMessage);
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

    private void loadMessages(String ssId){
        ChatClientManager chatClientManager = Application.getInstance().getChatClientManager();
        if(chatClientManager!=null){
            System.out.println("checking ss 16161616 " );
            com.twilio.chat.Channels channels = chatClientManager.getChatClient().getChannels();
            if(channels!=null){
                System.out.println("checking ss 17171717 " );
                channels.getChannel(ssId, new CallbackListener<Channel>() {
                    @Override
                    public void onSuccess(com.twilio.chat.Channel channel) {
                        System.out.println("checking ss 18181818 " );
                        channel.addListener(ChatActivity.this);
                        Messages messages = channel.getMessages();
                        messagesObject = messages;
                        if(messages!=null){
                            System.out.println("checking ss 1919191 " );
                            setMessageInputEnabled(true);
                            binding.editTextMessage.requestFocus();
                            messages.getLastMessages(100, new CallbackListener<List<Message>>() {
                                @Override
                                public void onSuccess(List<Message> messages) {
                                    System.out.println("checking ss 20202020 " + messages.size());
                                    messageArrayList = messages;
                                    chatAdapter.setList(messageArrayList);
                                }
                            });
                        }

                    }
                });
            }
        }
    }

    @Override
    public void onChatItemClick(Message message, int position, List<Message> messageList) {

    }
}
