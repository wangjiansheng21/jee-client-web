package org.jee.boot.client.web.vo;

import lombok.Data;

@Data
public class LoginVO {
    /**
     * 登录账号名（不可以重复）
     */
    private String loginName;

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
    private String phone;
    /**
     * 邮箱
     */
    private String email;

    /**
     * token
     */
    private String token;

    /**
     * 用户id
     */
    private Long userId;
}
