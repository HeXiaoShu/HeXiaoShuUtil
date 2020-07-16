package com.util;

import com.common.Result;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author hexiaoshu
 * @Description: 文件处理工具类
 * @date 2019/7/411:07
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 项目jar 部署时，获取jar同级目录,用于图片存储等
     * @param subdirectory 创建目录
     * @return 路径
     */
    public static String  getJarPath(String subdirectory){
        File upload;
        try {
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            if(!path.exists()) {
                path = new File("");
            }
            upload = new File(path.getAbsolutePath(),subdirectory);
            if(!upload.exists()) {
                upload.mkdirs();//如果不存在则创建目录
            }
            return upload + File.separator;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("获取服务器路径发生错误！");
        }
    }

    /**
     * 文件上传
     * @param file      file
     * @param staticUrl  当前指定静态资源路径
     * @param fileUrl   保存分类路径
     * @return          数据存储路径
     */
    public static String uploadFile(MultipartFile file, String staticUrl, String fileUrl){
        //图片后缀
        String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        //图片全称
        String fileName= DateUtil.NowDateYmd()+System.currentTimeMillis()+fileSuffix;
        //存储路径
        String path="";
        //数据库路径
        String dataBaseUrl="";
        if (StringUtil.isEmpty(fileUrl)) {
            path = staticUrl;
            File file1 = new File(staticUrl+File.separator+fileName);
            if (!file1.exists()){
                file1.mkdirs();
            }
            //数据库存储路径
            dataBaseUrl=fileName;
        }else {
            path = staticUrl+File.separator+fileUrl;
            File file1 = new File(path);
            if (!file1.exists()){
                file1.mkdirs();
            }
            //数据库存储路径
            dataBaseUrl="/"+fileUrl+"/"+fileName;
        }
        File newFile = new File(path+File.separator+fileName);
        logger.info("数据库存储路径:"+dataBaseUrl);
        logger.info("上传路径:"+newFile);
        try {
            file.transferTo(newFile);
            logger.info("上传成功");
        }catch (IOException e){
            e.printStackTrace();
            logger.info("上传失败");
            return null;
        }
        return dataBaseUrl;
    }

    /**
     * 多文件上传
     * @param files   文件集
     * @param staticUrl  当前指定静态资源路径
     * @param fileUrl 分类存储路径
     * @return  数据存储路径，逗号拼接字符串
     */
    public static String uploadFiles(MultipartFile files[], String staticUrl, String fileUrl){
        //存储路径
        String path="";
        String dataBaseUrl="";
        for (int i=0;i<files.length;i++){
            //图片后缀
            String fileSuffix = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf("."));
            //图片全称
            String fileName= DateUtil.NowDateYmd()+System.currentTimeMillis()+ StringUtil.getuuid()+fileSuffix;
            if (StringUtil.isEmpty(fileUrl)) {
                path = staticUrl;
                File file1 = new File(path);
                if (!file1.exists()){
                    file1.mkdirs();
                }
                //数据库存储路径
                dataBaseUrl=fileName+","+dataBaseUrl;
            }else {
                path = staticUrl+File.separator+fileUrl;
                File file1 = new File(path);
                if (!file1.exists()){
                    file1.mkdirs();
                }
                //数据库存储路径
                dataBaseUrl="/"+fileUrl+"/"+fileName+","+dataBaseUrl;
            }
            File newFile = new File(path+File.separator+fileName);
            logger.info("数据库存储路径:"+dataBaseUrl);
            logger.info("上传路径:"+newFile);
            try {
                files[i].transferTo(newFile);
                logger.info("上传成功");
            }catch (IOException e){
                e.printStackTrace();
                logger.info("上传失败");
                return null;
            }
        }
        logger.info("数据库存储路径:"+dataBaseUrl);
        return dataBaseUrl;
    }

    /**
     * 删除图片
     * @param sPath 数据库存储路径
     * @return boolean
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file;
        try {
            logger.info("删除路径: "+sPath);
            file = new File(sPath);
            if (file.isFile() && file.exists()) {
                file.delete();
                flag = true;
                logger.info("图片删除成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 图片base64字符，转图片，并保存
     * @param base64 img
     * @param path   存储路径
     * @return boolean
     */
    public static boolean base64ToFile(String base64, String path) {
        try {
            byte[] buffer = Base64.getDecoder().decode(base64);
            FileOutputStream out = new FileOutputStream(path);
            out.write(buffer);
            out.close();
            out.flush();
            return true;
        } catch (Exception var4) {
            throw new RuntimeException("base64字符串异常或地址异常\n" + var4.getMessage());
        }
    }

    /**
     * 文件下载 , 浏览器弹出下载窗口，类型
     * @param response      response
     * @param AbsolutePath  绝对路径，全路径 eg:E://a.text ;
     * @param FileName      文件名称         eg: a.text;
     */
    public static void downLoadFile(HttpServletResponse response, HttpServletRequest request, String AbsolutePath, String FileName) throws Exception{
        //通过文件名称获取MIME类型
        String contentType = request.getServletContext().getMimeType(FileName);
        String contentDisposition = "attachment;filename=" + filenameEncoding(FileName, request);
        // 文件转流
        FileInputStream input = new FileInputStream(AbsolutePath);
        //设置头
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Disposition", contentDisposition);
        // 获取绑定了响应端的流
        ServletOutputStream output = response.getOutputStream();
        //把输入流中的数据写入到输出流中。---输出流的节点就是客户端
        IOUtils.copy(input, output);
        input.close();
    }
    /**
     * 下载文件编码
     * @param filename  文件名称
     * @param request   request
     * @throws IOException
     */
    public static String filenameEncoding(String filename, HttpServletRequest request) throws IOException {
        String agent = request.getHeader("User-Agent"); //获取浏览器
        if (agent.contains("Firefox")) {
            BASE64Encoder base64Encoder = new BASE64Encoder();
            filename = "=?utf-8?B?"+ base64Encoder.encode(filename.getBytes("utf-8"))+ "?=";
        } else if(agent.contains("MSIE")) {
            filename = URLEncoder.encode(filename, "utf-8");
        } else {
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 文件压缩. 默认压缩到同级目录 (不支持加密)
     * @param sourceFileName 源文件，eg：E:/text.txt
     * @param format         压缩格式 rar zip
     * @return result
     * @throws Exception
     */
    public static Result compressFile(String sourceFileName, String format) throws Exception {
        String fileSuffix;////文件后缀 eg: .txt
        String zipName;//压缩文件名
        File file = new File(sourceFileName);
        if (sourceFileName.lastIndexOf(".") != -1){//是否是目录
             fileSuffix = sourceFileName.substring(sourceFileName.lastIndexOf("."));//后缀
             zipName=sourceFileName.replaceAll(fileSuffix,"")+"."+format;
        }else {
             zipName=file.getParent()+File.separator+sourceFileName.substring(sourceFileName.lastIndexOf("/")).replaceAll("/","")+"."+format;
        }
        long startTime=System.currentTimeMillis();
        //创建zip输出流
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipName));
        File sourceFile = new File(sourceFileName);
        //调用函数
        compress(out, sourceFile, sourceFile.getName());// sourceFile.getName() E:/aa/bb  name为 aa.
        out.close();
        long endTime=System.currentTimeMillis();
        logger.info("压缩完成！"+",耗时:"+(endTime-startTime)+"毫秒");
        logger.info("输出路径:"+zipName);
        return Result.ok("压缩完成"+",耗时:"+(endTime-startTime)+"毫秒");
    }

    public static void compress(ZipOutputStream out, File sourceFile, String base) throws Exception {
        //如果路径为目录（文件夹）
        if(sourceFile.isDirectory()) {
            //取出文件夹中的文件（或子文件夹）
            File[] flist = sourceFile.listFiles();
            if(flist.length==0) {//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
                System.out.println(base + File.separator);
                out.putNextEntry(new ZipEntry(base + File.separator));
            } else {//如果文件夹不为空，则递归调用compress,文件夹中的每一个文件（或文件夹）进行压缩
                for(int i=0; i<flist.length; i++) {
                    compress(out, flist[i], base+File.separator+flist[i].getName());
                }
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            FileInputStream fos = new FileInputStream(sourceFile);
            BufferedInputStream bis = new BufferedInputStream(fos);
            int len;
            byte[] buf = new byte[1024];
            System.out.println(base);
            while((len=bis.read(buf, 0, 1024)) != -1) {
                out.write(buf, 0, len);
            }
            bis.close();
            fos.close();
        }
    }

    /**
     * 豪哥版
     * 压缩包解压,不支持解密 ,支持rar，zip 格式
     * @param srcFile      源文件
     * @param destDirPath  输出目录
     */
    public static Result uncompressFile(File srcFile, String destDirPath){
        long start = System.currentTimeMillis();
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            return Result.error(srcFile.getPath() + "源文件不存在");
        }
        // 开始解压
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                System.out.println("解压" + entry.getName());
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if(!targetFile.getParentFile().exists()){
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();
                }
            }
            long end = System.currentTimeMillis();
            logger.info("解压完成，耗时：" + (end - start) +"毫秒");
            return Result.ok("解压完成，耗时：" + (end - start) +"毫秒");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(zipFile != null){
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("解压失败");
    }

    /**
     *  解压 .gz格式，压缩文件
     * @param inFileName eg: E:\\2019072308.gz
     */
    public static void uncompressGz(String inFileName) {
        try {
            if (!getExtension(inFileName).equalsIgnoreCase("gz")) {
                System.err.println(" 文件不是gz类型压缩包 ");
                System.exit(1);
            }
            System.out.println("开始解压文件....");
            Long startTime=System.currentTimeMillis();
            GZIPInputStream in = null;
            try {
                in = new GZIPInputStream(new FileInputStream(inFileName));
            } catch(FileNotFoundException e) {
                System.err.println("File not found. " + inFileName);
                System.exit(1);
            }
            System.out.println("Open the output file.");
            String outFileName = getFileName(inFileName);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(outFileName);
            } catch (FileNotFoundException e) {
                System.err.println("Could not write to file. " + outFileName);
                System.exit(1);
            }
            System.out.println("Transfering bytes from compressed file to the output file.");
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            Long endTime=System.currentTimeMillis();
            System.out.println("解压完成,耗时:"+(startTime-endTime)+"毫秒");
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    /**
     * 获取文件扩展名
     * @param f E:\\2019072308.gz
     * @return <code>String</code> representing the extension of the incoming
     *         file.
     */
    public static String getExtension(String f) {
        String ext = "";
        int i = f.lastIndexOf('.');
        if (i > 0 &&  i < f.length() - 1) {
            ext = f.substring(i+1);
        }
        return ext;
    }
    /**
     * 用于提取没有扩展名的文件名
     * @param f Incoming file to get the filename
     * @return <code>String</code> representing the filename without its
     *         extension.
     */
    public static String getFileName(String f) {
        String fname = "";
        int i = f.lastIndexOf('.');
        if (i > 0 &&  i < f.length() - 1) {
            fname = f.substring(0,i);
        }
        return fname;
    }

    /**
     * 以行为单位读取文件文件数据。
     * 使用BufferedReader以行为单位读取文件，读取到最后一行.
     * @param is 文件IO流
     * @param c 缓存 使用BufferedReader,默认每次读入（5 * 1024 * 1024）5M数据.减少IO
     * @return  List<String>
     */
    public static List<String> readFileContent(InputStream is, int c) {
        BufferedReader reader;
        List<String> listContent = new ArrayList<>();
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            reader = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8), c==0?5 * 1024 * 1024:c);
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                listContent.add(tempString);
            }
            is.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listContent;
    }

    /**
     * 向文件写入内容
     * @param contest 内容集合
     * @param os      new FileOutputStream(filePath)
     * @param c        c 缓存 使用BufferedReader,默认每次读入（5 * 1024 * 1024）5M数据.减少IO
     */
    public static void writeFileContent(List<String> contest,OutputStream os,int c){
        BufferedWriter bw;
        try {
            BufferedOutputStream bos = new BufferedOutputStream(os);
            bw = new BufferedWriter(new OutputStreamWriter(bos, StandardCharsets.UTF_8), c==0?5 * 1024 * 1024:c);
            for(String str:contest){
                bw.write(str);
                bw.newLine();
                bw.flush();
            }
            bw.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * 从网络Url中下载文件
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void  downLoadFromUrl(String urlStr,String fileName,String savePath) throws IOException{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+ File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }
        System.out.println("info:"+url+" download success");
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}

