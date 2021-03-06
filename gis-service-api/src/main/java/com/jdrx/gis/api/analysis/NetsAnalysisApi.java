package com.jdrx.gis.api.analysis;

import com.jdrx.gis.api.basic.BasciDevApi;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.analysis.AnalysisRecordDTO;
import com.jdrx.gis.beans.dto.analysis.RecondParamasDTO;
import com.jdrx.gis.beans.dto.analysis.SecondAnalysisDTO;
import com.jdrx.gis.beans.dto.analysis.ExportValveDTO;
import com.jdrx.gis.beans.dto.analysis.ExportValveRecondDTO;
import com.jdrx.gis.service.analysis.NetsAnalysisService;
import com.jdrx.gis.service.query.QueryDevService;
import com.jdrx.gis.util.RedisComponents;
import com.jdrx.platform.commons.rest.beans.dto.IdDTO;
import com.jdrx.platform.commons.rest.beans.enums.EApiStatus;
import com.jdrx.platform.commons.rest.beans.vo.ResposeVO;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.platform.commons.rest.factory.ResponseFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Description 爆管分析
 * @Author lr
 * @Time 2019/7/19 0019 下午 2:13
 */

@RestController
@Api("爆管分析查询")
@RequestMapping(value = "api/0/analysis", method = RequestMethod.POST)
public class NetsAnalysisApi {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(BasciDevApi.class);

    @Autowired
    private NetsAnalysisService netsAnalysisService;

    @Autowired
    private RedisComponents redisComponents;

    @Autowired
    private QueryDevService queryDevService;

    @ApiOperation(value = "获取爆管分析结果")
    @RequestMapping(value ="getAnalysisiResult")
    public ResposeVO getAnalysisiResult(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<String> dto) throws Exception{
        if (dto == null || dto.getId() ==null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/getAnalysisiResult 获取爆管分析结果");
        return  ResponseFactory.ok(netsAnalysisService.getAnalysisResult(dto));

    }

    @ApiOperation(value = "获取二次关阀分析结果")
    @RequestMapping(value ="getSecondAnalysisiResult" )
    public ResposeVO getSecondAnalysisiResult(@ApiParam(name = "dto", required = true) @RequestBody @Valid SecondAnalysisDTO secondAnalysisDTO) throws Exception{
        if (secondAnalysisDTO == null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/getSecondAnalysisiResult 获取二次关阀分析结果");
        return  ResponseFactory.ok(netsAnalysisService.getSecondAnalysisResult(secondAnalysisDTO));
    }

    @ApiOperation(value = "保存关阀分析结果")
    @RequestMapping(value ="saveSecondAnalysisiResult" )
    public ResposeVO saveSecondAnalysisiResult(@ApiParam(name = "dto", required = true) @RequestBody @Valid AnalysisRecordDTO recordDTO,
                                               @RequestHeader(name ="deptPath") String deptPath) throws Exception{
        if (recordDTO == null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        Logger.debug("api/0/analysis/saveSecondAnalysisiResult 保存关阀分析结果");
        return  ResponseFactory.ok(netsAnalysisService.saveAnalysisRecond(recordDTO,deptPath));
    }

    @ApiOperation(value = "获取爆管历史记录列表")
    @RequestMapping(value ="getAnalysisiReconds" )
    public ResposeVO getAnalysisiReconds(@ApiParam(name = "dto", required = true) @RequestBody @Valid RecondParamasDTO recondParamasDTO) throws Exception{
        Logger.debug("api/0/analysis/getAnalysisiReconds 获取爆管历史记录列表");
        return  ResponseFactory.ok(netsAnalysisService.getAnalysisRecondList(recondParamasDTO));
    }

    @ApiOperation(value = "获取某条爆管记录详细关阀信息")
    @RequestMapping(value ="getAnalysisiValveByID" )
    public ResposeVO getAnalysisiValveByID(@ApiParam(name = "dto", required = true) @RequestBody @Valid IdDTO<Long> idDTO) throws Exception{
        Logger.debug("api/0/analysis/getAnalysisiValveByID 获取某条爆管记录详细关阀信息");
        return  ResponseFactory.ok(netsAnalysisService.getValveById(idDTO));
    }

    @ApiOperation(value = "导出关阀分析结果")
    @RequestMapping(value ="exportAnalysisiResult" )
    public ResposeVO exportAnalysisiResult(@ApiParam(name = "dto", required = true) @RequestBody @Valid ExportValveRecondDTO dto) throws Exception{
        if (dto == null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        try {
            String key = dto.getLineId() + GISConstants.UNDER_LINE + dto.getTime();
            new Thread(() -> {
                try {
                    String result = netsAnalysisService.exportAnalysisRecond(dto);
                    redisComponents.set(key, result, GISConstants.DOWNLOAD_EXPIRE);
                    Logger.debug("导出关阀分析结果，key = {}", key);
                } catch (BizException e) {
                    e.printStackTrace();
                    Logger.error("导出关阀分析结果！{}", Thread.currentThread().getName());
                    redisComponents.set(key, EApiStatus.ERR_SYS.getStatus(), 60);
                    try {
                        throw new BizException(e);
                    } catch (BizException e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();
            return ResponseFactory.ok(Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseFactory.err("文件生成中...", EApiStatus.ERR_SYS);
    }

    @ApiOperation(value = "导出历史关阀分析结果")
    @RequestMapping(value ="exportAnalysisiRecond" )
    public ResposeVO exportAnalysisiRecond(@ApiParam(name = "dto", required = true) @RequestBody @Valid ExportValveRecondDTO dto) throws Exception{
        if (dto == null){
            return ResponseFactory.err("列表参数为空", EApiStatus.ERR_VALIDATE);
        }
        try {
            String key = dto.getLineId() + GISConstants.UNDER_LINE + dto.getTime();
            new Thread(() -> {
                try {
                    String result = netsAnalysisService.exportAnalysisRecond(dto);
                    redisComponents.set(key, result, GISConstants.DOWNLOAD_EXPIRE);
                    Logger.debug("导出历史关阀分析结果，key = {}", key);
                } catch (BizException e) {
                    e.printStackTrace();
                    Logger.error("导出历史关阀分析结果！{}", Thread.currentThread().getName());
                    redisComponents.set(key, EApiStatus.ERR_SYS.getStatus(), 60);
                    try {
                        throw new BizException(e);
                    } catch (BizException e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();
            return ResponseFactory.ok(Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseFactory.err("文件生成中...", EApiStatus.ERR_SYS);
    }

    @ApiOperation(value = "查询下载导出关阀分析文件")
    @RequestMapping(value = "getExportAnalysisiResult")
    public ResposeVO getExportAnalysisiResult(@RequestBody ExportValveRecondDTO dto) throws BizException {
        Logger.debug("查询下载导出关阀分析文件");
        String key = dto.getLineId() + GISConstants.UNDER_LINE + dto.getTime();
        String result = queryDevService.getDownLoadFile(key);
        return ResponseFactory.ok(result);
    }
}
