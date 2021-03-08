package org.jee.boot.client.web.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Code2SessionVO  implements Serializable {

    //微信登录sessionKey
    private String  sessionKey;

    //微信用户id
    private String openId;
}
