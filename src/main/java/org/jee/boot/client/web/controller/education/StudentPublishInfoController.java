package org.jee.boot.client.web.controller.education;

import org.jee.boot.client.web.entity.StudentPublishInfo;
import org.jee.boot.client.web.service.education.StudentPublishInfoService;
import org.jee.boot.dubbo.response.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/studentPublishInfo")
public class StudentPublishInfoController {

    @Autowired
    StudentPublishInfoService studentPublishInfoService;

    @RequestMapping("/save")
    public RpcResponse saveStudentPublishInfo(@RequestBody StudentPublishInfo studentPublishInfo){
        return studentPublishInfoService.saveStudentPublishInfo(studentPublishInfo);
    }



}
