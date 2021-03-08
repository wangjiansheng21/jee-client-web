package org.jee.boot.client.web.controller.wx;

import org.jee.boot.client.web.request.wx.Code2SessionRequest;
import org.jee.boot.client.web.request.wx.GetPhoneNumRequest;
import org.jee.boot.client.web.service.wx.WxService;
import org.jee.boot.client.web.vo.Code2SessionVO;
import org.jee.boot.dubbo.response.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/wx")
public class WXController {

    @Autowired
    WxService wxService;

    /**
     * 小程序登录根据code获取session
     * @param code2SessionRequest
     * @return
     */
    @PostMapping("/code2Session")
    RpcResponse<Code2SessionVO> code2Session(@RequestBody  @Validated Code2SessionRequest code2SessionRequest){
        return wxService.code2Session(code2SessionRequest);
    }



    /**
     * 获取微信手机号码
     * @param getPhoneNumRequest
     * @return
     */
    @PostMapping("/getPhoneNum")
    RpcResponse<String> getPhoneNum(@RequestBody @Validated GetPhoneNumRequest getPhoneNumRequest){
        return  wxService.getPhoneNum(getPhoneNumRequest);
    }



}
