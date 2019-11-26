package com.jdrx.gis.service.dataManage;

import com.alibaba.fastjson.JSONObject;
import com.jdrx.gis.beans.constants.basic.GISConstants;
import com.jdrx.gis.beans.dto.dataManage.*;
import com.jdrx.gis.beans.entry.basic.DictDetailPO;
import com.jdrx.gis.beans.entry.basic.GISDevExtPO;
import com.jdrx.gis.beans.entry.basic.ShareDevPO;
import com.jdrx.gis.beans.entry.basic.ShareDevTypePO;
import com.jdrx.gis.beans.vo.basic.PointVO;
import com.jdrx.gis.beans.vo.query.FieldNameVO;
import com.jdrx.gis.config.DictConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.basic.ShareDevPOMapper;
import com.jdrx.gis.dao.basic.ShareDevTypePOMapper;
import com.jdrx.gis.dao.query.DevQueryDAO;
import com.jdrx.gis.service.analysis.NetsAnalysisService;
import com.jdrx.gis.service.basic.DictDetailService;
import com.jdrx.gis.service.basic.GISDeviceService;
import com.jdrx.platform.commons.rest.exception.BizException;
import com.jdrx.share.service.SequenceDefineService;
import org.postgresql.util.PGobject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description
 * @Author lr
 * @Time 2019/11/6 0006 下午 1:25
 */
@Service
public class DataEditorService {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(DataEditorService.class);

    @Autowired
    private DictConfig dictConfig;

    @Autowired
    private DictDetailService detailService;

    @Autowired
    ShareDevTypePOMapper shareDevTypePOMapper;

    @Autowired
    DevQueryDAO devQueryDAO;

    @Autowired
    SequenceDefineService sequenceDefineService;

    @Autowired
    GISDeviceService gisDeviceService;

    @Autowired
    GISDevExtPOMapper gisDevExtPOMapper;

    @Autowired
    ShareDevPOMapper shareDevPOMapper;

    @Autowired
    NetsAnalysisService netsAnalysisService;


    /**
     * 获取所有点类型
     * @return
     */
    public List<ShareDevTypePO> getAllPointType(){
        List<ShareDevTypePO> shareDevTypePOS = new ArrayList<>();
        try {
            String layerUrl = dictConfig.getPointType();
            List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(layerUrl);
            if (detailPOs != null){
                String stringIds = detailPOs.get(0).getVal();
                shareDevTypePOS = shareDevTypePOMapper.findPointTypeByIds(stringIds);
            }

        }catch (Exception e){
            Logger.error(e.getMessage());
        }
        return shareDevTypePOS;
    }

    /**
     * 获取所有管线类型
     * @return
     */
    public List<ShareDevTypePO> getAllLineType(){
        List<ShareDevTypePO> shareDevTypePOS = new ArrayList<>();
        try {
            String layerUrl = dictConfig.getLineType();
            List<DictDetailPO> detailPOs = detailService.findDetailsByTypeVal(layerUrl);
            if (detailPOs != null){
                String stringIds = detailPOs.get(0).getVal();
                shareDevTypePOS = shareDevTypePOMapper.findLineTypeByIds(stringIds);
            }

        }catch (Exception e){
            Logger.error(e.getMessage());
        }
        return shareDevTypePOS;
    }

