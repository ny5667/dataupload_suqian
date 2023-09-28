package com.supcon.ses.dataupload.model.setting;

import lombok.Data;

import java.util.List;

@Data
public class SettingRealData {

    /**
     * 组织设置
     */
    private List<CompanyConfig> companies;

}