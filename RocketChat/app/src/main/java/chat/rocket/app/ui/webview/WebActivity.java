package chat.rocket.app.ui.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by julio on 15/12/15.
 */
//NOTE: Activity used to forward the http request to the browser
public class WebActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View(this));
        Uri uri = getIntent().getData();
        String url = uri.getPath().replaceFirst("/", "");
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
        } finally {
            finish();
        }
    }
}
