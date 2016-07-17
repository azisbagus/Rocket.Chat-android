package chat.rocket.app.ui.chat.members;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import chat.rocket.app.R;
import chat.rocket.app.db.collections.RcRoom;
import chat.rocket.app.ui.adapters.UsernamesAdapter;

/**
 * Created by julio on 26/11/15.
 */
public class RoomMembersFragment extends Fragment {
    private static final String RID = "rid";
    private static final String USERNAME = "username";
    private ListView mListView;
    private UsernamesAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_members_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.ListView);

        String rid = getArguments().getString(RID);
        String username = getArguments().getString(USERNAME);

        //TODO: Make it async
        RcRoom room = RcRoom.getRCRoom(rid);

        List<String> usernames = room.getUsernames();
        mAdapter = new UsernamesAdapter(getActivity(), usernames);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener((parent, view1, position, id) -> showDetailOf(usernames.get(position)));
        if (username != null) {
            showDetailOf(username);
        }

    }

    private void showDetailOf(String username) {
        getChildFragmentManager().beginTransaction().replace(R.id.userDetailContainer, RoomMemberDetailFragment.newInstance(username)).commit();
    }


    public static RoomMembersFragment newInstace(String rid, String username) {
        RoomMembersFragment fragment = new RoomMembersFragment();

        Bundle bundle = new Bundle();
        bundle.putString(RID, rid);
        bundle.putString(USERNAME, username);
        fragment.setArguments(bundle);
        return fragment;
    }
}
