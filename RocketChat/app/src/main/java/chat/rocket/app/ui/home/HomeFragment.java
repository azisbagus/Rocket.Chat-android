package chat.rocket.app.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chat.rocket.app.R;
import chat.rocket.app.ui.home.tabs.DirectMessagesTabFragment;
import chat.rocket.app.ui.home.tabs.FavoritesTabFragment;
import chat.rocket.app.ui.home.tabs.PrivateGroupsTabFragment;
import chat.rocket.app.ui.home.tabs.RCSubscriptionsTabFragment;

/**
 * Created by julio on 28/11/15.
 */
public class HomeFragment extends Fragment {
    private TabLayout mTabLayout;
    private FragmentAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mAdapter = new FragmentAdapter(getContext(), getChildFragmentManager());
        viewPager.setAdapter(mAdapter);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            mTabLayout.getTabAt(i).setIcon(mAdapter.getIcon(i));
        }
    }

    enum TABS {
        FAVORITES(FavoritesTabFragment.class.getName(), R.string.favorites, R.drawable.ic_star_white_24dp),
        CHANNELS(RCSubscriptionsTabFragment.class.getName(), R.string.channels, R.drawable.ic_forum_white_24dp),
        DIRECT_MESSAGES(DirectMessagesTabFragment.class.getName(), R.string.dms, R.drawable.ic_person_pin_white_24dp),
        PRIVATE_GROUPS(PrivateGroupsTabFragment.class.getName(), R.string.private_groups, R.drawable.ic_forum_lock_white_24dp);

        private final String fragName;
        private final int icon;
        private final int title;

        TABS(String fragName, @StringRes int title, @DrawableRes int icon) {

            this.fragName = fragName;
            this.title = title;
            this.icon = icon;
        }
    }

    static class FragmentAdapter extends FragmentPagerAdapter {
        private final Context mContext;
        private TABS[] mTabs = TABS.values();

        public FragmentAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            return Fragment.instantiate(mContext, mTabs[position].fragName);
        }

        @Override
        public int getCount() {
            return mTabs.length;
        }

        public int getIcon(int position) {
            return mTabs[position].icon;
        }
    }
}
