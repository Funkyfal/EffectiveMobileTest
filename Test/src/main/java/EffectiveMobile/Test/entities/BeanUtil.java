package EffectiveMobile.Test.entities;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanUtil implements ApplicationContextAware {
    private static ApplicationContext ctx;
    public void setApplicationContext(ApplicationContext c) {
        ctx = c;
    }
    public static <T> T getBean(Class<T> cls) {
        return ctx.getBean(cls);
    }
}
