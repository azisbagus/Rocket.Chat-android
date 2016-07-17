package chat.rocket.rc.listeners;

import meteor.operations.MeteorException;
import meteor.operations.Protocol;
import meteor.operations.ResultListener;
import timber.log.Timber;

/**
 * Created by julio on 19/11/15.
 */
public class LogListener implements ResultListener {
    @Override
    public void onSuccess(String result) {
        if (result != null) {
            Timber.d(result);
        } else {
            Timber.d("Empty Result");
        }
    }

    @Override
    public void onError(MeteorException e) {
        Protocol.Error err = e.getError();
        String error = err.getError();
        String reason = err.getReason();
        String details = err.getDetails();
        Timber.d("error: " + error + ", reason: " + reason + ", details: " + details);
    }
}
