package com.jdrx.gis.beans.dto.analysis;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * @Description
 * @Author lr
 * @Time 2019/7/23 0023 下午 7:41
 */
@Data
public class NodeDTO {
    @ApiModelProperty("设备id")
    private String dev_id;
    @ApiModelProperty("编号")
    private String code;
    @ApiModelProperty("x坐标")
    private Double x;
    @ApiModelProperty("y坐标")
    private Double y;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeDTO dto = (NodeDTO) o;
        return Objects.equals(dev_id, dto.dev_id);
    }


}
