package com.leyou.goods.client;

import api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author jiangdong
 * @date 2021/8/9 19:27
 */
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi {
}
