package com.leyou.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.commom.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResults;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiangdong
 * @date 2021/8/10 9:11
 */

@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private static final ObjectMapper OBJECT_MAPPER=new ObjectMapper();
    @Autowired
    private GoodsRepository goodsRepository;

    public SearchResults search(SearchRequest request) {
        String key = request.getKey();
        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        // 1、对key进行全文检索查询
        //MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);;
        BoolQueryBuilder boolQueryBuilder=buildBooleanQueryBuilder(request);
        queryBuilder.withQuery(boolQueryBuilder);

        // 2、通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(
                new String[]{"id","skus","subTitle"}, null));

        // 3、分页
        // 准备分页参数
        int page = request.getPage();
        int size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page - 1, size));

        //添加分类和品牌的聚合
        String categoryAggName= "categories";
        String brandAggName="brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));



        // 4、查询，获取结果
        AggregatedPage<Goods> pageInfo = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());

        //获取聚合结果集并解析

        List<Map<String,Object>> categories=getCategoryAggResult(pageInfo.getAggregation(categoryAggName));
        List<Brand> brands=getBrandAggResult(pageInfo.getAggregation(brandAggName));

        List<Map<String,Object>> specs=null;
        //判断是否是是一个分类，只有一个分类时，才做聚合
        if(!CollectionUtils.isEmpty(categories)&&categories.size()==1){
            //对参数进行聚合
            specs=getParamAggResult((long)categories.get(0).get("id"),boolQueryBuilder);
        }
        // 封装结果并返回
        return new SearchResults(pageInfo.getTotalElements(), (long)pageInfo.getTotalPages(), pageInfo.getContent(),categories,brands,specs);
    }
    /**
     * 构建bool查询构建器
     * @param request
     * @return
     */
    private BoolQueryBuilder buildBooleanQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));

        // 添加过滤条件
        if (CollectionUtils.isEmpty(request.getFilter())){
            return boolQueryBuilder;
        }

        for (Map.Entry<String, Object> entry : request.getFilter().entrySet()) {

            String key = entry.getKey();
            // 如果过滤条件是“品牌”, 过滤的字段名：brandId
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                // 如果是“分类”，过滤字段名：cid3
                key = "cid3";
            } else {
                // 如果是规格参数名，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }

        return boolQueryBuilder;
    }
