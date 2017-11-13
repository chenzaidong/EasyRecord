package cn.chenzd.easyrecord.constant;


import cn.chenzd.easyrecord.R;

/**
 * 常量
 * Created by chenzaidong on 2017/8/25.
 */

public interface Constant {
    String SP_NAME = "easyRecordConfig";
    /**
     * 是否初始化数字密码key
     */
    String SP_KEY_INIT_DATABASE = "initDatabase";
    String SP_KEY_FINGER_PRINT = "fingerprint";
    String SP_KEY_INPUT_COUNT = "inputCount";
    String SP_KEY_INPUT_LAST_TIME = "inputLastTime";
    String SP_KEY_IS_FIRST = "isFirst";
    String DB_NAME = "easyRecord.db";
    int DEFAULT_ICON_ID = R.drawable.icon33;
    String DEFAULT_TRANSITION_NAME = "TransitionName";
    int MAX_PAGES = 15;
    int REQUEST_MEDIA_PROJECTION = 0x2893;
    int REQUEST_INIT_NUMBER_PASSWORD = 0x0001;
    int REQUEST_CHECK_NUMBER_PASSWORD = 0x0002;
    /**
     * 震动间隔时间
     */
    long VIBRATE_TIME=300;
    /**
     * 输入密码最大次数
     */
    int MAX_INPUT_NUM_MAX_COUNT=5;
    /**
     * 输错密码等待时间（秒）
     */
    int PASSWORD_ERROR_WAIT_TIME=20;

    String BAIDU_AK="1dc2dII7ZpL5bzCDUFT2uW4kQx4re04x";


    String BAIDU_MAP_BASE = "http://api.map.baidu.com/staticimage/v2?";
}
