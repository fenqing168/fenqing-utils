package com.fenqing.code;

import com.fenqing.bean.Course;
import com.fenqing.bean.CourseType;
import com.fenqing.bean.Lesson;
import com.fenqing.config.Config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AiKuReptileSite extends AiKuReptile {

    private List<CourseType> courseTypes;

    private String[] types;

    private AiKuReptileSite(String root, String location) {
        super(root, location);
    }

    public AiKuReptileSite(String location, String[] types){
        super(Config.AI_KU_ROOT_URL, location);
        this.courseTypes = new ArrayList<>();
        this.types = types;
    }

    @Override
    public void start() throws IOException, InterruptedException {
        String rootSrc = getSrc();
        List<String> typeLiSrcs = getTypeLiSrcs(rootSrc);
        setcourseTypes(typeLiSrcs);
        download();
    }

    private void setcourseTypes(List<String> cuorseLiSrcs) throws IOException, InterruptedException {

        for(String courseLiSrc : cuorseLiSrcs){
            Pattern url = Pattern.compile("(?<=<a href=\")([\\s\\S]*?)(?=\" class=flipper target=_blank>)");
            Pattern course = Pattern.compile("(?<=\\ title=)([\\s\\S]*?)(?=>\n</a>)");
            CourseType courseType = new CourseType();
            Matcher courseM = course.matcher(courseLiSrc);
            if(courseM.find()){
                String name = courseM.group();
                name = name.replaceAll("\"", "");
                courseType.setName(name);
                if(isCunzai(name)){
                    continue;
                }
                System.out.println("捕获类型名称：" + courseType.getName());
            }

            Matcher urlM = url.matcher(courseLiSrc);
            if(urlM.find()){
                String u = urlM.group();
                String courseSrc = getRespStr(Config.AI_KU_ROOT_URL + u);
                List<Course> courses = getCourses(courseSrc);
                courseType.setCourses(courses);
            }
            courseTypes.add(courseType);
        }
    }

    private List<Course> getCourses(String courseSrc) throws IOException, InterruptedException {

        List<String> courseLiSrcs = getCourseLiSrcs(courseSrc);
        List<Course> courses = new ArrayList<>();
        Pattern name = Pattern.compile("(?<=<img alt=\")([\\s\\S]*?)(?=\" src=\")");
        Pattern url = Pattern.compile("(?<=<a href=\")([\\s\\S]*?)(?=\" target=_blank title=)");

        for(String courseLiSrc : courseLiSrcs){
            Course course = new Course();
            Matcher nameM = name.matcher(courseLiSrc);
            if(nameM.find()){
                course.setName(nameM.group());
                System.out.println("捕获课程名称：" + course.getName());
            }
            Matcher urlM = url.matcher(courseLiSrc);
            if(urlM.find()){
                String u = urlM.group();
                String lessonSrc = getRespStr(Config.AI_KU_ROOT_URL + u);
                course.setLessons(getLesson(lessonSrc));
            }
            courses.add(course);
        }
        return courses;
    }

    private List<String> getCourseLiSrcs(String courseSrc) {

        List<String> courseLiSrcs = new ArrayList<>();
        Pattern pattern = Pattern.compile("<li class=course-one>[\\s\\S]+?</a>\n</li>");
        Matcher matcher = pattern.matcher(courseSrc);
        while(matcher.find()){
            courseLiSrcs.add(matcher.group());
        }
        return courseLiSrcs;

    }

    private List<String> getTypeLiSrcs(String rootSrc){
        System.out.println("获取全部课程类型：");
        List<String> typeLiSrcs = new ArrayList<>();
        Pattern pattern = Pattern.compile("<li class=affix-item>[\\s\\S]+?</a>\n</li>");
        Matcher matcher = pattern.matcher(rootSrc);
        while(matcher.find()){
            typeLiSrcs.add(matcher.group());
        }
        return typeLiSrcs;
    }

    private boolean isCunzai(String name) {
        for(String type : types){
            if(type.equals(name)){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void download() throws IOException {
        File root = new File(getLocation());
        for(CourseType courseType : courseTypes){
            File type = new File(root, courseType.getName());
            List<Course> courses = courseType.getCourses();
            for (Course course : courses) {
                File co = new File(type, course.getName());
                if(!co.exists()){
                    co.mkdirs();
                }
                List<Lesson> lessons = course.getLessons();
                for(Lesson lesson : lessons){
                    File f = new File(co, lesson.getName() + ".MP4");
                    System.out.println("正在下载：" + f.getAbsolutePath());
                    downloadFile(lesson.getResp().getData(), f);
                }
            }
        }
    }





}
