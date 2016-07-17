package chat.rocket.app.ui.chat.members;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import chat.rocket.app.R;
import chat.rocket.app.ui.widgets.AvatarView;

/**
 * Created by julio on 16/12/15.
 */
public class RoomMemberDetailFragment extends Fragment {
    public static final String USERNAME = "username";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_member_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String username = getArguments().getString(USERNAME);
        AvatarView avatarView = (AvatarView) view.findViewById(R.id.avatarView);
        TextView tv = (TextView) view.findViewById(R.id.usernameTextView);
        avatarView.loadAvatarTo(username);
        tv.setText(username);

        view.findViewById(R.id.startConversationButton).setOnClickListener(v -> startConversationWith(username));
    }

    private void startConversationWith(String username) {
        //TODO
        Toast.makeText(getContext(), "TODOOOOOOOO", Toast.LENGTH_LONG).show();
    }

    public static RoomMemberDetailFragment newInstance(String username) {
        RoomMemberDetailFragment fragment = new RoomMemberDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString(USERNAME, username);
        fragment.setArguments(bundle);
        return fragment;
    }

}
