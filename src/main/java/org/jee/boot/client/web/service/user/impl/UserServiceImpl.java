package org.jee.boot.client.web.service.user.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.jee.boot.client.web.service.user.UserService;
import org.jee.boot.client.web.vo.UserSessionVO;
import org.jee.boot.user.api.UserInfoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static org.jee.boot.client.web.util.RedisKey.LOGIN_TOKEN_KEY;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    RedisTemplate redisTemplate;

    @Reference
    UserInfoApi userInfoApi;

    @Override
    public  UserSessionVO getUserSessionVO(String token) {
        Object obj = redisTemplate.opsForValue().get(LOGIN_TOKEN_KEY + token);
        if (obj != null) {
            return JSON.parseObject(obj.toString(), UserSessionVO.class);
        }
        return null;
    }
}
