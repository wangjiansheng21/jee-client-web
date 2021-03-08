package org.jee.boot.client.web.service.user.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import org.jee.boot.client.web.request.login.LoginOutRequest;
import org.jee.boot.client.web.request.login.LoginRequest;
import org.jee.boot.client.web.request.login.UserRegisterRequest;
import org.jee.boot.client.web.service.user.LoginService;
import org.jee.boot.client.web.vo.LoginVO;
import org.jee.boot.dubbo.response.RpcResponse;
import org.jee.boot.user.api.UserInfoApi;
import org.jee.boot.user.api.request.AddUserInfoRequest;
import org.jee.boot.user.api.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LoginServiceImpl implements LoginService {

    @Reference
    UserInfoApi userInfoApi;

    @Override
    public RpcResponse<LoginVO> login(LoginRequest loginRequest) {
        //手机号+密码登录；账号+密码登录；邮箱+密码登录；手机号+验证码登录

        //登录成功返回token及用户基本信息

        return null;
    }


    public LoginVO loginForWXMini(LoginRequest loginRequest){
        //根据微信
        return  null;

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
