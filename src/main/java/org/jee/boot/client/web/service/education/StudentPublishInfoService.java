package org.jee.boot.client.web.service.education;

import org.jee.boot.client.web.entity.StudentPublishInfo;
import org.jee.boot.client.web.request.education.StudentPublishInfoListRequest;
import org.jee.boot.dubbo.response.RpcResponse;

public interface StudentPublishInfoService {

    public RpcResponse saveStudentPublishInfo(StudentPublishInfo studentPublishInfo);

    public RpcResponse getDetail(String id);

    public RpcResponse getList(StudentPublishInfoListRequest studentPublishInfoListRequest);
}
