package com.leyou.sms;

import com.leyou.pojo.Sms;
import org.springframework.stereotype.Component;

/**
 * @author jiangdong
 * @date 2021/8/14 18:50
 */
@Component
public class SmsUtils {
    public Sms sendSms(String phone){
        Sms sms = new Sms();
        sms.setCode(phone);
        return sms;
    }
}
