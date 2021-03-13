package lk.mobilevisions.kiki.modules.chat;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import lk.mobilevisions.kiki.app.Application;
import lk.mobilevisions.kiki.modules.api.ChatAPI;

@Module
public class ChatWebModule {

    ChatAPI chatAPI;
    Application application;

    public ChatWebModule(Application application, ChatAPI chatAPI){
        this.application = application;
        this.chatAPI = chatAPI;
    }

    @Provides
    @Singleton
    public ChatWebManager providesChatWebManager(){
        return new ChatWebManager(application, chatAPI);
    }
}
