package com.jdrx.gis.api.query;

import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.base.InsertDTO;
import com.jdrx.gis.beans.dto.base.UpdateDTO;
import com.jdrx.gis.beans.dto.basic.CriteriaDTO;
import com.jdrx.gis.beans.dto.basic.CriteriaQueryDTO;
import com.jdrx.gis.beans.dto.query.AttrQeuryDTO;
import com.jdrx.gis.beans.dto.query.CaliberDTO;
import com.jdrx.gis.beans.dto.query.MeterialDTO;
import com.jdrx.gis.beans.vo.query.FieldNameVO;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.service.basic.AttCriteriaService;
import com.jdrx.gis.service.query.AttrQueryService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 根据属性查设备信息
 * @Author: liaosijun
 * @Time: 2019/6/21 10:07
 */
@RestController
@Api("属性查询服务")
@RequestMapping(value = "api/0/attrQuery", method = RequestMethod.POST)
public class AttrQueryApi {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(AttrQueryApi.class);

	@Autowired
	AttrQueryService attrQueryService;

	@Autowired
	RedisComponents redisComponents;

	@Autowired
	PathConfig pathConfig;

	@Autowired
	AttCriteriaService attCriteriaService;

	@ApiOperation(value = "根据设备类型ID递归查询模板")
	@RequestMapping(value = "findHasTplDevTypeListById")
	public ResposeVO findHasTplDevTypeListById(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto)
			throws BizException {
		Logger.debug("api/0/query/findHasTplDevTypeListById 根据设备类型ID递归查询模板，typeId = {}", dto.getId());
		return ResponseFactory.ok(attrQueryService.findHasTplDevTypeListById(dto.getId()));
	}

	@ApiOperation(value = "根据设备类型ID查模板配置信息")
	@RequestMapping(value = "findAttrListByTypeId")
	public ResposeVO findAttrListByTypeId(@ApiParam(name = "iddto", required = true) @RequestBody @Valid IdDTO<Long> dto)
		throws BizException {
		Logger.debug("api/0/query/findAttrListByTypeId 根据设备类型ID查模板配置信息，typeId = {}", dto.getId());
		return ResponseFactory.ok(attrQueryService.findAttrListByTypeId(dto.getId()));
	}


	@ApiOperation(value = "根据所选区域或键入的参数值查设备列表信息")
	@RequestMapping(value = "findDevListByAreaOrInputVal")
	public ResposeVO findDevListByAreaOrInputVal(@RequestBody @Valid AttrQeuryDTO dto) throws BizException
	{
		Logger.debug("api/0/query/findDevListByAreaOrInputVal 根据所选区域或属性键入的参数值查设备列表信息，参数值 = {}", dto.toString());
		if (Objects.isNull(dto)) {
			throw new BizException("参数为空");
		}
		return ResponseFactory.ok(attrQueryService.findDevListByAreaOrInputVal(dto));
	}

