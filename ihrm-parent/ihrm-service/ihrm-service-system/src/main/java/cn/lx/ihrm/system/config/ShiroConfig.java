package cn.lx.ihrm.system.config;

import cn.lx.ihrm.system.realm.MyRealm;
import cn.lx.ihrm.system.service.IUserService;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * cn.lx.shiro.config
 *
 * @Author Administrator
 * @date 10:54
 */

@Configuration
public class ShiroConfig {


    @Autowired
    private IUserService iUserService;


    /**
     * 注入自己的realm域
     */
   /*@Autowired
   private MyRealm myRealm;*/
   @Bean
   public MyRealm myRealm(){
       MyRealm myRealm = new MyRealm();
       myRealm.setIUserService(iUserService);
       //设置缓存管理器
       myRealm.setCacheManager(redisCacheManager);
       //设置启用缓存
       myRealm.setAuthenticationCachingEnabled(true);
       myRealm.setAuthorizationCachingEnabled(true);
       return myRealm;
   }

    /**
     * shiro的redis缓存管理器
     */
   @Autowired
   private RedisCacheManager redisCacheManager;

    /**
     * shiro的session缓存管理器
     */
   @Autowired
   private RedisCacheSessionDAO redisCacheSessionDAO;

    /**
     * 定义一个安全管理器
     * 在springboot项目中，它已经自动向spring容器中注入了一个，但是，
     * 他的默认设置不符合我们的需求，我们需要自己创建一个，使用自己的配置
     * 原始配置我们会在下面给出来
     * @return
     */
   @Bean
   public SessionsSecurityManager securityManager(){
       DefaultWebSecurityManager defaultWebSecurityManager=new DefaultWebSecurityManager();
       defaultWebSecurityManager.setRealm(myRealm());
       defaultWebSecurityManager.setCacheManager(redisCacheManager);
       defaultWebSecurityManager.setSessionManager(sessionManager());
       return defaultWebSecurityManager;
   }


    /**
     * 配置一个session管理器
     * @return
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisCacheSessionDAO);
        return sessionManager;
    }


    /**
     * 路径映射到给定的过滤器，以允许不同的路径具有不同的访问级别
     * 这个我们也需要覆盖springboot的自动配置
     * @return
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        //匿名访问
        chainDefinition.addPathDefinition("/user/login", "anon");
        chainDefinition.addPathDefinition("/unauthorizedUrl", "anon");
        chainDefinition.addPathDefinition("/successUrl", "anon");
        //登出的url
        chainDefinition.addPathDefinition("/user/logout", "logout");
        //其他所有路径全部需要认证
        chainDefinition.addPathDefinition("/**", "authc");
        return chainDefinition;
    }


    /**
     *  @DependsOn("lifecycleBeanPostProcessor") 控制bean初始化顺序
     *  表示该bean依赖于lifecycleBeanPostProcessor这个bean
     *  lifecycleBeanPostProcessor 这个spring-boot已经为我们自动注入了
     *  就在ShiroBeanAutoConfiguration中
     *
     *  这个bean和下面那个都是参照shiro官网中的spring配置文件来创建的bean
     *  {
     *  <!-- Enable Shiro Annotations for Spring-configured beans.  Only run after -->
     *  <!-- the lifecycleBeanProcessor has run: -->
     *  <bean class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator" depends-on="lifecycleBeanPostProcessor"/>
     *      <bean class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
     *      <property name="securityManager" ref="securityManager"/>
     *  </bean>
     *  }
     *  官网上已经指明了如果想使用注解，就必须创建这两个bean
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator =
                new DefaultAdvisorAutoProxyCreator();
        //shiro官网未指明需要该项配置，但在springboot中，必须加入
        // ，否则配置的匿名访问不生效
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
    }
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

}
