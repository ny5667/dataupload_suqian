package com.supcon.ses.dataupload.cache;

import com.supcon.ses.dataupload.model.setting.CompanyConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DataUploadSettingCache {

    private DataUploadSettingCache() {

    }

    /************************************************配置缓存***********************************************************/
    /**
     * 组织配置信息
     */
    @Getter
    @Setter
    private static List<CompanyConfig> companyConfigs;

}
