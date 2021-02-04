package lk.mobilevisions.kiki.chat.channels;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.Channel.ChannelType;
import com.twilio.chat.ChannelDescriptor;
import com.twilio.chat.Channels;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Paginator;
import com.twilio.chat.StatusListener;
import com.twilio.chat.User;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.chat.ChatClientManager;
import lk.mobilevisions.kiki.chat.listeners.TaskCompletionListener;

public class ChannelManager implements ChatClientListener {
  private static ChannelManager sharedManager = new ChannelManager();
  public Channel generalChannel;
  private ChatClientManager chatClientManager;
  private ChannelExtractor channelExtractor;
  private List<Channel> channels;
  private Channels channelsObject;
  private ChatClientListener listener;
  private String defaultChannelName;
  private String defaultChannelUniqueName;
  private Handler handler;
  private Boolean isRefreshingChannels = false;

  private ChannelManager() {
    this.chatClientManager = Application.getInstance().getChatClientManager();
    this.channelExtractor = new ChannelExtractor();
    this.listener = this;
    defaultChannelName = getStringResource(R.string.default_channel_name);
//    defaultChannelUniqueName = getStringResource(R.string.default_channel_unique_name);
    handler = setupListenerHandler();
  }

  public static ChannelManager getInstance() {
    return sharedManager;
  }

  public List<Channel> getChannels() {
    return channels;
  }

  public String getDefaultChannelName() {
    return this.defaultChannelName;
  }

  public void leaveChannelWithHandler(Channel channel, StatusListener handler) {
    channel.leave(handler);
  }

  public void deleteChannelWithHandler(Channel channel, StatusListener handler) {
    channel.destroy(handler);
  }

  public void populateChannels(final LoadChannelListener listener) {
    System.out.println("djdjjdjjddjj 111111");
    if (this.chatClientManager == null) {
      System.out.println("djdjjdjjddjj 22222");
      return;
    }


    handler.post(new Runnable() {
      @Override
      public void run() {
        channelsObject = chatClientManager.getChatClient().getChannels();
        System.out.println("djdjjdjjddjj 3333 " + channelsObject);
        channelsObject.getPublicChannelsList(new CallbackListener<Paginator<ChannelDescriptor>>() {
          @Override
          public void onSuccess(Paginator<ChannelDescriptor> channelDescriptorPaginator) {
            System.out.println("djdjjdjjddjj 444444 " + channelsObject);
            extractChannelsFromPaginatorAndPopulate(channelDescriptorPaginator, listener);
          }
        });

      }
    });
  }

  private void extractChannelsFromPaginatorAndPopulate(final Paginator<ChannelDescriptor> channelsPaginator,
                                                       final LoadChannelListener listener) {
    channels = new ArrayList<>();
    ChannelManager.this.channels.clear();
    channelExtractor.extractAndSortFromChannelDescriptor(channelsPaginator,
        new TaskCompletionListener<List<Channel>, String>() {
      @Override
      public void onSuccess(List<Channel> channels) {
        System.out.println("djdjjdjjddjj 5555 " + channelsObject);
        ChannelManager.this.channels.addAll(channels);
        Collections.sort(ChannelManager.this.channels, new CustomChannelComparator());
        ChannelManager.this.isRefreshingChannels = false;
        chatClientManager.addClientListener(ChannelManager.this);
        listener.onChannelsFinishedLoading(ChannelManager.this.channels);
      }

      @Override
      public void onError(String errorText) {
        System.out.println("djdjjdjjddjj 66666 " + errorText);
      }
    });
  }

  public void createChannelWithName(String name, final StatusListener handler) {
    this.channelsObject
        .channelBuilder()
        .withFriendlyName(name)
        .withType(ChannelType.PUBLIC)
        .build(new CallbackListener<Channel>() {
          @Override
          public void onSuccess(final Channel newChannel) {
            handler.onSuccess();
          }

          @Override
          public void onError(ErrorInfo errorInfo) {
            handler.onError(errorInfo);
          }
        });
  }

  public void joinOrCreateGeneralChannelWithCompletion(final StatusListener listener) {
    channelsObject.getChannel(defaultChannelUniqueName, new CallbackListener<Channel>() {
      @Override
      public void onSuccess(Channel channel) {
        ChannelManager.this.generalChannel = channel;
        if (channel != null) {
          joinGeneralChannelWithCompletion(listener);
        } else {
          createGeneralChannelWithCompletion(listener);
        }
      }
    });
  }

