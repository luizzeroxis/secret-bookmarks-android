package io.github.luizeldorado.secretbookmarks;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.IBinder;

import androidx.core.content.ContextCompat;

import java.io.IOException;

public class ServerService extends Service {
	WebServer webServer;
	BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// When pressing stop on notification
			Manager.setServePreference(context, false);
			Manager.setServeService(context, false);
		}
	};

	@Override
	public void onCreate() {
		webServer = new WebServer(getApplicationContext());
		try {
			webServer.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		IntentFilter filter = new IntentFilter("io.github.luizeldorado.secretbookmarks.STOP_SERVER");
		ContextCompat.registerReceiver(this, br, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
	}

	@Override
	public void onDestroy() {
		webServer.stop();
		webServer = null;

		unregisterReceiver(br);
	}

	public int onStartCommand(Intent i, int flags, int startId) {
		CharSequence name = "Service running";
		int importance = NotificationManager.IMPORTANCE_LOW;
		NotificationChannel channel = new NotificationChannel("0", name, importance);
		NotificationManager notificationManager = getSystemService(NotificationManager.class);
		notificationManager.createNotificationChannel(channel);

		// Intent for tapping notification
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent =
			PendingIntent.getActivity(this, 0, intent,
				PendingIntent.FLAG_IMMUTABLE);

		// Intent for tapping stop on notification
		Intent stopIntent = new Intent("io.github.luizeldorado.secretbookmarks.STOP_SERVER");
		PendingIntent stopPendingIntent =
			PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

		Notification notification =
			new Notification.Builder(this, "0")
				.setContentTitle("Secret bookmarks is running")
				.setSmallIcon(android.R.drawable.sym_def_app_icon)
				.setContentIntent(pendingIntent)
				.addAction(
					new Notification.Action.Builder(
						Icon.createWithResource(this, android.R.drawable.sym_def_app_icon),
						"Stop",
						stopPendingIntent)
						.build()
				)
				.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
				.build();

		startForeground(1, notification);

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}