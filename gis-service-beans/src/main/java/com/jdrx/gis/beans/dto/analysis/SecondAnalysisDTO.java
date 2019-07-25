package com.jdrx.gis.beans.dto.analysis;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author lr
 * @Time 2019/7/25 0025 下午 7:17
 */

@Data
public class SecondAnalysisDTO {
    @ApiModelProperty("第一次关阀列表")
    protected List<String> fmlist;
    @ApiModelProperty("关阀失败的列表")
    protected List<String>fealtureList;
}
