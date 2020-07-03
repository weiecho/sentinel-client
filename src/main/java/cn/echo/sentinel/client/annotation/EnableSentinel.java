package cn.echo.sentinel.client.annotation;


import cn.echo.sentinel.client.config.SentinelPostProcessor;
import cn.echo.sentinel.client.config.SentinelProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SentinelProperties.class, SentinelPostProcessor.class})
public @interface EnableSentinel {

}