  private void joinGeneralChannelWithCompletion(final StatusListener listener) {
    if (generalChannel.getStatus() == Channel.ChannelStatus.JOINED) {
      listener.onSuccess();
      return;
    }
    this.generalChannel.join(new StatusListener() {
      @Override
      public void onSuccess() {
        listener.onSuccess();
      }

      @Override
      public void onError(ErrorInfo errorInfo) {
        listener.onError(errorInfo);
      }
    });
  }

  private void createGeneralChannelWithCompletion(final StatusListener listener) {
    this.channelsObject
        .channelBuilder()
        .withFriendlyName(defaultChannelName)
        .withUniqueName(defaultChannelUniqueName)
        .withType(ChannelType.PUBLIC)
        .build(new CallbackListener<Channel>() {
          @Override
          public void onSuccess(final Channel channel) {
            ChannelManager.this.generalChannel = channel;
            ChannelManager.this.channels.add(channel);
            joinGeneralChannelWithCompletion(listener);
          }

          @Override
          public void onError(ErrorInfo errorInfo) {
            listener.onError(errorInfo);
          }
        });
  }

  public void setChannelListener(ChatClientListener listener) {
    this.listener = listener;
  }

  private String getStringResource(int id) {
    Resources resources = Application.getInstance().getResources();
    return resources.getString(id);
  }

  @Override
  public void onChannelAdded(Channel channel) {
    if (listener != null) {
      listener.onChannelAdded(channel);
    }
  }

  @Override
  public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {
    if (listener != null) {
      listener.onChannelUpdated(channel, updateReason);
    }
  }

  @Override
  public void onChannelDeleted(Channel channel) {
    if (listener != null) {
      listener.onChannelDeleted(channel);
    }
  }

  @Override
  public void onChannelSynchronizationChange(Channel channel) {
    if (listener != null) {
      listener.onChannelSynchronizationChange(channel);
    }
  }

  @Override
  public void onError(ErrorInfo errorInfo) {
    if (listener != null) {
      listener.onError(errorInfo);
    }
  }

  @Override
  public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {

  }

  @Override
  public void onChannelJoined(Channel channel) {

  }

  @Override
  public void onChannelInvited(Channel channel) {

  }

  @Override
  public void onUserUpdated(User user, User.UpdateReason updateReason) {
    if (listener != null) {
      listener.onUserUpdated(user, updateReason);
    }
  }

  @Override
  public void onUserSubscribed(User user) {

  }

  @Override
  public void onUserUnsubscribed(User user) {

  }

  @Override
  public void onNewMessageNotification(String s, String s1, long l) {

  }

  @Override
  public void onAddedToChannelNotification(String s) {

  }

  @Override
  public void onInvitedToChannelNotification(String s) {

  }

  @Override
  public void onRemovedFromChannelNotification(String s) {

  }

  @Override
  public void onNotificationSubscribed() {

  }

  @Override
  public void onNotificationFailed(ErrorInfo errorInfo) {

  }

  @Override
  public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {

  }

  @Override
  public void onTokenExpired() {
    refreshAccessToken();
  }

  @Override
  public void onTokenAboutToExpire() {
    refreshAccessToken();
  }

  private void refreshAccessToken() {
//    com.twilio.twiliochat.chat.accesstoken.AccessTokenFetcher accessTokenFetcher = chatClientManager.getAccessTokenFetcher();
//    accessTokenFetcher.fetch(new TaskCompletionListener<String, String>() {
//      @Override
//      public void onSuccess(String token) {
//        ChannelManager.this.chatClientManager.getChatClient().updateToken(token, new StatusListener() {
//          @Override
//          public void onSuccess() {
//            Log.d(TwilioChatApplication.TAG, "Successfully updated access token.");
//          }
//        });
//      }
//
//      @Override
//      public void onError(String message) {
//        Log.e(TwilioChatApplication.TAG,"Error trying to fetch token: " + message);
//      }
//    });
  }

  private Handler setupListenerHandler() {
    Looper looper;
    Handler handler;
    if ((looper = Looper.myLooper()) != null) {
      handler = new Handler(looper);
    } else if ((looper = Looper.getMainLooper()) != null) {
      handler = new Handler(looper);
    } else {
      throw new IllegalArgumentException("Channel Listener must have a Looper.");
    }
    return handler;
  }
}
