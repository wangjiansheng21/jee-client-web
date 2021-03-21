package org.jee.boot.client.web.service.wx.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jee.boot.client.web.request.wx.Code2SessionRequest;
import org.jee.boot.client.web.request.wx.GetPhoneNumRequest;
import org.jee.boot.client.web.service.wx.WxService;
import org.jee.boot.client.web.vo.AccesTokenVO;
import org.jee.boot.client.web.vo.Code2SessionVO;
import org.jee.boot.client.web.vo.WxSessionVO;
import org.jee.boot.client.web.wx.AESForWeixinGetPhoneNumber;
import org.jee.boot.dubbo.response.RpcResponse;
import org.redisson.Redisson;
import org.redisson.RedissonLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

import static org.jee.boot.client.web.util.RedisKey.WX_ACCES_TOEKN;
import static org.jee.boot.client.web.util.RedisKey.WX_SESSION_KEY;

@Service
@Slf4j
public class WxServiceImpl implements WxService {


    @Value("${appId}")
    private String appId;

    @Value("${appSecret}")
    private String appSecret;

    @Value("${wx.api.url}")
    private String wxApiUrl;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public RpcResponse<Code2SessionVO> code2Session(Code2SessionRequest code2SessionRequest) {
        RpcResponse rpcResponse = RpcResponse.ok();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(wxApiUrl + "sns/jscode2session?" + "appid=" + appId + "&secret=" + appSecret + "&js_code=" + code2SessionRequest.getCode() + "&grant_type=authorization_code", String.class);
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        log.info("调用微信登录校验接口响应：{}", body);
        if (jsonObject.get("errcode") != null) {
            log.error("调用微信登录校验接口响应异常：{}", body);
            rpcResponse.setSysException();
        } else {
            String openId = jsonObject.get("openid").toString();
            String sessionKey = jsonObject.get("session_key").toString();
            Code2SessionVO code2SessionVO = new Code2SessionVO();
            code2SessionVO.setOpenId(openId);
            code2SessionVO.setSessionKey(sessionKey);
            rpcResponse.setData(code2SessionVO);
            //redis存储微信登录会话标志
            WxSessionVO wxSessionVO = new WxSessionVO();
            wxSessionVO.setOpenId(openId);
            wxSessionVO.setSessionKey(sessionKey);
            redisTemplate.opsForValue().set(WX_SESSION_KEY + sessionKey, JSON.toJSONString(wxSessionVO), 1, TimeUnit.DAYS);
        }
        return rpcResponse;
    }

    @Override
    public RpcResponse<String> getPhoneNum(GetPhoneNumRequest getPhoneNumRequest) {
        RpcResponse rpcResponse = RpcResponse.ok();
        WxSessionVO wxSessionVO = getWxSessionVO(getPhoneNumRequest.getSessionKey());
        if (wxSessionVO == null) {
            rpcResponse.setSysFail("sessionKey无效!");
            return rpcResponse;
        }
        AESForWeixinGetPhoneNumber aes = new AESForWeixinGetPhoneNumber(getPhoneNumRequest.getEncryptedData(), getPhoneNumRequest.getSessionKey(), getPhoneNumRequest.getIv());
        String phoneNum = aes.decrypt();
        rpcResponse.setData(phoneNum);
        wxSessionVO.setPhoneNum(phoneNum);
        //redis存储微信登录会话标志
        redisTemplate.opsForValue().set(WX_SESSION_KEY + wxSessionVO.getSessionKey(), JSON.toJSONString(wxSessionVO), 1, TimeUnit.DAYS);
        return rpcResponse;
    }

    @Override
    public WxSessionVO getWxSessionVO(String sessionKey) {
        WxSessionVO wxSessionVO = null;
        Object session = redisTemplate.opsForValue().get(WX_SESSION_KEY + sessionKey);
        if (session != null) {
            wxSessionVO = (WxSessionVO) JSON.parseObject(session.toString(), WxSessionVO.class);
        }
        return wxSessionVO;
    }

    @Override
    public RpcResponse<AccesTokenVO> getAccesToken(String appId) {
        RpcResponse rpcResponse = RpcResponse.ok();
        if (StringUtils.isEmpty(appId)) {
            rpcResponse.setSysFail("appId 不可以为空");
            return rpcResponse;
        }
        AccesTokenVO accesTokenVO = null;
        //获取redis中的accesToke凭证
        Object accesTokenObj = redisTemplate.opsForValue().get(WX_ACCES_TOEKN + appId);
        //redis没有则调用微信接口获取，并存储在redis中
        if (accesTokenObj == null) {
            String url = wxApiUrl + "cgi-bin/token?grant_type=client_credential&" + "appid=" + appId + "&secret=" + appSecret;
            log.info("调用微信获取accesToken接口请求url：{}", url);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            String body = responseEntity.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            log.info("调用微信获取accesToken接口响应：{}", body);
            if (jsonObject.get("errcode") != null) {
                log.error("调用微信获取accesToken接口响应响应异常：{}", body);
                rpcResponse.setSysException();
            }
            String accesToken = jsonObject.get("access_token").toString();
            Integer expire = (Integer) jsonObject.get("expires_in");
            accesTokenVO = new AccesTokenVO();
            accesTokenVO.setAccesToken(accesToken);
            accesTokenVO.setExpiresIn(expire.longValue());
            redisTemplate.opsForValue().set(WX_ACCES_TOEKN + appId, JSON.toJSONString(accesTokenVO), expire - (60 * 10), TimeUnit.SECONDS);
        } else {
            accesTokenVO = JSON.parseObject(accesTokenObj.toString(), AccesTokenVO.class);
        }

        rpcResponse.setData(accesTokenVO);
        return rpcResponse;
    }

}
