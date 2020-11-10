package cn.lx.ihrm.common.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * cn.lx.ihrm.common.shiro
 *
 * @Author Administrator
 * @date 16:37
 */
@Configuration
public class CommonConfig {


    /**
     * 自定义session构建工厂
     *
     * @return
     */
    @Bean
    public SessionFactory sessionFactory() {
        SessionFactory sessionFactory = new SessionFactory() {
            /**
             * Creates a new {@code Session} instance based on the specified contextual initialization data.
             *
             * @param initData the initialization data to be used during {@link Session} creation.
             * @return a new {@code Session} instance.
             * @since 1.0
             */
            @Override
            public Session createSession(SessionContext initData) {
                if (initData != null) {
                    String host = initData.getHost();
                    if (host != null) {
                        SimpleSession simpleSession = new SimpleSession(host);
                        //设置session超时时间
                        simpleSession.setTimeout(1000 * 30);
                        return simpleSession;
                    }
                }
                SimpleSession simpleSession = new SimpleSession();
                //设置session超时时间
                simpleSession.setTimeout(1000 * 30);
                return simpleSession;

            }
        };
        return sessionFactory;
    }
}
