package com.heaboy.annotation;

import javax.xml.ws.RequestWrapper;
import java.lang.annotation.*;

/**
 * controller声明
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @ interface Controller {
}
