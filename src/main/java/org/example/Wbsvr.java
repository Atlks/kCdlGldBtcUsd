package org.example;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;

import static org.example.OpenCoinMarketCap.*;

public class Wbsvr {

    static {
        // 设置 slf4j-simple 的日志配置
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "true");
        System.setProperty("org.slf4j.simpleLogger.showLogName", "true");       // 全类名
        System.setProperty("org.slf4j.simpleLogger.showShortLogName", "false"); // 短类名
    }
    private static final Logger log = LoggerFactory.getLogger(Wbsvr.class);

    //        Handler hdl2crp = ctx -> {
//
//
//        };
    public static void main(String[] args) {

        Javalin app = Javalin.create().start(8888);

        // http://13.212.95.142:8888/screenshotCrpt?currencies=bitcoin
        app.get("/screenshotCrpt", Wbsvr::hdl2crp);
        app.get("/screenshotGld", Wbsvr::hdl2gld);


    }

    public static void hdl2crp(Context ctx) throws IOException, InterruptedException {
        String currencies = ctx.queryParam("currencies");

        byte[] imageBytes = getBytesPicFrmCrptsite(browser4crp, currencies);
                //readPic("C:\\Users\\Administrator\\IdeaProjects\\kCandlPrj\\gld1747480497886.png");
        //


        ctx.contentType("image/png");
        ctx.result(imageBytes);
//        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//
//        ctx.result(base64Image);
//        ctx.contentType("image/png");

    }


    public static void hdl2gld(Context ctx) throws IOException, InterruptedException {
        String currencies = ctx.queryParam("currencies");

        byte[] imageBytes = getBytesPicFrmCrptsite(browser4crp, currencies);


        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        ctx.result(base64Image);
        ctx.contentType("image/png");
    }
}
