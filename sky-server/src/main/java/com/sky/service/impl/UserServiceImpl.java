package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties properties;

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User WxLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获取微信用户的OpenId
        String openid = getOpenid(userLoginDTO);

        //判断这个OpenId是否为空，如果为空就表明登录失败，抛出异常
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断这个OpenId是否已经在我们的表里面（即新老用户）
        User user = userMapper.getByOpenid(openid);
        //如果是新用户，就应该自动注册
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();

            userMapper.insert(user);
        }
        //返回这个用户对象
        return user;
    }

    private String getOpenid(UserLoginDTO userLoginDTO) {
        //调用微信服务器的接口，来获取当前用户微信的OpenId  请求这个地址：https://api.weixin.qq.com/sns/jscode2session
        //通过httpClient 来向这个地址发送请求
        Map<String, String> map = new HashMap<String, String>();
        map.put("appid", properties.getAppid());
        map.put("secret", properties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        //获取OpenId
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("OpenId");
        return openid;
    }
}
