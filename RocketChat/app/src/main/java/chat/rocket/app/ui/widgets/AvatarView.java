package chat.rocket.app.ui.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;

import chat.rocket.app.R;
import chat.rocket.app.utils.Util;

/**
 * Created by julio on 09/12/15.
 */
public class AvatarView extends FrameLayout {
    private RoundedImageView mAvatarImageView;
    private TextView mAvatarTextView;
    //https://github.com/RocketChat/Rocket.Chat/blob/develop/server/startup/avatar.coffee
    private static String[] COLORS = new String[]{"#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFC107", "#FF9800", "#FF5722", "#795548", "#9E9E9E", "#607D8B"};

    public AvatarView(Context context) {
        super(context);
        init();
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.avatar_view_layout, this, true);
        mAvatarImageView = (RoundedImageView) findViewById(R.id.avatarImageView);
        mAvatarTextView = (TextView) findViewById(R.id.avatarTextView);
    }

    public void setDefaultAvatarBackgroundColor(int color) {
        Drawable background = mAvatarTextView.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(color);
        }
    }

    public void loadAvatarTo(String username) {

        username = username.replaceAll("[^A-Za-z0-9]", ".").replaceAll("\\.+", ".").replaceAll("(^\\.)|(\\.$)", "");

        String[] parts = username.split("\\.");
        String firstName = parts[0];
        String initials = firstName.substring(0, 1);
        if (parts.length > 1) {
            initials += parts[parts.length - 1].substring(0, 1);
        } else {
            initials += firstName.substring(firstName.length() - 1);
        }

        int position = username.length() % COLORS.length;
        setDefaultAvatarBackgroundColor(Color.parseColor(COLORS[position]));
        mAvatarTextView.setText(initials.toUpperCase());

        Glide.with(getContext())
                .load(Util.getAvatarServerUrl(username))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        mAvatarImageView.setVisibility(View.INVISIBLE);
                        mAvatarTextView.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mAvatarTextView.setVisibility(View.INVISIBLE);
                        mAvatarImageView.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(mAvatarImageView);

    }
}
