package org.microboot.web.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author 胡鹏
 */
public class FileTransferUtils {

    /**
     * 下载（传统方式）
     * 如果希望增加效率，可以考虑使用MappedByteBuffer，但是有以下几个问题：
     * 1、MappedByteBuffer会占用文件，直到垃圾回收后才会释放，在这之前，文件无法删除
     * 2、FileChannelImpl虽然实现了unmap，但却是私有的，所以需要通过反射实现（网上有方法）
     * 3、MappedByteBuffer使用的是DirectBuffer，而申请和释放DirectBuffer的开销比较大，所以小文件不适合
     * 4、MappedByteBuffer一次只能读Integer.MAX_VALUE（2G）的文件，超过2G会报错，此时需要对大文件进行分块读取处理（断点续传）
     * 总结，如果有大文件下载的需求，可以考虑新增一个基于MappedByteBuffer技术，专门针对大文件的download方法
     *
     * @param fileName
     * @param filePath
     * @param response
     * @throws Exception
     */
    public static void download(String fileName, String filePath, HttpServletResponse response) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件下载失败，文件路径：" + filePath + " 不存在");
        }
        try (
                FileInputStream in = new FileInputStream(file);
                ServletOutputStream out = response.getOutputStream()
        ) {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" +
                    URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            response.setStatus(HttpStatus.OK.value());
            IOUtils.copy(in, out);
        } catch (Throwable e) {
            throw new IOException("文件下载失败：" + e.getMessage());
        }
    }

    public static void upload(String directory, MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String filePath = directory + fileName;
        File file = new File(filePath);
        FileUtils.forceMkdirParent(file);
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
        file.createNewFile();
        try (
                InputStream in = multipartFile.getInputStream();
                FileOutputStream out = new FileOutputStream(file)
        ) {
            IOUtils.copy(in, out);
        } catch (Throwable e) {
            throw new IOException("文件上传失败：" + e.getMessage());
        }
    }

    public static void upload(String directory, MultipartFile multipartFile, String newFileName) throws IOException {
        String fileName = StringUtils.isBlank(newFileName) ? multipartFile.getOriginalFilename() : newFileName;
        String filePath = directory + fileName;
        File file = new File(filePath);
        FileUtils.forceMkdirParent(file);
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
        file.createNewFile();
        try (
                InputStream in = multipartFile.getInputStream();
                FileOutputStream out = new FileOutputStream(file)
        ) {
            IOUtils.copy(in, out);
        } catch (Throwable e) {
            throw new IOException("文件上传失败：" + e.getMessage());
        }
    }
}