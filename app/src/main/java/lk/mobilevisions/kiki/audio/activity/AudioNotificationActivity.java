package lk.mobilevisions.kiki.audio.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.databinding.ActivityAudioNotificationBinding;
import lk.mobilevisions.kiki.modules.api.APIListener;
import lk.mobilevisions.kiki.modules.api.dto.Notification;
import lk.mobilevisions.kiki.modules.notifications.NotificationManager;
import lk.mobilevisions.kiki.ui.notifications.NotificationsAdapter;
import timber.log.Timber;

public class AudioNotificationActivity extends AppCompatActivity implements NotificationsAdapter.OnNotificationItemClickListener, View.OnClickListener {

    ActivityAudioNotificationBinding binding;
    NotificationsAdapter notificationsAdapter;
    LinearLayoutManager notificationsLayoutManager;

    @Inject
    NotificationManager notificationManager;
    Menu menu;
    private static final int NOTIFICATION_LIMIT = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_notification);
        ((Application) getApplication()).getInjector().inject(this);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.dialogBackground));
        }
        ((Application) getApplication()).getAnalytics().setCurrentScreen(this, "VideoNotificationActivity", null);
        binding.includedToolbar.titleTextview.setText(R.string.menu_notifications);
        binding.includedToolbar.backImageview.setOnClickListener(this);
        binding.clearAllButton.setOnClickListener(this);
        final ArrayList<Integer> readNotificationIds = new ArrayList<>();
        notificationManager.getNotifications(NOTIFICATION_LIMIT, new APIListener<List<Notification>>() {
            @Override
            public void onSuccess(List<Notification> notifications, List<Object> params) {
                System.out.println("fbdkfbfbhbffb " + notifications.size());
                binding.aviProgress.setVisibility(View.GONE);
                if (notifications.size() > 0) {
                    for (Notification notification : notifications) {
                        if (!notification.getRead()) {
                            readNotificationIds.add(notification.getMessageId());
                        }
                    }


                    notificationsLayoutManager = new LinearLayoutManager(AudioNotificationActivity.this);
                    binding.recycleViewNotification.setLayoutManager(notificationsLayoutManager);
                    notificationsAdapter = new NotificationsAdapter(AudioNotificationActivity.this, notifications, AudioNotificationActivity.this);
                    binding.recycleViewNotification.setAdapter(notificationsAdapter);
                    if (notifications.size() == 1) {
                        binding.recycleViewNotification.setVisibility(View.VISIBLE);
                        binding.textViewEmptyNotifications.setVisibility(View.GONE);
                    } else {
                        binding.clearAllButton.setVisibility(View.VISIBLE);
                        binding.recycleViewNotification.setVisibility(View.VISIBLE);
                        binding.textViewEmptyNotifications.setVisibility(View.GONE);
                    }

                } else {
                    binding.clearAllButton.setVisibility(View.GONE);

                    binding.recycleViewNotification.setVisibility(View.GONE);
                    binding.textViewEmptyNotifications.setVisibility(View.VISIBLE);
                }


                if (readNotificationIds.size() > 0) {
                    notificationManager.markNotificationAsRead(readNotificationIds, new APIListener<Void>() {
                        @Override
                        public void onSuccess(Void result, List<Object> params) {
                            Timber.d("Notifications markd as read %s", readNotificationIds);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Utils.Error.onServiceCallFail(AudioNotificationActivity.this, t);
                        }
                    });
                }


            }

            @Override
            public void onFailure(Throwable t) {
                Utils.Error.onServiceCallFail(AudioNotificationActivity.this, t);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actvity_menu, menu);

        this.menu = menu;


        menu.findItem(R.id.menuButtonNotifications).getActionView()
                .findViewById(R.id.menu_badge).setVisibility(View.GONE);

        MenuItem menuItemNotifications = menu.findItem(R.id.menuButtonNotifications);

        final FrameLayout buttonMenuNotifications = (FrameLayout) menuItemNotifications.getActionView().findViewById(R.id.menuBadgeRoot);
        buttonMenuNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menuButtonNotifications) {

            return true;
        }
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onNotificationItemClick(final View view, final Notification notification, final int position) {
        new MaterialDialog.Builder(AudioNotificationActivity.this)
                .title(getString(R.string.app_name))
                .content(R.string.delete_notification)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteNotificationById(position, notification.getMessageId());
                    }
                })
                .show();
    }

    private void deleteNotificationById(final int position, int notificationId) {
        binding.aviProgress.setVisibility(View.VISIBLE);
        notificationManager.clearNotification(notificationId,
                new APIListener<List<Notification>>() {
                    @Override
                    public void onSuccess(List<Notification> result, List<Object> params) {
                        binding.aviProgress.setVisibility(View.GONE);

                        if (position == RecyclerView.NO_POSITION) {
                            return;
                        }
                        int size = notificationsAdapter.removeItemAtPosition(position);
                        if (size == 1) {
                            binding.clearAllButton.setVisibility(View.GONE);
                        }
                        if (size == 0) {
                            binding.textViewEmptyNotifications.setVisibility(View.VISIBLE);
                        }


                    }

                    @Override
                    public void onFailure(Throwable t) {
                        binding.aviProgress.setVisibility(View.GONE);
                        Utils.Error.onServiceCallFail(AudioNotificationActivity.this, t);
                    }
                });
    }

    private void alertForAllDelete() {
        new MaterialDialog.Builder(AudioNotificationActivity.this)
                .title(getString(R.string.app_name))
                .content("Are you sure you want to delete all notifications?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteAllNotification();
                    }
                })
                .show();
    }

    private void deleteAllNotification() {
        binding.aviProgress.setVisibility(View.VISIBLE);
        notificationManager.clearAllNotifications(new APIListener<List<Notification>>() {
            @Override
            public void onSuccess(List<Notification> result, List<Object> params) {
                binding.aviProgress.setVisibility(View.GONE);
                int size = notificationsAdapter.removeAllPosition();
                if (size == 0) {
                    binding.textViewEmptyNotifications.setVisibility(View.VISIBLE);
                    binding.clearAllButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                binding.aviProgress.setVisibility(View.GONE);
                Utils.Error.onServiceCallFail(AudioNotificationActivity.this, t);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_imageview:
                finish();
                break;
            case R.id.clearAllButton:
                alertForAllDelete();
                break;
            default:
        }
    }
}
