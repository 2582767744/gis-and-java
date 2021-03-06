package com.jdrx.gis.service.dataManage;

import com.google.common.collect.Lists;
import com.jdrx.gis.beans.entity.basic.ExcelLinePo;
import com.jdrx.gis.beans.entity.basic.ExcelPointPo;
import com.jdrx.gis.config.PathConfig;
import com.jdrx.gis.dao.basic.GISDevExtPOMapper;
import com.jdrx.gis.dao.query.SelfExamination;
import com.jdrx.gis.util.ExcelStyleUtil;
import com.jdrx.gis.util.JavaFileToFormUpload;
import com.jdrx.platform.commons.rest.exception.BizException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 验证客户给到的设备数据
 *
 * @Author: yangsheng
 * @Time: 2020/1/13 15:34
 */
@Service
public class VerifyDataService {

    @Autowired
    private SelfExamination selfExamination;

    @Autowired
    private PathConfig pathConfig;


    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(VerifyDataService.class);

    public String exportSelfExaminationReport(MultipartFile file) throws BizException {
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        List pointList = analysisExcelPoint(file);
        List lineList = analysisExcelLine(file);
        //excel数据管点编码重复
        SXSSFSheet sheet = workbook.createSheet("excel数据管点编码重复");
        List<ExcelPointPo> repeatCodeList = findExcelRepeatCode(pointList);
        Row headerRow = sheet.createRow(0);
        getPointHeader(headerRow);
        for (int i = 0; i < repeatCodeList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(repeatCodeList.get(i).getPointCode());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(repeatCodeList.get(i).getPointX());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(repeatCodeList.get(i).getPointY());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(repeatCodeList.get(i).getGroundHeight());
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(repeatCodeList.get(i).getDepth());
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(repeatCodeList.get(i).getMaterial());
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(repeatCodeList.get(i).getName());
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(repeatCodeList.get(i).getAddress());
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(repeatCodeList.get(i).getSpec());
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(repeatCodeList.get(i).getSurveyCompany());
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(repeatCodeList.get(i).getSurveyDate());
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(repeatCodeList.get(i).getBelongTo());
            Cell cell12 = row.createCell(12);
            cell12.setCellValue(repeatCodeList.get(i).getRemark());
        }
        //获得x坐标为空的管点编码
        SXSSFSheet sheet1 = workbook.createSheet("x坐标为空的管点编码数据");
        List<ExcelPointPo> xIsNullCodeList = findXIsNullCode(pointList);
        Row headerRow1 = sheet1.createRow(0);
        getPointHeader(headerRow1);
        for (int i = 0; i < xIsNullCodeList.size(); i++) {
            Row row = sheet1.createRow(i + 1);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(xIsNullCodeList.get(i).getPointCode());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(xIsNullCodeList.get(i).getPointX());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(xIsNullCodeList.get(i).getPointY());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(xIsNullCodeList.get(i).getGroundHeight());
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(xIsNullCodeList.get(i).getDepth());
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(xIsNullCodeList.get(i).getMaterial());
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(xIsNullCodeList.get(i).getName());
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(xIsNullCodeList.get(i).getAddress());
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(xIsNullCodeList.get(i).getSpec());
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(xIsNullCodeList.get(i).getSurveyCompany());
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(xIsNullCodeList.get(i).getSurveyDate());
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(xIsNullCodeList.get(i).getBelongTo());
            Cell cell12 = row.createCell(12);
            cell12.setCellValue(xIsNullCodeList.get(i).getRemark());
        }
        //获得y坐标为空的管点编码
        SXSSFSheet sheet2 = workbook.createSheet("y坐标为空的管点编码数据");
        List<ExcelPointPo> yIsNullCodeList = findYIsNullCode(pointList);
        Row headerRow2 = sheet2.createRow(0);
        getPointHeader(headerRow2);
        for (int i = 0; i < yIsNullCodeList.size(); i++) {
            Row row = sheet2.createRow(i + 1);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(yIsNullCodeList.get(i).getPointCode());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(yIsNullCodeList.get(i).getPointX());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(yIsNullCodeList.get(i).getPointY());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(yIsNullCodeList.get(i).getGroundHeight());
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(yIsNullCodeList.get(i).getDepth());
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(yIsNullCodeList.get(i).getMaterial());
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(yIsNullCodeList.get(i).getName());
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(yIsNullCodeList.get(i).getAddress());
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(yIsNullCodeList.get(i).getSpec());
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(yIsNullCodeList.get(i).getSurveyCompany());
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(yIsNullCodeList.get(i).getSurveyDate());
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(yIsNullCodeList.get(i).getBelongTo());
            Cell cell12 = row.createCell(12);
            cell12.setCellValue(yIsNullCodeList.get(i).getRemark());
        }
        //excel数据在系统中存在的管点编码
        SXSSFSheet sheet3 = workbook.createSheet("系统已存在的管点编码数据");
        List<ExcelPointPo> listRepeat = findDataBaseRepeatCode(pointList);
        Row headerRow3 = sheet3.createRow(0);
        getPointHeader(headerRow3);
        for (int i = 0; i < listRepeat.size(); i++) {
            Row row = sheet3.createRow(i + 1);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(listRepeat.get(i).getPointCode());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(listRepeat.get(i).getPointX());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(listRepeat.get(i).getPointY());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(listRepeat.get(i).getGroundHeight());
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(listRepeat.get(i).getDepth());
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(listRepeat.get(i).getMaterial());
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(listRepeat.get(i).getName());
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(listRepeat.get(i).getAddress());
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(listRepeat.get(i).getSpec());
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(listRepeat.get(i).getSurveyCompany());
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(listRepeat.get(i).getSurveyDate());
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(listRepeat.get(i).getBelongTo());
            Cell cell12 = row.createCell(12);
            cell12.setCellValue(listRepeat.get(i).getRemark());
        }
        //管线起点编码在excel和系统中不存在的编码
        SXSSFSheet sheet4 = workbook.createSheet("管线起点编码在excel和系统中不存在");
        List<ExcelLinePo> noExistStartCodesList = findNoExistStartCodes(lineList, pointList);
        Row headerRow4 = sheet4.createRow(0);
        getLineHeader(headerRow4);
        for (int i = 0; i < noExistStartCodesList.size(); i++) {
            Row row = sheet4.createRow(i + 1);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(noExistStartCodesList.get(i).getStartCode());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(noExistStartCodesList.get(i).getEndCode());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(noExistStartCodesList.get(i).getMaterial());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(noExistStartCodesList.get(i).getCaliber());
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(noExistStartCodesList.get(i).getStartDepth());
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(noExistStartCodesList.get(i).getEndDepth());
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(noExistStartCodesList.get(i).getBuryType());
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(noExistStartCodesList.get(i).getSurveyCompany());
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(noExistStartCodesList.get(i).getSurveyDate());
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(noExistStartCodesList.get(i).getBelong_to());
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(noExistStartCodesList.get(i).getRemark());
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(noExistStartCodesList.get(i).getAddress());
        }
        //管线终点编码在excel和系统中不存在的编码
        SXSSFSheet sheet5 = workbook.createSheet("管线终点编码在excel和系统中不存在");
        List<ExcelLinePo> noExistEndCodesList = findNoExistEndCodes(lineList, pointList);
        Row headerRow5 = sheet5.createRow(0);
        getLineHeader(headerRow5);
        for (int i = 0; i < noExistEndCodesList.size(); i++) {
            Row row = sheet5.createRow(i + 1);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(noExistEndCodesList.get(i).getStartCode());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(noExistEndCodesList.get(i).getEndCode());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(noExistEndCodesList.get(i).getMaterial());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(noExistEndCodesList.get(i).getCaliber());
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(noExistEndCodesList.get(i).getStartDepth());
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(noExistEndCodesList.get(i).getEndDepth());
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(noExistEndCodesList.get(i).getBuryType());
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(noExistEndCodesList.get(i).getSurveyCompany());
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(noExistEndCodesList.get(i).getSurveyDate());
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(noExistEndCodesList.get(i).getBelong_to());
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(noExistEndCodesList.get(i).getRemark());
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(noExistEndCodesList.get(i).getAddress());
        }
        //管线起点编码和终点编码重复的数据
        SXSSFSheet sheet6 = workbook.createSheet("excel管线编码重复的数据");
        List<ExcelLinePo> escelRepeatLineList = findExcelRepeatLine(lineList);
        Row headerRow6 = sheet6.createRow(0);
        getLineHeader(headerRow6);
        for (int i = 0; i < escelRepeatLineList.size(); i++) {
            Row row = sheet6.createRow(i + 1);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(escelRepeatLineList.get(i).getStartCode());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(escelRepeatLineList.get(i).getEndCode());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(escelRepeatLineList.get(i).getMaterial());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(escelRepeatLineList.get(i).getCaliber());
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(escelRepeatLineList.get(i).getStartDepth());
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(escelRepeatLineList.get(i).getEndDepth());
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(escelRepeatLineList.get(i).getBuryType());
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(escelRepeatLineList.get(i).getSurveyCompany());
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(escelRepeatLineList.get(i).getSurveyDate());
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(escelRepeatLineList.get(i).getBelong_to());
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(escelRepeatLineList.get(i).getRemark());
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(escelRepeatLineList.get(i).getAddress());
        }
        //系统已存在的管线数据
        SXSSFSheet sheet7 = workbook.createSheet("系统已存在的管线数据");
        List<ExcelLinePo> dataBaseRepeatLineList = findDataBaseRepeatLine(lineList);
        Row headerRow7 = sheet7.createRow(0);
        getLineHeader(headerRow7);
        for (int i = 0; i < dataBaseRepeatLineList.size(); i++) {
            Row row = sheet7.createRow(i + 1);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(dataBaseRepeatLineList.get(i).getStartCode());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(dataBaseRepeatLineList.get(i).getEndCode());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(dataBaseRepeatLineList.get(i).getMaterial());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(dataBaseRepeatLineList.get(i).getCaliber());
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(dataBaseRepeatLineList.get(i).getStartDepth());
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(dataBaseRepeatLineList.get(i).getEndDepth());
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(dataBaseRepeatLineList.get(i).getBuryType());
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(dataBaseRepeatLineList.get(i).getSurveyCompany());
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(dataBaseRepeatLineList.get(i).getSurveyDate());
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(dataBaseRepeatLineList.get(i).getBelong_to());
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(dataBaseRepeatLineList.get(i).getRemark());
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(dataBaseRepeatLineList.get(i).getAddress());
        }
        //管线管径为空的数据
        SXSSFSheet sheet8 = workbook.createSheet("管线管径为空");
        List<ExcelLinePo> caliberIsNullList = findLineCaliberIsNull(lineList);
        Row headerRow8 = sheet8.createRow(0);
        getLineHeader(headerRow8);
        for (int i = 0; i < caliberIsNullList.size(); i++) {
            Row row = sheet8.createRow(i + 1);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(caliberIsNullList.get(i).getStartCode());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(caliberIsNullList.get(i).getEndCode());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(caliberIsNullList.get(i).getMaterial());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(caliberIsNullList.get(i).getCaliber());
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(caliberIsNullList.get(i).getStartDepth());
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(caliberIsNullList.get(i).getEndDepth());
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(caliberIsNullList.get(i).getBuryType());
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(caliberIsNullList.get(i).getSurveyCompany());
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(caliberIsNullList.get(i).getSurveyDate());
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(caliberIsNullList.get(i).getBelong_to());
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(caliberIsNullList.get(i).getRemark());
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(caliberIsNullList.get(i).getAddress());
        }
        //管线材质为空的数据
        SXSSFSheet sheet9 = workbook.createSheet("管线材质为空");
        List<ExcelLinePo> materialIsNullList = findLineMaterialIsNull(lineList);
        Row headerRow9 = sheet9.createRow(0);
        getLineHeader(headerRow9);
        for (int i = 0; i < materialIsNullList.size(); i++) {
            Row row = sheet9.createRow(i + 1);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(materialIsNullList.get(i).getStartCode());
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(materialIsNullList.get(i).getEndCode());
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(materialIsNullList.get(i).getMaterial());
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(materialIsNullList.get(i).getCaliber());
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(materialIsNullList.get(i).getStartDepth());
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(materialIsNullList.get(i).getEndDepth());
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(materialIsNullList.get(i).getBuryType());
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(materialIsNullList.get(i).getSurveyCompany());
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(materialIsNullList.get(i).getSurveyDate());
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(materialIsNullList.get(i).getBelong_to());
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(materialIsNullList.get(i).getRemark());
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(materialIsNullList.get(i).getAddress());
        }
        String result;
        try {
            String filePath = pathConfig.getDownloadPath() + File.separator + "管网错误数据分析报告.xlsx";
            File localFile = new File(filePath);
            if (!localFile.exists()) {
                localFile.createNewFile();
            }
            FileOutputStream bos;
            bos = new FileOutputStream(new File(filePath));
            workbook.write(bos);
            workbook.close();
            result = JavaFileToFormUpload.send(pathConfig.getUploadFileUrl(), filePath);
            Logger.debug("管网错误数据分析报告:"+result);
        } catch (IOException e) {
            throw new BizException("IO异常");
        }
        return result;
    }


