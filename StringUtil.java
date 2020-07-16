package com.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author hexiaoshu
 * @Description: 字符处理工具类
 */
public class StringUtil {

    /**
     * 判断字符串为空
     * @param str str
     * @return boolean
     */
    public static boolean isEmpty(String str){
        boolean flag = false;
        if(str==null||str.trim().length()==0){
            flag=true;
        }
        return  flag;
    }


    /**
     * 判断字符串非空
     * @param str str
     * @return boolean
     */
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

    /**
     * 判断对象为空
     * @param obj 对象名
     * @return 是否为空
     */
    public static boolean objIsEmpty(Object obj){
        if (obj == null)
        {
            return true;
        }
        if ((obj instanceof List))
        {
            return ((List) obj).size() == 0;
        }
        if ((obj instanceof String))
        {
            return "".equals(((String) obj).trim());
        }
        return false;
    }

    /**
     * 判断对象不为空
     * @param obj 对象名
     * @return 是否不为空
     */
    public static boolean objIsNotEmpty(Object obj)
    {
        return !objIsEmpty(obj);
    }


    /**
     * 字符串去 空格
     * @param str
     * @return
     */
    public static String removeStrSpace(String str){
        if (isNotEmpty(str)){
            return str.replaceAll(" ", "");
        }else {
            return null;
        }
    }
    /**
     * UUID获取
     * @return uuid
     */
    public static String getUuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取ip地址
     * @param request request
     * @return IP
     */
    public static String getIp(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
    }

    /**
     * 	作用：map转xml
     */
    public static String mapToXml(Map<String,String> paramMap){
        StringBuilder xml = new StringBuilder("<xml>");
        for (String key : paramMap.keySet()) {
            //值是否只有字母和数字
            if(paramMap.get(key).matches("^[\\da-zA-Z]*$")){
                xml.append("<").append(key).append(">").append(paramMap.get(key)).append("</").append(key).append(">");
            }else{
                xml.append("<").append(key).append("><![CDATA[").append(paramMap.get(key)).append("]]></").append(key).append(">");
            }
        }
        xml.append("</xml>");
        return xml.toString();
    }

    /**
     * xml 转  map
     * @param xml
     * @return
     */
    public static Map<String,String> xmlToMap(String xml) {
        try {
            Map<String,String> map = new HashMap<String,String>();
            Document document = DocumentHelper.parseText(xml);
            Element nodeElement = document.getRootElement();
            List node = nodeElement.elements();
            for (Object o : node) {
                Element elm = (Element) o;
                String val = elm.getText();
                val = val.replace("<![CDATA[", "");
                val = val.replace("]]>", "");
                map.put(elm.getName(), val);
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json转map
     * @param json
     * @return
     */
    public static Map jsonStrToMap(String json){
        String mapStr = JSON.toJSONString(json);
        return JSON.parseObject(mapStr, Map.class);
    }

    /**
     * 判断String 是否是json格式
     * @param string String
     * @return boolear
     */
    public static boolean isJson(String string){
        try {
            JSONObject jsonObject = JSONObject.parseObject(string);
            return  true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 通过文件全路径，获取文件的MIME类型
     * @param filePath 路径
     * @return contentType
     */
    public static String getContentType(String filePath){
        String type = null;
        Path path = Paths.get(filePath);
        try {
            type = Files.probeContentType(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return type;
    }


    /**
     * 生成随机密码， 包含数字，字母，大小写
     * @param length 密码长度
     * @return pwd
     */
    public static String randomGenerate(int length) {
        List<String> list = new ArrayList<>(CHARS.length);
        for (char aChar : CHARS) {
            list.add(String.valueOf(aChar));
        }
        Collections.shuffle(list);
        int count = 0;
        StringBuilder sb = new StringBuilder();
        Random random = new Random(System.nanoTime());
        while (count < length) {
            int i = random.nextInt(list.size());
            sb.append(list.get(i));
            count++;
        }
        return sb.toString();
    }
    private static final char[] CHARS = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };


    /**
     * 分页参数处理
     */
    private static final String CURRENT_PAGE="currentPage";
    private static final String PAGE_SIZE="pageSize";
    public static Map<String,Integer> getPageParam(Map<String,Object> paramMap){
        int startPage;
        int size;
        if (paramMap.get(CURRENT_PAGE)!=null){
            String pageNum = (String) paramMap.get("currentPage");
            startPage=Integer.parseInt(pageNum);
        }else {
            startPage=1;
        }
        if (paramMap.get(PAGE_SIZE)!=null){
            String pageSize = (String) paramMap.get("pageSize");
            size=Integer.parseInt(pageSize);
        }else {
            size=10;
        }
        Map<String,Integer> resultMap = new HashMap<>(16);
        resultMap.put("currentPage",startPage);
        resultMap.put("pageSize",size);
        return resultMap;
    }

    /**
     * 拼接 % %,模糊查询参数
     * @param str 拼接字符
     * @return
     */
    public static String appendLike(String str){
        if (isNotEmpty(str)){
            StringBuilder builder = new StringBuilder(str);
            builder.insert(0,"%");
            builder.insert(builder.length(),"%");
            return builder.toString();
        }else {
            return null;
        }
    }


}
