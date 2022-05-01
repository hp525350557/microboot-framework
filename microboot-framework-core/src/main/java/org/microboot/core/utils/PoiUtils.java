package org.microboot.core.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 胡鹏
 */
public class PoiUtils {

    private static final Logger logger = LogManager.getLogger(PoiUtils.class);

    /**
     * 判断是否office文件
     *
     * @param inputStream
     * @return
     */
    public static Boolean isOfficeFile(InputStream inputStream) {
        if (inputStream == null) {
            return false;
        }
        boolean result = false;
        try {
            FileMagic fileMagic = FileMagic.valueOf(inputStream);
            if (Objects.equals(fileMagic, FileMagic.OLE2) || Objects.equals(fileMagic, FileMagic.OOXML)) {
                result = true;
            }
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        }
        return result;
    }

    /**
     * 判断是否office文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Boolean isOfficeFile(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }
        boolean result = false;
        File file = new File(filePath);
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file))) {
            result = isOfficeFile(bufferedInputStream);
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        }
        return result;
    }

    /**
     * 判断扩展名是否是excel扩展名
     *
     * @param filePath
     * @return
     */
    public static Boolean checkExtension(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
        return Lists.newArrayList("xls", "xlsx", "XLS", "XLSX").contains(extension);
    }

    /**
     * 根据文件类型创建对应的Workbook
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Workbook getWorkbook(String filePath) {
        /** 判断文件的类型，是2003还是2007 */
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        boolean isExcel2003 = isExcel2003(filePath);
        boolean isExcel2007 = isExcel2007(filePath);
        Workbook wb = null;
        File file = new File(filePath);
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file))) {
            if (isExcel2003) {
                wb = new HSSFWorkbook(is);
            } else if (isExcel2007) {
                wb = new XSSFWorkbook(is);
            }
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        }
        return wb;
    }

    /**
     * 判断文件类型是否是xls
     *
     * @param filePath
     * @return
     */
    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * 判断文件类型是否是xlsx
     *
     * @param filePath
     * @return
     */
    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * 读excel（标准行列个格式）
     *
     * @param filePath
     * @param isKeepNull
     * @return
     */
    public static Map<String, List<Map<String, Object>>> readSimpleExcel(String filePath, boolean isKeepNull) {
        return readSimpleExcel(filePath, isKeepNull, 1);
    }

    /**
     * 读excel（标准行列个格式）
     *
     * @param filePath
     * @param isKeepNull
     * @param sheetNum
     * @return
     */
    public static Map<String, List<Map<String, Object>>> readSimpleExcel(String filePath, boolean isKeepNull, int sheetNum) {
        return readSimpleExcel(filePath, isKeepNull, 0, 0, sheetNum);
    }

    /**
     * 读excel（标准行列个格式）
     *
     * @param filePath
     * @param isKeepNull
     * @param ignoreBefore
     * @param ignoreAfter
     * @return
     */
    public static Map<String, List<Map<String, Object>>> readSimpleExcel(String filePath, boolean isKeepNull, int ignoreBefore, int ignoreAfter) {
        return readSimpleExcel(filePath, isKeepNull, ignoreBefore, ignoreAfter, 1);
    }

    /**
     * 读excel（标准行列个格式）
     *
     * @param filePath
     * @param isKeepNull
     * @param ignoreBefore
     * @param ignoreAfter
     * @param sheetNum
     * @return
     */
    public static Map<String, List<Map<String, Object>>> readSimpleExcel(String filePath, boolean isKeepNull, int ignoreBefore, int ignoreAfter, int sheetNum) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        if (!checkExtension(filePath)) {
            throw new IllegalArgumentException("Excel读取失败：文件后缀名不正确");
        }
        if (!isOfficeFile(filePath)) {
            throw new IllegalArgumentException("Excel读取失败：文件类型不正确");
        }
        //正确的文件类型 自动判断2003或者2007
        try (Workbook workbook = getWorkbook(filePath)) {
            if (workbook == null) {
                return null;
            }
            Map<String, List<Map<String, Object>>> resultMap = Maps.newHashMap();
            for (int i = 0; i < sheetNum; i++) {
                List<Map<String, Object>> rowList = analysis(workbook, isKeepNull, i, ignoreBefore, ignoreAfter);
                resultMap.put("sheet" + i, rowList);
            }
            return resultMap;
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        }
        return null;
    }

    /**
     * 写excel（标准行列个格式）
     *
     * @param filePath
     * @param rowList
     * @return
     */
    public static boolean writeSimpleExcel(String filePath, List<Map<String, Object>> rowList) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }
        if (!checkExtension(filePath)) {
            throw new IllegalArgumentException("Excel写入失败：文件后缀名不正确");
        }
        if (CollectionUtils.isEmpty(rowList)) {
            return false;
        }
        try {
            File file = new File(filePath);
            //创建文件excel
            FileUtils.forceMkdirParent(file);
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }
            file.createNewFile();
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        }
        try (
                Workbook workbook = new XSSFWorkbook();
                FileOutputStream fos = new FileOutputStream(filePath)
        ) {
            //获取excel头
            List<String> fieldList = Lists.newArrayList();
            fieldList.addAll(rowList.get(0).keySet());
            //创建sheet页
            Sheet sheet = workbook.createSheet();
            //创建第1行 --> excel头
            Row firstRow = sheet.createRow(0);
            for (int i = 0; i < fieldList.size(); i++) {
                Cell cell = firstRow.createCell(i);
                cell.setCellValue(fieldList.get(i));
            }
            //写入数据，从第2行开始，因为第1行是头
            for (int i = 0; i < rowList.size(); i++) {
                Map<String, Object> rowMap = rowList.get(i);
                Row row = sheet.createRow(i + 1);
                int j = 0;
                for (String key : rowMap.keySet()) {
                    String value = MapUtils.getString(rowMap, key, "");
                    Cell cell = row.createCell(j);
                    cell.setCellValue(value);
                    j++;
                }
            }
            workbook.write(fos);
            return true;
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        }
        return false;
    }

    private static List<Map<String, Object>> analysis(Workbook workbook, boolean isKeepNull, int index, int ignoreBefore, int ignoreAfter) {
        //读取指定下标的sheet页
        Sheet sheet = workbook.getSheetAt(index);
        //默认第一行是字段名
        Row firstRow = sheet.getRow(sheet.getFirstRowNum() + ignoreBefore);
        //获取sheet页有多少行
        int rows = sheet.getPhysicalNumberOfRows();
        //获取sheet页有多少列
        int fields = firstRow.getLastCellNum();
        List<String> fieldList = Lists.newArrayList();
        for (int i = 0; i < fields; i++) {
            Cell cell = firstRow.getCell(i);
            fieldList.add(cell.toString());
        }
        //默认数据从第二行开始
        List<Map<String, Object>> rowList = Lists.newArrayList();
        for (int i = sheet.getFirstRowNum() + 1 + ignoreBefore; i < rows - ignoreAfter; i++) {
            //获取每行row
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Map<String, Object> rowMap = Maps.newHashMap();
            for (int j = 0; j < fields; j++) {
                //获取每单元格cell
                Cell cell = row.getCell(j);
                if (cell != null) {
                    rowMap.put(fieldList.get(j), cell.toString());
                } else {
                    //是否保留空值
                    if (isKeepNull) {
                        rowMap.put(fieldList.get(j), "");
                    }
                }
            }
            rowList.add(rowMap);
        }
        return rowList;
    }
}