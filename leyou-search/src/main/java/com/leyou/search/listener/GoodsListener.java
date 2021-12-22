package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author jiangdong
 * @date 2021/8/14 9:35
 */
@Component
public class GoodsListener {
    @Autowired
    private SearchService searchService;

    @RabbitListener(bindings =@QueueBinding(
            value = @Queue(value = "LEYOU.SEARCH.SAVE.QUEUE",durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.update","item.insert"}
    ))
    public void save(Long id) throws IOException {
        if (id==null){
            return;
        }
        this.searchService.save(id);
        System.out.println("key:item.delete消息消費成功，處理ID："+id);

    }

    @RabbitListener(bindings =@QueueBinding(
            value = @Queue(value = "LEYOU.SEARCH.DELETE.QUEUE",durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(Long id) throws IOException {
        System.out.println(1);
        if (id==null){
            return;
        }
        this.searchService.delete(id);
        System.out.println("消息消費成功，處理ID："+id);

    }
}
