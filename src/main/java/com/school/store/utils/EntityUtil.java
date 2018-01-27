package com.school.store.utils;

import com.school.store.base.model.BaseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 所有entity的工具方法，只有标注为 component 才能被扫到
 */
@Component
public class EntityUtil {

    /**
     * 传进一个 继承自 BaseEntity的类，然后set他的创建时间，最后更新时间，创建者，最后更新者
     * 这是默认方法，不用传递时间，只需要给创建者和更新者，如果 创建者  和 更新者 为 null就不进行set
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T extends BaseEntity> T updateInfoDefault(T entity, String creatorId, String updatorId, boolean create_or_update) {

        if (entity != null) {
            // 如果是创建就需要修改创建时间，否则不需要
            if(create_or_update){
                entity.setCreateTime(getNowDate());
            }
            entity.setUpdateTime(getNowDate());

            if (creatorId != null) {
                entity.setCreateAdmin(creatorId);
            }
            if (updatorId != null) {
                entity.setUpdateAdmin(updatorId);
            }
        }
        return entity;
    }


    /**
     * 传进一个 继承自 BaseEntity的类，然后set他的创建时间，最后更新时间，创建者，最后更新者
     * 这是自定义方法，跟上面的默认方法对立
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T extends BaseEntity> T updateInfo(T entity, Date createTime, Date updateTime, String creatorId, String updatorId) {

        if (entity != null) {

            if (createTime != null) {
                entity.setCreateTime(createTime);
            }
            if (updateTime != null) {
                entity.setUpdateTime(updateTime);
            }
            if (creatorId != null) {
                entity.setCreateAdmin(creatorId);
            }
            if (updatorId != null) {
                entity.setUpdateAdmin(updatorId);
            }
        }
        return entity;
    }


    /**
     * 获取当前时间
     *
     * @return
     */
    public Date getNowDate() {
        return new Date();
    }

}
