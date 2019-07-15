package com.jdrx.gis.beans.dto.basic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 空间测量DTO
 * @Author lr
 * @Time 2019/6/25 0025 上午 11:26
 */
@Data
public class MeasurementDTO {
    @ApiModelProperty("名称")
    protected String name;
    @ApiModelProperty("测量值")
    protected Double meaturedValue ;
    @ApiModelProperty("备注信息")
    protected String remark;
    @ApiModelProperty("空间信息")
    protected  String geom;
}