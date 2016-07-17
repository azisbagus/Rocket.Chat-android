package chat.rocket.app.ui.home.tabs;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import chat.rocket.app.R;
import chat.rocket.app.db.dao.RcSubscriptionDAO;
import chat.rocket.app.ui.adapters.RCSubscriptionAdapter;
import chat.rocket.app.ui.chat.ChatActivity;
import chat.rocket.models.RcSubscription;
import chat.rocket.rc.enumerations.ChannelType;

/**
 * Created by julio on 29/11/15.
 */
public class RCSubscriptionsTabFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private ListView mListView;
    private RCSubscriptionAdapter mAdapter;
    private AdapterView.OnItemClickListener mListViewListener = (parent, view, position, id) -> {
        RcSubscription item = mAdapter.getItem(position);
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(ChatActivity.RC_SUB, item);
        startActivity(intent);
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rc_subscriptions_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.listview);
        mAdapter = new RCSubscriptionAdapter(getContext());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mListViewListener);
        getLoaderManager().initLoader(getLoaderId(), null, this);
    }

    protected int getLoaderId() {
        return LOADER_ID;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return RcSubscriptionDAO.getLoader(ChannelType.CHANNEL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
