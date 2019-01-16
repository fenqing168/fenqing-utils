package com.fenqing.code;

import com.fenqing.bean.Course;
import com.fenqing.bean.Lesson;

import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class AiKuReptileCourse extends AiKuReptile {

    private Course course;

    public AiKuReptileCourse(String root, String location) {
        super(root, location);
    }

    @Override
    public void start() throws IOException, InterruptedException {
        String rootSrc = getSrc();
        String courseName = getCourseName(rootSrc);
        course = new Course();
        course.setName(courseName);
        List<Lesson> lessons = this.getLesson(rootSrc);
        this.getResp(lessons);
        course.setLessons(lessons);
        this.download();
        System.out.println(lessons.size());
    }

    @Override
    protected void download() throws IOException {
        File file = new File(getLocation() + "/" + this.course.getName());
        if (!file.exists()) {
            file.mkdirs();
        }

        Iterator var3 = this.course.getLessons().iterator();

        while(var3.hasNext()) {
            Lesson lesson = (Lesson)var3.next();
            String filename = lesson.getName() + ".mp4";
            System.out.println("正在下载：" + filename);
            downloadFile(lesson.getResp().getData(), new File(file, filename));
        }
    }
}
