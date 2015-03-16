/*******************************************************************************
 * Copyright 2013 Kumar Bibek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    
 * http://www.apache.org/licenses/LICENSE-2.0
 * 	
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.kbeanie.imagechooser.api;

import java.io.File;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.kbeanie.imagechooser.api.config.Config;
import com.kbeanie.imagechooser.threads.VideoProcessorListener;
import com.kbeanie.imagechooser.threads.VideoProcessorThread;

/**
 * Easy Image Chooser Library for Android Apps. Forget about coding workarounds
 * for different devices, OSes and folders.
 * 
 * @author Beanie
 */
public class VideoChooserManager extends BChooser implements
		VideoProcessorListener {
	private final static String TAG = "VideoChooserManager";

	private final static String DIRECTORY = "bvideochooser";

	private VideoChooserListener listener;

	/**
	 * Simplest constructor. Specify the type
	 * {@link ChooserType.REQUEST_CHOOSE_IMAGE} or
	 * {@link ChooserType.REQUEST_TAKE_PICTURE}
	 * 
	 * @param activity
	 * @param type
	 */
	public VideoChooserManager(Activity activity, int type) {
		super(activity, type, DIRECTORY, true);
	}

	public VideoChooserManager(Fragment fragment, int type) {
		super(fragment, type, DIRECTORY, true);
	}

	public VideoChooserManager(android.app.Fragment fragment, int type) {
		super(fragment, type, DIRECTORY, true);
	}

	public VideoChooserManager(Activity activity, int type, String foldername) {
		super(activity, type, foldername, true);
	}

	public VideoChooserManager(Fragment fragment, int type, String foldername) {
		super(fragment, type, foldername, true);
	}

	public VideoChooserManager(android.app.Fragment fragment, int type,
			String foldername) {
		super(fragment, type, foldername, true);
	}

	public VideoChooserManager(Activity activity, int type,
			boolean shouldCreateThumbnails) {
		super(activity, type, DIRECTORY, shouldCreateThumbnails);
	}

	public VideoChooserManager(Fragment fragment, int type,
			boolean shouldCreateThumbnails) {
		super(fragment, type, DIRECTORY, shouldCreateThumbnails);
	}

	public VideoChooserManager(android.app.Fragment fragment, int type,
			boolean shouldCreateThumbnails) {
		super(fragment, type, DIRECTORY, shouldCreateThumbnails);
	}

	public VideoChooserManager(Activity activity, int type, String foldername,
			boolean shouldCreateThumbnails) {
		super(activity, type, foldername, shouldCreateThumbnails);
	}

	public VideoChooserManager(Fragment fragment, int type, String foldername,
			boolean shouldCreateThumbnails) {
		super(fragment, type, foldername, shouldCreateThumbnails);
	}

	public VideoChooserManager(android.app.Fragment fragment, int type,
			String foldername, boolean shouldCreateThumbnails) {
		super(fragment, type, foldername, shouldCreateThumbnails);
	}

	/**
	 * Set a listener, to get callbacks when the videos and the thumbnails are
	 * processed
	 * 
	 * @param listener
	 */
	public void setVideoChooserListener(VideoChooserListener listener) {
		this.listener = listener;
	}

	@Override
	public String choose() throws Exception {
		String path = null;
		if (listener == null) {
			throw new IllegalArgumentException(
					"ImageChooserListener cannot be null. Forgot to set ImageChooserListener???");
		}
		switch (type) {
		case ChooserType.REQUEST_CAPTURE_VIDEO:
			path = captureVideo();
			break;
		case ChooserType.REQUEST_PICK_VIDEO:
			pickVideo();
			break;
		default:
			throw new IllegalArgumentException(
					"Cannot choose an image in VideoChooserManager");
		}
		return path;
	}

	private String captureVideo() throws Exception {
		int sdk = Build.VERSION.SDK_INT;
		if (sdk >= Build.VERSION_CODES.GINGERBREAD
				&& sdk <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			return captureVideoPatchedMethodForGingerbread();
		} else {
			return captureVideoCurrent();
		}
	}

	private String captureVideoCurrent() throws Exception {
		checkDirectory();
		try {
			Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			filePathOriginal = FileUtils.getDirectory(foldername)
					+ File.separator + Calendar.getInstance().getTimeInMillis()
					+ ".mp4";
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(filePathOriginal)));
			if (extras != null) {
				intent.putExtras(extras);
			}
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			throw new Exception("Activity not found");
		}
		return filePathOriginal;
	}

	private String captureVideoPatchedMethodForGingerbread() throws Exception {
		try {
			Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			if (extras != null) {
				intent.putExtras(extras);
			}
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			throw new Exception("Activity not found");
		}
		return null;
	}

	private void pickVideo() throws Exception {
		checkDirectory();
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			if (extras != null) {
				intent.putExtras(extras);
			}
			intent.setType("video/*");
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			throw new Exception("Activity not found");
		}
	}

	@Override
	public void submit(int requestCode, Intent data) {
		switch (type) {
		case ChooserType.REQUEST_PICK_VIDEO:
			processVideoFromGallery(data);
			break;
		case ChooserType.REQUEST_CAPTURE_VIDEO:
			processCameraVideo(data);
			break;
		}
	}

	@SuppressLint("NewApi")
	private void processVideoFromGallery(Intent data) {
		if (data != null && data.getDataString() != null) {
			String uri = data.getData().toString();
			sanitizeURI(uri);
			if (filePathOriginal == null || TextUtils.isEmpty(filePathOriginal)) {
				onError("File path was null");
			} else {
				if (Config.DEBUG) {
					Log.i(TAG, "File: " + filePathOriginal);
				}
				String path = filePathOriginal;
				VideoProcessorThread thread = new VideoProcessorThread(path,
						foldername, shouldCreateThumbnails);
				thread.setListener(this);
				if (activity != null) {
					thread.setContext(activity.getApplicationContext());
				} else if (fragment != null) {
					thread.setContext(fragment.getActivity()
							.getApplicationContext());
				} else if (appFragment != null) {
					thread.setContext(appFragment.getActivity()
							.getApplicationContext());
				}
				thread.start();
			}
		}
	}

	@SuppressLint("NewApi")
	private void processCameraVideo(Intent intent) {
		String path = null;
		int sdk = Build.VERSION.SDK_INT;
		if (sdk >= Build.VERSION_CODES.GINGERBREAD
				&& sdk <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			path = intent.getDataString();
		} else {
			path = filePathOriginal;
		}
		VideoProcessorThread thread = new VideoProcessorThread(path,
				foldername, shouldCreateThumbnails);
		thread.setListener(this);
		if (activity != null) {
			thread.setContext(activity.getApplicationContext());
		} else if (fragment != null) {
			thread.setContext(fragment.getActivity().getApplicationContext());
		} else if (appFragment != null) {
			thread.setContext(appFragment.getActivity().getApplicationContext());
		}
		thread.start();
	}

	@Override
	public void onProcessedVideo(ChosenVideo video) {
		if (listener != null) {
			listener.onVideoChosen(video);
		}
	}

	@Override
	public void onError(String reason) {
		if (listener != null) {
			listener.onError(reason);
		}
	}
}
