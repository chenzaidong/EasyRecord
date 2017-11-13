package cn.chenzd.easyrecord.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lzy.ninegrid.NineGridView;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.util.ArrayList;
import java.util.List;

import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.db.DaoMaster;
import cn.chenzd.easyrecord.db.DaoSession;
import cn.chenzd.easyrecord.db.TypeDao;
import cn.chenzd.easyrecord.entity.Type;
import cn.chenzd.easyrecord.utils.ActivityUtils;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static cn.chenzd.easyrecord.constant.Constant.DB_NAME;
import static cn.chenzd.easyrecord.constant.Constant.SP_KEY_INIT_DATABASE;
import static cn.chenzd.easyrecord.constant.Constant.SP_NAME;


/**
 * Created by chenzaidong on 2017/8/24.
 */

public class EasyRecordApplication extends Application {
    private static Context mContext;
    private static DaoSession mDaoSession;
    private static int[] resIds;
    private static final String KEY = "EasyRecordDBKEY";
    @Override
    public void onCreate() {
        super.onCreate();
        //LeakCanary.install(this);
        //Stetho.initializeWithDefaults(this);
        mContext = getApplicationContext();
        initLog();
        initDB();
        initIconResource();
        initNineGridImageLoader();
    }


    /**
     * 初始化九宫格视图图片加载器
     */
    private void initNineGridImageLoader() {
        NineGridView.setImageLoader(new GlideImageLoader());
    }

    /**
     * Glide 加载
     */
    private class GlideImageLoader implements NineGridView.ImageLoader {

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context).load(url).error(R.drawable.error)
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }

    /**
     * 初始化Log信息
     */
    private void initLog() {
        Log.i("EasyRecord","initLog");
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("EasyRecord")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });

    }

    /**
     * 初始化图标资源
     */
    private void initIconResource() {
        resIds = ActivityUtils.getListRes(R.array.icons_id);
    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        if (mDaoSession == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME,null);
            DaoMaster daoMaster = new DaoMaster(helper.getEncryptedWritableDb(KEY));
            mDaoSession = daoMaster.newSession();
        }
        SharedPreferences sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        boolean initDB = sharedPreferences.getBoolean(SP_KEY_INIT_DATABASE, false);
        if (!initDB) {
            Observable.just(getContext().getResources().getStringArray(R.array.default_types))
                    .observeOn(Schedulers.computation())
                    .map(new Function<String[], List<Type>>() {
                        @Override
                        public List<Type> apply(@NonNull String[] strings) throws Exception {
                            ArrayList<Type> types = new ArrayList<>();
                            int[] icons = ActivityUtils.getListRes(R.array.default_type_icons);
                            for (int i = 0; i < strings.length; i++) {
                                Type type = new Type();
                                type.setName(strings[i]);
                                type.setOrder(i);
                                type.setIconResId(icons[i]);
                                types.add(type);
                                type.setIsDefaultType(true);
                            }
                            return types;
                        }
                    }).observeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Type>>() {//保存数据库
                        @Override
                        public void accept(List<Type> types) throws Exception {
                            Logger.d(types);
                            mDaoSession.getNumberPasswordDao().deleteAll();
                            mDaoSession.getAccountDao().deleteAll();
                            mDaoSession.getImagesDao().deleteAll();
                            TypeDao typeDao = mDaoSession.getTypeDao();
                            typeDao.deleteAll();
                            typeDao.insertInTx(types);
                        }
                    });
            getSharedPreferences(SP_NAME, MODE_PRIVATE).edit().putBoolean(SP_KEY_INIT_DATABASE, true).apply();
        }
    }

    /**
     * 获取application context
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    /**
     * 获取daoSession对象
     *
     * @return
     */
    public static DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * 获取图标资源
     */
    public static int[] getIconResource() {
        return resIds;
    }
}
