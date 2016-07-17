/*
 * Copyright (C) 2013 Steelkiwi Development, Julia Zudikova, Viacheslav Tiagotenkov
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skd.androidrecording.video;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/*
 * Manages media recorder recording 
 */

public class MediaRecorderManager {
	private static final String TAG = "MediaRecorderManager ";
	private MediaRecorder recorder;
	private boolean isRecording;

	public MediaRecorderManager() {
		recorder = new MediaRecorder();
	}

	public boolean startRecording(Camera camera, CamcorderProfile profile, String fileName, int cameraRotationDegree) {
		Log.i(TAG, String.format("start recording %dx%d -> %s",
				profile.videoFrameWidth, profile.videoFrameHeight, fileName));
		try {
			camera.unlock();
			recorder.setCamera(camera);
			recorder.setOrientationHint(cameraRotationDegree);
			recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			recorder.setProfile(profile);
			recorder.setOutputFile(fileName);
			recorder.prepare();
			recorder.start();
			isRecording = true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return isRecording;
	}

	public boolean stopRecording() {
		if (isRecording) {
			isRecording = false;
			recorder.stop();
			recorder.reset();
			return true;
		}
		return false;
	}

	public void releaseRecorder() {
		recorder.release();
		recorder = null;
	}

	public boolean isRecording() {
		return isRecording;
	}
}
