package org.jee.boot.client.web.service.user;

import org.jee.boot.client.web.request.login.LoginOutRequest;
import org.jee.boot.client.web.request.login.LoginRequest;
import org.jee.boot.client.web.request.login.UserRegisterRequest;
import org.jee.boot.client.web.request.login.WxAuthLoginRequest;
import org.jee.boot.client.web.vo.LoginVO;
import org.jee.boot.dubbo.response.RpcResponse;

public interface LoginService {

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    public RpcResponse<Long> registerUserInfo(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     * @param loginRequest
     * @return
     */
    public RpcResponse<LoginVO> login(LoginRequest loginRequest);

    /**
     * 微信授权登录
     * @param wxAuthLoginRequest
     * @return
     */
    public RpcResponse<LoginVO> wxAuthLogin(WxAuthLoginRequest wxAuthLoginRequest);


    /**
     * 用户登出
     * @param loginOutRequest
     * @return
     */
    public RpcResponse loginOut(LoginOutRequest loginOutRequest);





}
