package com.leyou.goods.listener;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.*;
/**
 * @author jiangdong
 * @date 2021/8/14 9:19
 */
@Component
public class GoodsListener {
    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.ITEM.SAVE.QUEUE",durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.update","item.insert"}
    ))
    public void save(Long id){
        if(id==null){
            return;
        }
        this.goodsHtmlService.createHtml(id);
        System.out.println("key:item.delete消息消費成功，處理ID："+id);

    }

}
