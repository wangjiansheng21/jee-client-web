package org.jee.boot.client.web.service.user;

import org.jee.boot.client.web.request.login.LoginRequest;
import org.jee.boot.client.web.vo.UserSessionVO;
import org.jee.boot.dubbo.response.RpcResponse;

public interface UserService {
    public UserSessionVO getUserSessionVO(String token);
}
