package cn.chenzd.easyrecord.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 入侵记录实体类
 * Created by chenzaidong on 2017/9/12.
 */

@Entity
public class InvasionRecord {
    @Id(autoincrement = true)
    Long id;
    /**
     * 头像地址
     */
    String imagePath;
    /**
     * 地址
     */
    String address;
    /**
     * 日期
     */
    String date;
    @Generated(hash = 1562312678)
    public InvasionRecord(Long id, String imagePath, String address, String date) {
        this.id = id;
        this.imagePath = imagePath;
        this.address = address;
        this.date = date;
    }
    @Generated(hash = 2028466928)
    public InvasionRecord() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getImagePath() {
        return this.imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
