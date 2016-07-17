package chat.rocket.app.ui.home.tabs;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;

import chat.rocket.app.db.dao.RcSubscriptionDAO;

/**
 * Created by julio on 29/11/15.
 */
public class FavoritesTabFragment extends RCSubscriptionsTabFragment {
    private static final int LOADER_ID = 18;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return RcSubscriptionDAO.getLoader(true);
    }

    protected int getLoaderId() {
        return LOADER_ID;
    }
}
