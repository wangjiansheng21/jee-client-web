package org.jee.boot.client.web.vo;

import lombok.Data;

/**
 * @Author jiansheng.wang
 * @Date 2021/3/12 11:10
 */
@Data
public class AccesTokenVO {

    //微信接口调用凭证
    private String accesToken;

    //凭证有效期
    private Long expiresIn;

}
