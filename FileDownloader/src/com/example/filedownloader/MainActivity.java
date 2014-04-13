package com.example.filedownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button buttonAction;
	private TextView textViewStatus;

	private static final String DOWNLOADED_IMAGE_NAME = "downloadedImage.jpg";

	private static final String IS_DOWNLOADING = "is_downloading";
	private boolean isDownloading = false;
	private static final String IS_DOWNLOADED = "is_downloaded";
	private boolean isDownloaded = false;

	private ProgressBar progressBarTimer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonAction = (Button) findViewById(R.id.buttonAction);
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
		progressBarTimer = (ProgressBar) findViewById(R.id.progressBar);

		if (savedInstanceState == null) {
			setStatusIdle();
			setOnClickeListnerDownload();
		}
	}

	private void setOnClickeListnerDownload() {
		buttonAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DownloadFileFromURL().execute(getString(R.string.url_img));
				setStatusDownloading();
			}
		});
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

				buttonAction.setVisibility(4);
				textViewStatus.setVisibility(4);
				progressBarTimer.setVisibility(4);
			}
		});
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
			setOnClickeListnerDownload();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(IS_DOWNLOADING, isDownloading);
		outState.putBoolean(IS_DOWNLOADED, isDownloaded);
	}

	private class DownloadFileFromURL extends
			AsyncTask<String, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... fileUrl) {
			int count;
			URL url;
			try {
				url = new URL(fileUrl[0]);

				URLConnection conection = url.openConnection();

				conection.connect();

				InputStream input = new BufferedInputStream(url.openStream(),
						8192);

				int lenghtOfFile = conection.getContentLength();

				OutputStream output = new FileOutputStream(
						Environment.getExternalStorageDirectory()
								+ "/download/" + DOWNLOADED_IMAGE_NAME);

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress((int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();
			} catch (MalformedURLException e) {
				Toast.makeText(getApplicationContext(), "No such URL",
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Exception",
						Toast.LENGTH_SHORT).show();
			}
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			progressBarTimer.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Integer fileUrl) {
			setStatusDownloaded();
			setOnClickeListnerOpen();
		}

	}

	private void setStatusDownloading() {
		buttonAction.setText("Downloading");
		textViewStatus.setText("Status: Downloading");
		isDownloading = true;
		isDownloaded = false;
		progressBarTimer.setVisibility(1);
		buttonAction.setEnabled(false);
	}

	private void setStatusDownloaded() {
		buttonAction.setText("Open");
		textViewStatus.setText("Status: Downloaded");
		isDownloading = false;
		isDownloaded = true;
		buttonAction.setEnabled(true);
	}

	private void setStatusIdle() {
		buttonAction.setText("Download");
		textViewStatus.setText("Status: Idle");
		isDownloading = false;
		isDownloaded = false;
		buttonAction.setEnabled(true);
		progressBarTimer.setVisibility(4);
	}

}