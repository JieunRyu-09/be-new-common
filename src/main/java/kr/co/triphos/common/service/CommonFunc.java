package kr.co.triphos.common.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
@SuppressWarnings("unchecked")
public class CommonFunc {

    public static boolean isBlank(Object object) {
        String parseObject = object.toString().trim();
        return parseObject.isEmpty() || parseObject == null;
    }

    /**
     * JSON 형식 Header 가져오기
     * @param request HttpServletRequest
     * @return HttpHeaders
     */
    public static HttpHeaders getHttpHeader(HttpServletRequest request) {
        return getHttpHeader(request, "Application/json");
    }

    /**
     * 사용자 정의 형식 Header 가져오기
     * @param request HttpServletRequest
     * @param type Doc Type
     * @return HttpHeaders
     */
    public static HttpHeaders getHttpHeader(HttpServletRequest request, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store");
        headers.add("Pragma", "no-cache");
        headers.setDate("Expires", 0);
        if (request.getProtocol().equals("HTTP/1.1")) headers.add("Cache-Control", "no-cache");
        headers.add("Content-Type", type + "; charset=UTF-8");

        return headers;
    }

    /**
     * 허용된 HTML 태그이외에는 모두 사용못함 처리
     * @param s String
     * @param allowTagStr 태그와 태그사이에 ,로 구분
     * @return convert String
     */
    public static String htmlReplace(String s, String allowTagStr) {
        if (isNull(s)) return "";
        if (s.isEmpty()) return "";

        String pattern = "<(\\/?)(?!\\/####)([^<|>]+)?>";
        String subStitute = "&lt;$1$2&gt;";
        String[] allowTags = allowTagStr.split(",");
        StringBuilder buffer = new StringBuilder();

        for (String allowTag : allowTags) {
            buffer.append("|").append(allowTag.trim()).append("(?!\\w)");
        }

        pattern = pattern.replace("####", buffer.toString());
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(s).replaceAll(subStitute);
    }

    /**
     * 문자열을 Integer형으로 변경
     * @param s 숫자형 문자열
     * @return Integer형이 아닌경우에는 0으로 반환
     */
    public static Integer parseInt(String s) {
        int value = 0;

        try {
            value = Integer.parseInt(s);
        }
        catch (Exception ignored) {}

        return value;
    }

    /***
     * 문자열을 Long형으로 변환
     * @param s 숫자형 문자열
     * @return Long형이 아닌경우에는 0으로 반환
     */
    public static Long parseLong(String s) {
        long value = 0L;

        try {
            value = Long.parseLong(s);
        }
        catch (Exception ignored) {}

        return value;
    }

    /***
     * 문자열을 Double형으로 변환
     * @param s 숫자형 문자열
     * @return Double형이 아닌경우에는 0으로 반환
     */
    public static Double parseDouble(String s) {
        double value = 0.0d;

        try {
            value = Double.parseDouble(s);
        }
        catch (Exception ignored) {}

        return value;
    }

    /***
     * 문자열을 Double형으로 변환
     * @param s 숫자형 문자열
     * @param defaultValue Double형이 아닌경우 기본값
     * @return Double형이 아닌경우에는 0으로 반환
     */
    public static Double parseDouble(String s, Double defaultValue) {
        Double value = defaultValue;

        try {
            value = Double.parseDouble(s);
        }
        catch (Exception ignored) {}

        return value;
    }

    /**
     * 문자열을 Float형으로 변환
     * @param s 숫자형 문자열
     * @return Float형이 아닌경우에는 0으로 반환
     */
    public static Float parseFloat(String s) {
        float value = 0.0f;

        try {
            value = Float.parseFloat(s);
        }
        catch (Exception ignored) {}

        return value;
    }

    /**
     * 문자열 null여부 확인
     * @param s String
     * @return Boolean
     */
    public static Boolean isNull(String s) {
        return s == null || s.trim().equals("") || s.equals("null");
    }

    /**
     * 문자열 빈값 여부 확인
     * @param s String
     * @return Boolean
     */
    public static Boolean isStringEmpty(String s) {
        if (isNull(s)) {
            return true;
        }

        return s.trim().isEmpty();
    }

