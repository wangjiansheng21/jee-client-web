package org.jee.boot.client.web.service.user.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.jee.boot.client.web.request.login.LoginOutRequest;
import org.jee.boot.client.web.request.login.LoginRequest;
import org.jee.boot.client.web.request.login.UserRegisterRequest;
import org.jee.boot.client.web.request.login.WxAuthLoginRequest;
import org.jee.boot.client.web.service.user.LoginService;
import org.jee.boot.client.web.service.wx.WxService;
import org.jee.boot.client.web.vo.LoginVO;
import org.jee.boot.client.web.vo.UserSessionVO;
import org.jee.boot.client.web.vo.WxSessionVO;
import org.jee.boot.dubbo.response.RpcResponse;
import org.jee.boot.user.api.UserInfoApi;
import org.jee.boot.user.api.UserThirdAuthsApi;
import org.jee.boot.user.api.enums.UserThirdAuthsEnum;
import org.jee.boot.user.api.request.AddUserInfoRequest;
import org.jee.boot.user.api.request.AddUserThirdAuthsRequest;
import org.jee.boot.user.api.vo.ThirdAuthVO;
import org.jee.boot.user.api.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.jee.boot.client.web.util.RedisKey.LOGIN_TOKEN_KEY;
import static org.jee.boot.client.web.util.RedisKey.WX_SESSION_KEY;

@Service
public class LoginServiceImpl implements LoginService {

    @Reference
    UserInfoApi userInfoApi;

    @Reference
    UserThirdAuthsApi userThirdAuthsApi;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    WxService wxService;

    @Override
    public RpcResponse<LoginVO> login(LoginRequest loginRequest) {
        RpcResponse<LoginVO> rpcResponse = RpcResponse.ok();
        if (loginRequest.getLoginType() == null) {
            rpcResponse.setSysFail("loginType必填");
            return rpcResponse;
        }
        UserInfoVO userInfoVO = null;
        //手机号+密码登录；账号+密码登录；邮箱+密码登录；手机号+验证码登录
        if (loginRequest.getLoginType().intValue() == 1) {
            if (StringUtils.isEmpty(loginRequest.getPhone()) || StringUtils.isEmpty(loginRequest.getPassWord())) {
                rpcResponse.setSysFail("手机号、密码必填");
                return rpcResponse;
            }
            RpcResponse<Boolean> authResponse = userInfoApi.authByPhoneAndPassWord(loginRequest.getPhone(), loginRequest.getPassWord());
            if (!authResponse.isSucces()) {
                rpcResponse.copy(authResponse);
                return rpcResponse;
            }
            if (!authResponse.getData()) {
                rpcResponse.setSysFail("密码错误");
                return rpcResponse;
            }
            //校验通过,生成用户登录信息
            RpcResponse<UserInfoVO> userInfoVORpcResponse = userInfoApi.getUserInfoByPhone(loginRequest.getPhone());
            if (!userInfoVORpcResponse.isSucces()) {
                rpcResponse.copy(authResponse);
                return rpcResponse;
            }
            userInfoVO = userInfoVORpcResponse.getData();

        }
        //登录成功返回token及用户基本信息
        LoginVO loginVO = saveUserSessionVO(userInfoVO);
        rpcResponse.setData(loginVO);
        return rpcResponse;
    }

