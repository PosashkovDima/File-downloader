package com.example.filedownloader;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;

public class TaskFragment extends Fragment {

	private static final String DOWNLOADED_IMAGE_NAME = "downloadedImage.jpg";

	private TaskCallbacks mCallbacks;
	private DownloadFileAsyncTask mTask;
	private boolean mRunning;

	public static interface TaskCallbacks {

		void onProgressUpdate(int percent);

		void onPostExecute();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (TaskCallbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	/**
	 * Start the background task.
	 */
	public void start() {
		if (!mRunning) {
			mTask = new DownloadFileAsyncTask();
			mTask.execute(getString(R.string.url_img));
			mRunning = true;
		}
	}

	public boolean isRunning() {
		return mRunning;
	}

	/**
	 * Set the callback to null so we don't accidentally leak the Activity
	 * instance.
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	private class DownloadFileAsyncTask extends
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
			} catch (Exception e) {
			}
			return null;
		}

		protected void onProgressUpdate(Integer... percent) {
			mCallbacks.onProgressUpdate(percent[0]);
		}

		@Override
		protected void onPostExecute(Integer fileUrl) {
			mCallbacks.onPostExecute();
			mRunning = false;
		}
	}
}
