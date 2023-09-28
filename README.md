~~企业编码：SQ3213000015~~
~~企业名称：宿迁新亚科技有限公司~~
~~AES秘钥：f271379419e349ba~~

企业编码：SQ3213000043
企业名称：新亚强硅化学股份有限公司
AES秘钥：f271379419e349ba

创建配置表
```roomsql
CREATE TABLE RT_CONFIG (
    序号 INT PRIMARY KEY,
    企业名称 VARCHAR(255),
    企业编号 VARCHAR(255),
    建筑物名称 VARCHAR(255),
    建筑物编号 VARCHAR(255),
    车间名称 VARCHAR(255),
    车间编号 VARCHAR(255),
    工艺名称 VARCHAR(255),
    工艺编号 VARCHAR(255),
    采样点名称 VARCHAR(255),
    采样点编号 VARCHAR(255),
    最小值 FLOAT,
    最大值 FLOAT,
    低低设 FLOAT,
    低设 FLOAT,
    高设 FLOAT,
    高高设 FLOAT,
    类别 VARCHAR(255),
    描述 VARCHAR(255),
    x坐标 FLOAT,
    y坐标 FLOAT,
    高度 FLOAT,
    宽度 FLOAT,
    单位 VARCHAR(255),
    测点 VARCHAR(255)
);
```
* 选择`RT_CONFIG`表，右键点击导入数据
![微信图片_20230928110300.png](/images/微信图片_20230928110300.png)

* 选择CSV文件导入
![Snipaste_2023-09-28_11-03-51.png](/images/Snipaste_2023-09-28_11-03-51.png)

* 设置编码为`gbk`，并选择源端
![Snipaste_2023-09-28_11-04-23.png](/images/Snipaste_2023-09-28_11-04-23.png)

* 后面一直下一步，数据就导入到对应表中

创建中间表：
```roomsql
CREATE TABLE RT_QUOTAS_CONFIG (
    biz_id VARCHAR(255) PRIMARY KEY,
    company_code VARCHAR(255),
    company_name VARCHAR(255),
    alarm_name VARCHAR(255),
    quota_id VARCHAR(255),
    origin_name VARCHAR(255),
    tag_name VARCHAR(255),
    value FLOAT,
    valid BIT DEFAULT 1
);
```

执行SQL把配置信息导出来，到中间表中
```roomsql
SELECT
采样点编号 biz_id,
企业编号 company_code,
企业名称 company_name,
采样点名称 alarm_name,
采样点编号 quota_id,
类别 origin_name
--'' tag_name
FROM RT_CONFIG rc
```

测试服务器能否通
```shell
telnet 192.168.100.163 8010
```
