package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author jiangdong
 * @date 2021/8/10 10:17
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
