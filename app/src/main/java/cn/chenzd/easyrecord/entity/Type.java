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
import cn.chenzd.easyrecord.db.AccountDao;
import cn.chenzd.easyrecord.db.TypeDao;


/**
 * 账户类型封装类
 * Created by chenzaidong on 2017/8/24.
 */
@Entity
public class Type implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    /**
     * 账户名称
     */
    @Index @NotNull
    private String name;
    /**
     * 图标资源Id
     */
    private int iconResId;

    private boolean isDefaultType;
    /**
     * 排序序号
     */
    private int order;
    /**
     * 一对多关系 对应多个账号
     */
    @ToMany(referencedJoinProperty = "typeId")
    private List<Account> accounts;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.iconResId);
        dest.writeByte(this.isDefaultType ? (byte) 1 : (byte) 0);
        dest.writeInt(this.order);
        dest.writeTypedList(this.accounts);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResId() {
        return this.iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public boolean getIsDefaultType() {
        return this.isDefaultType;
    }

    public void setIsDefaultType(boolean isDefaultType) {
        this.isDefaultType = isDefaultType;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 96040862)
    public List<Account> getAccounts() {
        if (accounts == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AccountDao targetDao = daoSession.getAccountDao();
            List<Account> accountsNew = targetDao._queryType_Accounts(id);
            synchronized (this) {
                if (accounts == null) {
                    accounts = accountsNew;
                }
            }
        }
        return accounts;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 121514453)
    public synchronized void resetAccounts() {
        accounts = null;
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
    @Generated(hash = 627329482)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTypeDao() : null;
    }

    public Type() {
    }

    protected Type(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.iconResId = in.readInt();
        this.isDefaultType = in.readByte() != 0;
        this.order = in.readInt();
        this.accounts = in.createTypedArrayList(Account.CREATOR);
    }

    @Generated(hash = 152560934)
    public Type(Long id, @NotNull String name, int iconResId, boolean isDefaultType,
            int order) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
        this.isDefaultType = isDefaultType;
        this.order = order;
    }

    public static final Parcelable.Creator<Type> CREATOR = new Parcelable.Creator<Type>() {
        @Override
        public Type createFromParcel(Parcel source) {
            return new Type(source);
        }

        @Override
        public Type[] newArray(int size) {
            return new Type[size];
        }
    };
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1125686680)
    private transient TypeDao myDao;
}
