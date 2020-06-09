package lk.mobilevisions.kiki.audio.event;

public class SearchNavigationEvent {
    public String mSearchKey;
    public String mtype;

    public SearchNavigationEvent (String searchKey, String type){
        mSearchKey = searchKey;
        mtype = type;
    }

    public String getmSearchKey(){
        return mSearchKey;
    }

    public String getMtype(){
        return mtype;
    }

}
