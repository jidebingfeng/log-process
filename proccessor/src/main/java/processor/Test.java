package processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2018/5/27 11:18
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Test {

}