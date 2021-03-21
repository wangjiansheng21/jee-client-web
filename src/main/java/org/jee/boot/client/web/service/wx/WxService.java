package org.jee.boot.client.web.service.wx;

import org.jee.boot.client.web.request.wx.Code2SessionRequest;
import org.jee.boot.client.web.request.wx.GetPhoneNumRequest;
import org.jee.boot.client.web.vo.AccesTokenVO;
import org.jee.boot.client.web.vo.Code2SessionVO;
import org.jee.boot.client.web.vo.WxSessionVO;
import org.jee.boot.dubbo.response.RpcResponse;

public interface WxService {


    /**
     * 小程序登录根据code获取session
     * @param code2SessionRequest
     * @return
     */
    RpcResponse<Code2SessionVO> code2Session(Code2SessionRequest code2SessionRequest);

    /**
     * 微信小程序获取微信手机号码
     * @param getPhoneNumRequest
     * @return
     */
    RpcResponse<String> getPhoneNum(GetPhoneNumRequest getPhoneNumRequest);

    /**
     * 获取微信会话session
     * @param sessionKey
     * @return
     */
    public WxSessionVO getWxSessionVO(String sessionKey);


    /**
     * 微信应用appId
     * @param appId
     * @return
     */
    public RpcResponse<AccesTokenVO>  getAccesToken(String appId);
}
