package org.jee.boot.client.web.controller.user;

import org.jee.boot.client.web.request.login.LoginRequest;
import org.jee.boot.client.web.request.login.UserRegisterRequest;
import org.jee.boot.client.web.request.login.WxAuthLoginRequest;
import org.jee.boot.client.web.service.user.LoginService;
import org.jee.boot.client.web.vo.LoginVO;
import org.jee.boot.dubbo.response.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    LoginService loginService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/registerUserInfo")
    public RpcResponse<Long> registerUserInfo(@RequestBody @Validated UserRegisterRequest userRegisterRequest) {
        return loginService.registerUserInfo(userRegisterRequest);
    }

    /**
     * 用户登录
     *
     * @param loginRequest
     * @return
     */
    @PostMapping("/userLogin")
    public RpcResponse<LoginVO> login(@RequestBody @Validated LoginRequest loginRequest) {
        return loginService.login(loginRequest);
    }

    /**
     * 微信授权登录
     *
     * @param wxAuthLoginRequest
     * @return
     */
    @PostMapping("/wxAuthLogin")
    public RpcResponse<LoginVO> wxAuthLogin(@RequestBody WxAuthLoginRequest wxAuthLoginRequest) {
        return loginService.wxAuthLogin(wxAuthLoginRequest);
    }
}
