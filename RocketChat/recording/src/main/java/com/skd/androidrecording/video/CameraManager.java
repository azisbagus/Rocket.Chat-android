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
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.SurfaceHolder;

import java.io.IOException;

/*
 * Manages camera preview
 */

public class CameraManager {

	private Camera camera;
	private int camerasCount;
	private int defaultCameraID;
	private int cameraRotationDegree;
	private boolean isPreviewStarted = false;
	private CamcorderProfile profile;
	
	public CameraManager() {
		camerasCount = CameraHelper.getAvailableCamerasCount();
		defaultCameraID = CameraHelper.getDefaultCameraID();
	}
	
	public void openCamera() {
		if (camera != null) {
			releaseCamera();
		}
		camera = Camera.open(defaultCameraID);
	}
	
	public void releaseCamera() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}
	
	public void switchCamera() {
		stopCameraPreview();
		
		defaultCameraID = (defaultCameraID + 1) % camerasCount;
		openCamera();
	}
	
	public void setupCameraAndStartPreview(SurfaceHolder sf, Size sz, int displayRotation) {
		stopCameraPreview();
		
		cameraRotationDegree = CameraHelper.setCameraDisplayOrientation(defaultCameraID, camera, displayRotation);

		chooseCamcorderProfile(sz);

		// tweak profile
		profile.fileFormat = MediaRecorder.OutputFormat.THREE_GPP;
		profile.audioSampleRate = 16000;
		profile.audioChannels = 1;
		profile.audioBitRate = 96000;

		Parameters param = camera.getParameters();

		param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		param.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        if (Build.VERSION.SDK_INT >= 14)
            param.setRecordingHint(true);
		camera.setParameters(param);
		
		if (setDisplay(sf)) {
			startCameraPreview();
		}	
	}

	private void chooseCamcorderProfile(Size sizeHint) {
		// For android 2.3 devices video quality = low
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			profile = (CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
		else {
			// For >= Android 3.0 devices select 720p, 480p or low quality of video
			if (CamcorderProfile.hasProfile(getCameraID(), CamcorderProfile.QUALITY_720P)
					&& (sizeHint == null || sizeHint.height >= 720)) {
				profile = (CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
				return;
			}

			if (CamcorderProfile.hasProfile(getCameraID(), CamcorderProfile.QUALITY_480P)
					&& (sizeHint == null || sizeHint.height >= 480)) {
				profile = (CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
				return;
			}

			profile = (CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
		}
	}

	public boolean setDisplay(SurfaceHolder sf) {
		try {
			camera.setPreviewDisplay(sf);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void startCameraPreview() {
		try {
			camera.startPreview();
			isPreviewStarted = true;
		} catch (RuntimeException e) {
			// java.lang.RuntimeException: startPreview failed
			e.printStackTrace();
		}
	}
	
	public void stopCameraPreview() {
		if (isPreviewStarted && (camera != null)) {
			isPreviewStarted = false;
			camera.stopPreview();
		}
	}

	public Camera getCamera() {
		return camera;
	}

	public CamcorderProfile getProfile() {
		return profile;
	}

	public int getCameraDisplayOrientation() {
		return (CameraHelper.isCameraFacingBack(defaultCameraID)) ? cameraRotationDegree : cameraRotationDegree + 180;
	}
	
	public boolean hasMultipleCameras() {
		return (camerasCount > 1);
	}

	public boolean isPreviewStarted() {
		return isPreviewStarted;
	}

	public int getCameraID() {
		return defaultCameraID;
	}
}
