package org.jee.boot.client.web.request.login;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author jiansheng.wang
 * @Date 2021/3/9 16:11
 */
@Data
public class WxAuthLoginRequest {

    @NotNull
    //微信会话id
    private String sessionKey;

    @NotNull
    //微信手机号码
    private String phoneNum;
}
