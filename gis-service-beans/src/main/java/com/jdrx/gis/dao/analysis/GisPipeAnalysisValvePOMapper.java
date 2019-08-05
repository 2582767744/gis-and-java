package com.jdrx.gis.dao.analysis;


import com.jdrx.gis.beans.entry.analysis.ExportValveDTO;
import com.jdrx.gis.beans.entry.analysis.GisPipeAnalysisValvePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GisPipeAnalysisValvePOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GisPipeAnalysisValvePO record);

    int insertSelective(GisPipeAnalysisValvePO record);

    List<GisPipeAnalysisValvePO> selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GisPipeAnalysisValvePO record);

    int updateByPrimaryKey(GisPipeAnalysisValvePO record);
}