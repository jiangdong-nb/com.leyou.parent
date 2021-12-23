package com.leyou.search.client;

import api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author jiangdong
 * @date 2021/8/9 19:26
 */

@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
}
