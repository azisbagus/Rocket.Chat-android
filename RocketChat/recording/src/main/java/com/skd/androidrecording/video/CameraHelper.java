/*
 * Copyright (C) 2013 Steelkiwi Development, Julia Zudikova, Viacheslav Tyagotenkov
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

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Build;
import android.view.Surface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/*
 * Represents camera management helper class.
 * Holds method for setting camera display orientation. 
 */

public class CameraHelper {

	public static int getAvailableCamerasCount() {
		return Camera.getNumberOfCameras();
	}
	
	public static int getDefaultCameraID() {
		int camerasCnt = getAvailableCamerasCount();
		int defaultCameraID = 0;
		CameraInfo cameraInfo = new CameraInfo();
        for (int i=0; i <camerasCnt; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
            	defaultCameraID = i;
            }
        }
        return defaultCameraID;
	}
	
	public static boolean isCameraFacingBack(int cameraID) {
		CameraInfo cameraInfo = new CameraInfo();
		Camera.getCameraInfo(cameraID, cameraInfo);
		return (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK);
	}
	
	@SuppressLint("NewApi")
	public static List<Size> getCameraSupportedVideoSizes(android.hardware.Camera camera) {
		if (camera != null) {
			if ((Build.VERSION.SDK_INT >= 11) && camera.getParameters().getSupportedVideoSizes() != null) {
				return camera.getParameters().getSupportedVideoSizes();
			} else {
				// Video sizes may be null, which indicates that all the supported
				// preview sizes are supported for video recording.
				HashSet<String> allSizesLiteral = new HashSet<>();
				for (Camera.Size sz : camera.getParameters().getSupportedPreviewSizes()) {
					allSizesLiteral.add(String.format("%dx%d", sz.width, sz.height));
				}

				// on Samsung Galaxy 3, the supported preview sizes are too many,
				// but it seems that not all of them can be used as recording video size.
				// the following set are used by the built-in camera app.
				Camera.Size[] preferredSizes = {
						camera.new Size(1920, 1080),
						camera.new Size(1280, 720),
						camera.new Size(720, 480),
						camera.new Size(640, 480),
						camera.new Size(320, 240)
				};

				List<Size> result = new ArrayList<>(preferredSizes.length);
				for (Camera.Size sz : preferredSizes) {
					if (allSizesLiteral.contains(String.format("%dx%d", sz.width, sz.height)))
						result.add(sz);
				}

				return result;
			}
		}
		else {
			return null;
		}
	}
	
	public static int setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera, int displayRotation) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int degrees = 0;
		switch (displayRotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}

		int camRotationDegree = 0;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			camRotationDegree = (info.orientation + degrees) % 360;
			camRotationDegree = (360 - camRotationDegree) % 360; // compensate the mirror
		} else { 
			camRotationDegree = (info.orientation - degrees + 360) % 360;
		}

		if (camera != null) {
			try {
				camera.setDisplayOrientation(camRotationDegree);
			} catch (RuntimeException e) {
				// java.lang.RuntimeException: set display orientation failed
				e.printStackTrace();
			}
		}
		return camRotationDegree;
	}
}
