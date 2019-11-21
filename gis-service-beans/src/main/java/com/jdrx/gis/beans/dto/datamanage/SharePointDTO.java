package com.jdrx.gis.beans.dto.datamanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/20 0020 上午 10:51
 */
@Data
public class SharePointDTO {
    @ApiModelProperty("管点属性")
    private Map<String,Object> mapAttr;

    @ApiModelProperty("设备类型id")
    private Long typeId;

    @ApiModelProperty("x坐标")
    private Double x;

    @ApiModelProperty("y坐标")
    private Double y;


}
