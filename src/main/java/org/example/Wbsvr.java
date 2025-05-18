package org.example;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.Header;
import io.javalin.http.UploadedFile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.example.BotBroswer.getBrowser4disabGpu;
import static org.example.BotBroswer.getBrowserContextFast;

import static org.example.OpenCoinMarketCap.*;
import static org.example.Util.iniLogCfg;

public class Wbsvr {
    private static Logger log;

    static {
        iniLogCfg();
        log = LoggerFactory.getLogger(Wbsvr.class);
    }

    public static BrowserContext context = getBrowserContextFast();


    //        Handler hdl2crp = ctx -> {
//
//
//        };
    static LoadingCache<String, Page> cachePage;
    static LoadingCache<String, byte[]> cache;

    public static void main(String[] args) {
        iniLogCfg();
        Wbsvr.log = LoggerFactory.getLogger(Wbsvr.class);
        iniImgCache();

        //    iniCachePage();

        // Javalin app = Javalin.create().start(8888);

        Javalin app = Javalin.create(getJavalinConfigCrossdmain()).start(8888);
        setCrsdmnOptionHdl(app);


        // http://13.212.95.142:8888/screenshotCrpt?currencies=bitcoin
        // http://127.0.0.1:8888/screenshotCrpt?currencies=bitcoin
        app.get("/screenshotK", Wbsvr::hdl2crp);
        app.get("/screenshotGld", Wbsvr::hdl2gld);

        app.post("/upload", ctx -> {
            // 读取文本框中的保存文件名
            String saveName = ctx.formParam("savename");
            if (saveName == null || saveName.isBlank()) {
                ctx.status(400).result("Missing savename");
                return;
            }

            // 获取上传的文件
            UploadedFile uploadedFile = ctx.uploadedFile("file");
            if (uploadedFile == null) {
                ctx.status(400).result("No file uploaded");
                return;
            }

            // 保存到目标路径
            File targetFile = new File("uploads/" + saveName);
            try {
                Files.createDirectories(targetFile.getParentFile().toPath()); // 确保目录存在
                Files.copy(uploadedFile.content(), targetFile.toPath());
                ctx.result("File saved as: " + saveName);
            } catch (IOException e) {
                ctx.status(500).result("Failed to save file: " + e.getMessage());
            }
        });

    }

    private static void setCrsdmnOptionHdl(Javalin app) {
        app.options("/*", ctx -> {
            ctx.header(Header.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            ctx.header(Header.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS");
            ctx.header(Header.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type,Authorization");
            ctx.header(Header.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

            ctx.status(204); // No Content
        });
    }

    @NotNull
    private static Consumer<JavalinConfig> getJavalinConfigCrossdmain() {
        return config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
        };
    }

    private static void iniImgCache() {
        // 创建一个自动加载缓存
        String cacheName = "imgCache";
        cache = CacheBuilder.newBuilder()
                .maximumSize(999)  // 最多缓存100个条目
                .expireAfterWrite(120, TimeUnit.SECONDS)  // 缓存项在写入10秒后过期
                .removalListener(notification ->
                        System.out.println("移除缓存frm imgcache: " + notification.getKey() + " -> " + notification.getCause())
                )
                .build(new CacheLoader<String, byte[]>() {
                    @Override
                    public byte[] load(String url) throws Exception {
                        // 模拟从数据库或外部系统加载数据
                        log.info(cacheName + "加载数据start：" + url);

                        Page page = context.newPage();
                        byte[] imageBytes = getBytesFrmUrl(url, page);
                        log.info(cacheName + "加载数据finish： key=" + url);
                        return imageBytes;
                    }
                });
    }

    private static byte[] getBytesFrmUrl(String url, Page page) throws InterruptedException {
        byte[] imageBytes;
        if (url.contains("coinmarketcap"))
            imageBytes = getBytesFrmPage4crpt(url, page);
        else if (url.contains("baidu.com"))
            imageBytes = FrnExch.getImgBytes(url, page);
        else if (url.contains("cashbackforex"))
            imageBytes = Gld2.getImgBytes(url, page);
       else
            imageBytes = getBytesFrmPage4crpt(url, page);
        return imageBytes;
    }

//    private static void handleOptions(@NotNull HttpExchange exchange) throws Exception {
//        setCrossDomain(exchange);
//        exchange.getResponseHeaders().add("Allow", "GET, POST, PUT, DELETE, OPTIONS");
//
//

    /// /返回状态码 204（无内容）是标准做法。
//        exchange.sendResponseHeaders(204, -1); // No Content
//        exchange.close();
//    }
//    public static void setCrossDomain(HttpExchange exchange) {
//        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
//        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
//        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
//    }
    private static void iniCachePage() {
        // 创建一个自动加载缓存
        String cacheName = "imgCache";
        cachePage = CacheBuilder.newBuilder()
                .maximumSize(9)  // 最多缓存100个条目
                .expireAfterWrite(600, TimeUnit.SECONDS)  // 缓存项在写入10秒后过期
                .removalListener(notification ->
                        // cache.cleanUp() 主动触发清理。
                        //timer clearup just ok
                        System.out.println(cacheName + "移除缓存: " + notification.getKey() + " -> " + notification.getCause())
                )
                .build(new CacheLoader<String, Page>() {
                    @Override
                    public Page load(String k) throws Exception {
                        // 模拟从数据库或外部系统加载数据
                        log.info(cacheName + "加载数据start：" + k);
                        Page pg;
                        if (k.contains("coinmarketcap"))
                            pg = getPage4crp();
                        else
                            pg = getPage4gld();
                        log.info(cacheName + "加载数据finish： key=" + k);
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
        String url = ctx.queryParam("url");


        //readPic("C:\\Users\\Administrator\\IdeaProjects\\kCandlPrj\\gld1747480497886.png");
        //


        ctx.contentType("image/png");
        assert url != null;
        //    String url = "https://coinmarketcap.com/currencies/" + url + "/";
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
