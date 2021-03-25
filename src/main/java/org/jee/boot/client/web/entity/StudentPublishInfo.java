package org.jee.boot.client.web.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "student_publish_info")
@Data
public class StudentPublishInfo extends BaseEntity {
    //标题
    private String title;

    //联系人
    private  String contact;

    //联系电话
    private String contactTel;

    //性别
    private Integer sex;

    //年级id
    private Integer gradeId;

    //科目id
    private Integer subjectId;


}
