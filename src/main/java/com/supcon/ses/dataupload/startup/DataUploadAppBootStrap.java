package com.supcon.ses.dataupload.startup;

import com.supcon.ses.dataupload.cache.DataUploadSettingCache;
import com.supcon.ses.dataupload.exceptions.ConfigurationErrorException;
import com.supcon.ses.dataupload.model.setting.SettingRealData;
import com.supcon.ses.datauploadparent.utils.FileHelper;
import com.supcon.ses.datauploadparent.utils.JsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class DataUploadAppBootStrap implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataUploadAppBootStrap.class);

    @Value("${upload.setting}")
    private String settingPath;

    @Override
    public void run(String... args) throws Exception {
        this.initialization();
    }

    /*-------------------------------公共方法------------------------------*/


    /**
     * 根据配置文件读取配置并缓存
     */
    private void initialization() {
        logger.info("通过读取配置文件的方式获取配置信息, 配置文件路径：{}.", settingPath);
        if (StringUtils.isBlank(settingPath)) {
            throw new ConfigurationErrorException("未指定数上报配置文件地址.");
        }
        String settings = FileHelper.readFileContent(settingPath);
        if (StringUtils.isBlank(settings)) {
            throw new ConfigurationErrorException("未指定数上报配置文件地址.");
        }
        SettingRealData setting = JsonHelper.parseJson(settings, SettingRealData.class);
        if (null == setting) {
            throw new ConfigurationErrorException("配置文件内容格式不合规.");
        }
        //对配置进行校正、修订
        this.correct(setting);
        // 缓存到内存
        this.cached(setting);
        logger.info("初始化数据上报配置完成...");
    }

    /**
     * 对配置文件进行修正
     * <p>
     * 配置重复
     * 配置不完整
     * </p>
     */
    private void correct(SettingRealData setting) {
        if (CollectionUtils.isEmpty(setting.getCompanies())) {
            throw new ConfigurationErrorException("未指定组织设置");
        }
    }

    /**
     * 配置信息保存到内存
     *
     * @param setting
     */
    private void cached(SettingRealData setting) {
        /******** adp服务信息 ********/
        DataUploadSettingCache.setCompanyConfigs(setting.getCompanies());
    }

}
