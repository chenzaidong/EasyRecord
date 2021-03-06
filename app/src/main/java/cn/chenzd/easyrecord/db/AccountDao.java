package cn.chenzd.easyrecord.db;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import cn.chenzd.easyrecord.entity.Account;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ACCOUNT".
*/
public class AccountDao extends AbstractDao<Account, Long> {

    public static final String TABLENAME = "ACCOUNT";

    /**
     * Properties of entity Account.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TypeId = new Property(1, Long.class, "typeId", false, "TYPE_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Title = new Property(3, String.class, "title", false, "TITLE");
        public final static Property Password = new Property(4, String.class, "password", false, "PASSWORD");
        public final static Property Notes = new Property(5, String.class, "notes", false, "NOTES");
        public final static Property CreateDate = new Property(6, String.class, "createDate", false, "CREATE_DATE");
        public final static Property IconId = new Property(7, int.class, "iconId", false, "ICON_ID");
        public final static Property LastModifyTime = new Property(8, String.class, "lastModifyTime", false, "LAST_MODIFY_TIME");
    }

    private DaoSession daoSession;

    private Query<Account> type_AccountsQuery;

    public AccountDao(DaoConfig config) {
        super(config);
    }
    
    public AccountDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACCOUNT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TYPE_ID\" INTEGER," + // 1: typeId
                "\"NAME\" TEXT," + // 2: name
                "\"TITLE\" TEXT NOT NULL ," + // 3: title
                "\"PASSWORD\" TEXT," + // 4: password
                "\"NOTES\" TEXT," + // 5: notes
                "\"CREATE_DATE\" TEXT," + // 6: createDate
                "\"ICON_ID\" INTEGER NOT NULL ," + // 7: iconId
                "\"LAST_MODIFY_TIME\" TEXT);"); // 8: lastModifyTime
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_ACCOUNT_TITLE ON \"ACCOUNT\"" +
                " (\"TITLE\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACCOUNT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Account entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long typeId = entity.getTypeId();
        if (typeId != null) {
            stmt.bindLong(2, typeId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
        stmt.bindString(4, entity.getTitle());
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(5, password);
        }
 
        String notes = entity.getNotes();
        if (notes != null) {
            stmt.bindString(6, notes);
        }
 
        String createDate = entity.getCreateDate();
        if (createDate != null) {
            stmt.bindString(7, createDate);
        }
        stmt.bindLong(8, entity.getIconId());
 
        String lastModifyTime = entity.getLastModifyTime();
        if (lastModifyTime != null) {
            stmt.bindString(9, lastModifyTime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Account entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long typeId = entity.getTypeId();
        if (typeId != null) {
            stmt.bindLong(2, typeId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
        stmt.bindString(4, entity.getTitle());
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(5, password);
        }
 
        String notes = entity.getNotes();
        if (notes != null) {
            stmt.bindString(6, notes);
        }
 
        String createDate = entity.getCreateDate();
        if (createDate != null) {
            stmt.bindString(7, createDate);
        }
        stmt.bindLong(8, entity.getIconId());
 
        String lastModifyTime = entity.getLastModifyTime();
        if (lastModifyTime != null) {
            stmt.bindString(9, lastModifyTime);
        }
    }

    @Override
    protected final void attachEntity(Account entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Account readEntity(Cursor cursor, int offset) {
        Account entity = new Account( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // typeId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.getString(offset + 3), // title
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // password
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // notes
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // createDate
            cursor.getInt(offset + 7), // iconId
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // lastModifyTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Account entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTypeId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTitle(cursor.getString(offset + 3));
        entity.setPassword(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setNotes(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCreateDate(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setIconId(cursor.getInt(offset + 7));
        entity.setLastModifyTime(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Account entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Account entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Account entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "accounts" to-many relationship of Type. */
    public List<Account> _queryType_Accounts(Long typeId) {
        synchronized (this) {
            if (type_AccountsQuery == null) {
                QueryBuilder<Account> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.TypeId.eq(null));
                type_AccountsQuery = queryBuilder.build();
            }
        }
        Query<Account> query = type_AccountsQuery.forCurrentThread();
        query.setParameter(0, typeId);
        return query.list();
    }

}
