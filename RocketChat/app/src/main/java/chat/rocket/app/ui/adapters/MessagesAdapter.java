package chat.rocket.app.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import chat.rocket.app.R;
import chat.rocket.app.db.dao.MessageDAO;
import chat.rocket.app.ui.widgets.AvatarView;
import chat.rocket.app.ui.widgets.LinkfiedTextView;
import chat.rocket.rc.models.Message;


/**
 * Created by julio on 29/11/15.
 */
public class MessagesAdapter extends CursorAdapter {


    public MessagesAdapter(Context context) {
        super(context, null, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.message_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Message message = new MessageDAO(cursor);
        LinkfiedTextView messageTextView = (LinkfiedTextView) view.findViewById(R.id.messageTextView);
        String username = message.getUsernameId().getUsername();
        String msg = message.getMsg();

        if (message.getType() != null) {
            //TODO: localize strings!!! Create a script/program to import!!
            //https://github.com/RocketChat/Rocket.Chat/blob/develop/i18n/en.i18n.json
            switch (message.getType()) {
                case MESSAGE_REMOVED:
                    msg = "Message removed";
                    break;
                case WELCOME:
                    msg = "Welcome " + username;
                    break;
                case USER_LEFT:
                    msg = "Has left the channel.";
                    break;
                case USER_REMOVED_BY:
                    msg = "User " + msg + " removed by " + username + ".";
                    break;
                case USER_JOINED_CHANNEL:
                    msg = "has joined the channel.";
                    break;
                case USER_ADDED_BY:
                    msg = "User " + msg + " added by " + username + ".";
                    break;
                case ROOM_NAME_CHANGED:
                    msg = "room name changed to: " + msg + " by " + username + ".";
                    break;
                case ROOM_CHANGED_TOPIC:
                    msg = "room topic changed to: " + msg + " by " + username + ".";
                    break;
                case ROOM_CHANGED_PRIVACE:
                    msg = "room type changed to: " + msg + " group by " + username + ".";
                    break;
                case RENDER_MESSAGE:
                default:
            }
        }
        messageTextView.setLinkText(msg);

        AvatarView avatarView = (AvatarView) view.findViewById(R.id.avatarView);
        avatarView.loadAvatarTo(username);

        TextView usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
        usernameTextView.setText(message.getUsernameId().getUsername());

    }
}