    /**
     * TODO 문자열이 널이면 ""로 반환
     * @param s String
     * @return String
     */
    public static String ifNull(String s) {
        return ifNull(s, "");
    }

    /**
     * 문자열이 널이면 defaultValue로 반환
     * @param s String
     * @param defaultValue String
     * @return String
     */
    public static String ifNull(String s, String defaultValue) {
        if (isNull(s)) {
            return defaultValue;
        }
        return s.trim();
    }

    /**
     * 문자열이 영문(대소문자 구분없음), 숫자로 구성되어 있는지 확인
     * @param s String
     * @return Boolean
     */
    public static Boolean isEng(String s) {
        if (isNull(s)) {
            return false;
        }

        Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher m = p.matcher(s);

        return m.matches();
    }

    /**
     * 문자열이 숫자로만 구성되어 있는지 확인
     * @param s String
     * @return Boolean
     */
    public static Boolean isNum(String s) {
        if (isNull(s)) {
            return false;
        }

        Pattern p = Pattern.compile("^[0-9]+$");
        Matcher m = p.matcher(s);

        return m.matches();
    }

    /**
     * 문자열내 사용자정의 형식 확인
     * @param s String
     * @param ptrn 정규식 패턴
     * @return Boolean
     */
    public static Boolean isVal(String s, String ptrn) {
        if (isNull(s)) return false;
        if (isNull(ptrn)) return false;

        Pattern p = Pattern.compile(ptrn);
        Matcher m = p.matcher(s);

        return m.matches();
    }

    /**
     * 문자열이 전화번호로 구성되어 있는지 확인 000-0000-0000
     * @param s 전화번호 형식 00-0000-0000, 000-0000-0000
     * @return Boolean
     */
    public static Boolean isTel(String s) {
        return isVal(s, "^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})-?[0-9]{3,4}-?[0-9]{4}$");
    }

    /**
     * 문자열이 이메일형식이니 확인
     * @param s 이메일 형식 문자열
     * @return Boolean
     */
    public static Boolean isEMail(String s) {
        return isVal(s, "^[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$");
    }

    /**
     * 문자열이 날짜형식인지 확인 (yyyy-MM-dd)
     * @param s 날짜형식 문자열
     * @return Boolean
     */
    public static Boolean isDate(String s) {
        if (s.length() != 10) {
            return false;
        }
        return isDate(s, "yyyy-MM-dd");
    }

    /**
     * 문자열이 대시없는 날짜형식인지 확인 (yyyyMMdd)
     * @param s 날짜형식 문자열
     * @return Boolean
     */
    public static Boolean isDateNoDash(String s) {
        return isDate(s, "yyyyMMdd");
    }

    public static Date convertDtDash(String s) throws ParseException {
        SimpleDateFormat inputFormat  = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (isDateNoDash(s)) {
            Date date = inputFormat.parse(s);
            s = outputFormat.format(date);
        }

        return parseDate(s);
    }

    /**
     * 문자열이 날짜형식인지 확
     * @param s 날짜형식 문자열
     * @param ptn 날짜패턴
     * @return Boolean
     */
    public static Boolean isDate(String s, String ptn) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(ptn);
            format.parse(s);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * 날짜형식(yyyy-MM-dd)의 문자열을 날짜형으로 변경
     * @param s 날짜형식 문자열
     * @return 잘못된 형식이면 현재날짜로 반환
     */
    public static Date parseDate(String s) {
        return parseDate(s, "yyyy-MM-dd");
    }

