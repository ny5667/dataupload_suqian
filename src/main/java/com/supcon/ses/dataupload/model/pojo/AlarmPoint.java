package com.supcon.ses.dataupload.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlarmPoint {

    /**
     * 主键
     */
    private String bizId;

    /**
     * 实时数据
     */
    private Float value;

}