	@ApiOperation(value = "导出根据所选区域或键入的参数值所查询的设备列表信息")
	@RequestMapping(value = "exportDevListByAreaOrInputVal")
	public ResposeVO exportDevListByAreaOrInputVal(@RequestBody @Valid AttrQeuryDTO dto) {
		Logger.debug("api/0/query/exportDevListByAreaOrInputVal 导出根据所选区域或属性键入的参数值所查询设备列表信息，参数值 = {}", dto.toString());
		try {
			String key = dto.getTypeId() + GISConstants.UNDER_LINE + dto.getTime();
			new Thread(() -> {
				try {
					String result = attrQueryService.exportDevListByAreaOrInputVal(dto);
					redisComponents.set(key, result, GISConstants.DOWNLOAD_EXPIRE);
					Logger.debug("生成导出文件成功，key = {}", key);
				} catch (BizException e) {
					e.printStackTrace();
					Logger.error("导出设备列表信息失败！{}", Thread.currentThread().getName());
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


	@ApiOperation(value = "根据所选区域或键入的参数值查设备列表信息(分页)")
	@RequestMapping(value = "findDevListPageByAreaOrInputVal")
	public ResposeVO findDevListPageByAreaOrInputVal(@RequestBody @Valid AttrQeuryDTO dto) throws BizException {
		Logger.debug("api/0/query/findDevListPageByAreaOrInputVal 根据所选区域或属性键入的参数值查设备列表信息，分页，参数值 = {}", dto.toString());
		if (Objects.isNull(dto)) {
			throw new BizException("参数为空");
		}
		return ResponseFactory.ok(attrQueryService.findDevListByAreaOrInputVal(dto));
	}

	@ApiOperation(value = "根据管径范围值获取对应的图层url")
	@RequestMapping(value = "findCaliberLayerUrl")
	public ResposeVO findCaliberLayerUrl(@RequestBody @Valid CaliberDTO caliberDTO) throws BizException {
		if (Objects.isNull(caliberDTO)) {
			throw new BizException("参数为空");
		}
		return ResponseFactory.ok(attrQueryService.findCiliberLayerUrlByNum(caliberDTO));
	}

	@ApiOperation(value = "根据管材值获取对应的图层url")
	@RequestMapping(value = "findMeterialLayerUrl")
	public ResposeVO findMeterialLayerUrl(@RequestBody @Valid MeterialDTO meterial) throws BizException {
		if (Objects.isNull(meterial)) {
			throw new BizException("参数为空");
		}
		return ResponseFactory.ok(attrQueryService.findMeterialLayerUrlByNum(meterial));
	}

	@ApiOperation(value = "根据设备ID获取模板信息")
	@RequestMapping(value = "getTplByDevId")
	public ResposeVO getTplByDevId(@RequestBody @Valid IdDTO<String> idDTO) throws BizException {
		Logger.debug("根据设备ID获取模板信息, 设备ID = {}", idDTO.getId());
		return ResponseFactory.ok(attrQueryService.getFieldNames(idDTO.getId()));
	}

	@ApiOperation(value = "保存属性的筛选条件")
	@RequestMapping(value = "saveCriteriaRecord")
	public ResposeVO saveCriteriaRecord(@RequestBody @Validated({InsertDTO.class}) CriteriaDTO criteriaDTO) throws BizException {
		Logger.debug("筛选条件需存信息：{}", criteriaDTO.toString());
		return ResponseFactory.ok(attCriteriaService.saveCriteriaRecord(criteriaDTO));
	}

	@ApiOperation(value = "通过主键更新属性的筛选条件")
	@RequestMapping(value = "updateCriteriaRecordById")
	public ResposeVO updateCriteriaRecordById(@RequestBody @Validated({UpdateDTO.class}) CriteriaDTO criteriaDTO) throws BizException {
		Logger.debug("筛选条件更新信息：{}", criteriaDTO.toString());
		return ResponseFactory.ok(attCriteriaService.updateCriteriaRecord(criteriaDTO));
	}


	@ApiOperation(value = "物理删除属性的筛选条件")
	@RequestMapping(value = "deleteCriteriaRecordById")
	public ResposeVO deleteCriteriaRecordById(@RequestBody @Valid IdDTO<Long> idDTO) throws BizException {
		Logger.debug("物理删除属性的筛选条件的ID={}", idDTO.getId());
		return ResponseFactory.ok(attCriteriaService.deleteCriteriaRecordById(idDTO.getId()));
	}

	@ApiOperation(value = "查询属性的筛选条件")
	@RequestMapping(value = "findCriteriaRecords")
	public ResposeVO findCriteriaRecords(@RequestBody @Valid CriteriaQueryDTO criteriaQueryDTO) throws BizException {
		Logger.debug("查询属性的筛选条件入参：{}", criteriaQueryDTO.toString());
		return ResponseFactory.ok(attCriteriaService.findConditionRecords(criteriaQueryDTO));
	}

	@ApiOperation(value = "验证筛选条件")
	@RequestMapping(value = "validateCriteria")
	public ResposeVO validateCriteria(@RequestBody @Valid AttrQeuryDTO dto) throws BizException {
		try {
			Logger.debug("验证查询属性的筛选条件入参：{}", dto.toString());
			return ResponseFactory.ok(attrQueryService.validateCriteria(dto));
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("验证失败！");
		}
	}

    @ApiOperation(value = "根据主键获取筛选条件")
    @RequestMapping(value = "getCriteriaByPrimaryKey")
	public ResposeVO getCriteriaByPrimaryKey(@RequestBody @Valid IdDTO<Long> idDTO) throws BizException {
	    return ResponseFactory.ok(attCriteriaService.getCriteriaByPrimaryKey(idDTO.getId()));
    }
}