    /**
     * 保存管点及新增管线信息（线上加点）
     * @param dto
     */
    public Boolean saveAddedSharePoint(ShareAddedPointDTO dto) throws BizException{
        try {
            if(!savaSharePointOnLine(dto)){
                throw new BizException("保存管点信息失败！");
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通过设备类型id查詢最顶端父id，获取设备属性模板
     * @param typeId
     * @return
     */
     public List<FieldNameVO> getDevExtByTopPid(Long typeId) throws BizException{
        try {
            List<Long> typeIdList = getAllLineTypeIds();
            Boolean bl = typeIdList.contains(typeId);
            List<FieldNameVO> fieldNameVOS =  devQueryDAO.findFieldNamesByDevTypeId(typeId);
            FieldNameVO fieldNameVO = new FieldNameVO();
            Iterator iterator = fieldNameVOS.iterator();
            while (iterator.hasNext()){
                fieldNameVO = (FieldNameVO) iterator.next();
                if(fieldNameVO.getFieldName().equals("dev_id") || fieldNameVO.getFieldName().equals("geom")){
                    iterator.remove();
                }
                if(bl && fieldNameVO.getFieldName().equals("code")){
                    iterator.remove();
                }

            }


            return fieldNameVOS;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BizException(e);
        }

    }

    /**
     * 线上加点保存点信息到gis_dev_ext及share_dev
     * @param dto
     * @return
     */
    public Boolean savaSharePointOnLine(ShareAddedPointDTO dto){
        try {
            Map<String,Object> map = dto.getMap();
            Long seq = sequenceDefineService.increment(gisDeviceService.sequenceKey());
            String devId = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seq);

            String geom = "POINT("+dto.getX()+" "+dto.getY()+")";
            String srid = netsAnalysisService.getValByDictString(dictConfig.getWaterPipeSrid());
            String transformGeom = gisDevExtPOMapper.transformWgs84ToCustom(geom,Integer.parseInt(srid));

            map.put("dev_id",devId);
            String jsonStr = JSONObject.toJSONString(map);
            PGobject jsonObject = new PGobject();
            jsonObject.setValue(jsonStr);
            jsonObject.setType("jsonb");

            GISDevExtPO po = new GISDevExtPO();
            po.setDevId(devId);
            po.setCode(map.get("code").toString());
            po.setName(map.get("name").toString());
            po.setGeom(transformGeom);
            po.setDataInfo(jsonObject);
            po.setTplTypeId(dto.getTypeId());

            ShareDevPO shareDevPO = new ShareDevPO();
            shareDevPO.setId(devId);
            shareDevPO.setName(map.get("name").toString());
            shareDevPO.setTypeId(dto.getTypeId());
            shareDevPO.setLng(String.format("%.8f",dto.getX()));
            shareDevPO.setLat(String.format("%.8f",dto.getY()));
            if (map.containsKey("addr")){
                shareDevPO.setAddr(map.get("addr").toString());
            }

            //保存管点信息
            gisDevExtPOMapper.insertSelective(po);
            shareDevPOMapper.insertSelective(shareDevPO);

            //获取管线信息
            GISDevExtPO gisDevExtPOLine = gisDevExtPOMapper.getDevExtByDevId(dto.getLineDevId());
            String geomLine =  gisDevExtPOLine.getGeom();
            String geomText = gisDevExtPOMapper.transformGeomAsText(geomLine);

            PointVO pointVO = gisDevExtPOMapper.getPointXYFromGeom(transformGeom);
            String newLineGeomStr1 = geomText.substring(0,geomText.indexOf(',')+1) + pointVO.getX().toString()+" "+pointVO.getY().toString()+")";
            String newLineGeomStr2 = "LINESTRING("+pointVO.getX() + " " +pointVO.getY() +geomText.substring(geomText.indexOf(','));

            String lineGeom1 = gisDevExtPOMapper.addGeomWithSrid(newLineGeomStr1,Integer.parseInt(srid));
            String lineGeom2 = gisDevExtPOMapper.addGeomWithSrid(newLineGeomStr2,Integer.parseInt(srid));

            //构造新的管线GISDevExtPO对象
            GISDevExtPO gisDevExtPOLine1 = gisDevExtPOMapper.getDevExtByDevId(dto.getLineDevId());
            GISDevExtPO gisDevExtPOLine2 = gisDevExtPOMapper.getDevExtByDevId(dto.getLineDevId());

            Long seqLine1 = sequenceDefineService.increment(gisDeviceService.sequenceKey());
            String devIdLine1 = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seqLine1);
            gisDevExtPOLine1.setDevId(devIdLine1);
            Long seqLine2 = sequenceDefineService.increment(gisDeviceService.sequenceKey());
            String devIdLine2 = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seqLine2);
            gisDevExtPOLine2.setDevId(devIdLine2);

            //更新data_info里面的值
            JSONObject jb1 = JSONObject.parseObject(gisDevExtPOLine1.getDataInfo().toString());
            Map<String,Object> map1 = (Map)jb1;
            String lineCode1 = null;
            if (map1.containsKey("qdbm")){
                lineCode1 = map1.get("qdbm").toString()+"-"+po.getCode();
                map1.replace("code",map1.get("code"),lineCode1);
                map1.replace("zdbm",map1.get("zdbm"),po.getCode());
            }
            map1.replace("dev_id",map1.get("dev_id"),devIdLine1);
            String jsonStr1 = JSONObject.toJSONString(map1);
            PGobject jsonObject1 = new PGobject();
            jsonObject1.setValue(jsonStr1);
            jsonObject1.setType("jsonb");

            JSONObject jb2 = JSONObject.parseObject(gisDevExtPOLine2.getDataInfo().toString());
            Map<String,Object> map2 = (Map)jb2;
            String lineCode2 = null;
            if (map2.containsKey("zdbm")){
                lineCode2 = po.getCode()+"-"+map2.get("zdbm").toString();
                map2.replace("code",map2.get("code"),lineCode2);
                map2.replace("qdbm",map2.get("qdbm"),po.getCode());
            }
            map2.replace("dev_id",map2.get("dev_id"),devIdLine2);
            String jsonStr2 = JSONObject.toJSONString(map2);
            PGobject jsonObject2 = new PGobject();
            jsonObject2.setValue(jsonStr2);
            jsonObject2.setType("jsonb");


            gisDevExtPOLine1.setGeom(lineGeom1);
            gisDevExtPOLine1.setDataInfo(jsonObject1);
            if (lineCode1 != null){
                gisDevExtPOLine1.setCode(lineCode1);
            }
            if (lineCode2 != null){
                gisDevExtPOLine2.setCode(lineCode2);
            }
            gisDevExtPOLine2.setGeom(lineGeom2);
            gisDevExtPOLine2.setDataInfo(jsonObject2);
            gisDevExtPOLine1.setId(null);
            gisDevExtPOLine2.setId(null);


            //构造新的管线--share_dev
            ShareDevPO shareDevPOLine = shareDevPOMapper.selectByPrimaryKey(dto.getLineDevId());
            ShareDevPO shareDevPOLine1 = shareDevPOMapper.selectByPrimaryKey(dto.getLineDevId());
            ShareDevPO shareDevPOLine2 = shareDevPOMapper.selectByPrimaryKey(dto.getLineDevId());

            shareDevPOLine1.setId(gisDevExtPOLine1.getDevId());
            shareDevPOLine1.setTypeId(shareDevPOLine.getTypeId());
            shareDevPOLine2.setId(gisDevExtPOLine2.getDevId());
            shareDevPOLine2.setTypeId(shareDevPOLine.getTypeId());

            //先删除gis_dev_ext原来管线
            gisDevExtPOMapper.deleteDevExtByDevId(dto.getLineDevId());

            //增加的两条管线
            gisDevExtPOMapper.insertSelective(gisDevExtPOLine1);
            gisDevExtPOMapper.insertSelective(gisDevExtPOLine2);

            //先删除share_dev原来管线
            shareDevPOMapper.deleteByPrimaryKey(dto.getLineDevId());

            shareDevPOMapper.insertSelective(shareDevPOLine1);
            shareDevPOMapper.insertSelective(shareDevPOLine2);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 保存添加管网（管点和管线）
     * @param dto
     * @return
     */
    public Boolean saveShareNets(ShareAddedNetsDTO dto) throws BizException{
        try {
            if((!saveSharePoint(dto.getPointList())) ||(!savaShareLine(dto.getLineList()))){
                return false;
            }else {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new  BizException("保存管线失败！");
        }
    }
    /**
     * 保存管点
     * @param list
     * @return
     */
    public Boolean saveSharePoint(List<SharePointDTO> list) throws BizException{
        if (list == null||list.size() == 0 ){
            return false;
        }
        try {
            for(SharePointDTO dto:list){
                Long seq = sequenceDefineService.increment(gisDeviceService.sequenceKey());
                String devId = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seq);

                String geom = "POINT("+dto.getX()+" "+dto.getY()+")";
                String srid = netsAnalysisService.getValByDictString(dictConfig.getWaterPipeSrid());
                String transformGeom = gisDevExtPOMapper.transformWgs84ToCustom(geom,Integer.parseInt(srid));
                PointVO pointVO = gisDevExtPOMapper.getPointXYFromGeom(transformGeom);

                String jsonStr = JSONObject.toJSONString(dto.getMapAttr());
                PGobject jsonObject = new PGobject();
                jsonObject.setValue(jsonStr);
                jsonObject.setType("jsonb");

                GISDevExtPO po = new GISDevExtPO();
                po.setDevId(devId);
                po.setCode(dto.getMapAttr().get("code").toString());
                po.setName(dto.getMapAttr().get("name").toString());
                po.setGeom(transformGeom);
                po.setDataInfo(jsonObject);
                po.setTplTypeId(dto.getTypeId());

                ShareDevPO shareDevPO = new ShareDevPO();
                shareDevPO.setId(po.getDevId());
                shareDevPO.setName(po.getName());
                shareDevPO.setTypeId(po.getTplTypeId());
                shareDevPO.setLng(String.format("%.8f",pointVO.getX()));
                shareDevPO.setLat(String.format("%.8f",pointVO.getY()));
                if(dto.getMapAttr().containsKey("dlm")){
                    shareDevPO.setAddr(dto.getMapAttr().get("dlm").toString());
                }

                gisDevExtPOMapper.insertSelective(po);
                shareDevPOMapper.insertSelective(shareDevPO);

            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("新增管网保存管点失败！"+list.toString());
            throw new BizException("新增管网保存管点失败！");
        }
    }

    /**
     * 保存管线
     * @param list
     * @return
     */
    public Boolean savaShareLine(List<ShareLineDTO> list) throws BizException{
        if (list == null||list.size() == 0 ){
            return false;
        }
        try {
            for (ShareLineDTO dto:list){
                GISDevExtPO startPointPO = gisDevExtPOMapper.selectByCode(dto.getQdbm());
                GISDevExtPO endPointPO = gisDevExtPOMapper.selectByCode(dto.getZdbm());

                PointVO startVO = gisDevExtPOMapper.getPointXYFromGeom(startPointPO.getGeom());
                PointVO endVO = gisDevExtPOMapper.getPointXYFromGeom(endPointPO.getGeom());

                String srid = netsAnalysisService.getValByDictString(dictConfig.getWaterPipeSrid());

                String lineGeomStr = "LINESTRING("+startVO.getX()+" "+startVO.getY()+","+endVO.getX()+" "+endVO.getY()+")";
                String transformGeomStr = gisDevExtPOMapper.addGeomWithSrid(lineGeomStr,Integer.parseInt(srid));

                String jsonStr = JSONObject.toJSONString(dto.getMapAttr());
                PGobject jsonObject = new PGobject();
                jsonObject.setValue(jsonStr);
                jsonObject.setType("jsonb");

                Long seq = sequenceDefineService.increment(gisDeviceService.sequenceKey());
                String devId = String.format("%04d%s%06d",dto.getTypeId(), GISConstants.PLATFORM_CODE, seq);
                GISDevExtPO gisDevExtPO = new GISDevExtPO();
                gisDevExtPO.setDevId(devId);
                gisDevExtPO.setCode(dto.getQdbm()+"-"+dto.getZdbm());
                gisDevExtPO.setCaliber(dto.getCaliber());
                gisDevExtPO.setMaterial(dto.getMaterial());
                gisDevExtPO.setTplTypeId(dto.getTypeId());
                gisDevExtPO.setDataInfo(jsonObject);
                gisDevExtPO.setGeom(transformGeomStr);

                ShareDevPO shareDevPO = new ShareDevPO();
                shareDevPO.setId(gisDevExtPO.getDevId());
                shareDevPO.setTypeId(gisDevExtPO.getTplTypeId());
                shareDevPO.setName(getNameByCaliber(gisDevExtPO.getCaliber()));

                //保存管线
                gisDevExtPOMapper.insertSelective(gisDevExtPO);
                shareDevPOMapper.insertSelective(shareDevPO);

            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("新增管网保存管线失败！"+list.toString());
            throw new BizException("保存管线失败！");
        }
    }

    /**
     * 根据设备编码查询模板属性
     * @param code
     * @return
     */
    public List<FieldNameVO> getAttrByCode(String code) throws BizException{
        try {
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.selectByCode(code);
            List<FieldNameVO> fieldNameVOS =  getDevExtByTopPid(gisDevExtPO.getTplTypeId());
            if(fieldNameVOS.size() == 0 || fieldNameVOS == null){
                throw new BizException("没有查询到设备信息！");
            }
            return fieldNameVOS;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("根据设备编码查询模板属性失败"+code);
            throw new BizException("根据设备编码查询模板属性失败!");
        }
    }
    /**
     * 更新设备属性信息
     * @param map
     * @return
     * @throws BizException
     */
    public Boolean updateGISDevExtAttr(Map<String,Object> map) throws BizException{
        try {
            String code = null;
            if(!map.containsKey("code")){
                code = map.get("qdbm").toString()+"-"+map.get("zdbm").toString();
            }else {
                code = map.get("code").toString();
            }
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.selectByCode(code);
            if(gisDevExtPO == null){
                Logger.error("查找无设备信息");
                throw new BizException("查询设备失败！");
            }
            List<DictDetailPO> dictDetailPOS = detailService.findDetailsByTypeVal(dictConfig.getLineType());
            List<Long> list = shareDevTypePOMapper.getAllTypeIdByTopId(Long.valueOf(dictDetailPOS.get(0).getVal()));
            //判断是否是线类型
            if(list.contains(gisDevExtPO.getTplTypeId())){
                if (!(Integer.parseInt(map.get("caliber").toString()) == gisDevExtPO.getCaliber())){
                    gisDevExtPO.setCaliber(Integer.parseInt(map.get("caliber").toString()));
                    String name = getNameByCaliber(Integer.parseInt(map.get("caliber").toString()));
                    gisDevExtPO.setName(name);
                }
                if (!map.get("material").toString().equals(gisDevExtPO.getMaterial())){
                    gisDevExtPO.setMaterial(map.get("material").toString());
                }
                //判断类型是否改变
                if(!gisDevExtPO.getTplTypeId().equals(Long.valueOf(map.get("typeId").toString()))){
                    gisDevExtPO.setTplTypeId(Long.valueOf(map.get("typeId").toString()));
                }
            }else {
                //判断类型是否改变
                if(!gisDevExtPO.getTplTypeId().equals(Long.valueOf(map.get("typeId").toString()))){
                    gisDevExtPO.setName(map.get("name").toString());
                    gisDevExtPO.setTplTypeId(Long.valueOf(map.get("typeId").toString()));
                }

            }

            String jsonStr = JSONObject.toJSONString(map);
            PGobject jsonObject = new PGobject();
            jsonObject.setValue(jsonStr);
            jsonObject.setType("jsonb");
            gisDevExtPO.setDataInfo(jsonObject);

            gisDevExtPOMapper.updateByPrimaryKeySelective(gisDevExtPO);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("更新设备属性信息失败！"+map.toString());
            throw new BizException("更新设备属性信息失败！");
        }
    }

    /**
     * 判断管点编号是否重复
     * @param code
     * @return
     */
    public Boolean getCodeExist(String code){
        GISDevExtPO po = gisDevExtPOMapper.selectByCode(code);
        return po == null? false: true;
    }

    /**
     * 根据编码查询设备详细信息
     * @param code
     * @return
     */
    public GISDevExtPO getGISDevExtByCode(String code) throws BizException{
        try {
            GISDevExtPO po =  gisDevExtPOMapper.selectByCode(code);
            if (po == null){
                throw new BizException("根据编码查询设备详细信息失败!");
            }
            return po;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("根据编码查询设备详细信息失败！"+code);
            throw new BizException("根据编码查询设备详细信息失败！");

        }
    }

    /**
     * 移动管点
     * @param dto
     * @return
     * @throws BizException
     */
    public Boolean moveShareDevPoint(MovePointDTO dto)throws BizException{
        try {
            GISDevExtPO gisDevExtPO = gisDevExtPOMapper.getDevExtByDevId(dto.getDevId());
            String geom = "POINT("+dto.getX()+" "+dto.getY()+")";
            String srid = detailService.findDetailsByTypeVal(dictConfig.getWaterPipeSrid()).get(0).getVal();
            String transformGeom = gisDevExtPOMapper.addGeomWithSrid(geom,Integer.parseInt(srid));
            PointVO pointVO = gisDevExtPOMapper.getPointXYFromGeom(transformGeom);

            //设置更新新的管点空间信息
            gisDevExtPO.setGeom(transformGeom);
            gisDevExtPOMapper.updateByPrimaryKeySelective(gisDevExtPO);

            //查找相关联的管线
            List<GISDevExtPO> gisDevExtPOLines = gisDevExtPOMapper.selectLineByCode(gisDevExtPO.getCode());
            for(GISDevExtPO po: gisDevExtPOLines ){
                Object datainfo = po.getDataInfo();
                JSONObject jb = JSONObject.parseObject(datainfo.toString());
                Map<String,Object> map = (Map)jb;
                String lineGeom = po.getGeom();
                String lineGeomTmp = null;
                String lineGeomSrid = null;
                if((map.containsKey("qdbm")) && (map.containsKey("qdbm"))){
                    if (map.get("qdbm").equals(gisDevExtPO.getCode())){
                        lineGeomTmp = "LINESTRING("+pointVO.getX()+" "+pointVO.getY()+lineGeom.substring(lineGeom.indexOf(","));
                        lineGeomSrid = gisDevExtPOMapper.addGeomWithSrid(lineGeomTmp,Integer.parseInt(srid));
                        po.setGeom(lineGeomSrid);
                    }
                    if (map.get("zdbm").equals(gisDevExtPO.getCode())){
                        lineGeomTmp = lineGeom.substring(0,lineGeom.indexOf(",")+1)+pointVO.getX()+" "+pointVO.getY()+")";
                        lineGeomSrid = gisDevExtPOMapper.addGeomWithSrid(lineGeomTmp,Integer.parseInt(srid));
                        po.setGeom(lineGeomSrid);
                    }
                    gisDevExtPOMapper.updateByPrimaryKeySelective(po);
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("移动管点失败！"+dto.toString());
            throw new BizException("移动管点失败！");
        }
    }

    /**
     * 根据管径获取管线设备名称
     * @param caliber
     * @return
     */
    public String getNameByCaliber(Integer caliber){
        if (caliber >= 0 && caliber < 100){
            return GISConstants.CALIBER_0;
        }else if(caliber >=100 && caliber <200){
            return GISConstants.CALIBER_100;
        }else if(caliber >=200 && caliber <400){
            return GISConstants.CALIBER_200;
        }else if(caliber >=400 && caliber <600){
            return GISConstants.CALIBER_400;
        }else if(caliber >=600 && caliber <900){
            return GISConstants.CALIBER_600;
        }else if(caliber >=900){
            return GISConstants.CALIBER_900;
        }
        return null;
    }

    /**
     * 获取所有管线设备类型id
     * @return
     */
    public List<Long> getAllLineTypeIds(){
        List<Long> list = new ArrayList<>();
        try {
            List<DictDetailPO> detailPOS = detailService.findDetailsByTypeVal(dictConfig.getLineType());
            list = shareDevTypePOMapper.getAllTypeIdByTopId(Long.valueOf(detailPOS.get(0).getVal()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 逻辑删除gis_dev_ext 和share_dev设备
     * @param devId
     * @return
     * @throws BizException
     */
    public Boolean deleteShareDevByDevId(String devId) throws BizException{
        try {
            gisDevExtPOMapper.deleteDevExtByDevId(devId);
            shareDevPOMapper.deleteByPrimaryKey(devId);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("根据devId删除设备失败！dev_id ="+devId);
            throw new BizException("根据devId删除设备失败!");
        }
    }


}
