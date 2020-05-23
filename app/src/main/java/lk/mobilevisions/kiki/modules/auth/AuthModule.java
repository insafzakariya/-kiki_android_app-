/**
 * Created by Chatura Dilan Perera on 27/8/2016.
 */
package lk.mobilevisions.kiki.modules.auth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.modules.api.API;

@Module
public class AuthModule {

    API api;
    Application application;

    public AuthModule(Application application, API api){
        this.application = application;
        this.api = api;
    }

    @Provides
    @Singleton
    public AuthManager providesAuthManager(){
        return new AuthManager(application, api);
    }

}