    @Override
    public RpcResponse<LoginVO> wxAuthLogin(WxAuthLoginRequest wxAuthLoginRequest) {
        RpcResponse<LoginVO> rpcResponse = RpcResponse.ok();
        //sessionKey 是否合法
        WxSessionVO wxSessionVO = wxService.getWxSessionVO(wxAuthLoginRequest.getSessionKey());
        if (wxSessionVO == null) {
            rpcResponse.setSysFail("sessionKey无效!");
            return rpcResponse;
        }
        //判断手机号码是否合法
        if (!wxAuthLoginRequest.getPhone().equals(wxSessionVO.getPhoneNum())) {
            rpcResponse.setSysFail("手机号和当前登录微信不一致!");
            return rpcResponse;
        }
        //根据手机号码查询用户基本信息
        RpcResponse<UserInfoVO> userInfoByPhoneResponse = userInfoApi.getUserInfoByPhone(wxSessionVO.getPhoneNum());
        if (!userInfoByPhoneResponse.isSucces()) {
            rpcResponse.copy(userInfoByPhoneResponse);
            return rpcResponse;
        }
        UserInfoVO userInfoVO = userInfoByPhoneResponse.getData();
        //用户不存在插入用户信息
        if (userInfoVO == null) {
            AddUserInfoRequest addUserInfoRequest = new AddUserInfoRequest();
            addUserInfoRequest.setPhone(wxSessionVO.getPhoneNum());
            RpcResponse<Long> addUserInfoResponse = userInfoApi.addUserInfo(addUserInfoRequest);
            if (!addUserInfoResponse.isSucces()) {
                rpcResponse.copy(addUserInfoResponse);
                return rpcResponse;
            }
            userInfoVO.setPhone(wxSessionVO.getSessionKey());
            userInfoVO.setUserId(addUserInfoResponse.getData());
        }
        //查询授权信息记录
        RpcResponse<ThirdAuthVO> authVORpcResponse = userThirdAuthsApi.getUserThirdAuthInfo(UserThirdAuthsEnum.WX.getValue(), wxSessionVO.getOpenId());
        if (!authVORpcResponse.isSucces()) {
            rpcResponse.copy(authVORpcResponse);
            return rpcResponse;
        }
        if (authVORpcResponse.getData() == null) {
            //授权信息不存在，插入用户授权信息
            AddUserThirdAuthsRequest addUserThirdAuthsRequest = new AddUserThirdAuthsRequest();
            addUserThirdAuthsRequest.setIdentifier(wxSessionVO.getOpenId());
            addUserThirdAuthsRequest.setIdentityType(UserThirdAuthsEnum.WX.getValue());
            addUserThirdAuthsRequest.setUserId(userInfoVO.getUserId());
            userThirdAuthsApi.addUserThirdAuths(addUserThirdAuthsRequest);
        }
        LoginVO loginVO = saveUserSessionVO(userInfoVO);
        rpcResponse.setData(loginVO);
        return rpcResponse;
    }

    private LoginVO saveUserSessionVO(UserInfoVO userInfoVO) {
        //生成token
        String token = UUID.randomUUID().toString();
        UserSessionVO userSessionVO = new UserSessionVO();
        BeanUtils.copyProperties(userInfoVO, userSessionVO);
        redisTemplate.opsForValue().set(LOGIN_TOKEN_KEY + token, JSON.toJSONString(userSessionVO), 60*24, TimeUnit.MINUTES);
        //返回登录信息
        LoginVO loginVO = new LoginVO();
        BeanUtils.copyProperties(userInfoVO, loginVO);
        loginVO.setToken(token);
        return loginVO;
    }


    @Override
    public RpcResponse loginOut(LoginOutRequest loginOutRequest) {
        return null;
    }

    @Override
    public RpcResponse<Long> registerUserInfo(UserRegisterRequest userRegisterRequest) {
        RpcResponse rpcResponse = new RpcResponse();
        //必填参数校验 TODO


        //参数合法性校验：手机号码格式校验 TODO
        if (!StringUtils.isEmpty(userRegisterRequest.getPhone())) {

        }

        //参数合法性校验：邮箱校验 TODO
        if (!StringUtils.isEmpty(userRegisterRequest.getEmail())) {

        }

        //手机号码是否已存在校验
        if (!StringUtils.isEmpty(userRegisterRequest.getPhone())) {
            RpcResponse<UserInfoVO> response = userInfoApi.getUserInfoByPhone(userRegisterRequest.getPhone());
            if (!response.isSucces()) {
                rpcResponse.copy(rpcResponse);
                return rpcResponse;
            }
            if (response.getData() != null) {
                rpcResponse.setSysFail("该手机号已注册!");
                return rpcResponse;
            }
        }

        //账号是否已存在校验
        if (!StringUtils.isEmpty(userRegisterRequest.getLoginName())) {
            RpcResponse<UserInfoVO> response = userInfoApi.getUserInfoByLoginName(userRegisterRequest.getLoginName());
            if (!response.isSucces()) {
                rpcResponse.copy(rpcResponse);
                return rpcResponse;
            }
            if (response.getData() != null) {
                rpcResponse.setSysFail("该账号已注册!");
                return rpcResponse;
            }
        }

        AddUserInfoRequest addUserInfoRequest = new AddUserInfoRequest();
        BeanUtils.copyProperties(userRegisterRequest, addUserInfoRequest);
        //添加用户基本信息
        RpcResponse<Long> response = userInfoApi.addUserInfo(addUserInfoRequest);
        rpcResponse.setData(response.getData());
        rpcResponse.setCode(response.getCode());
        rpcResponse.setMsg(response.getMsg());
        return rpcResponse;
    }

}
