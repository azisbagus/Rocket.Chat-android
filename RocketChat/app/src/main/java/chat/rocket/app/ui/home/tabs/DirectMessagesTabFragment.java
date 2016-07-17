package chat.rocket.app.ui.home.tabs;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chat.rocket.app.R;
import chat.rocket.app.db.dao.RcSubscriptionDAO;
import chat.rocket.rc.enumerations.ChannelType;

/**
 * Created by julio on 29/11/15.
 */
public class DirectMessagesTabFragment extends RCSubscriptionsTabFragment {
    private static final int LOADER_ID = 5;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return RcSubscriptionDAO.getLoader(ChannelType.DIRECT);
    }

    protected int getLoaderId() {
        return LOADER_ID;
    }

}
