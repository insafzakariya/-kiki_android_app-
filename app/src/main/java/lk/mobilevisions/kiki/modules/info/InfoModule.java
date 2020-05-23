/**
 * Created by Chatura Dilan Perera on 23/9/2016.
 */
package lk.mobilevisions.kiki.modules.info;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.modules.api.ComAPI;

@Module
public class InfoModule {

    ComAPI api;
    Application application;

    public InfoModule(Application application, ComAPI api){
        this.application = application;
        this.api = api;
    }

    @Provides
    @Singleton
    public InfoManager providesInfoManager(){
        return new InfoManager(application, api);
    }
}
