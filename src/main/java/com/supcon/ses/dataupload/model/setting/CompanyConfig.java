package com.supcon.ses.dataupload.model.setting;

import com.supcon.ses.dataupload.constant.DefaultSettingConstant;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class CompanyConfig {

    /**
     * adp平台企业cid
     */
    private String cid = DefaultSettingConstant.DEFAULT_CID;

    /**
     * 企业编号，由系统下发
     */
    private String companyCode = StringUtils.EMPTY;

    /**
     * 企业名称，由系统下发
     */
    private String companyName = StringUtils.EMPTY;

    /**
     * 加密密钥，由系统下发
     */
    private String aesKey = StringUtils.EMPTY;

    /**
     * socketIp配置，由平台提供
     */
    private String socketIp = StringUtils.EMPTY;

    /**
     * socketPort配置，由平台提供
     */
    private String socketPort = StringUtils.EMPTY;

    /**
     * 上送服务器地址
     */
    private String serverAddress = StringUtils.EMPTY;

    /**
     * ADP平台的IP
     */
    private String adpServerIp = StringUtils.EMPTY;

    /**
     * ADP平台的端口
     */
    private String adpServerPort = StringUtils.EMPTY;

    /**
     * 上报一个批次的数量
     */
    private String bitchSize = StringUtils.EMPTY;

}
