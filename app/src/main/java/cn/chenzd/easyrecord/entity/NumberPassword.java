package cn.chenzd.easyrecord.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 数字密码实体类
 * Created by chenzaidong on 2017/9/11.
 */
@Entity
public class NumberPassword {
    @Id(autoincrement = true)
    private Long id;
    private String password;
    @Generated(hash = 1623097523)
    public NumberPassword(Long id, String password) {
        this.id = id;
        this.password = password;
    }
    @Generated(hash = 1152569392)
    public NumberPassword() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
