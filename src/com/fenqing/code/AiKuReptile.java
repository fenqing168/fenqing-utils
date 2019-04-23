package com.fenqing.code;

import com.fenqing.bean.Course;
import com.fenqing.bean.Lesson;
import com.fenqing.bean.Resp;
import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AiKuReptile extends Reptile {

    public AiKuReptile(String root, String location) {
        super(root, location);
    }

    @Override
    protected String getCourseName(String rootSrc) {
        Pattern pattern = Pattern.compile("(?<=<h1>).+(?=</h1>)");
        Matcher matcher = pattern.matcher(rootSrc);
        if (matcher.find()) {
            String courseName = matcher.group();
            return courseName;
        } else {
            return null;
        }
    }

    @Override
    protected List<Lesson> getLesson(String src) throws IOException, InterruptedException {
        List<Lesson> lessons = new ArrayList();
        Pattern pattern = Pattern.compile("(?<=<ul class=video-list>)([\\s\\S]*?)(?=</ul>)");
        Matcher matcher = pattern.matcher(src);
        if (matcher.find()) {
            String lis = matcher.group();
            pattern = Pattern.compile("(?<=<li>)([\\s\\S]*?)(?=<i class)");
            Matcher matcher1 = pattern.matcher(lis);

            while(matcher1.find()) {
                String lia = matcher1.group();
                Lesson lesson = new Lesson();
                Pattern pid = Pattern.compile("(?<=<a href=\"/play/)([\\s\\S]*?)(?=\" class=)");
                Matcher mid = pid.matcher(lia);
                mid.find();
                String id = mid.group();
                lesson.setDownloadUrl("http://www.icoolxue.com/video/play/url/" + id);
                Pattern pname = Pattern.compile("(?<=title=\")([\\s\\S]*?)(?=\" target=)");
                Matcher mname = pname.matcher(lia);
                mname.find();
                String name = mname.group();
                lesson.setName(name);
                lessons.add(lesson);
                System.out.println("捕获课次名称：" + lesson.getName());
            }
        }
        getResp(lessons);
        return lessons;
    }

    protected void getResp(List<Lesson> lessons) throws UnsupportedEncodingException, IOException, InterruptedException {
        Iterator var3 = lessons.iterator();

        while(var3.hasNext()) {
            Lesson lesson = (Lesson)var3.next();
            Thread.sleep(100);
            getUrl(lesson);
        }

    }

    public void getUrl(Lesson lesson) {
        try {

            String url = lesson.getDownloadUrl();
            System.out.println("获取视频链接：" + url);
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Cookie", "BAIDU_SSP_lcr=http://www.so.com/link?m=aYLD02QvpE5TygVagslACfjDjQI342Cdr7axwLoaJ8AekYvYhvA00TgZyvVZ9HGZ4uCBh8rkthazxPdxr3qcCmH%2F3n61Y9G2dB6AjPzJlT0JxJm75; Hm_lvt_315805d5d93485b7b39f1f10c5456261=1546341314; MEIQIA_EXTRA_TRACK_ID=1FAA37WfpRbj2dszQJ5BqXEFDy2; JSESSIONID=abfe9d3e-329f-47bd-b930-5fc195521f41; Hm_lpvt_315805d5d93485b7b39f1f10c5456261=1546353549; MEIQIA_VISIT_ID=1FAYpHLaI7aYW5HcsVD1bUKIyVQ");
            httpGet.setHeader("Host", "www.icoolxue.com");
            httpGet.setHeader("method", "ajax");
            httpGet.setHeader("Referer", getRoot());
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
            Resp resp = gson.fromJson(result, Resp.class);
            lesson.setResp(resp);
            if(resp.getData() == null){
                System.out.println(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
