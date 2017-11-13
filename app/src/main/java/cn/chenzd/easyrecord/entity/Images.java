package cn.chenzd.easyrecord.entity;


import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 图片资源
 * Created by chenzaidong on 2017/8/24.
 */
@Entity
public class Images implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    /**
     * 账户Id
     */
    private Long accountId;
    /**
     * 图片路径
     */
    @NotNull
    private String path;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.accountId);
        dest.writeString(this.path);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Images() {
    }

    protected Images(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.accountId = (Long) in.readValue(Long.class.getClassLoader());
        this.path = in.readString();
    }

    @Generated(hash = 1888254051)
    public Images(Long id, Long accountId, @NotNull String path) {
        this.id = id;
        this.accountId = accountId;
        this.path = path;
    }

    public static final Parcelable.Creator<Images> CREATOR = new Parcelable.Creator<Images>() {
        @Override
        public Images createFromParcel(Parcel source) {
            return new Images(source);
        }

        @Override
        public Images[] newArray(int size) {
            return new Images[size];
        }
    };
}
