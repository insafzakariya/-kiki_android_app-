package lk.mobilevisions.kiki.modules.api;

import java.util.HashMap;
import java.util.List;

import lk.mobilevisions.kiki.chatweb.dto.ChatWebToken;
import lk.mobilevisions.kiki.modules.chat.ChatWebDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ChatAPI {

    @POST("rest/chat/chatlist")
    Call<List<ChatWebDTO>> getChatChannels(@Header("Content-Type") String content_type,
                                          @Body HashMap<String, Object> request);

    @POST("auth/login")
    Call<ChatWebToken> chatLogin(@Header("Content-Type") String content_type,
                                 @Body HashMap<String, Object> request);

}
