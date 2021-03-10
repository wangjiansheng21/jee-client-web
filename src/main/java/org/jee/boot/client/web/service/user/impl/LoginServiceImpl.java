package org.jee.boot.client.web.service.user.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import org.jee.boot.client.web.request.login.LoginOutRequest;
import org.jee.boot.client.web.request.login.LoginRequest;
import org.jee.boot.client.web.request.login.UserRegisterRequest;
import org.jee.boot.client.web.request.login.WxAuthLoginRequest;
import org.jee.boot.client.web.service.user.LoginService;
import org.jee.boot.client.web.service.wx.WxService;
import org.jee.boot.client.web.vo.LoginVO;
import org.jee.boot.client.web.vo.WxSessionVO;
import org.jee.boot.dubbo.response.RpcResponse;
import org.jee.boot.user.api.UserInfoApi;
import org.jee.boot.user.api.request.AddUserInfoRequest;
import org.jee.boot.user.api.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static org.jee.boot.client.web.util.RedisKey.WX_SESSION_KEY;

@Service
public class LoginServiceImpl implements LoginService {

    @Reference
    UserInfoApi userInfoApi;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    WxService wxService;

    @Override
    public RpcResponse<LoginVO> login(LoginRequest loginRequest) {
        //手机号+密码登录；账号+密码登录；邮箱+密码登录；手机号+验证码登录

        //登录成功返回token及用户基本信息

        return null;
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
        if (!wxAuthLoginRequest.getPhoneNum().equals(wxSessionVO.getPhoneNum())) {
            rpcResponse.setSysFail("手机号和当前登录微信不一致!");
            return rpcResponse;
        }
        //调用用户中心第三方授权接口
        return null;
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
