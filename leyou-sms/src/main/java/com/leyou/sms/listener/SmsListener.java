package com.leyou.sms.listener;

import com.leyou.pojo.Sms;
import com.leyou.sms.SmsUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author jiangdong
 * @date 2021/8/14 18:57
 */
@Component
public class SmsListener {
    @Autowired
    private SmsUtils smsUtils;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SMS.QUEUE", durable = "true"),
            exchange = @Exchange(value = "LEYOU.SMS.EXCHANGE",
                    ignoreDeclarationExceptions = "true"),
            key = {"sms.verify.code"}))
    public void listenSms(Map<String, String> msg) throws Exception {
        if (msg == null || msg.size() <= 0) {
            // 放弃处理
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");

        if (phone==null|| code==null) {
            // 放弃处理
            return;
        }
        // 发送消息
        Sms sms = this.smsUtils.sendSms(phone);
        System.out.println("消息發送成功,phone："+phone+"code:"+code);

    }
}