    /**
     * 날짜형식의 문자열을 날짜형으로 변경
     * @param s 날짜형식 문자열
     * @param ptn 날짜패턴
     * @return Date
     */
    public static Date parseDate(String s, String ptn) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(ptn);
            return format.parse(s);
        }
        catch (Exception ex) {
            return null;
        }
    }

    /**
     * 두 날짜사이의 일수 반환
     * @param d1 시작일
     * @param d2 종료일
     * @return 일수
     */
    public static Long DayDiff(Date d1, Date d2) {
        Long t1 = d1.getTime();
        Long t2 = d2.getTime();
        return (t2 - t1) / (24 * 3600 * 1000);
    }

    /**
     * 두 날짜사이의 월수 반환
     * @param d1 시작일
     * @param d2 종료일
     * @return 월수
     */
    public static int MonthDiff(Date d1, Date d2) {
        /* 해당년도에 12를 곱해서 총 개월수를 구하고 해당 월을 더 한다. */
        Calendar cal = Calendar.getInstance();

        cal.setTime(d1);
        int month1 = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH);

        cal.setTime(d2);
        int month2 = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH);

        return month2 - month1;
    }

    /**
     * 입력값 HTML 태그 제거
     * @param s 문자열
     * @return 태그가 제거된 문자열
     */
    public static String clearTag(String s) {
        if (isNull(s)) return "";
        return s.replaceAll("(?:<!.*?(?:--.*?--\\s*)*.*?>)|(?:<(?:[^>'\"]*|\".*?\"|'.*?')+>)", "");
    }

    /***
     * Client IP 가져오기
     * @param request HttpServletRequest
     * @return IP
     */
    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /***
     * 문자열 SQL Injection, HTML Tag Event 처리
     * @param s 문자열
     * @return 제한문자가 제거된 문자열
     */
    public static String setStringFilter(String s) {
        String[] sqlMethod = "AND,OR,UNION,SELECT,DELETE,INSERT,UPDATE,DROP,TRUNCATE,EXEC".split(",");
        String[] tagEvent = "onabort,onactivate,onafterprint,onafterupdate,onbeforeactivate,onbeforecopy,onbeforecut,onbeforedeactivate,onbeforeeditfocus,onbeforepaste,onbeforeprint,onbeforeunload,onbeforeupdate,onblur,onbounce,oncellchange,onchange,onclick,oncontextmenu,oncontrolselect,oncopy,oncut,ondataavailable,ondatasetchanged,ondatasetcomplete,ondblclick,ondeactivate,ondrag,ondragend,ondragenter,ondragleave,ondragover,ondragstart,ondrop,onerror,onerrorupdate,onfilterchange,onfinish,onfocus,onfocusin,onfocusout,onhelp,onkeydown,onkeypress,onkeyup,onlayoutcomplete,onload,onlosecapture,onmousedown,onmouseenter,onmouseleave,onmousemove,onmouseout,onmouseover,onmouseup,onmousewheel,onmove,onmoveend,onmovestart,onpaste,onpropertychange,onreadystatechange,onreset,onresize,onresizeend,onresizestart,onrowenter,onrowexit,onrowsdelete,onrowsinserted,onscroll,onselect,onselectionchange,onselectstart,onstart,onstop,onsubmit,onunload".split(",");

        for (String value : sqlMethod) {
            s = s.replaceAll(" (?i)" + value + " ", " *" + value.substring(1) + " ");
            s = s.replaceAll("\t(?i)" + value + "\t", "\t*" + value.substring(1) + "\t");
        }

        // HTML Tag Event
        for (String value : tagEvent) {
            s = s.replaceAll(" (?i)" + value + "=", " *" + value.substring(1) + "=");
        }

        return s;
    }

    private static Key getAESKey(String key) throws Exception {
        Key keySpec;

        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes(StandardCharsets.UTF_8);

        int len = b.length;

        if (len > keyBytes.length) {
            len = keyBytes.length;
        }

        System.arraycopy(b, 0, keyBytes, 0, len);
        keySpec = new SecretKeySpec(keyBytes, "AES");

        return keySpec;
    }

    private static byte[] hexToByteArray(String s) {
        byte[] retValue = null;

        if (s != null && !s.isEmpty()) {
            retValue = new byte[s.length() / 2];

            for (int i = 0; i < retValue.length; i++) {
                retValue[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
            }
        }

        return retValue;
    }

    private static String byteArrayToHex(byte[] buf) {
        StringBuilder strbuf = new StringBuilder(buf.length * 2);

        for (byte b : buf) {
            if (((int) b & 0xff) < 0x10) {
                strbuf.append("0");
            }

            strbuf.append(Long.toString((int) b & 0xff, 16));
        }

        return strbuf.toString();
    }

    /**
     * CBC PKCS5 암호화
     * @param s 문자열
     * @param key Key
     * @return 암호화된 문자열
     */
    public static String encryptAESCBC(String s, String key) throws Exception {
        String encrypted = null;
        Key keySpec = getAESKey(key);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(key.getBytes(StandardCharsets.UTF_8)));
        encrypted = byteArrayToHex(cipher.doFinal(s.getBytes(StandardCharsets.UTF_8)));

        return encrypted;
    }

    /**
     * CBC PKCS5 복호화
     * @param s 문자열
     * @param key Key
     * @return 복호화된 문자열
     */
    public static String decryptAESCBC(String s, String key) throws Exception {
        String decrypted = null;
        Key keySpec = getAESKey(key);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(key.getBytes(StandardCharsets.UTF_8)));
        decrypted = new String(cipher.doFinal(hexToByteArray(s)));

        return decrypted;
    }

    /**
     * SHA-256 해싱
     * @param s 문자열
     * @return Hexa 문자열
     */
    public static String sha256_Hex(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        md.update(bytes);

        // Hexa
        return byteArrayToHex(md.digest());
    }

    public static String sha256_Base64(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        md.update(bytes);

        // Base64
        //return new String(Base64.encodeBase64(md.digest()));
        return new String(Base64.getEncoder().encode(md.digest()));
    }

    /**
     * HttpServletRequest 파라메터 값 가져오기
     * @param req HttpServletRequest
     * @param name Parameter Name
     * @param defaultValue 기본값
     */
    public static String getParam(HttpServletRequest req, String name, String defaultValue) {
        String value = req.getParameter(name);

        if (isNull(value)) {
            value = defaultValue;
        }
        else {
            value = value.trim();
        }

        return value;
    }

    public static int getParam(HttpServletRequest req, String name, int defaultValue) {
        int value;

        try {
            value = Integer.parseInt(req.getParameter(name));
        }
        catch (Exception ignored) {
            value = defaultValue;
        }

        return value;
    }

    public static float getParam(HttpServletRequest req, String name, float defaultValue) {
        float value;

        try {
            value = Float.parseFloat(req.getParameter(name));
        }
        catch (Exception ignored) {
            value = defaultValue;
        }

        return value;
    }

    public static double getParam(HttpServletRequest req, String name, double defaultValue) {
        double value;

        try {
            value = Double.parseDouble(req.getParameter(name));
        }
        catch (Exception ignored) {
            value = defaultValue;
        }

        return value;
    }

    /**
     * 렌덤 숫자 생성
     * @param digits 자릿수
     * @return String
     */
    public static String getRandomNumber(int digits) {
        StringBuilder result = new StringBuilder();

        for (int i = 0 ; i < digits ; i++) {
            result.append((int) (Math.random() * 10));
        }

        return result.toString();
    }

    /**
     * JSONObject를 JSON 객체로 변환
     * @param obj JSONObject
     * @return JSON
     */
    public static JSON convertFromJSONObject(JSONObject obj) {
        JSON json = new JSON();

        for (Object o : obj.keySet()) {
            String key = String.valueOf(o);
            Object value = obj.get(key);
            json.put(key, value);
        }

        return json;
    }

    /**
     * JSONObject를 JSON 객체로 변환
     * @param obj JSONObject
     * @return JSON
     */
    public static JSON convertFromJSONObject(Object obj) {
        JSON json = new JSON();

        if (!(obj instanceof JSONObject)) {
            return null;
        }

        JSONObject temp = (JSONObject) obj;

        for (Object o : temp.keySet()) {
            String key = String.valueOf(o);
            Object value = temp.get(key);
            json.put(key, value);
        }

        return json;
    }

    /**
     * Map 객체를 JSON 객체로 변환
     * @param map Map<String, Object>
     * @return JSON
     */
    public static JSON convertFromMap(Map<String, Object> map) {
        JSON json = new JSON();

        if (map == null) {
            return null;
        }

        json.putAll(map);

        return json;
    }

    /**
     * DB Exception인 경우 특정코드 오류 내용만 가져오기
     * @param ex Exception
     * @param code Oracle Error Code
     * @return String
     */
    public static String getErrorMessageFromCode(Exception ex, String code) {
        String errorMsg = ex.getMessage();
        int pos = ex.getMessage().indexOf(code);

        if (pos > -1) {
            errorMsg = ex.getMessage().substring(pos + 10);
            pos = errorMsg.indexOf("\n");

            if (pos > -1) {
                errorMsg = errorMsg.substring(0, pos);
            }
        }

        return errorMsg;
    }

    /**
     * 파일명에 확장자 가져오기
     * @param fileNM String
     * @return String
     */
    public static String getFileExtension(String fileNM) {
        if (fileNM == null || fileNM.trim().isEmpty()) {
            return "";
        }

        return fileNM.substring(fileNM.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 파일 이동
     * @param source File
     * @param target File
     */
    public static void fileMove(File source, File target) {
        if (source == null || target == null) {
            return;
        }

        boolean isMove = false;

        try {
            isMove = source.renameTo(target);
        }
        catch (Exception ignored) {}

        if (!isMove) {
            byte[] buf = new byte[1024];
            FileInputStream fin = null;
            FileOutputStream fout = null;

            try {
                fin = new FileInputStream(source);
                fout = new FileOutputStream(target);

                int read = 0;

                while ((read = fin.read(buf, 0, buf.length)) != -1) {
                    fout.write(buf, 0, read);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            try { if (fin != null) fin.close(); } catch (Exception ignored) {}
            try { if (fout != null) fout.close(); } catch (Exception ignored) {}
            try { source.delete(); } catch (Exception ignored) {}
        }
    }

    /**
     * 파일 복사
     * @param source File
     * @param target File
     */
    public static void fileCopy(File source, File target) {
        if (source == null || target == null) {
            return;
        }

        byte[] buf = new byte[1024];
        FileInputStream fin = null;
        FileOutputStream fout = null;

        try {
            fin = new FileInputStream(source);
            fout = new FileOutputStream(target);

            int read = 0;

            while ((read = fin.read(buf, 0, buf.length)) != -1) {
                fout.write(buf, 0, read);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        try { if (fin != null) fin.close(); } catch (Exception ignored) {}
        try { if (fout != null) fout.close(); } catch (Exception ignored) {}
    }

    /**
     * 문자열을 camelCase 형식으로 변환
     * @param s String
     * @return String
     */
    public static String camelCaseConvert(String s) {
        String[] words = s.split("[\\W_]+");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (i == 0) {
                word = word.isEmpty() ? word : word.toLowerCase();
            }
            else {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            }

            builder.append(word);
        }

        return builder.toString();
    }

    /**
     * 파일 압축
     * @param srcPaths 압축할 파일 경로
     * @param saveFileName 압축파일내 저장할 파일명
     * @param zipFileName 저장위치
     */
    public static void zipFiles(String[] srcPaths, String[] saveFileName, String zipFileName) {
        if (srcPaths == null || saveFileName == null || zipFileName == null) {
            return;
        }

        if (srcPaths.length != saveFileName.length) {
            return;
        }

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(zipFileName)))) {
            int pos = 0;

            for (String srcPath : srcPaths) {
                Path src = Paths.get(srcPath);

                try (FileInputStream fis = new FileInputStream(src.toFile())) {
                    ZipEntry zipEntry = new ZipEntry(saveFileName[pos]);
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;

                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }

                    zos.closeEntry();
                }

                ++pos;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 예외메세지 표시
     * @param ex Exception
     * @return String
     */
    public static String getExceptionMessage(Exception ex) {
        String message = ex.getMessage();

        if (StringUtils.hasText(message)) {
            message = message + "\n \t ";
        }
        else {
            message = "\n \t ";
        }

        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        return message + stackTraceElements[0];
    }

    /**
     * xls파일 GetCellValue
     * @param hssfRow HSSFRow
     * @param index int
     * @return String
     */
    /*public static String GetCellValue(HSSFRow hssfRow, int index) {
        String value = "";
        HSSFCell cell = hssfRow.getCell(index);

        if (cell != null) {
            try {
                // 셀 타입 체크 타입에 맞는 value 선택
                switch (cell.getCellType()) {
                    case FORMULA:
                        // 셀이 수식이면 계산 결과 타입 체크
                        FormulaEvaluator evaluator = hssfRow.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                        CellType forCellType = evaluator.evaluateFormulaCell(cell);

                        switch (forCellType) {
                            case STRING:
                                value = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                if ((int) cell.getNumericCellValue() == (int) Math.floor(cell.getNumericCellValue())) {
                                    value = String.valueOf((int) cell.getNumericCellValue());
                                }
                                else {
                                    value = String.valueOf(cell.getNumericCellValue());
                                }
                                break;
                            default:
                                value = String.valueOf(cell);
                                break;
                        }
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            DataFormatter formatter = new DataFormatter();
                            value = formatter.formatCellValue(cell);
                        }
                        else {
                            if ((int) cell.getNumericCellValue() == (int) Math.floor(cell.getNumericCellValue())) {
                                value = String.valueOf((int) cell.getNumericCellValue());
                            }
                            else {
                                value = String.valueOf(cell.getNumericCellValue());
                            }
                        }
                        break;
                    case STRING:
                        value = cell.getStringCellValue();
                        break;
                    case ERROR:
                        value = String.valueOf(cell.getErrorCellValue());
                        break;
                    default:
                        value = String.valueOf(cell);
                        break;
                }
            }
            catch (Exception ignored) {}
        }

        if (value == null) {
            value = "";
        }
        else {
            value = value.trim();
            value = value.replace("'", "''");
            value = value.replace("%", "");
            value = value.replace(System.lineSeparator(), "");
        }

        return value;
    }*/

    /**
     * xlsx파일 GetCellValue
     * @param xssfRow XSSFRow
     * @param index int
     * @return String
     */
    /*public static String GetCellValue(XSSFRow xssfRow, int index) {
        String value = "";
        XSSFCell cell = xssfRow.getCell(index);

        if (cell != null) {
            try {
                // 셀 타입 체크 타입에 맞는 value 선택
                switch (cell.getCellType()) {
                    case FORMULA:
                        // 셀이 수식이면 계산 결과 타입 체크
                        FormulaEvaluator evaluator = xssfRow.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                        switch (evaluator.evaluateFormulaCell(cell)) {
                            case STRING:
                                value = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                value = parseInt(String.valueOf(cell.getNumericCellValue())) == 0
                                        ? String.valueOf(cell.getNumericCellValue())
                                        : String.valueOf(parseInt(String.valueOf(cell.getNumericCellValue())));
                                break;
                            default:
                                value = cell.getRawValue();
                                break;
                        }
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            DataFormatter formatter = new DataFormatter();
                            value = formatter.formatCellValue(cell);
                        }
                        else {
                            value = cell.getRawValue().contains(".")
                                    ? String.valueOf(cell.getNumericCellValue())
                                    : String.valueOf(parseInt(cell.getRawValue()));
                        }
                        break;
                    case STRING:
                        value = cell.getStringCellValue();
                        break;
                    case ERROR:
                        value = String.valueOf(cell.getErrorCellValue());
                        break;
                    default:
                        value = cell.getRawValue();
                        break;
                }
            }
            catch (Exception ignored){}
        }

        if (value == null) {
            value = "";
        }
        else {
            value = value.trim();
            value = value.replace("'", "''");
            value = value.replace("%", "");
            value = value.replace(System.lineSeparator(), "");
        }

        return value;
    }*/

    /**
     * 서버 URL 가져오기
     * @param request HttpServletRequest
     * @return String
     */
    public static String getServerUrl(HttpServletRequest request) {
        String scheme = request.getScheme(); // http 또는 https
        String serverName = request.getServerName(); // 서버 호스트 이름
        int serverPort = request.getServerPort(); // 서버 포트 번호
        String contextPath = request.getContextPath(); // 컨텍스트 경로

        if (request.isSecure()) {
            scheme = "https";
        }

        if (request.getRequestURL().toString().contains("ngrok-free.app")) {
            scheme = "https";
        }

        if (request.getRequestURL().toString().contains("ngrok.app")) {
            scheme = "https";
        }

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath);

        return url.toString();
    }

}
