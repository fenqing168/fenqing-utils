package com.fenqing.code;

import com.fenqing.bean.Lesson;
import com.fenqing.bean.Resp;
import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class AiKuDownloadUrlThread extends Thread {

    private Lesson lesson;

    private Reptile reptile;

    public AiKuDownloadUrlThread(Lesson lesson, Reptile reptile) {
        this.lesson = lesson;
        this.reptile = reptile;
    }

    @Override
    public void run() {
        try {

            String url = lesson.getDownloadUrl();
            System.out.println("获取视频链接：" + url);
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Cookie", "BAIDU_SSP_lcr=http://www.so.com/link?m=aYLD02QvpE5TygVagslACfjDjQI342Cdr7axwLoaJ8AekYvYhvA00TgZyvVZ9HGZ4uCBh8rkthazxPdxr3qcCmH%2F3n61Y9G2dB6AjPzJlT0JxJm75; Hm_lvt_315805d5d93485b7b39f1f10c5456261=1546341314; MEIQIA_EXTRA_TRACK_ID=1FAA37WfpRbj2dszQJ5BqXEFDy2; JSESSIONID=abfe9d3e-329f-47bd-b930-5fc195521f41; Hm_lpvt_315805d5d93485b7b39f1f10c5456261=1546353549; MEIQIA_VISIT_ID=1FAYpHLaI7aYW5HcsVD1bUKIyVQ");
            httpGet.setHeader("Host", "www.icoolxue.com");
            httpGet.setHeader("method", "ajax");
            httpGet.setHeader("Referer", reptile.getRoot());
            httpGet.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate");
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            httpGet.setHeader("client", "web");
            BasicHttpResponse httpRequest = null;
            httpRequest = (BasicHttpResponse) client.execute(httpGet);
            String result = EntityUtils.toString(httpRequest.getEntity(), "utf-8");
            Gson gson = new Gson();
            Resp resp = (Resp) gson.fromJson(result, Resp.class);
            lesson.setResp(resp);
            if(resp.getData() == null){
                System.out.println(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
