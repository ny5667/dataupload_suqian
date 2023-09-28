package com.supcon.ses.dataupload.model.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class UploadDataDto {

    /**
     * 时间戳，格式yyyyMMddHHmmss
     */
    private String collectTime;

    /**
     * 数据源连通性，true 表示数据源
     * 连通正常，数据有效；false 表
     * 示数据源连通异常，数据无效
     */
    private Boolean isConnectDataSource;

    /**
     * 报文类型，report 表示实时报
     * 文；continues 表示断点续传的
     * 报文。
     */
    private String reportType;

    /**
     * 指标数据集合
     */
    private List<Map<String,Object>> datas;

}
