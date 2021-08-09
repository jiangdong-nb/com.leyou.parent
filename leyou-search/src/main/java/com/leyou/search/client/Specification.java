package com.leyou.search.client;

import api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author jiangdong
 * @date 2021/8/9 19:27
 */
@FeignClient("item-service")
public interface Specification extends SpecificationApi {
}
