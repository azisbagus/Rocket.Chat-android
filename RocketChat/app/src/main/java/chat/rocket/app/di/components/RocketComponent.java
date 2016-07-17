package chat.rocket.app.di.components;

import chat.rocket.app.RocketApp;
import chat.rocket.app.di.modules.RocketModule;
import chat.rocket.app.di.scopes.ServerScope;
import dagger.Subcomponent;

/**
 * Created by julio on 16/12/15.
 */
@ServerScope
@Subcomponent(modules = RocketModule.class)
public interface RocketComponent {
    void inject(RocketApp rocketApp);
}
