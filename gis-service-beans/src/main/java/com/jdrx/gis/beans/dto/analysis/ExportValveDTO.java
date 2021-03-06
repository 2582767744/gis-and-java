package com.jdrx.gis.beans.dto.analysis;

import com.jdrx.gis.beans.dto.base.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author lr
 * @Time 2019/8/2 0002 下午 5:24
 */

@Data
public class ExportValveDTO  {
    @ApiModelProperty("名称")
    String name;
    @ApiModelProperty("爆管点编号")
    Long lineId;
    @ApiModelProperty("爆管点经纬度")
    Double[] point;
    @ApiModelProperty("成功阀门设备ID集合")
    private String[] valveDevIds;
    @ApiModelProperty("失败阀门设备ID集合")
    private String[] failedDevIds;
}
