package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.entity.basic.GisDevTplAttrPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GisDevTplAttrPOMapper {

    int insert(GisDevTplAttrPO record);

    int insertSelective(GisDevTplAttrPO record);

    GisDevTplAttrPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GisDevTplAttrPO record);

    int updateByPrimaryKey(GisDevTplAttrPO record);

	/**
	 * 根据设备ID查模板配置信息
	 * @param devId
	 * @return
	 */
	@Deprecated
	List<GisDevTplAttrPO> findTplAttrsByDevId(Long devId);

	/**
	 * 根据设备类型ID查模板配置信息
	 * @param typeId
	 * @return
	 */
	List<GisDevTplAttrPO> findAttrListByTypeId(Long typeId);

	/**
	 * 根据数据类型查typcategory Code
	 * @param dataType
	 * @return
	 */
	String getCategoryCodeByDataType(String dataType);

	/**
	 * 批量插入模板数据
	 * @param tplAttrPOList
	 * @return
	 */
	int batchInsertSelective(@Param("tplAttrPOList") List<GisDevTplAttrPO> tplAttrPOList);

	/**
	 * 获取每个tpl_id对应的模板（列转行）
	 * @return
	 */
	List<Map<String, String>> selectTypeIdDescMap();

	/**
	 * 逻辑删除typeId相应模板
	 * @param typeId
	 * @return
	 */
	int delByTypeId(@Param("typeId") Long typeId);

	/**
	 * 根据模板名称查模板字段信息
	 * @param tplName
	 * @return
	 */
	List<GisDevTplAttrPO> selectTplByTplName(String tplName);

	/**
	 * 根据设备类型id查字段英文名称
	 * @param tqlId
	 * @return
	 */
	List<GisDevTplAttrPO> selectNameByTqlId(Integer tqlId);
}