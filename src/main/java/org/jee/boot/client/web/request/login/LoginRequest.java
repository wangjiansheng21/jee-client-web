package org.jee.boot.client.web.request.login;

import lombok.Data;

@Data
public class LoginRequest {

    //登录类型：1=手机号+密码登录 2=手机号+验证码登录，3=账号+密码登录，4=手机号+密码登录，5=邮箱+密码登录
    private Integer loginType;

    //手机号码
    private String phone;

    //密码
    private String passWord;

    //账号
    private String loginName;

    //邮箱
    private String email;

    //微信code
    private String code;


    private String encryptedData;


    private String sessionKey;


}
