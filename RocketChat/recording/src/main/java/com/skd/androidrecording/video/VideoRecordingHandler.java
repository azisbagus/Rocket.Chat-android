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

import android.hardware.Camera.Size;

public interface VideoRecordingHandler {
	public boolean onPrepareRecording();
	public Size getVideoSize();
	public int getDisplayRotation();
	public void onStoppedRecording();

	/**
	 * Report video duration and file size.
	 * @param duration Duration in sec.
	 * @param fileSize File size in bytes.
	 */
	public void onRecordingProgress(int duration, int fileSize);
}
