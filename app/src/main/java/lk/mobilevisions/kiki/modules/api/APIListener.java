package lk.mobilevisions.kiki.modules.api;

import java.util.List;

/**
 * Created by Chatura Dilan Perera on 28/8/2016.
 */
public interface APIListener<T> {

    public void onSuccess(T result, List<Object> params);

    public void onFailure(Throwable t);
}
