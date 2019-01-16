package com.fenqing.code;

import com.fenqing.bean.Lesson;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public abstract class Reptile {
    private String root;
    private String location;

    public Reptile(String root, String location) {
        this.root = root;
        this.location = location;
    }

    public abstract void start() throws IOException, InterruptedException;

    protected abstract void download() throws IOException;



    protected List<Lesson> getLesson() throws IOException, InterruptedException {
        return this.getLesson(this.getSrc());
    }

    protected abstract List<Lesson> getLesson(String src) throws IOException, InterruptedException;

    protected String getCourseName() throws IOException {
        return this.getCourseName(this.getSrc());
    }

    protected abstract String getCourseName(String rootSrc);

    protected String getSrc() throws IOException {
        System.out.println("获取" + root + "的源代码！");
        return this.getRespStr(this.root);
    }

    protected String getRespStr(String srcUrl) throws UnsupportedEncodingException, IOException {
        URL url = new URL(srcUrl);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
        StringBuilder sb = new StringBuilder();

        String tmp;
        while((tmp = br.readLine()) != null) {
            sb.append(tmp);
            sb.append("\n");
        }

        br.close();
        return sb.toString();
    }

    protected String getRoot() {
        return root;
    }

    protected String getLocation() {
        return location;
    }

    protected void downloadFile(String url, File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        URL u = new URL(url);
        InputStream is = u.openStream();
        byte[] bytes = new byte[1024];

        int len;
        while((len = is.read(bytes)) > 0) {
            os.write(bytes, 0, len);
        }

        os.flush();
        os.close();
        is.close();
    }


}
