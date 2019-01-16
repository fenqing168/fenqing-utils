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
            new AiKuDownloadUrlThread(lesson, this).start();
        }

    }


}
