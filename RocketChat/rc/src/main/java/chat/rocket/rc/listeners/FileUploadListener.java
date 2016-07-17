package chat.rocket.rc.listeners;

import meteor.operations.ResultListener;

/**
 * Created by julio on 05/12/15.
 */
public interface FileUploadListener extends ResultListener {
    void onProgress(float progress);
}
