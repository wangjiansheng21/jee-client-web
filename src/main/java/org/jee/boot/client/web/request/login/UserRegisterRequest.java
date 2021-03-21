package org.jee.boot.client.web.request.login;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserRegisterRequest {
    /**
     * 登录账号名（不可以重复）
     */
    @NotNull(message = "loginName 不可以为空")
    private String loginName;
    /**
     * 密码
     */
    @NotNull(message = "passWord 不可以为空")
    private String passWord;

    /**
     * 姓名，如张三
     */
    private String userName;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像地址相对路径
     */
    private String avatar;
    /**
     * 手机号码
     */
    @NotNull(message = "phone 不可以为空")
    private String phone;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 注册ip
     */
    private String registerIp;

    /**
     * 性别 1=男，2=女
     */
    private Byte gender;
}
