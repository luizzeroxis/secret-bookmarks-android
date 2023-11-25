package io.github.luizeldorado.secretbookmarks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

	SwitchCompat switchServe;
	BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		// When pressing stop on notification
		setServeSwitch(false);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		switchServe = findViewById(R.id.switch1);

		switchServe.setOnCheckedChangeListener(this::onSwitchCheckedChanged);
	}

	@Override
	protected void onStart() {
		super.onStart();

		boolean serve = Manager.getServePreference(this);
		setServeSwitch(serve);

		// In case app was closed
		Manager.setServeService(this, serve);

		IntentFilter filter = new IntentFilter("io.github.luizeldorado.secretbookmarks.STOP_SERVER");
		ContextCompat.registerReceiver(this, br, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(br);
	}

	private void onSwitchCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Manager.setServePreference(this, isChecked);
		Manager.setServeService(this, isChecked);
	}

	public void setServeSwitch(boolean serve) {
		switchServe.setOnCheckedChangeListener(null);
		switchServe.setChecked(serve);
		switchServe.setOnCheckedChangeListener(this::onSwitchCheckedChanged);
	}
}