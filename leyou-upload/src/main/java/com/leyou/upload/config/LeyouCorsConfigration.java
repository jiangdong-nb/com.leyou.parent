package com.leyou.upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfigration {
    /**
     * 图片上传跨域
     * @return
     */
    @Bean
    public CorsFilter corsFilter(){
        //返回corsFilter实例，参数：cors配置源对象
        //初始化cors配置对象
        CorsConfiguration corsConfiguration=new CorsConfiguration();
        //允许跨域的域名,如果要允许cookie则不能写*
        corsConfiguration.addAllowedOrigin("http://manage.leyou.com");
        corsConfiguration.setAllowCredentials(true);//设置是否允许携带cookie
        corsConfiguration.addAllowedMethod("*");//*代表所有的请求方法GET POST PUT
        corsConfiguration.addAllowedMethod("*");//允许携带任何头信息
        //初始化cors配置源对象
        UrlBasedCorsConfigurationSource configurationSource=new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",corsConfiguration);

        return new CorsFilter(configurationSource);
    }
}
