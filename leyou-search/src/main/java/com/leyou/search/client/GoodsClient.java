package com.leyou.search.client;

import api.GoodsApi;
import com.leyou.commom.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.search.pojo.Goods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author jiangdong
 * @date 2021/8/9 19:03
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {

}
