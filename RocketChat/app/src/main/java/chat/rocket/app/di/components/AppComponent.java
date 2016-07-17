package chat.rocket.app.di.components;

import chat.rocket.app.di.modules.AppModule;
import chat.rocket.app.di.modules.RocketModule;
import chat.rocket.app.di.scopes.AppScope;
import dagger.Component;

/**
 * Created by julio on 16/12/15.
 */
@Component(modules = AppModule.class)
@AppScope
public interface AppComponent {

    RocketComponent plus(RocketModule module);
}
