package com.jdrx.gis.dao.basic;

import com.jdrx.gis.beans.dto.query.AttrQeuryDTO;
import com.jdrx.gis.beans.entity.basic.CodeXYPO;
import com.jdrx.gis.beans.entity.basic.GISDevExtPO;
import com.jdrx.gis.beans.entity.query.PipeLengthPO;
import com.jdrx.gis.beans.vo.basic.FeatureVO;
import com.jdrx.gis.beans.vo.basic.PointVO;
import com.jdrx.gis.beans.vo.datamanage.NeoLineVO;
import com.jdrx.gis.beans.vo.datamanage.NeoPointVO;
import com.jdrx.gis.beans.vo.query.GISDevExt2VO;
import com.jdrx.gis.beans.vo.query.GISDevExtVO;
import org.apache.ibatis.annotations.Param;
import org.postgresql.util.PGobject;

import java.util.List;
import java.util.Map;

public interface GISDevExtPOMapper {

    GISDevExtPO getDevExtByDevId(String devId);

	/**
	 * 根据ID集合查询设备列表信息
	 */
	List<GISDevExtVO> findDevListByDevIds(@Param("devIds") List<String> devIds);

	/**
	 * 根据所选区域或属性键入的参数值查设备列表信息
	 * @param dto
	 * @return
	 */
	List<GISDevExtVO> findDevListByAreaOrInputVal(@Param("dto") AttrQeuryDTO dto, @Param("devIds") String devIds);

	/**
	 * 根据所选区域或属性键入的参数值查设备列表 个数
	 * @param dto
	 * @param devIds
	 * @return
	 */
	Integer findDevListByAreaOrInputValCount(@Param("dto") AttrQeuryDTO dto, @Param("devIds") String devIds);

	/**
	 * 查水管总长度
	 * @return
	 */
	PipeLengthPO findPipeLength(@Param("val") String val);

	/**
	 * 根据devIds 获取设备属性信息，并附type_id
	 * @param devIds
	 * @return
	 */
	List<GISDevExt2VO> findDevListAttTypeByDevIds(@Param("devIds") String devIds);

	/**
	 * 根据关键字搜索相关设备要素
	 * @param val
	 * @return
	 */
	List<FeatureVO> findFeaturesByString(@Param("val") String val);

	/**
	 * 根据devIds获取要素基础信息
	 * @param devIds
	 * @return
	 */
	List<FeatureVO> findFeaturesByDevIds(@Param("devIds") String devIds);

	/**
	 * 获取管点数据
	 * @return
	 */
	List<NeoPointVO> getPointDevExt(String devIds);

	/**
	 * 获取管线数据
	 * @return
	 */
	List<NeoLineVO> getLineDevExt(String devIds);

	/**
	 * 根据设备的编码获取设备信息
	 * @param code
	 * @return
	 */
	GISDevExtPO selectByCode(@Param("code") String code);

	/**
	 * 根据传入的坐标列表转换成geometry的文本形式
	 * @param codeXYPOs
	 * @param srid
	 * @return
	 */
	List<Map<String, Object>> findGeomMapByPointCode(@Param("codeXYPOs") List<CodeXYPO> codeXYPOs, @Param("srid") Integer srid);

	/**
	 * 批量插入
	 * @param gisDevExtPOList
	 * @return
	 */
	Integer batchInsertSelective(@Param("gisDevExtPOList") List<GISDevExtPO> gisDevExtPOList);

	/**
	 * 根据批次号查询该批数据
	 * @param batchNum
	 * @return
	 */
	List<GISDevExtPO> selectExistRecords(@Param("batchNum") String batchNum);

	/**
	 * 经纬度坐标系转自定义坐标系
	 * @param geom
	 * @param srid
	 * @return
	 */
	String transformWgs84ToCustom(@Param("geom") String geom,@Param("srid") Integer srid);

	/**
	 * 保存单个实体对象
	 * @param record
	 * @return
	 */
	int insertSelective(GISDevExtPO record);

	/**
	 * 将geom从WKB转为WKT
	 * @param geom
	 * @return
	 */
	String transformGeomAsText(@Param("geom") String geom);

	/**
	 * 将geom转为带指定srid的字符串
	 * @param geom
	 * @param srid
	 * @return
	 */
	String addGeomWithSrid(@Param("geom") String geom,@Param("srid") Integer srid);

	/**
	 * 根据geom获取点XY坐标
	 * @param geom
	 * @return
	 */
	PointVO getPointXYFromGeom(@Param("geom") String geom);

	/**
	 * 根据设备id逻辑删除
	 * @param devId
	 * @return
	 */
	Integer deleteDevExtByDevId(@Param("devId")String devId);

	/**
	 * 批量更新
	 * @param gisDevExtPOList
	 * @return
	 */
	Integer batchUpdate(@Param("gisDevExtPOList") List<GISDevExtPO> gisDevExtPOList);

	/**
	 * 根据code获取ext数据
	 * @param codes
	 * @return
	 */
	List<GISDevExtPO> selectByCodes(@Param("codes") String codes);

	/**
	 * 选择更细
	 * @param record
	 * @return
	 */
	int updateByPrimaryKeySelective(GISDevExtPO record);

	/**
	 * 根据管点编码查询关联的管线
	 * @param val
	 * @return
	 */
	List<GISDevExtPO> selectLineByCode(@Param("val") String val);

	/**
	 * 更新设备的data_info
	 * @param dataInfo
	 * @param devId
	 * @return
	 */
	int updateDataInfoByDevId(@Param("dataInfo") PGobject dataInfo, @Param("devId") String devId);
}

