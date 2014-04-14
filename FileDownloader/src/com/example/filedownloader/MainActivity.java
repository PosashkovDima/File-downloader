package com.example.filedownloader;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private Button buttonAction;
	private TextView textViewStatus;
	private ProgressBar mProgressBar;

	private static final String DOWNLOADED_IMAGE_NAME = "downloadedImage.jpg";
	private static final String IS_DOWNLOADING = "is_downloading";
	private boolean isDownloading = false;
	private static final String IS_DOWNLOADED = "is_downloaded";
	private boolean isDownloaded = false;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {

				int resultCode = bundle.getInt(DownloadService.RESULT);
				if (resultCode == RESULT_OK) {
					setStatusDownloaded();
					setOnClickeListnerOpen();
				} else {
					setStatusIdle();
				}
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, new IntentFilter(
				DownloadService.NOTIFICATION));
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonAction = (Button) findViewById(R.id.buttonAction);
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	public void onClickDownload(View view) {

		Intent intent = new Intent(this, DownloadService.class);

		intent.putExtra(DownloadService.FILENAME, DOWNLOADED_IMAGE_NAME);
		intent.putExtra(DownloadService.URL, getString(R.string.url_img));
		startService(intent);

		setStatusDownloading();
	}

	private void setOnClickeListnerOpen() {
		buttonAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String imagePath = Environment.getExternalStorageDirectory()
						+ "/download/" + DOWNLOADED_IMAGE_NAME;
				File file = new File(imagePath);

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "image/*");
				startActivity(intent);
				mProgressBar.setVisibility(4);
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(IS_DOWNLOADING, isDownloading);
		outState.putBoolean(IS_DOWNLOADED, isDownloaded);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.getBoolean(IS_DOWNLOADING)) {
			setStatusDownloading();
		} else if (savedInstanceState.getBoolean(IS_DOWNLOADED)) {
			setStatusDownloaded();
			setOnClickeListnerOpen();
		} else {
			setStatusIdle();
		}
	}

	private void setStatusDownloading() {
		buttonAction.setText("Downloading");
		textViewStatus.setText("Status: Downloading");
		isDownloading = true;
		isDownloaded = false;
		mProgressBar.setVisibility(1);
		buttonAction.setEnabled(false);
	}

	private void setStatusDownloaded() {
		buttonAction.setText("Open");
		textViewStatus.setText("Status: Downloaded");
		isDownloading = false;
		isDownloaded = true;
		buttonAction.setEnabled(true);
		mProgressBar.setVisibility(4);
	}

	private void setStatusIdle() {
		buttonAction.setText("Download");
		textViewStatus.setText("Status: Idle");
		isDownloading = false;
		isDownloaded = false;
		buttonAction.setEnabled(true);
		mProgressBar.setVisibility(4);
	}
}