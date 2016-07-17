package chat.rocket.app.di.modules;

import chat.rocket.app.di.scopes.ServerScope;
import chat.rocket.rc.RocketMethods;
import chat.rocket.rc.RocketSubscriptions;
import chat.rocket.rxrc.RxRocketMethods;
import chat.rocket.rxrc.RxRocketSubscriptions;
import dagger.Module;
import dagger.Provides;
import meteor.operations.Meteor;
import meteor.operations.Persistence;
import rxmeteor.operations.RxMeteor;

/**
 * Created by julio on 16/12/15.
 */
@Module
public class RocketModule {
    private String mUrl;

    public RocketModule(String url) {
        this.mUrl = url;
    }

    @Provides
    @ServerScope
    public Meteor providesMeteor(Persistence persistence) {
        return new Meteor(persistence, mUrl, Meteor.SUPPORTED_DDP_VERSIONS[0]);
    }

    @Provides
    @ServerScope
    public RxMeteor providesRxMeteor(Meteor meteor) {
        return new RxMeteor(meteor);
    }

    @Provides
    @ServerScope
    public RocketMethods providesRocketMethods(Meteor meteor) {
        return new RocketMethods(meteor);
    }

    @Provides
    @ServerScope
    public RxRocketMethods providesRxRocketMethods(RocketMethods rocketMethods) {
        return new RxRocketMethods(rocketMethods);
    }

    @Provides
    @ServerScope
    public RocketSubscriptions providesRocketSubscriptions(Meteor meteor) {
        return new RocketSubscriptions(meteor);
    }

    @Provides
    @ServerScope
    public RxRocketSubscriptions providesRxRocketSubscriptions(RocketSubscriptions rocketSubscriptions) {
        return new RxRocketSubscriptions(rocketSubscriptions);
    }

}
