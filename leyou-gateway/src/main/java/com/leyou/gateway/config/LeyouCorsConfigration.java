package com.leyou.gateway.config;

import com.netflix.config.sources.URLConfigurationSource;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfigration {
    @Bean
    public CorsFilter corsFilter(){
        //返回corsFilter实例，参数：cors配置源对象
        //初始化cors配置对象
        CorsConfiguration corsConfiguration=new CorsConfiguration();
        //允许跨域的域名,如果要允许cookie则不能写*
        corsConfiguration.addAllowedOrigin("http://manage.leyou.com");
        corsConfiguration.addAllowedOrigin("http://www.leyou.com");
        corsConfiguration.setAllowCredentials(true);//设置是否允许携带cookie
        corsConfiguration.addAllowedMethod("OPTIONS");
        corsConfiguration.addAllowedMethod("HEAD");
        corsConfiguration.addAllowedMethod("GET");
        corsConfiguration.addAllowedMethod("PUT");
        corsConfiguration.addAllowedMethod("POST");
        corsConfiguration.addAllowedMethod("DELETE");
        corsConfiguration.addAllowedMethod("PATCH");
        //问题不会出现在这个地方吧！！！！！！
        corsConfiguration.addAllowedHeader("*");//允许携带任何头信息
        //初始化cors配置源对象
        UrlBasedCorsConfigurationSource configurationSource=new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",corsConfiguration);

        return new CorsFilter(configurationSource);
    }
}
