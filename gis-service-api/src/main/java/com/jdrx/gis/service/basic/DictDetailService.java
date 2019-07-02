package com.jdrx.gis.service.basic;

import com.jdrx.gis.beans.dto.basic.DictDetailDTO;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.beans.vo.basic.DictDetailVO;
import com.jdrx.gis.dao.basic.DictDetailPOMapper;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 数据字典服务
 * @Author: liaosijun
 * @Time: 2019/6/27 13:08
 */
@Service
public class DictDetailService {

	private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DictDetailService.class);

	@Autowired
	private DictDetailPOMapper dictDetailPOMapper;

	/**
	 * 根据dict_type中val的值查询配置的dict_detail列表
	 * @param val dict_type中的val字段对应的值
	 * @return
	 * @throws BizException
	 */
	public List<DictDetailPO> findDetailsByTypeVal(String val) throws BizException {
		try {
			return dictDetailPOMapper.selectByVal(val);
		} catch (Exception e) {
			Logger.error("根据dict_type中val的值查询配置的dict_detail列表失败！", e.getMessage());
			throw new BizException("根据dict_type中val的值查询配置的dict_detail列表失败！");
		}
	}

	/**
	 * 新增字典数据
	 * @param dictDetailDTO
	 * @return
	 * @throws BizException
	 */
	public Boolean addDictDetail(DictDetailDTO dictDetailDTO) throws BizException {
		try {
			DictDetailPO dictDetailPO = new DictDetailPO();
			if (Objects.nonNull(dictDetailDTO)) {
				BeanUtils.copyProperties(dictDetailDTO, dictDetailPO);
			}
			int affectedRows = dictDetailPOMapper.insertSelective(dictDetailPO);
			return affectedRows > 0 ? true : false;
		} catch (Exception e) {
			Logger.error("新增字典数据失败！", e.getMessage());
			throw new BizException("新增字典数据失败！");
		}
	}

	/**
	 * 逻辑删除字典详情数据
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public Boolean delDictDetailById(Long id) throws BizException {
		try {
			int affectedRows = dictDetailPOMapper.logicDeleteById(id);
			return affectedRows > 0 ? true : false;
		} catch (Exception e) {
			Logger.error("删除字典数据失败！", e.getMessage());
			throw new BizException("删除字典数据失败！");
		}
	}

	/**
	 * 更新字典数据
	 * @param dictDetailDTO
	 * @return
	 * @throws BizException
	 */
	public Boolean updateDictType(DictDetailDTO dictDetailDTO) throws BizException {
		try {
			DictDetailPO dictDetailPO = new DictDetailPO();
			if (Objects.nonNull(dictDetailDTO)) {
				BeanUtils.copyProperties(dictDetailDTO, dictDetailPO);
			}
			int affectedRows = dictDetailPOMapper.updateByPrimaryKeySelective(dictDetailPO);
			return affectedRows > 0 ? true : false;
		} catch (Exception e) {
			Logger.error("更新字典数据失败！", e.getMessage());
			throw new BizException("更新字典数据失败！");
		}
	}

	/**
	 * 根据id查数据
	 * @param id
	 * @return
	 * @throws BizException
	 */
	public DictDetailVO getDictTypeById(Long id) throws BizException {
		try {
			DictDetailPO dictDetailPO = dictDetailPOMapper.selectByPrimaryKey(id);
			DictDetailVO dictDetailVO = new DictDetailVO();
			BeanUtils.copyProperties(dictDetailPO, dictDetailVO);
			return dictDetailVO;
		} catch (Exception e) {
			Logger.error("根据ID{}查询失败！", id);
			throw new BizException("根据ID查询失败！");
		}
	}
}