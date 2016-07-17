package chat.rocket.app.ui.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.widget.TextView;

import com.emojione.Emojione;

import java.util.regex.Pattern;

import chat.rocket.app.db.DBContentProvider;

/**
 * Created by julio on 14/12/15.
 */
public class LinkfiedTextView extends TextView {
    private static String sUrlProvider = DBContentProvider.WEB_CONTENT_URI.toString();
    public static Pattern sUrlMatcher = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private static String sUsernameProvider = DBContentProvider.USERNAME_CONTENT_URI.toString();
    public static Pattern sUsernameMatcher = Pattern.compile("(?:^||\\n)(@:?)[0-9a-zA-Z-_.]+");

    public LinkfiedTextView(Context context) {
        super(context);
        init();
    }

    public LinkfiedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinkfiedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LinkfiedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void setLinkText(String text) {
        setText(Emojione.shortnameToUnicode(text, false));
        Linkify.addLinks(this, sUrlMatcher, sUrlProvider);
        Linkify.addLinks(this, sUsernameMatcher, sUsernameProvider);
    }
}
