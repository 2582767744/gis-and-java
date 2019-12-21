package com.jdrx.gis.dao.analysis;

import com.jdrx.gis.beans.entity.analysis.GisPipeAnalysisValvePO;
import java.util.List;

public interface GisPipeAnalysisValvePOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(GisPipeAnalysisValvePO record);

    int insertSelective(GisPipeAnalysisValvePO record);

    List<GisPipeAnalysisValvePO> selectByPrimaryKey(Long id);

    List<String> getDevIdsByDevId(List<String>list);
}