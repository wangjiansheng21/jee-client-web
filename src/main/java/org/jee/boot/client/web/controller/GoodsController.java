package org.jee.boot.client.web.controller;

import org.jee.boot.dubbo.response.RpcResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author jiansheng.wang
 * @Date 2021/3/10 16:56
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @RequestMapping("/getGoodsList")
    public RpcResponse getGoodsList() {
        return RpcResponse.ok();
    }
}
