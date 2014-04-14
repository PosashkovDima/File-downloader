package com.example.filedownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

public class DownloadService extends IntentService {
	private int result = Activity.RESULT_CANCELED;
	public static final String URL = "urlPath";
	public static final String FILE_NAME = "fileName";
	public static final String FILE_PATH = "filePath";
	public static final String RESULT = "result";
	public static final String NOTIFICATION = "com.example.servicetest";
	public static final String TOTAL_DOWNLOADED = "totalDownloaded";
	private Intent intent = new Intent(NOTIFICATION);

	public DownloadService() {
		super("DownloadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String urlPath = intent.getStringExtra(URL);
		String fileName = intent.getStringExtra(FILE_NAME);
		File fileOutput = new File(Environment.getExternalStorageDirectory(),
				fileName);

		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			URL url = new URL(urlPath);

			URLConnection conection = url.openConnection();

			conection.connect();

			inputStream = new BufferedInputStream(url.openStream(), 8192);

			// int lenghtOfFile = conection.getContentLength();

			outputStream = new FileOutputStream(fileOutput.getPath());
			int next = -1;
			// long total = 0;
			while ((next = inputStream.read()) != -1) {
				// total += next;
				outputStream.write(next);
				// intent.putExtra(TOTAL_DOWNLOADED,
				// (int) ((total * 100) / lenghtOfFile));
			}

			result = Activity.RESULT_OK;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		publishResults(result);
	}

	private void publishResults(int result) {

		intent.putExtra(RESULT, result);
		sendBroadcast(intent);
	}
}
