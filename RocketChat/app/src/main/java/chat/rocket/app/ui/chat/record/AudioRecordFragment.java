package chat.rocket.app.ui.chat.record;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skd.androidrecording.audio.AudioRecordingHandler;
import com.skd.androidrecording.audio.AudioRecordingThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import chat.rocket.app.R;
import chat.rocket.app.ui.chat.files.FileCallback;
import chat.rocket.app.utils.Util;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class AudioRecordFragment extends Fragment {
    //TODO: why do I limit the recording time? I don't know, I will think about it... :-P
    private static final int MAX_DURATION = 10000;


    private AudioRecordingThread recordingThread;

    private static final int RECORDING_REQUEST_CODE = 112;
    private final String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecord.wav";

    private MediaPlayer mPlayer = null;
    private SeekBar mSeekBar;
    private Subscription mTimerSubscription;
    private Date mStartTime;
    private TextView mProgressTextView;
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mPlayButtonListener.onClick(null);
        }
    };
    private View mSendButton;
    private FileCallback mCallback;

    private void onRecord(boolean start) {
        mPlayButton.setEnabled(!start);
        mSendButton.setEnabled(!start);
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        mRecordButton.setEnabled(!start);
        mSendButton.setEnabled(!start);
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setOnCompletionListener(mCompletionListener);
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            startPlayProgress();
        } catch (IOException e) {
            Timber.d("prepare() failed");
        }
    }

    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        unsubscribeTimer();
        Util.setTint(mSeekBar, getResources().getColor(R.color.accentColor));
    }

    private void startRecording() {
        recordingThread = new AudioRecordingThread(mFileName, new AudioRecordingHandler() {
            @Override
            public void onFftDataCapture(final byte[] bytes) {
                final Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
            }

            @Override
            public void onRecordSuccess() {
            }

            @Override
            public void onRecordingError(Exception e) {
                Timber.e(e, "Stoping recording due error!");
                final Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                activity.runOnUiThread(() -> stopRecording());
            }

            @Override
            public void onRecordSaveError(Exception e) {
                Timber.e(e, "Error while trying to save recording...!");
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                activity.runOnUiThread(() -> stopRecording());
            }
        });
        recordingThread.start();
        startRecordProgress();
    }

    private void startPlayProgress() {
        mStartTime = new Date();
        Util.setTint(mSeekBar, getResources().getColor(R.color.primaryColor));
        mTimerSubscription = Observable.interval(50, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    int time = (int) (System.currentTimeMillis() - mStartTime.getTime());
                    // evey 100 milliseconds this method is called
                    mSeekBar.setProgress(time);
                    mProgressTextView.setText((time / 1000) + "/10s");
                    if (time > MAX_DURATION) {
                        unsubscribeTimer();
                    }
                });
    }

    private void startRecordProgress() {
        mStartTime = new Date();
        Util.setTint(mSeekBar, getResources().getColor(R.color.accentColor));
        mTimerSubscription = Observable.interval(50, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    int time = (int) (System.currentTimeMillis() - mStartTime.getTime());
                    // evey 100 milliseconds this method is called
                    mSeekBar.setProgress(time);
                    mProgressTextView.setText((time / 1000) + "/10s");
                    if (time > MAX_DURATION) {
                        mRecordButtonListener.onClick(null);
                    }
                });
    }

    private void unsubscribeTimer() {
        if (mTimerSubscription != null) {
            mTimerSubscription.unsubscribe();
            mTimerSubscription = null;
        }
    }

    private void stopRecording() {
        if (recordingThread != null) {
            recordingThread.stopRecording();
            recordingThread = null;
        }
        unsubscribeTimer();

    }

    private Button mRecordButton;
    private boolean mStartRecording = true;

    private View.OnClickListener mRecordButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            onRecord(mStartRecording);
            if (mStartRecording) {
                mRecordButton.setText(R.string.stop_recording);
            } else {
                mRecordButton.setText(R.string.start_recording);
            }
            mStartRecording = !mStartRecording;
        }
    };

    private Button mPlayButton;
    private boolean mStartPlaying = true;
    private View.OnClickListener mPlayButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            onPlay(mStartPlaying);
            if (mStartPlaying) {
                mPlayButton.setText(R.string.stop_playing);
            } else {
                mPlayButton.setText(R.string.start_playing);
            }
            mStartPlaying = !mStartPlaying;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audio_record, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPlayButton = (Button) view.findViewById(R.id.PlayButton);
        mRecordButton = (Button) view.findViewById(R.id.RecordButton);

        mPlayButton.setOnClickListener(mPlayButtonListener);
        mSendButton = view.findViewById(R.id.SendButton);
        mSendButton.setOnClickListener(v -> {
            mCallback.processFile(mFileName, "audio");

        });

        mProgressTextView = (TextView) view.findViewById(R.id.ProgressTextView);

        mSeekBar = (SeekBar) view.findViewById(R.id.SeekBar);
        mSeekBar.setMax(MAX_DURATION);
        mSeekBar.setOnTouchListener((v, event) -> true);

        mPlayButton.setEnabled(false);
        mSendButton.setEnabled(false);
        requestNeededPermissions();
    }

    private void requestNeededPermissions() {
        List<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissions.isEmpty()) {
            mRecordButton.setOnClickListener(mRecordButtonListener);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions.toArray(new String[0]),
                    RECORDING_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORDING_REQUEST_CODE: {
                String whatToAsk = "";
                boolean allGranted = true;
                for (int i = 0; i < grantResults.length; ++i) {
                    int result = grantResults[i];
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[i])) {
                            if (permissions[i].equals(Manifest.permission.RECORD_AUDIO)) {
                                whatToAsk = "We need the audio recording permission to let you record your beautiful voice.";
                            }
                            if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                whatToAsk += "\nWe need the sdcard permission to temporarily save your audio files.";
                            }
                            if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                whatToAsk += "\nWe need the sdcard permission to read your saved audio files.";
                            }
                        }
                    }
                }
                if (!allGranted && !TextUtils.isEmpty(whatToAsk)) {
                    Snackbar snackbar = Snackbar.make(mPlayButton, whatToAsk, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Show permission dialog", v -> {
                        requestNeededPermissions();
                    });
                    snackbar.show();

                } else if (allGranted) {
                    mRecordButton.setOnClickListener(mRecordButtonListener);
                } else {
                    // TODO: Think in a better way to deal with it..
                    Toast.makeText(getContext(), "Permissions not granted...", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecording();
        stopPlaying();
        unsubscribeTimer();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (FileCallback) context;
    }
}