    /**
     * 将管点数据封装到List中
     *
     * @param file
     * @return
     * @throws BizException
     */
    public List<ExcelPointPo> analysisExcelPoint(MultipartFile file) throws BizException {
        InputStream inputStream;
        Workbook workbook;
        List list = Lists.newArrayList();
        try {
            inputStream = file.getInputStream();
            workbook = WorkbookFactory.create(inputStream);
            //读取点坐标
            for (int numSheet = 0; numSheet < 1; numSheet++) {
                Sheet sheet = workbook.getSheetAt(numSheet);
                //遍历所有行
                for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    ExcelPointPo po = new ExcelPointPo();
                    Row row = sheet.getRow(rowNum);
                    Cell cell0 = row.getCell(0);//管点编码的单元格
                    po.setPointCode(verifyCell(cell0));
                    if(po.getPointCode()==null){
                        continue;
                    }
                    Cell cell1 = row.getCell(1);//管点x坐标的单元格
                    po.setPointX(verifyCell(cell1));
                    Cell cell2 = row.getCell(2);//管点y坐标的单元格
                    po.setPointY(verifyCell(cell2));
                    Cell cell3 = row.getCell(3);//管点地面高程
                    po.setGroundHeight(verifyCell(cell3));
                    Cell cell4 = row.getCell(4);//管点埋深
                    po.setDepth(verifyCell(cell4));
                    Cell cell5 = row.getCell(5);//管点材质
                    po.setMaterial(verifyCell(cell5));
                    Cell cell6 = row.getCell(6);//管点名称
                    po.setName(verifyCell(cell6));
                    Cell cell7 = row.getCell(7);//管点道路名称
                    po.setAddress(verifyCell(cell7));
                    Cell cell8 = row.getCell(8);//管点规格
                    po.setSpec(verifyCell(cell8));
                    Cell cell9 = row.getCell(9);//管点勘测单位
                    po.setSurveyCompany(verifyCell(cell9));
                    Cell cell10 = row.getCell(10);//管点勘测日期
                    po.setSurveyDate(vertifyDateCell(cell10));
                    Cell cell11 = row.getCell(11);//管点权属单位
                    po.setBelongTo(verifyCell(cell11));
                    Cell cell12 = row.getCell(12);//管点备注
                    po.setRemark(verifyCell(cell12));
                    list.add(po);
                }
            }
            return list;
        } catch (Exception e) {
            throw new BizException(e);
        }

    }

    /**
     * 将管线数据封装到List中，
     *
     * @param file
     * @return
     * @throws BizException
     */
    public List<ExcelLinePo> analysisExcelLine(MultipartFile file) throws BizException {
        InputStream inputStream;
        Workbook workbook;
        List list = Lists.newArrayList();
        try {
            inputStream = file.getInputStream();
            workbook = WorkbookFactory.create(inputStream);
            int sheetNums = workbook.getNumberOfSheets();
            //读取线sheet
            Sheet sheet = workbook.getSheetAt(1);
            //遍历所有行
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                ExcelLinePo po = new ExcelLinePo();
                Row row = sheet.getRow(rowNum);
                Cell cell0 = row.getCell(0);//管线起点编码
                po.setStartCode(verifyCell(cell0));
                Cell cell1 = row.getCell(1);//管线终点编码
                po.setEndCode(verifyCell(cell1));
                Cell cell2 = row.getCell(2);//管线材质
                po.setMaterial(verifyCell(cell2));
                Cell cell3 = row.getCell(3);//管径
                po.setCaliber(verifyCell(cell3));
                Cell cell4 = row.getCell(4);//管线起点埋深
                po.setStartDepth(verifyCell(cell4));
                Cell cell5 = row.getCell(5);//管线终点埋深
                po.setEndDepth(verifyCell(cell5));
                Cell cell6 = row.getCell(6);//埋设类型
                po.setBuryType(verifyCell(cell6));
                Cell cell7 = row.getCell(7);//勘测单位
                po.setSurveyCompany(verifyCell(cell7));
                Cell cell8 = row.getCell(8);//勘测日期
                po.setSurveyDate(vertifyDateCell(cell8));
                Cell cell9 = row.getCell(9);//权属单位
                po.setBelong_to(verifyCell(cell9));
                Cell cell10 = row.getCell(10);//备注
                po.setRemark(verifyCell(cell10));
                Cell cell11 = row.getCell(11);//道路名称
                po.setAddress(verifyCell(cell11));
                list.add(po);
            }
            return list;
        } catch (Exception e) {
            throw new BizException(e);
        }

    }


    /**
     * 获得excel数据编码重复的数据
     */
    public List findExcelRepeatCode(List<ExcelPointPo> list) throws BizException {
        Map<String,List<ExcelPointPo>> map = list.stream().collect(Collectors.groupingBy(ExcelPointPo::getPointCode));
        List<ExcelPointPo> rLists = Lists.newArrayList();
        for(Map.Entry<String, List<ExcelPointPo>> o : map.entrySet()) {
            if (o.getValue().size() > 1) {
                rLists.addAll(o.getValue());
            }
        }
        return rLists;
    }


    /**
     * 获得x坐标为空的管点编码
     */
    public List findXIsNullCode(List<ExcelPointPo> list) throws BizException {
        List codeList = new ArrayList();
        for (ExcelPointPo po : list) {
            String X = po.getPointX();
            if (X == null) {
                codeList.add(po);
            }
        }
        return codeList;
    }

    /**
     * 获得y坐标为空的管点编码
     */
    public List findYIsNullCode(List<ExcelPointPo> list) throws BizException {
        List codeList = new ArrayList();
        for (ExcelPointPo po : list) {
            String Y = po.getPointY();
            if (Y == null) {
                codeList.add(po);
            }
        }
        return codeList;
    }

    /**
     * 获得管点编码为空的数据 返回excel所在的行
     */
    public List<Integer> findCodeIsNullCode(List<ExcelPointPo> list) throws BizException {
        List codeList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPointCode() == null) {
                codeList.add(i + 2);
            }
        }
        return codeList;
    }

    /**
     * 管点编码在数据库中存在的数据
     */
    public List findDataBaseRepeatCode(List<ExcelPointPo> list) throws BizException {
        List<String> excelCodeList = new ArrayList();
        List<String> dataBaseCodeList = selfExamination.findCodes();
        List listRepeat = new ArrayList();
        for (ExcelPointPo po : list) {
            if (dataBaseCodeList.contains(po.getPointCode())) {
                listRepeat.add(po);
            }
        }
        return listRepeat;
    }

    /**
     * 获得起点编码为空的数据  返回所在的excel所在的行数
     */
    public List findStartCodeIsNull(List<ExcelLinePo> list) throws BizException {
        List startCodeList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStartCode() == null) {
                startCodeList.add(i + 2);
            }
        }
        return startCodeList;
    }

    /**
     * 获得终点编码为空的数据    返回所在的excel所在的行数
     */
    public List findEndCodeIsNull(List<ExcelLinePo> list) throws BizException {
        List endCodeList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStartCode() == null) {
                endCodeList.add(i + 2);
            }
        }
        return endCodeList;
    }

    /**
     * 获得管径为空的数据   返回所在的excel行数
     */
    public List findCalierIsNull(List<ExcelLinePo> list) throws BizException {
        List calierList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStartCode() == null) {
                calierList.add(i + 2);
            }
        }
        return calierList;
    }

    /**
     * 获得起点编码在数据库中和excel中不存在的起点编码
     */
    public List findNoExistStartCodes(List<ExcelLinePo> lineList, List<ExcelPointPo> pointList) throws BizException {
        List<String> excelCodeList = new ArrayList();
        List listRepeatStartCode = new ArrayList();
        List<String> dataBaseCodeList = selfExamination.findCodes();
        //取得excel中所有的管点code
        for (ExcelPointPo po : pointList) {
            if (po.getPointCode() != null) {
                excelCodeList.add(po.getPointCode());
            }
        }
        //将excel中的code和数据库中的code放在一个list中
        dataBaseCodeList.addAll(excelCodeList);
        for (ExcelLinePo startCode : lineList) {
            if (!dataBaseCodeList.contains(startCode.getStartCode())) {
                listRepeatStartCode.add(startCode);
            }
        }
        listRepeatStartCode.removeAll(Collections.singletonList(null));
        return listRepeatStartCode;
    }

    /**
     * 获得终点编码在数据库中和excel中不存在的起点编码
     */
    public List findNoExistEndCodes(List<ExcelLinePo> lineList, List<ExcelPointPo> pointList) throws BizException {
        List<String> excelCodeList = new ArrayList();
        List listRepeatEndCode = new ArrayList();
        List<String> dataBaseCodeList = selfExamination.findCodes();
        //取得excel中所有的管点code
        for (ExcelPointPo po : pointList) {
            if (po.getPointCode() != null) {
                excelCodeList.add(po.getPointCode());
            }
        }
        //将excel中的code和数据库中的code放在一个list中
        dataBaseCodeList.addAll(excelCodeList);
        for (ExcelLinePo endCode : lineList) {
            if (!dataBaseCodeList.contains(endCode.getEndCode())) {
                listRepeatEndCode.add(endCode);
            }
        }
        listRepeatEndCode.removeAll(Collections.singletonList(null));
        return listRepeatEndCode;
    }

    /**
     * 将excel中的管线的数据 起点编码和终点编码重复的数据找出来
     */
    public List findExcelRepeatLine(List<ExcelLinePo> list ) throws BizException {
        //找出管线中起点编码和终点编码为空的数据防止报错。
           Iterator<ExcelLinePo> it = list.iterator();
           while(it.hasNext()){
               ExcelLinePo po = it.next();
               if(po.getStartCode()==null||po.getEndCode()==null){
                   it.remove();
               }
           }
        Map<String,Map<String, List<ExcelLinePo>>> map = list.stream().collect(Collectors.groupingBy(ExcelLinePo::getStartCode, Collectors.groupingBy(ExcelLinePo::getEndCode)));
        List<ExcelLinePo> rLists = Lists.newArrayList();
        Set<ExcelLinePo>  set = new HashSet<>();
        for(Map.Entry<String,Map<String, List<ExcelLinePo>>> o : map.entrySet()) {
            for (Map.Entry<String, List<ExcelLinePo>> a : o.getValue().entrySet()) {
                if (a.getValue().size() > 1) {
                    rLists.addAll(a.getValue());
                }
            }
        }
            for(ExcelLinePo b:list){
                for(ExcelLinePo c:list){
                    if(b.getStartCode()!=null&&b.getEndCode()!=null&&c.getStartCode()!=null&&c.getEndCode()!=null){
                        if(b.getStartCode().equals(c.getEndCode())&&b.getEndCode().equals(c.getStartCode())){
                            set.add(b);
                            set.add(c);
                        }
                    }
                }
            }
            rLists.addAll(set);
            return rLists;
        }

    /**
     * 将excel在系统中已经存在的管线数据找出来
     */
    public List findDataBaseRepeatLine(List<ExcelLinePo> list ) throws BizException {
        List<String> dataBaseCodeList = selfExamination.findCodes();
        List listRepeat = new ArrayList();
        for(ExcelLinePo s:list){
            String code = s.getStartCode()+"-"+s.getEndCode();
            String code1 = s.getEndCode()+"-"+s.getStartCode();
            if(dataBaseCodeList.contains(code)||dataBaseCodeList.contains(code1)){
                listRepeat.add(s);
            }
        }
        return listRepeat;
    }

    /**
     * excel管线管径数据为空的数据
     */
    public List findLineCaliberIsNull(List<ExcelLinePo> list ) throws BizException {
        List list1 = new ArrayList();
        for(ExcelLinePo s:list){
            if(s.getCaliber()==null){
               list1.add(s);
            }
        }
        return list1;
    }

    /**
     * excel管线材质为空的数据
     */
    public List findLineMaterialIsNull(List<ExcelLinePo> list ) throws BizException {
        List list1 = new ArrayList();
        for(ExcelLinePo s:list){
            if(s.getMaterial()==null){
                list1.add(s);
            }
        }
        return list1;
    }

    /**
     * 读取单元格内容判断是否为空  空或者空字符串返回null
     */
    public static String verifyCell(Cell cell) {
        if (cell != null) {
            cell.setCellType(CellType.STRING);
            if (cell.getStringCellValue() != null && !"".equals(cell.getStringCellValue())) {
                return cell.getStringCellValue();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    public static String vertifyDateCell(Cell cell) throws BizException{
        if (cell==null){
            return null;
        }
        String addr = "第" + (cell.getAddress().getRow() + 1) + "行" +  (cell.getAddress().getColumn() + 1) + "列";
        try {
            boolean isDateCell = false;
            try {
                isDateCell = DateUtil.isCellDateFormatted(cell);
            } catch (Exception e) {
                throw new BizException(addr + "不是日期格式，请更改单元格格式！");
            }
            if (!isDateCell) {
                throw new BizException(addr + "不是日期格式，请更改单元格格式！");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date cellDate = cell.getDateCellValue();
            if(cellDate==null){
                return null;
            }
            return sdf.format(cellDate);
        } catch (Exception e) {
            Logger.error(addr + "单元格的日期格式不正确！");
            throw new BizException(e.getMessage());
        }
    }

    /**
     * 设置excel管点数据的表头
     */
    public void getPointHeader(Row row){
        int i = 0;
        String [] str = {"管点编码","X坐标","Y坐标","地面高程(m)","埋深(m)","材质","名称","道路名称","规格","勘测单位","勘测日期","权属单位","备注"};
        for(String s:str){
            Cell cell =row.createCell(i++);
            cell.setCellValue(s);
        }
    }

    /**
     * 设置excel管线数据的表头
     */
    public void getLineHeader(Row row){
        int i = 0;
        String [] str = {"起点编码","终点编码","材质","管径(mm)","起点埋深(m)","终点埋深(m)","埋设类型","勘测单位","勘测日期","权属单位","备注","道路名称"};
        for(String s:str){
            Cell cell =row.createCell(i++);
            cell.setCellValue(s);
        }
    }
}
