package org.jee.boot.client.web.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author jiansheng.wang
 * @Date 2021/3/9 12:03
 */
@Data
public class WxSessionVO implements Serializable {
    private  String sessionKey;

    private String openId;

    private String phoneNum;
}
