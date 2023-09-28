package com.supcon.ses.dataupload.repository;

import com.supcon.ses.dataupload.model.pojo.AlarmPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class AlarmPointJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    public AlarmPointJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findAllMap() {
        String sql = "SELECT * FROM RT_QUOTAS_CONFIG WHERE VALID = 1";
        return jdbcTemplate.queryForList(sql);
    }


    public void batchUpdate(List<AlarmPoint> dataList) {
        String sql = "UPDATE RT_QUOTAS_CONFIG SET VALUE=? WHERE BIZ_ID=?";
        int[] updatedRows = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                AlarmPoint data = dataList.get(i);
                ps.setFloat(1, data.getValue());
                ps.setString(2, data.getBizId());
            }

            @Override
            public int getBatchSize() {
                return dataList.size();
            }
        });
        log.error("Batch updated " + updatedRows.length + " rows.");
    }

}
