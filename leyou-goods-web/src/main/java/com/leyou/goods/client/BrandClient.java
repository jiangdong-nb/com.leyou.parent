package com.leyou.goods.client;

import api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author jiangdong
 * @date 2021/8/9 19:22
 */

@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
