package org.jee.boot.client.web.service.wx.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jee.boot.client.web.request.wx.Code2SessionRequest;
import org.jee.boot.client.web.request.wx.GetPhoneNumRequest;
import org.jee.boot.client.web.service.wx.WxService;
import org.jee.boot.client.web.vo.Code2SessionVO;
import org.jee.boot.client.web.wx.AESForWeixinGetPhoneNumber;
import org.jee.boot.dubbo.response.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class WxServiceImpl implements WxService {

    @Value("${appId}")
    private String appId;

    @Value("${appSecret}")
    private String appSecret;

    @Value("${wx.session.url}")
    private String sessionUrl;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public RpcResponse<Code2SessionVO> code2Session(Code2SessionRequest code2SessionRequest) {
        RpcResponse rpcResponse=RpcResponse.ok();
        ResponseEntity<String> responseEntity= restTemplate.getForEntity(sessionUrl + "appid=" + appId + "&secret=" + appSecret + "&js_code=" + code2SessionRequest.getCode() + "&grant_type=authorization_code", String.class);
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        log.info("调用微信登录校验接口响应：{}",body);
        if(jsonObject.get("errcode")!=null){
            log.error("调用微信登录校验接口响应异常：{}",body);
            rpcResponse.setSysException();
        }else{
            String openId=  jsonObject.get("openid").toString();
            String sessionKey=jsonObject.get("session_key").toString();
            Code2SessionVO code2SessionVO=new Code2SessionVO();
            code2SessionVO.setOpenId(openId);
            code2SessionVO.setSessionKey(sessionKey);
            rpcResponse.setData(code2SessionVO);
            //redis 存储微信登录会话标志
        }
        return rpcResponse;
    }

    @Override
    public RpcResponse<String> getPhoneNum(GetPhoneNumRequest getPhoneNumRequest) {
        RpcResponse rpcResponse= RpcResponse.ok();
        AESForWeixinGetPhoneNumber aes = new AESForWeixinGetPhoneNumber(getPhoneNumRequest.getEncryptedData(), getPhoneNumRequest.getSessionKey(), getPhoneNumRequest.getIv());
        rpcResponse.setData(aes.decrypt());
        return  rpcResponse;
    }
}
