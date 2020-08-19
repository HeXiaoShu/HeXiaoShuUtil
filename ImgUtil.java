package com.util;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.util.ResourceUtils;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @description: 图片处理工具类
 * @author: hexiaoshu
 **/
@Slf4j
public class ImgUtil {


    /**
     * 图片内容提取，数字以前单次读取较准确，中文需要训练
     * @param imgPath       图片路径
     * @param languagePath  语言库路径
     * @param language      语言,默认英文 chi_sim 中文, eng 英文
     * @param rectangle     坐标范围，可选
     * @return Content
     */
    public static String readImg(String imgPath,String languagePath,String language,Rectangle rectangle){
        File file = new File(imgPath);
        if (!file.exists()){
            return "图片路径不存在";
        }
        ITesseract instance = new Tesseract();
        instance.setDatapath(languagePath);
        if (language==null || language.isEmpty()){
            instance.setLanguage("eng");
        }
        String result = "";
        try {
            if (rectangle==null){
                result =  instance.doOCR(file);
            }else {
                result =  instance.doOCR(file ,rectangle);
            }
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return result;
    }


    /** 设置文字水印
     * @param sourceImg 源图片路径
     * @param targetImg 保存的图片路径
     * @param watermark 水印内容
     * @param color 水印颜色 new Color(255,255,255,128)
     * @param font 水印字体  new Font("04b_08", Font.PLAIN, 30)
     * @throws IOException
     */
    public static boolean addWatermark(String sourceImg, String targetImg, String watermark,Color color,Font font){
        File srcImgFile = new File(sourceImg);
        Image srcImg;
        try {
            srcImg=ImageIO.read(srcImgFile);
        }catch (IOException e){
            return false;
        }
        int srcImgWidth = srcImg.getWidth(null);
        int srcImgHeight = srcImg.getHeight(null);
        BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufImg.createGraphics();
        g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
        g.setColor(color);
        g.setFont(font);
        //设置水印的坐标
        int x = srcImgWidth - (g.getFontMetrics(g.getFont()).charsWidth(watermark.toCharArray(), 0, watermark.length())+20);
        int y = srcImgHeight - 25;
        //加水印
        g.drawString(watermark, x, y);
        g.dispose();
        // 输出图片
        FileOutputStream outImgStream;
        try {
            outImgStream = new FileOutputStream(targetImg);
            ImageIO.write(bufImg, "jpg", outImgStream);
            outImgStream.flush();
            outImgStream.close();
        }catch (IOException e){
            return false;
        }
        return true;
    }

    /**
     * 获取跟目录---与jar包同级目录的upload目录下指定的子目录subdirectory
     * @param subdirectory  子目录
     * @return
     */
    public static String  getJarPath(String subdirectory){
        File upload;
        try {
            //本地测试时获取到的是"工程目录/target/upload/subdirectory
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            if(!path.exists()) {
                path = new File("");
            }
            upload = new File(path.getAbsolutePath(),subdirectory);
            if(!upload.exists()) {
                upload.mkdirs();
            }
            return upload + File.separator;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("获取服务器路径发生错误！");
        }
    }

    /**
     * 为图片添加图片水印
     * @param watermarkUrl 水印图片
     * @param source  原图
     * @param output 制作完成的图片
     * @return boolean
     */
    public static boolean markImgMark(String watermarkUrl, String source, String output){
        File file = new File(source);
        Image img;
        try {
            img = ImageIO.read(file);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        String thumbnailName = "";
        String thumbnailPath = "";
        String path = getJarPath("thumbnail_img");
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        ImageIcon imgIcon = new ImageIcon(watermarkUrl);
        int waterHeight = imgIcon.getImage().getHeight(null);
        int waterWidth = imgIcon.getImage().getWidth(null);
        if ( (waterHeight+waterWidth) > 400){
            log.info("调整水印图片");
            thumbnailName ="Thumbnail.png";
            thumbnailPath=path+File.separator+thumbnailName;
            ImgUtil.creatThumbnail(watermarkUrl, thumbnailPath,100,100);
            imgIcon = new ImageIcon(thumbnailPath);
        }
        Image con = imgIcon.getImage();
        float clarity = 0.6f;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, clarity));
        g.drawImage(con, 10, 10, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g.dispose();
        File sf = new File(output);
        try {
            ImageIO.write(bi, "jpg", sf);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        if (!thumbnailName.isEmpty()){
            File file1 = new File(thumbnailPath);
            file1.delete();
        }
        return true;
    }


    /**
     * 生成缩略图
     * @param sourceImg 原图片
     * @param outImg    生成图片
     * @param width and height     图片宽高
     * @return boolean
     */
    public static boolean creatThumbnail(String sourceImg,String outImg,int width,int height){
        File originalImg = new File(sourceImg);
        File thumbnailImg = new File(outImg);
        Thumbnails.Builder<File> builder = Thumbnails.of(originalImg).size(width, height).outputQuality(0.5f);
        try {
            builder.toFile(thumbnailImg);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
