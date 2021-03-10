package org.jee.boot.client.web.filter;


import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class DubboLogFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        HashMap<String, Object> logContent = new HashMap<>();
        logContent.put("args", invocation.getArguments());
        logContent.put("methodName", invocation.getMethodName());
        logContent.put("client", RpcContext.getContext().getRemoteAddressString());
        logContent.put("host", RpcContext.getContext().getLocalAddressString());
        Result result = null;
        try {
            long start = System.currentTimeMillis();
            result = invoker.invoke(invocation);
            long end = System.currentTimeMillis();
            String resultStr = JSON.toJSONString(result);
            logContent.put("elapsedtime", end - start);
            if (result.getException() != null) {
                logContent.put("exception", result.getException());
                log.error("{【dubbo fail】 request url is {},param is {},result is {}",
                        invoker.getUrl().getAbsolutePath(), JSON.toJSONString(logContent), resultStr);
            } else {
                log.info("【dubbo success】remote request url is {},param is {},result is {}", invoker.getUrl().getAbsolutePath(),
                        JSON.toJSONString(logContent), resultStr);
            }
        } catch (Exception ex) {
            log.error("【dubbo exception】request url is {},param is {}.", invoker.getUrl().getAddress(),
                    ex.getCause());
            throw ex;
        }
        return result;
    }
}
