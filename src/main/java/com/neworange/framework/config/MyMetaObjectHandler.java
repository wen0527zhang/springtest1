package com.neworange.framework.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2024/8/11 20:02
 * @ description
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        //metaObject 元数据
        this.setFieldValByName("createTime",new Date(),metaObject);
        this.setFieldValByName("updateTime",new Date(),metaObject);
        try {
            this.setFieldValByName("deleted",0,metaObject);
            //用户id
            this.setFieldValByName("uid",String.class,metaObject);
        }catch (Exception e){
            log.error("insertFill error ",e);
        }
    }
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }

}
