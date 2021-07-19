package java0.nio01.client;

import okhttp3.MediaType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ClientTest {
    private static final MediaType MEDIATYPE = MediaType.get("application/json; charset=utf-8");
    private static final String URL = "http://localhost:8801";

    public static void main(String[] args) throws IOException {

        // 使用OkHttpClient访问
//        OkHttpClient okHttpClient = new OkHttpClient();
//        // 连接url，设置传参类型为json
//        Request request = new Request.Builder().url(URL).post(RequestBody.create(MEDIATYPE,"")).build();
//        final Response response = okHttpClient.newCall(request).execute();
//        final String resultStr = response.body().string();
//        System.out.println("resultStr:" + resultStr);

        // 使用HttpClient访问
        HttpGet httpGet = new HttpGet(URL);

//        HttpPost httpPost = new HttpPost(URL);
//        String json = "{\"name\":\"123\"}";
//        StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
//        stringEntity.setContentEncoding("UTF-8");
//        stringEntity.setContentType("application/json");
//
//        httpPost.setEntity(stringEntity);

//        httpPost.setEntity(new StringEntity());
//        httpPost.setHeader("Content-Type","application/json;charset=utf8");

//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        HttpClient httpClient = HttpClientBuilder.create().build();
        final HttpResponse httpResponse = httpClient.execute(httpGet);
        final HttpEntity entity = httpResponse.getEntity();
        final String resultStr = EntityUtils.toString(entity);
        System.out.println("resultStr:" + resultStr);
    }
}
