package chat.rocket.app.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import chat.rocket.app.db.dao.RcSubscriptionDAO;
import chat.rocket.models.RcSubscription;

/**
 * Created by julio on 29/11/15.
 */
public class RCSubscriptionAdapter extends CursorAdapter {
    public RCSubscriptionAdapter(Context context) {
        super(context, null, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    public RcSubscription getItem(Cursor cursor) {
        return new RcSubscriptionDAO(cursor);
    }

    public RcSubscription getItem(int position) {
        return getItem((Cursor) super.getItem(position));
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        RcSubscription item = getItem(cursor);

        int unread = item.getUnread();

        text1.setText(item.getFormattedName() + " (" + unread + ")");
        if (item.hasAlert() || unread > 0) {
            text1.setTypeface(text1.getTypeface(), Typeface.DEFAULT_BOLD.getStyle());
        } else {
            text1.setTypeface(text1.getTypeface(), Typeface.NORMAL);
        }
    }
}
