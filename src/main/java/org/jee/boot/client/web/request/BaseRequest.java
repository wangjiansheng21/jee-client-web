package org.jee.boot.client.web.request;

import lombok.Data;

/**
 * @Author jiansheng.wang
 * @Date 2021/3/27 15:52
 */
@Data
public class BaseRequest {

    private Integer pageIndex = 1;

    private Integer pageSize = 10;

    public Integer getOffSet() {
        return (pageIndex - 1) * pageSize;
    }

}
