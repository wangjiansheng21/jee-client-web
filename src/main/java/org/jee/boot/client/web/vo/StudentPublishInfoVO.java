package org.jee.boot.client.web.vo;

import lombok.Data;
import org.jee.boot.client.web.entity.StudentPublishInfo;

/**
 * @Author jiansheng.wang
 * @Date 2021/3/27 15:42
 */
@Data
public class StudentPublishInfoVO extends StudentPublishInfo {

    //距离单位km
    private float distance;

    public float getDistance() {
        distance = distance * 100;
        return distance;
    }
}
