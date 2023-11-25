package io.github.luizeldorado.secretbookmarks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class Manager {
	static boolean getServePreference(Context context) {
		SharedPreferences sharedPref = context.getSharedPreferences(
			"io.github.luizeldorado.secretbookmarks.PREFERENCES", Context.MODE_PRIVATE);
		return sharedPref.getBoolean("serve", false);
	}
	static void setServePreference(Context context, boolean serve) {
		SharedPreferences sharedPref = context.getSharedPreferences(
			"io.github.luizeldorado.secretbookmarks.PREFERENCES", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean("serve", serve);
		editor.apply();
	}

	static void setServeService(Context context, boolean serve) {
		// Start/stop service
		if (serve) {
			context.startForegroundService(new Intent(context, ServerService.class));
		} else {
			context.stopService(new Intent(context, ServerService.class));
		}
	}
}
