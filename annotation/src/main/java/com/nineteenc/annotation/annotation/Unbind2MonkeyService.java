package com.nineteenc.annotation.annotation;

import com.nineteenc.annotation.util.ServiceNameEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author    zhengchengbin
 * Describe:
 * Data:      2019/8/20 12:09
 * Modify by:
 * Modification date:
 * Modify content:
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Unbind2MonkeyService {

    ServiceNameEnum serviceName();

}
