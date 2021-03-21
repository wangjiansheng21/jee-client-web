package org.jee.boot.client.web.controller.health;

import org.jee.boot.dubbo.response.RpcResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author jiansheng.wang
 * @Date 2021/3/10 15:32
 */
@RestController
@RequestMapping("/health")
public class HealthInfoCheck {

    @RequestMapping("/info")
    public RpcResponse healthInfo() {
        return RpcResponse.ok();
    }

}
