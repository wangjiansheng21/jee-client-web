package org.jee.boot.client.web.service.education;

import org.jee.boot.client.web.entity.StudentPublishInfo;
import org.jee.boot.dubbo.response.RpcResponse;

public interface StudentPublishInfoService {

    public RpcResponse saveStudentPublishInfo(StudentPublishInfo studentPublishInfo);
}
