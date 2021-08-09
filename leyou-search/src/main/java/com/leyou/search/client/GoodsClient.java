package com.leyou.search.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author jiangdong
 * @date 2021/8/9 19:03
 */
@FeignClient("item-service")
public interface GoodsClient {

}
