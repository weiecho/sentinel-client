package cn.echo.sentinel.client.config;

import com.alibaba.csp.sentinel.log.RecordLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

@Slf4j
public class SentinelPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }

        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
        // 通过主动调用beanFactory#getBean来显示实例化目标bean
        SentinelProperties sentinelProperties = this.beanFactory.getBean(SentinelProperties.class);
        log.info("Sentinel Post Processor init.");
    }
}