//    /**
//     * 构建bool查询或者组合查询
//     * @param request
//     * @return
//     */
//    private BoolQueryBuilder buildBoolQueryBulder(SearchRequest request) {
//        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
//        //给bool查询添加基本的查询条件
//        boolQueryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
//        //添加过滤条件
//        //获取用户选择的过滤信息
//        Map<String, Object> filter = request.getFilter();
//        for (Map.Entry<String, Object> entry : request.getFilter().entrySet()) {
//            String key = entry.getKey();
//            if(StringUtils.equals("品牌",key)){
//                key="brandId";
//            }else if (StringUtils.equals("分类",key)){
//                key="cid3";
//            }else {
//                key="specs."+key+".keyword";
//            }
//            boolQueryBuilder.filter(QueryBuilders.termQuery("key",entry.getValue()));
//        }
//        return boolQueryBuilder;
//    }

    /**
     * 根据查询条件聚合规格参数
     * @param id
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(long id, BoolQueryBuilder basicQuery) {
        //自定义查询对象构建器
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //添加基本查询条件
        nativeSearchQueryBuilder.withQuery(basicQuery);
        //查询要聚合的规格参数
        List<SpecParam> params = this.specificationClient.queryParamByGid(null, id, null, true);

        //添加规格参数的聚合
        params.forEach(param->{
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs."+param.getName()+".keyword"));
        });
        //添加结果集过滤
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsRepository.search(nativeSearchQueryBuilder.build());
        //解析聚合结果集
        //key-聚合名称（规格参数名），value聚合对象
        List<Map<String,Object>> specs=new ArrayList<>();
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            //初始化map，{k:规格参数名，options：聚合参数的值}
            Map<String,Object> map=new HashMap<>();
            map.put("k",entry.getKey());
            //初始化一个options几个，收集桶中的key
            List<String> options=new ArrayList<>();
            //获取聚合
            StringTerms terms=(StringTerms)entry.getValue();
            //获取桶集合
            terms.getBuckets().forEach(bucket ->{
                options.add(bucket.getKeyAsString());
            } );
            map.put("options",options);
            specs.add(map);
        }
        return specs;
    }

    /**
     * 解析品牌的聚合结果集
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {

        LongTerms terms=(LongTerms)aggregation;
//        List<Brand> brands=new ArrayList<>();
        //获取聚合中的桶

        return terms.getBuckets().stream().map(bucket -> {
            return this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
//        terms.getBuckets().forEach(bucket -> {
//            Brand brand=this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
//            brands.add(brand);
//
//        });
//
//
//        return brands;
    }

    /**
     * 解析分类的聚合结果集
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        LongTerms terms=(LongTerms)aggregation;

        return terms.getBuckets().stream().map(bucket -> {
            //初始化一个map
            Map<String,Object> map=new HashMap<>();
            //获取分类id
            Long id=bucket.getKeyAsNumber().longValue();
            //查询分类名称
            List<String> names=this.categoryClient.queryNamesByIds(Arrays.asList(id));
            map.put("id",id);
            map.put("name",names.get(0));
            return map;
        }).collect(Collectors.toList());

    }

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods=new Goods();
        //根据分类id查询名称
        List<String> names=this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
        //根据品牌id，查询品牌
        Brand brand=this.brandClient.queryBrandById(spu.getBrandId());

        //根据spu的id插叙所有的sku
        List<Sku> skus=this.goodsClient.querySkuBySpuId(spu.getId());
        List<Long> price=new ArrayList<>();
        //收集sku的必要信息
        List<Map<String,Object>> skuMapList=new ArrayList<>();
        skus.forEach(sku -> {
            price.add(sku.getPrice());

            Map<String,Object> map=new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            //获取sku中的图片，用逗号分开,如果是多张，只要第一张
            map.put("image",StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);
            skuMapList.add(map);
        });
        //根据spu中的cid3查询除所有的搜索规格参数
        List<SpecParam> params = this.specificationClient.queryParamByGid(null, spu.getCid3(), null, true);
        //根据spuid查询spudetails
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的规格的参数值，反序列化为map
        Map<String,Object> genericSpecMap = OBJECT_MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {});
        //把特殊的规格参数值，进行反序列化
        Map<String,List<Object>> specialSpecMap = OBJECT_MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {});
        Map<String, Object> spec=new HashMap<>();
        params.forEach(param->{
            if (param.getGeneric()){
                //如果是通用类型参数
                String value = genericSpecMap.get(param.getId().toString()).toString();
                //判断是否是数值类型
                if(param.getNumeric()){
                    value= chooseSegment(value, param);
                }
                spec.put(param.getName(),value);
            }else {
                //如果是特殊的规格参数
                List<Object> values = specialSpecMap.get(param.getId().toString());
                spec.put(param.getName(),values);
            }
        });
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //todo 拼接all字段，需要分类名称，以及品牌名称
        goods.setAll(spu.getTitle()+" "+ StringUtils.join(names, " ") +" "+brand.getName());
        //获取spu下面所有的sku的价格
        goods.setPrice(price);
        //获取spu下的所有的sku，转化为json字符串保存
        goods.setSkus(OBJECT_MAPPER.writeValueAsString(skuMapList));
        //获取所有的查询的规格参数{name：value}
        goods.setSpecs(spec);
        return goods;
    }
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }


    public void save(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = this.buildGoods(spu);
        this.goodsRepository.save(goods);
    }

    public void delete(Long id) {
        this.goodsRepository.deleteById(id);
    }
}
