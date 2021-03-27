package org.jee.boot.client.web.request.education;

import lombok.Data;
import org.jee.boot.client.web.request.BaseRequest;

/**
 * @Author jiansheng.wang
 * @Date 2021/3/27 15:38
 */
@Data
public class StudentPublishInfoListRequest extends BaseRequest {
    //经度
    private float lng;
    //维度
    private float lat;

    //用户id
    private Integer userId;

    //
    private  String provinceId;

    private  String cityId;

    private  String townId;
}
