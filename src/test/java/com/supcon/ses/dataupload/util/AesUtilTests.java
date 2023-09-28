package com.supcon.ses.dataupload.util;

import com.supcon.ses.datauploadparent.utils.JsonHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class AesUtilTests {

    public AesUtilTests(){

    }

    public static final String SECRET_KEY = "f271379419e349ba";

    @Test
    void AesUtil_Encrypt_returnString(){
        Map<String, Object> data = getStringObjectMap();

        String dataJson = JsonHelper.writeValue(data);
        String encrypt = AesUtil.encrypt(SECRET_KEY, dataJson);

        Assertions.assertThat(encrypt).isNotNull();

    }

    @Test
    void AesUtil_Decrypt_returnString(){
        Map<String, Object> data = getStringObjectMap();

        String dataJson = JsonHelper.writeValue(data);
        String encrypt = AesUtil.encrypt(SECRET_KEY, dataJson);

        String decrypt = AesUtil.decrypt(SECRET_KEY, encrypt);

        Assertions.assertThat(encrypt).isNotNull();
        Assertions.assertThat(decrypt).isNotNull();
        Assertions.assertThat(dataJson).isEqualTo(decrypt);
    }

    private Map<String, Object> getStringObjectMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("collectTime", "20230901162326");

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> valueData = new HashMap<>();
        valueData.put("personName", "张恒");
        valueData.put("personId", "15150762331");
        valueData.put("stationId", "操作工");
        valueData.put("isOutside", 0);
        valueData.put("longitude", "118.3650215");
        valueData.put("latitude", "34.1065155");
        list.add(valueData);

        data.put("datas", list);
        return data;
    }

}
