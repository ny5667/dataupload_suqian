package com.supcon.ses.dataupload.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.supcon.ses.dataupload.cache.DataUploadSettingCache;
import com.supcon.ses.dataupload.constant.DefaultSettingConstant;
import com.supcon.ses.dataupload.model.pojo.AlarmPoint;
import com.supcon.ses.dataupload.model.setting.CompanyConfig;
import com.supcon.ses.dataupload.model.upload.AESDataDto;
import com.supcon.ses.dataupload.model.upload.UploadDataDto;
import com.supcon.ses.dataupload.repository.AlarmPointJdbcTemplateRepository;
import com.supcon.ses.dataupload.util.AesUtil;
import com.supcon.ses.datauploadparent.model.vo.TagVo;
import com.supcon.ses.datauploadparent.repository.TagRestTemplateRepository;
import com.supcon.ses.datauploadparent.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class AlarmPointUpload {

    private static final Logger log = LoggerFactory.getLogger(AlarmPointUpload.class);

    private final List<String> ignoreColumnList = Arrays.asList("biz_id", DefaultSettingConstant.TAG_NAME, "valid", "company_code", "company_name", "alarm_name", "origin_name"); // 忽略的列名列表

    private SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private final AlarmPointJdbcTemplateRepository repository;

    private final TagRestTemplateRepository tagRestTemplateRepository;

    private static final Map<String, Float> value_map = new HashMap<>();

    static {
        value_map.put("false", 0f);
        value_map.put("true", 1f);
    }

    public AlarmPointUpload(AlarmPointJdbcTemplateRepository repository, TagRestTemplateRepository tagRestTemplateRepository) {
        this.repository = repository;
        this.tagRestTemplateRepository = tagRestTemplateRepository;
    }

    @Scheduled(fixedRate = 5 * 1000)
    public void run() throws JsonProcessingException {

        log.error("实时数据上报开始.");

        if (DataUploadSettingCache.getCompanyConfigs() == null) {
            log.error("配置信息为空，跳过.");
            return;
        }

        for (CompanyConfig company :
                DataUploadSettingCache.getCompanyConfigs()) {
            uploadDataByCompany(company);
        }

    }

    /*-----------------------------------------公共方法---------------------------------------------------*/

    private void uploadDataByCompany(CompanyConfig company) throws JsonProcessingException {

        //查询实时数据
        List<Map<String, Object>> allAlarmPoint = repository.findAllMap();

        //查询位号数据
        String ip = company.getAdpServerIp();
        String port = company.getAdpServerPort();
        List<String> tagNames = allAlarmPoint.stream().filter(v -> v.get(DefaultSettingConstant.TAG_NAME) != null).map(v -> v.get(DefaultSettingConstant.TAG_NAME).toString()).distinct().collect(Collectors.toList());
        if (tagNames.isEmpty()) {
            log.error("无实时上报数据.");
            return;
        }

        List<TagVo> tagVoList = tagRestTemplateRepository.findAll(ip, port, tagNames);

        //设置实时数据位号值
        setTagValueAndDefaultValue(allAlarmPoint, tagVoList);

        // 更新位号数据到数据库中
        // 如果下次测试数据取不到则用上一次实时数据库的值
        List<AlarmPoint> dataList = new ArrayList<>();
        allAlarmPoint.forEach(c -> {
            String bizId = (String) c.get("biz_id");
            Float value = (Float) c.get(DefaultSettingConstant.UPLOAD_TAG_VALUE);
            AlarmPoint po = new AlarmPoint(bizId, value);
            dataList.add(po);
        });
        repository.batchUpdate(dataList);
        log.error("数据更新结束.");

        //去掉不需要上报的字段
        ignoreColumnList.forEach(ignoreColumn -> allAlarmPoint.forEach(map -> map.remove(ignoreColumn)));

        //处理上报数据/加密
        int batchSize = Integer.parseInt(company.getBitchSize());  // 每批次的大小
        for (int i = 0; i < allAlarmPoint.size(); i += batchSize) {
            List<Map<String, Object>> batch = allAlarmPoint.subList(i, Math.min(i + batchSize, allAlarmPoint.size()));
            uploadData(batch, company);
        }

        log.error("实时数据上报结束.");
    }

    /**
     * 设置报警点的位号值和其他默认值
     *
     * @param allAlarmPoint 实时数据
     * @param tagVoList     位号列表
     */
    private void setTagValueAndDefaultValue(List<Map<String, Object>> allAlarmPoint, List<TagVo> tagVoList) {
        Map<String, TagVo> tagVoMap = tagVoList.stream()
                .collect(Collectors.toMap(TagVo::getName, Function.identity(), (v1, v2) -> v1));

        allAlarmPoint.forEach(c -> {
            if (c.get(DefaultSettingConstant.TAG_NAME) == null) {
                return;
            }
            String tagName = c.get(DefaultSettingConstant.TAG_NAME).toString();
            TagVo tagVo = tagVoMap.get(tagName);
            if (tagVo == null || tagVo.getValue() == null || tagVo.getValue().isEmpty()) {
                if (c.get(DefaultSettingConstant.TAG_VALUE) == null) {
                    c.put(DefaultSettingConstant.UPLOAD_TAG_VALUE, 0f);//默认设置为0
                } else {
                    String tagValue = c.get(DefaultSettingConstant.TAG_VALUE).toString();
                    float v = Float.parseFloat(tagValue);
                    c.put(DefaultSettingConstant.UPLOAD_TAG_VALUE, v);//设置为上一次查询出的位号值
                }
                return;
            }
            //如果是bool类型，则转成0/1类型
            Float boolString = value_map.get(tagVo.getValue());
            if (boolString != null) {
                c.put(DefaultSettingConstant.UPLOAD_TAG_VALUE, boolString);//位号实时值
                return;
            }
            c.put(DefaultSettingConstant.UPLOAD_TAG_VALUE, Float.parseFloat(tagVo.getValue()));//位号实时值
        });


    }

    /**
     * 上报数据
     *
     * @param batch   上报数据信息
     * @param company 公司信息
     */
    private void uploadData(List<Map<String, Object>> batch, CompanyConfig company) {

        String collectTime = mySimpleDateFormat.format(new Date());

        UploadDataDto uploadDataDto1 = UploadDataDto.builder().collectTime(collectTime).isConnectDataSource(true).reportType("report").datas(batch).build();
        String uploadDataJson = JsonHelper.writeValue(uploadDataDto1);


        String encrypt = AesUtil.encrypt(company.getAesKey(), uploadDataJson);

        String dataId = UUID.randomUUID().toString();
        String serviceId = DefaultSettingConstant.SERVICE_ID;
        String companyCode = company.getCompanyCode();

        AESDataDto aesDataDto = AESDataDto.builder().companyCode(companyCode).serviceId(serviceId).dataId(dataId).data(encrypt).build();
        String aesDataJson = JsonHelper.writeValue(aesDataDto);

        String socketIp = company.getSocketIp();
        String socketPort = company.getSocketPort();

        log.error(">>>>发送list数据:{}", uploadDataJson);

        log.error("发送报警消息:{}", aesDataJson);

        try {
            //发送报文
            Socket socket = new Socket(socketIp, Integer.parseInt(socketPort));
            getStringBuilder(socket, aesDataJson);
        } catch (Exception e) {
            log.error("报警消息发送失败", e);
        }

    }

    /**
     * 发送webSocket消息
     *
     * @param socket      webSocket对象
     * @param aesDataJson Aes加密过的数据
     * @throws IOException
     */
    private void getStringBuilder(Socket socket, String aesDataJson) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedWriter.write(aesDataJson);
        bufferedWriter.write("@@");
        bufferedWriter.flush();

        // 接收报文
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        log.error(">>>>发送返回报文 {}", stringBuilder);
    }

}
