/**
 * Created by Chatura Dilan Perera on 11/11/2016.
 */
package lk.mobilevisions.kiki.modules.subscriptions;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.modules.api.API;

@Module
public class SubscriptionsModule {

    API api;
    Application application;

    public SubscriptionsModule(Application application, API api){
        this.application = application;
        this.api = api;
    }

    @Provides
    @Singleton
    public SubscriptionsManager providesSubscriptionsManager(){
        return new SubscriptionsManager(application, api);
    }
}
