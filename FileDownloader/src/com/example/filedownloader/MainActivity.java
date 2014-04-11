package com.example.filedownloader;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button buttonAction;
	private ImageView imageDownloaded;
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
		imageDownloaded = (ImageView) findViewById(R.id.imageDownloaded);
		progressBarTimer = (ProgressBar) findViewById(R.id.progressBar);

		if (savedInstanceState == null) {
			setStatusIdle();
			setOnClickeListnerDownload();
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
				String imagePath = getFilesDir().toString() + "/"
						+ DOWNLOADED_IMAGE_NAME;
				// 0
				// imageDownloaded.setImageDrawable(Drawable
				// .createFromPath(imagePath));
				// 1
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse(imagePath), "image/*");
				startActivity(intent);
				// 2
				// intent.setType("image/*");
				// intent.setAction(Intent.ACTION_GET_CONTENT);
				// startActivityForResult(
				// Intent.createChooser(intent, imagePath), 1);

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

	class DownloadFileFromURL extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... f_url) {
			int count;
			try {
				URL url = new URL(f_url[0]);
				URLConnection conection = url.openConnection();
				conection.connect();
				int lenghtOfFile = conection.getContentLength();

				InputStream input = new BufferedInputStream(url.openStream(),
						8192);
				FileOutputStream output = openFileOutput(DOWNLOADED_IMAGE_NAME,
						Context.MODE_PRIVATE);

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();

			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Error.",
						Toast.LENGTH_LONG).show();
			}

			return null;
		}

		protected void onProgressUpdate(String... progress) {
			progressBarTimer.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String file_url) {
			setStatusDownloaded();
			setOnClickeListnerOpen();
		}

	}
}