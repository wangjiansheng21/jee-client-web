package org.jee.boot.client.web.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "student_publish_info")
@Data
public class StudentPublishInfo extends BaseEntity {

    //用户id
    private Integer userId;

    //标题
    private String title;

    //联系人
    private String contact;

    //联系电话
    private String contactTel;

    //性别
    private Integer sex;

    //年级id
    private Integer gradeId;

    //科目id
    private Integer subjectId;

    //地址
    private String address;

    //经纬度
    private float[] location;

    //省份id
    private String provinceId;

    //城市id
    private String cityId;

    //区id(县id)
    private String townId;


}
