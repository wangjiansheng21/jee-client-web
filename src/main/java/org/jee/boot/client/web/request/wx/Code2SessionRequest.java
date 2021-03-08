package org.jee.boot.client.web.request.wx;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Code2SessionRequest {
    //微信小程序登录code
    @NotNull
  private   String code;
}
