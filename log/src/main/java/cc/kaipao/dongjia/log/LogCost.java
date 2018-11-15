package cc.kaipao.dongjia.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * email:xubo@idongjia.cn
 * time:2018/11/7
 *
 * @author xb
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface LogCost {
    String value();
}
