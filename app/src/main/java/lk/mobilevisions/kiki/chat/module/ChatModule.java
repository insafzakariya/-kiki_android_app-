package lk.mobilevisions.kiki.chat.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.modules.api.API;

@Module
public class ChatModule {

    API api;
    Application application;

    public ChatModule(Application application, API api){
        this.application = application;
        this.api = api;
    }

    @Provides
    @Singleton
    public ChatManager providesChatManager(){
        return new ChatManager(application, api);
    }
}
