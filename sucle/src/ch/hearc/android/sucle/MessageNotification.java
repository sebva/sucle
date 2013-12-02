package ch.hearc.android.sucle;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class for showing and canceling message notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class MessageNotification {	
	/**
	 * The unique identifier for this type of notification.
	 */
	private static final String NOTIFICATION_TAG = "Message";

	/**
	 * Shows the notification, or updates a previously shown notification of
	 * this type, with the given parameters.
	 * <p>
	 * TODO: Customize this method's arguments to present relevant content in
	 * the notification.
	 * <p>
	 * TODO: Customize the contents of this method to tweak the behavior and
	 * presentation of message notifications. Make sure to follow the <a
	 * href="https://developer.android.com/design/patterns/notifications.html">
	 * Notification design guidelines</a> when doing so.
	 * 
	 * @see #cancel(Context)
	 */
	public static void basicNotification(final Context context, final String textToShow)
	{
		basicNotification(context, textToShow, false);
	}
	
	public static void basicNotification(final Context context, final String textToShow, final boolean progressBar) {
		final Resources res = context.getResources();

		final Bitmap picture = BitmapFactory.decodeResource(res,
				R.drawable.ic_launcher);

		final String ticker = textToShow;
		final String title = res.getString(R.string.message_task_notification_title);
		final String text = textToShow;
		final NotificationCompat.Builder builder = progressBar ? notificationWithProgress(context, text, picture, ticker, title) : basicNotification(context, text, picture, ticker, title);
		
		notify(context, builder.build());
	}

	private static NotificationCompat.Builder basicNotification(final Context context, final String text, final Bitmap picture, final String ticker, final String title)
	{
		return new NotificationCompat.Builder(context)
		.setDefaults(Notification.DEFAULT_ALL)
		.setSmallIcon(R.drawable.ic_stat_message)
		.setContentTitle(title).setContentText(text)
		.setPriority(NotificationCompat.PRIORITY_DEFAULT)
		.setLargeIcon(picture)
		.setTicker(ticker)
		.setAutoCancel(true);
	}
	
	private static NotificationCompat.Builder notificationWithProgress(final Context context, final String text, final Bitmap picture, final String ticker, final String title)
	{
		return new NotificationCompat.Builder(context)
		.setDefaults(Notification.DEFAULT_ALL)
		.setSmallIcon(R.drawable.ic_stat_message)
		.setContentTitle(title).setContentText(text)
		.setPriority(NotificationCompat.PRIORITY_DEFAULT)
		.setLargeIcon(picture)
		.setTicker(ticker)
		.setAutoCancel(true)
		.setProgress(0, 0, true);
	}
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	private static void notify(final Context context,
			final Notification notification) {
		final NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			nm.notify(NOTIFICATION_TAG, 0, notification);
		} else {
			nm.notify(NOTIFICATION_TAG.hashCode(), notification);
		}
	}

	/**
	 * Cancels any notifications of this type previously shown using
	 * {@link #notifyBasicly(Context, String, int)}.
	 */
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static void cancel(final Context context) {
		final NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			nm.cancel(NOTIFICATION_TAG, 0);
		} else {
			nm.cancel(NOTIFICATION_TAG.hashCode());
		}
	}
}