package com.leyou.user.service;

import com.leyou.com.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * @author jiangdong
 * @date 2021/8/14 16:49
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String KEY_PREFIX="user:verify:";
    /**
     * 校驗數據是否可用
     * @param data
     * @param type
     * @return
     */
    public Boolean checkUser(String data, Integer type) {
        User record = new User();
        if (type==1){
            record.setUsername(data);
        }else if (type==2){
            record.setPhone(data);
        }else {
            return null;
        }
        return this.userMapper.selectCount(record)==0;
    }

    public void verifyCode(String phone) {

        if(StringUtils.isBlank(phone)){
            return;
        }
        //生成驗證碼
        String code = NumberUtils.generateCode(6);
        Map<String,String> map=new HashMap<>();
        map.put("phone",phone);
        map.put("code",code);
        this.amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE","sms.verify.code",map);
        //發送消息到，rabitMQ
        this.stringRedisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
        //把驗證碼保存到redis


    }

    public void register(User user, String code) {
        //查询redis中的验证码
        String redisCode = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        //1:校验验证码
        if(!StringUtils.equals(code,redisCode)){
            System.out.println("用户输出验证码与系统中的验证码不一致,系统中："+redisCode+"用户输入:"+code);
            return ;
        }
        //生成salt
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        //新增用户
        user.setId(null);
        user.setCreated(new Date());
        this.userMapper.insert(user);
        //删除redis中的验证码，提升系统的内存利用率

    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        //判断user是否为空
        if(user==null){
            return null;
        }
        //获取盐的同时对盐进行加盐加密
        password=CodecUtils.md5Hex(password,user.getSalt());
        //和数据库中的密码比较
        if(StringUtils.equals(password,user.getPassword())){
            return user;
        }
        return null;
    }
}
