package com.fr.swift.cloud;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.Strings;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2019-02-25
 */
public class SwiftCloudUtils {
    public static Map<String, String> getUserInfo(String username, String password) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("username", username);
        params.put("password", password);

        Response response = null;
        try {
            response = getResponse(SwiftCloudConstants.ANALYZE_AUTH_URL, JsonBuilder.writeJsonString(params));
            Map<String, String> result = new HashMap<String, String>();
            if (null != response && response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                String jsonString = responseBody.string();

                Map responseMap = JsonBuilder.readValue(jsonString, Map.class);

                if (SwiftCloudConstants.SUCCESS.equals(responseMap.get("status"))) {
                    return (Map<String, String>) responseMap.get("data");
                }
            }
            return result;
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        } finally {
            IoUtil.close(response);
        }
        return null;
    }

    /**
     * 获取认证签名
     *
     * @param map       map
     * @param appSecret appSecret
     */
    public static String getSign(HashMap<String, Object> map, String appSecret) {
        String[] sortedArray = map.keySet().toArray(new String[0]);
        Arrays.sort(sortedArray);
        StringBuilder stringBuilder = new StringBuilder();
        for (String element : sortedArray) {
            stringBuilder.append(element).append(map.get(element));
        }
        return byte2hex(encryptHMAC(stringBuilder.toString(), appSecret));
    }

    private static byte[] encryptHMAC(String plainText, String appSecret) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(appSecret.getBytes(Charset.forName("UTF-8")), SwiftCloudConstants.HMAC_MD5);
            Mac mac = Mac.getInstance(secretKeySpec.getAlgorithm());
            mac.init(secretKeySpec);
            return mac.doFinal(plainText.getBytes(Charset.forName("UTF-8")));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        }
        return new byte[0];
    }

    private static String byte2hex(byte[] encryptHMAC) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte element : encryptHMAC) {
            String hexString = Integer.toHexString(element & 255);
            if (hexString.length() == 1) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hexString.toUpperCase());
        }
        return stringBuilder.toString();
    }


    public static String getDownloadLink(String appKey, String appSecret, String clientUserId, String clientAppId, String treasDate) {
        HashMap<String, Object> params = getSignMap(appKey, appSecret);
        // 数据包信息
        params.put("client_user_id", clientUserId);
        params.put("client_app_id", clientAppId);
        params.put("treas_date", treasDate);
        Response response = null;
        try {

            response = getResponse(SwiftCloudConstants.DOWNLOAD_URL, JsonBuilder.writeJsonString(params));

            if (null != response && response.isSuccessful()) {
                ResponseBody body = response.body();
                Map responseMap = JsonBuilder.readValue(body.string(), Map.class);
                if (SwiftCloudConstants.SUCCESS.equals(responseMap.get("status"))) {
                    return responseMap.get("data").toString();
                } else {

                }
                SwiftLoggers.getLogger().warn(responseMap.get("error").toString());
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn(e);
        } finally {
            IoUtil.close(response);
        }
        return Strings.EMPTY;
    }

    private static HashMap<String, Object> getSignMap(String appKey, String appSecret) {
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("app_key", appKey);
        params.put("v", SwiftCloudConstants.VERSION);
        params.put("sign_method", SwiftCloudConstants.SIGN_METHOD);
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        // 签名
        params.put("sign", getSign(params, appSecret));
        return params;
    }

    private static Response getResponse(String url, String requestJson) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestJson);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody).build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            SwiftLoggers.getLogger().warn(e);
        }
        return null;
    }

    /**
     * 获取云端运维报告上传的Token
     *
     * @param reportPath 报告上传的地址
     */
    public static String getToken(String appKey, String appSecret, String reportPath) {
        HashMap<String, Object> params = getSignMap(appKey, appSecret);
        // 报告信息
        params.put("key", reportPath);
        Response response = null;
        try {
            response = getResponse(SwiftCloudConstants.UPLOAD_TOKEN_URL, JsonBuilder.writeJsonString(params));
            if (null != response && response.isSuccessful()) {
                ResponseBody body = response.body();
                Map responseMap = JsonBuilder.readValue(body.string(), Map.class);
                if (SwiftCloudConstants.SUCCESS.equals(responseMap.get("status"))) {
                    return responseMap.get("data").toString();
                }
            }

        } catch (Exception e) {
            SwiftLoggers.getLogger().warn(e);
        }
        return Strings.EMPTY;
    }

    /**
     * 报告上传
     *
     * @param reportPath  云端的报告路径
     * @param uploadToken 报告上传的Token
     */
    public static boolean upload(File report, String reportPath, String uploadToken) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.addFormDataPart("token", uploadToken);
            builder.addFormDataPart("resource_key", reportPath);
            builder.addFormDataPart("key", reportPath);
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), report);
            RequestBody requestBody = builder
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(report.getName(), report.getName(), fileBody)
                    .build();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS).build();
            Request request = new Request.Builder()
                    .url(SwiftCloudConstants.UPLOAD_URL)
                    .post(requestBody)
                    .build();
            client.newCall(request).execute().close();
            return true;
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn(e);
        }
        return false;
    }

    /**
     * 更新帆软市场的数据库
     *
     * @param clientUserId 客户的用户ID
     * @param clientAppId  客户的应用ID
     * @param treasDate    客户的数据包日期
     * @param reportPath   报告上传的地址
     */
    public static boolean uploadSubmit(String appKey, String appSecret, String clientUserId, String clientAppId, String treasDate, String reportPath) {
        Map<String, Object> params = getSignMap(appKey, appSecret);
        // 报告信息
        params.put("client_user_id", clientUserId);
        params.put("client_app_id", clientAppId);
        params.put("treas_date", treasDate);
        params.put("report_path", reportPath);
        Response response = null;
        try {

            response = getResponse(SwiftCloudConstants.UPLOAD_SUBMIT_URL, JsonBuilder.writeJsonString(params));

            if (null != response && response.isSuccessful()) {
                ResponseBody body = response.body();
                Map responseMap = JsonBuilder.readValue(body.string(), Map.class);
                return SwiftCloudConstants.SUCCESS.equals(responseMap.get("status"));
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn(e);
        }
        return false;
    }
}
