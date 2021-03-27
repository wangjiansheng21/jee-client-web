package org.jee.boot.client.web.service.education.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jee.boot.client.web.entity.Location;
import org.jee.boot.client.web.entity.StudentPublishInfo;
import org.jee.boot.client.web.request.education.StudentPublishInfoListRequest;
import org.jee.boot.client.web.service.education.StudentPublishInfoService;
import org.jee.boot.client.web.vo.StudentPublishInfoVO;
import org.jee.boot.dubbo.response.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
public class StudentPublishInfoServiceImpl implements StudentPublishInfoService {

    static final String distanceField = "distance";

    static final String COLLECTION_NAME = "student_publish_info";

    @Value("${baidu.map.api}")
    private String baiduApiUrl;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    MongoTemplate mongoTemplate;


    @Override
    public RpcResponse saveStudentPublishInfo(StudentPublishInfo studentPublishInfo) {
        //TODO 校验必填参数
        RpcResponse rpcResponse = RpcResponse.ok();
        studentPublishInfo.setCreatedTime(new Date());
        studentPublishInfo.setUpdatedTime(studentPublishInfo.getCreatedTime());
        //根据地址获取经纬度
        Location location = getLocationByAddress(studentPublishInfo.getAddress());
        if (location != null) {
            float[] locationArray = {location.getLng(), location.getLat()};
            studentPublishInfo.setLocation(locationArray);
        }
        mongoTemplate.save(studentPublishInfo);
        rpcResponse.setData(studentPublishInfo.getId());
        return rpcResponse;
    }

    public Location getLocationByAddress(String address) {
        if (StringUtils.isEmpty(address)) {
            return null;
        }
        String url = baiduApiUrl + "geocoding/v3/?address=" + address + "&output=json&ak=zfGiMXD9ZD5CGmAG3C72TZnEnxKIhXQW";
        log.info("调用百度地图api接口请求url：{}", url);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        log.info("调用百度地图api接口响应：{}", body);
        if (jsonObject.get("result") != null) {
            JSONObject result = (JSONObject) jsonObject.get("result");
            return JSONObject.parseObject(JSON.toJSONString(result.get("location")), Location.class);
        }
        return null;
    }

    @Override
    public RpcResponse getDetail(String id) {
        RpcResponse rpcResponse = new RpcResponse();
        StudentPublishInfo studentPublishInfo = mongoTemplate.findById(id, StudentPublishInfo.class);
        rpcResponse.setData(studentPublishInfo);
        return rpcResponse;
    }

    @Override
    public RpcResponse getList(StudentPublishInfoListRequest studentPublishInfoListRequest) {
        RpcResponse rpcResponse = new RpcResponse();
        Criteria criteria = getListCriteria(studentPublishInfoListRequest);
        rpcResponse.setTotal(getListCount(criteria));
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.geoNear(NearQuery.near(studentPublishInfoListRequest.getLng(), studentPublishInfoListRequest.getLat()), distanceField),
                Aggregation.skip(studentPublishInfoListRequest.getOffSet()),
                Aggregation.limit(studentPublishInfoListRequest.getPageSize())
        );
        AggregationResults<StudentPublishInfoVO> studentPublishInfoAggregationResults = mongoTemplate.aggregate(aggregation, "student_publish_info", StudentPublishInfoVO.class);
        rpcResponse.setData(studentPublishInfoAggregationResults.getMappedResults());
        return rpcResponse;
    }

    public long getListCount(Criteria criteria) {
        long count = 0;
        Query query = new Query();
        query.addCriteria(criteria);
        count = mongoTemplate.count(query, COLLECTION_NAME);
        return count;
    }

    public Criteria getListCriteria(StudentPublishInfoListRequest publishInfoListRequest) {
        Criteria criteriaDefinition = new Criteria();
        if (!StringUtils.isEmpty(publishInfoListRequest.getProvinceId())) {
            ((Criteria) criteriaDefinition).and("provinceId").is(publishInfoListRequest.getProvinceId());
        }
        if (!StringUtils.isEmpty(publishInfoListRequest.getCityId())) {
            ((Criteria) criteriaDefinition).and("cityId").is(publishInfoListRequest.getCityId());
        }
        if (!StringUtils.isEmpty(publishInfoListRequest.getTownId())) {
            ((Criteria) criteriaDefinition).and("townId").is(publishInfoListRequest.getTownId());
        }
        return criteriaDefinition;
    }

}
