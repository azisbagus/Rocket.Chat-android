package chat.rocket.app.di.modules;

import android.app.Application;

import chat.rocket.app.RocketApp;
import chat.rocket.app.di.scopes.AppScope;
import dagger.Module;
import dagger.Provides;
import meteor.operations.Persistence;

/**
 * Created by julio on 16/12/15.
 */
@Module
public class AppModule {

    private final RocketApp mApp;

    public AppModule(RocketApp app) {
        mApp = app;
    }

    @Provides
    @AppScope
    Application provideApplicationContext() {
        return this.mApp;
    }

    @Provides
    @AppScope
    Persistence providesPersistence() {
        return this.mApp;
    }
}
