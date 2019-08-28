package com.jdrx.gis.beans.vo.analysis;

import com.jdrx.gis.beans.dto.analysis.NodeDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author lr
 * @Time 2019/7/19 0019 下午 3:01
 */
@Data
public class AnalysisResultVO {
    @ApiModelProperty("必须关闭的阀门列表")
    protected List<NodeDTO> fmlist;
    @ApiModelProperty("影响区域范围空间信息")
    protected String geom;
    @ApiModelProperty("影响用户")
    protected List userInfoPOS;
    @ApiModelProperty("影响用户总条数")
    protected int total;
}
