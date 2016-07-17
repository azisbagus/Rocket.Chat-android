package chat.rocket.app.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import chat.rocket.app.R;
import chat.rocket.app.ui.widgets.AvatarView;

/**
 * Created by julio on 15/12/15.
 */
public class UsernamesAdapter extends ArrayAdapter<String> {

    public UsernamesAdapter(Context context, List<String> items) {
        super(context, R.layout.username_list_item_layout, R.id.usernameTextView, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        AvatarView avatarView = (AvatarView) view.findViewById(R.id.avatarView);
        avatarView.loadAvatarTo(getItem(position));
        return view;
    }
}
