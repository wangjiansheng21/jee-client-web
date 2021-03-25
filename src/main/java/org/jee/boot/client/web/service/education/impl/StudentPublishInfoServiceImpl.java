package org.jee.boot.client.web.service.education.impl;

import org.jee.boot.client.web.entity.StudentPublishInfo;
import org.jee.boot.client.web.service.education.StudentPublishInfoService;
import org.jee.boot.dubbo.response.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class StudentPublishInfoServiceImpl implements StudentPublishInfoService {

    @Autowired
    MongoTemplate mongoTemplate;


    @Override
    public RpcResponse saveStudentPublishInfo(StudentPublishInfo studentPublishInfo) {
        //TODO 校验必填参数
        RpcResponse rpcResponse = RpcResponse.ok();
        mongoTemplate.save(studentPublishInfo);
        rpcResponse.setData(studentPublishInfo.getId());
        return rpcResponse;
    }
}
