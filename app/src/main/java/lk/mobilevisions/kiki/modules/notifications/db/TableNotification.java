/**
 * Created by Chatura Dilan Perera on 7/1/2017.
 */
package lk.mobilevisions.kiki.modules.notifications.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import lk.mobilevisions.kiki.db.MainDatabase;

@Table(database = MainDatabase.class)
public class TableNotification extends BaseModel {

    @PrimaryKey
    @Column
    private long uid;

    @Column
    private String message;

    @Column
    private Date date;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
