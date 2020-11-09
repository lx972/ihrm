package cn.lx.ihrm.common.feign;

import cn.lx.ihrm.common.entity.ResultCode;
import cn.lx.ihrm.common.exception.CommonException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * cn.lx.ihrm.common.feign
 *
 * @Author Administrator
 * @date 11:46
 */
@Configuration
public class FeignInterceptor {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                if (null==requestAttributes){
                    throw new CommonException(ResultCode.UNAUTHORISE);
                }
                template.header("Authorization",requestAttributes.getSessionId());
            }
        };
    }
}
