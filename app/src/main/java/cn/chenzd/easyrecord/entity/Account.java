package cn.chenzd.easyrecord.entity;


import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import cn.chenzd.easyrecord.db.DaoSession;
import cn.chenzd.easyrecord.db.ImagesDao;
import cn.chenzd.easyrecord.db.AccountDao;


/**
 * 账户
 * Created by chenzaidong on 2017/8/24.
 */
@Entity
public class Account implements Parcelable {
    /**
     * id
     */
    @Id(autoincrement = true)
    private Long id;
    /**
     * 账户类型id
     */
    private Long typeId;
    /**
     * 名称
     */
    private String name;
    /**
     * 标题
     */
    @Index @NotNull
    private String title;
    /**
     * 密码
     */
    private String password;
    /**
     * 备注
     */
    private String notes;
    /**
     * 创建时间
     */
    private String createDate;
    /**
     * 图片logo
     */
    private int iconId;
    /**
     * 最后修改时间
     */
    private String lastModifyTime;
    /**
     * 图片
     */
    @ToMany(referencedJoinProperty = "accountId")
    private List<Images> images;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.typeId);
        dest.writeString(this.name);
        dest.writeString(this.title);
        dest.writeString(this.password);
        dest.writeString(this.notes);
        dest.writeString(this.createDate);
        dest.writeInt(this.iconId);
        dest.writeString(this.lastModifyTime);
        dest.writeTypedList(this.images);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getIconId() {
        return this.iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 490224860)
    public List<Images> getImages() {
        if (images == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ImagesDao targetDao = daoSession.getImagesDao();
            List<Images> imagesNew = targetDao._queryAccount_Images(id);
            synchronized (this) {
                if (images == null) {
                    images = imagesNew;
                }
            }
        }
        return images;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 604059028)
    public synchronized void resetImages() {
        images = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1812283172)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAccountDao() : null;
    }

    public Account() {
    }

    protected Account(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.typeId = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.title = in.readString();
        this.password = in.readString();
        this.notes = in.readString();
        this.createDate = in.readString();
        this.iconId = in.readInt();
        this.lastModifyTime = in.readString();
        this.images = in.createTypedArrayList(Images.CREATOR);
    }

    @Generated(hash = 1032079000)
    public Account(Long id, Long typeId, String name, @NotNull String title, String password,
            String notes, String createDate, int iconId, String lastModifyTime) {
        this.id = id;
        this.typeId = typeId;
        this.name = name;
        this.title = title;
        this.password = password;
        this.notes = notes;
        this.createDate = createDate;
        this.iconId = iconId;
        this.lastModifyTime = lastModifyTime;
    }

    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 335469827)
    private transient AccountDao myDao;
}
