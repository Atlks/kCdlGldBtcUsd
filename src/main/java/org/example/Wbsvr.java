package org.example;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.example.BotBroswer.getBrowser4disabGpu;
import static org.example.OpenCoinMarketCap.*;

public class Wbsvr {

    static {
        iniLogCfg();
    }

    static void iniLogCfg() {
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
    static LoadingCache<String, Page> cachePage;
    static LoadingCache<String, byte[]> cache;
    public static void main(String[] args) {
        iniImgCache();

        iniCachePage();

        Javalin app = Javalin.create().start(8888);

        // http://13.212.95.142:8888/screenshotCrpt?currencies=bitcoin
        // http://127.0.0.1:8888/screenshotCrpt?currencies=bitcoin
        app.get("/screenshotCrpt", Wbsvr::hdl2crp);
        app.get("/screenshotGld", Wbsvr::hdl2gld);



    }

    private static void iniImgCache() {
        // 创建一个自动加载缓存
        String cacheName = "imgCache";
        cache = CacheBuilder.newBuilder()
                .maximumSize(999)  // 最多缓存100个条目
                .expireAfterWrite(1, TimeUnit.SECONDS)  // 缓存项在写入10秒后过期
                .removalListener(notification ->
                        System.out.println("移除缓存frm imgcache: " + notification.getKey() + " -> " + notification.getCause())
                )
                .build(new CacheLoader<String, byte[]>() {
                    @Override
                    public byte[] load(String url) throws Exception {
                        // 模拟从数据库或外部系统加载数据
                       log.info(cacheName+"加载数据start：" + url);

                        Page page=cachePage.get(url);
                        byte[] imageBytes = getBytesFrmPage4crpt(url, page);
                        log.info(cacheName+"加载数据finish： key=" + url);
                        return imageBytes;
                    }
                });
    }

    private static void iniCachePage() {
        // 创建一个自动加载缓存
        String cacheName = "imgCache";
        cachePage = CacheBuilder.newBuilder()
                .maximumSize(9)  // 最多缓存100个条目
                .expireAfterWrite(600, TimeUnit.SECONDS)  // 缓存项在写入10秒后过期
                .removalListener(notification ->
                        System.out.println(cacheName+"移除缓存: " + notification.getKey() + " -> " + notification.getCause())
                )
                .build(new CacheLoader<String,Page>() {
                    @Override
                    public Page load(String k) throws Exception {
                        // 模拟从数据库或外部系统加载数据
                        log.info(cacheName+"加载数据start：" + k);
                        Page pg ;
                        if(k.contains("coinmarketcap"))
                          pg =  getPage4crp();
                        else
                            pg=getPage4gld();
                        log.info(cacheName+"加载数据finish： key=" + k);
                        return pg;
                    }
                });
    }

    private static Page getPage4gld() {
        return null;
    }

    private static Page getPage4crp() {
        BrowserContext context = getBrowserContextFastOptmz(getBrowser4disabGpu());
        Page page = context.newPage();
        return page;
    }

    public static void hdl2crp(Context ctx) throws IOException, InterruptedException, ExecutionException {
        String currencies = ctx.queryParam("currencies");



                //readPic("C:\\Users\\Administrator\\IdeaProjects\\kCandlPrj\\gld1747480497886.png");
        //


        ctx.contentType("image/png");
        assert currencies != null;
        String url = "https://coinmarketcap.com/currencies/" + currencies + "/";
        ctx.result(cache.get(url));
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
