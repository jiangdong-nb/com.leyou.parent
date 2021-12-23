package com.leyou.goods.controller;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author jiangdong
 * @date 2021/8/12 21:55
 */
@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsHtmlService goodsHtmlService;
    @GetMapping("/item/{id}.html")
    public String doItemPage(@PathVariable("id")Long id, Model model){
        Map<String,Object> map=this.goodsService.loadData(id);
        model.addAllAttributes(map);
        System.out.println(1);
        System.out.println(map);
        for (Map.Entry<String,Object> entry:map.entrySet()){
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        this.goodsHtmlService.createHtml(id);
        return "item";
    }
}
