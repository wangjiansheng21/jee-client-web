package org.jee.boot.client.web.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author jiansheng.wang
 * @Date 2021/3/27 11:42
 */
@Data
public class Location implements Serializable {
    //经度
    private float lng;
    //维度
    private float lat;
}
