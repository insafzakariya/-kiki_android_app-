/**
 * Created by Chatura Dilan Perera on 30/1/2017.
 */
package lk.mobilevisions.kiki.ui.notifications;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.app.Utils;
import lk.mobilevisions.kiki.modules.api.dto.Notification;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    List<Notification> notifications;
    private Context context;
    private final SparseBooleanArray mCollapsedStatus;
    OnNotificationItemClickListener itemClickListener;
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView messageTextview;
        public AppCompatImageView deleteNotification;
        public View rootView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.messageTitleTextview);
            messageTextview = (TextView) view.findViewById(R.id.messageTextView);
            deleteNotification = (AppCompatImageView) view.findViewById(R.id.delete_notification);
            rootView = view;
        }
    }

    public NotificationsAdapter(Context context, List<Notification> notifications, OnNotificationItemClickListener itemClickListener) {
        this.context = context;
        this.notifications = notifications;
        mCollapsedStatus = new SparseBooleanArray();
        this.itemClickListener = itemClickListener;
    }
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_listitem_notification, parent, false);
        NotificationsAdapter.ViewHolder viewHolder = new NotificationsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NotificationsAdapter.ViewHolder holder, int position) {
        holder.messageTextview.setText(notifications.get(position).getMessage());
        holder.titleTextView.setText(Utils.DateUtil.getDisplayableDate(Utils.DateUtil.getDateFromString(
                notifications.get(position).getMessageDate())));
        if(!notifications.get(position).getRead()){
            holder.messageTextview.setTypeface(null, Typeface.BOLD);
        }else{
            holder.rootView.setAlpha(0.7f);
        }

        holder.deleteNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onNotificationItemClick(view, notifications.get(holder.getAdapterPosition()),
                        holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
    public int removeItemAtPosition(int position) {
        notifications.remove(position);
        notifyItemRemoved(position);
        return notifications.size();
    }
    public int removeAllPosition() {
        notifications.clear();
        notifyDataSetChanged();
        return notifications.size();
    }
    public interface OnNotificationItemClickListener {
        public void onNotificationItemClick(View view, Notification notification, int position);
    }
}