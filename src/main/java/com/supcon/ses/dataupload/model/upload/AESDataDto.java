package com.supcon.ses.dataupload.model.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AESDataDto {

    /**
     * companyCode，由系统下发
     */
    private String companyCode;

    /**
     * 服务id，由系统下发
     */
    private String serviceId;

    /**
     * dataId,由调用方生成与data 一一对应，应
     * 答时会携带该字段
     */
    private String dataId;

    /**
     * 实时消息，传输时需要加密，使用AES 算法
     * 进行加密，AES 密钥由系统下发
     */
    private String data;

}
