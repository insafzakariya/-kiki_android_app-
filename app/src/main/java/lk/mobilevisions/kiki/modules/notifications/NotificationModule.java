/**
 * Created by Chatura Dilan Perera on 1/3/2017.
 */
package lk.mobilevisions.kiki.modules.notifications;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.modules.api.API;

@Module
public class NotificationModule {

    API api;
    Application application;

    public NotificationModule(Application application, API api){
        this.application = application;
        this.api = api;
    }

    @Provides
    @Singleton
    public NotificationManager providesNotificationManager(){
        return new NotificationManager(application, api);
    }
}
