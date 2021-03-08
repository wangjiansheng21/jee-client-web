package org.jee.boot.client.web.request.wx;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetPhoneNumRequest {

    //微信加密数据
    @NotNull
    private  String encryptedData;

    //初始向量
    @NotNull
    private String iv;

    //会话密钥
    @NotNull
    private String sessionKey;

}
