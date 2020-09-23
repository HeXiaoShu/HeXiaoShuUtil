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
    
    
    private static Pattern p = Pattern.compile("(\\d+)");
    private static Pattern p2 = Pattern.compile("(\\d+\\.\\d+)");
    private static Pattern p3 = Pattern.compile("(\\d+)");

    /**
     * 提取字符串中的 ，金额
     * @param str 提取字符
     * @return String
     */
    public static String getNumber(String str){
        //先判断有没有整数，如果没有整数那就肯定就没有小数
        Matcher m = p.matcher(str);
        String result = "";
        if (m.find()) {
            Map<Integer, String> map = new TreeMap();
            m = p2.matcher(str);
            //遍历小数部分
            while (m.find()) {
                result = m.group(1) == null ? "" : m.group(1);
                int i = str.indexOf(result);
                String s = str.substring(i, i + result.length());
                map.put(i, s);
                //排除小数的整数部分和另一个整数相同的情况下，寻找整数位置出现错误的可能，还有就是寻找重复的小数
                // 例子中是排除第二个345.56时第一个345.56产生干扰和寻找整数345的位置时，前面的小数345.56会干扰
                str = str.substring(0, i) + str.substring(i + result.length());
            }
            //遍历整数
            m = p3.matcher(str);
            while (m.find()) {
                result = m.group(1) == null ? "" : m.group(1);
                int i = str.indexOf(result);
                //排除jia567.23.23在第一轮过滤之后留下来的jia.23对整数23产生干扰
                if (String.valueOf(str.charAt(i - 1)).equals(".")) {
                    //将这个字符串删除
                    str = str.substring(0, i - 1) + str.substring(i + result.length());
                    continue;
                }
                String s = str.substring(i, i + result.length());
                map.put(i, s);
                str = str.substring(0, i) + str.substring(i + result.length());
            }
            result = "";
            for (Map.Entry<Integer, String> e : map.entrySet()) {
                result += e.getValue() + ",";
            }
            result = result.substring(0, result.length()-1);
        } else {
            result = "";
        }
        return result;
    }

    /**
     * 	作用：生成签名 key-value 形式
     */
    public static String getSign(Map<String,String> paramMap){
        String params = formatBizQueryParaMap(paramMap, true, false,"test");
        params = EncryptionUtil.MD5Encode(params,"utf-8").toUpperCase();
        return params;
    }

    public static String formatBizQueryParaMap(Map<String,String> para,boolean sort, boolean encode,String keyValue){
        List<String> keys = new ArrayList<>(para.keySet());
        if (sort){
            Collections.sort(keys);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = para.get(key);
            if (encode) {
                try {
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                }
            }
            if (null != value && !"".equals(value) && !"sign".equals(value) && !"key".equals(key)){
                sb.append(key).append(QSTRING_EQUAL).append(value).append(QSTRING_SPLIT);
            }
        }
        sb.append("key=").append(keyValue);
        System.out.println("签名字符串："+sb.toString());
        return sb.toString();
    }
    /** = */
    public static final String QSTRING_EQUAL = "=";
    /** & */
    public static final String QSTRING_SPLIT = "&";

    /**
     * 对象装换map
     * @param obj
     * @return
     */
    public static Map<String, String> transBean2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>(16);
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class") && !key.equals("pageNo") && !key.equals("pageSize") && !key.equals("custId")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    if (getter.invoke(obj)!=null){
                        String value = getter.invoke(obj).toString();
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }
        return map;
    }
    
    /**
     * 产生4位随机数(0000-9999)
     * @return 4位随机数
    */
    public static String getFourRandom(){
        Random random = new Random();
        String fourRandom = random.nextInt(10000) + "";
        int randLength = fourRandom.length();
        if(randLength<4){
            for(int i=1; i<=4-randLength; i++) {
                fourRandom = "0" + fourRandom;
            }
        }
        return fourRandom;
    }
    
    /**
     * o如果为null则赋值 o2
     * @param o  o
     * @param o2 o2
     * @return obj
     */
    public static Object putIf(Object o,Object o2){
        Optional<Object> optional = Optional.ofNullable(o);
        return optional.orElse(o2);
    }
    
    /**
     * 字符串在某集合出现的次数
     * @param list
     * @param str
     * @return
     */
    public static int strListRepeCount(List<String> list,String str){
        return (int) list.stream().filter(e -> e.equals(str)).count();
    }
    
    
    /**
     * 将一个list均分成n个list,主要通过偏移量来实现的
     * @param source
     * @return
     */
    public static <T> List<List<T>> averageAssign(List<T> source,int n){
        List<List<T>> result=new ArrayList<List<T>>();
        //(先计算出余数)
        int remaider=source.size()%n;
        //然后是商
        int number=source.size()/n;
        //偏移量
        int offset=0;
        for(int i=0;i<n;i++){
            List<T> value;
            if(remaider>0){
                value=source.subList(i*number+offset, (i+1)*number+offset+1);
                remaider--;
                offset++;
            }else{
                value=source.subList(i*number+offset, (i+1)*number+offset);
            }
            result.add(value);
        }
        return result;
    }
    
}
