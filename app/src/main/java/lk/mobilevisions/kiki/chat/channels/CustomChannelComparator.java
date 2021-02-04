package lk.mobilevisions.kiki.chat.channels;

import com.twilio.chat.Channel;

import java.util.Comparator;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;

public class CustomChannelComparator implements Comparator<Channel> {
  private String defaultChannelName;

  CustomChannelComparator() {
    defaultChannelName =
        Application.getInstance().getResources().getString(R.string.default_channel_name);
  }

  @Override
  public int compare(Channel lhs, Channel rhs) {
    if (lhs.getFriendlyName().contentEquals(defaultChannelName)) {
      return -100;
    } else if (rhs.getFriendlyName().contentEquals(defaultChannelName)) {
      return 100;
    }
    return lhs.getFriendlyName().toLowerCase().compareTo(rhs.getFriendlyName().toLowerCase());
  }
}
