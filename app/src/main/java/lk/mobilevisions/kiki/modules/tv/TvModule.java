/**
 * Created by Chatura Dilan Perera on 23/9/2016.
 */
package lk.mobilevisions.kiki.modules.tv;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.modules.api.API;

@Module
public class TvModule {

    API api;
    Application application;

    public TvModule(Application application, API api){
        this.application = application;
        this.api = api;
    }

    @Provides
    @Singleton
    public TvManager providesTvManager(){
        return new TvManager(application, api);
    }
}
