package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.OpenCoinMarketCap.getBrowserContextFastOptmz;
import static org.example.Util.iniLogCfg;

public class BotBroswer {
    private static Logger log ;
    public  static void nvgt(String url, Page page) {
        page.navigate(url,new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }

    public static Browser getBrowser4disabGpu() {
        iniLogCfg();

        // 设置 slf4j-simple 的日志配置
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss");
        BotBroswer. log = LoggerFactory.getLogger(OpenCoinMarketCap.class);
        System.out.println(log);
        BotBroswer.  log.info("funx getBrowser4disabGpu");
        Playwright playwright = Playwright.create();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(false);
        options.setArgs(List.of(
                "--disable-blink-features=AutomationControlled",
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage"
        ));
        Browser browser = playwright.chromium().launch(options);
        log.info("endfun getBrowser4disabGpu");
        return browser;
    }

    private static final int POOL_SIZE =7 ;
    private static ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);



    private static Browser getBrowser4gld() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        return browser;
    }
    public  static Browser getBrowser4crp() {
        log = LoggerFactory.getLogger(BotBroswer.class);
        System.out.println(log);
        log.info("fun getBrowser4crp");
        Playwright playwright = Playwright.create();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(false);
        options.setArgs(List.of(
                "--disable-blink-features=AutomationControlled",
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage"
        ));
        Browser browser = playwright.chromium().launch(options);
        log.info("endfun getBrowser4crp");
        return browser;
    }
    @NotNull
    public  static BrowserContext getBrowserContextFast() {
        iniLogCfg();
        BotBroswer.  log= LoggerFactory.getLogger(BotBroswer.class);
        Browser browser = getBrowser4disabGpu();
        BotBroswer.  log.info("aft getBrowser4canvas");
        //  Browser.NewContextOptions options = getNewContextOptions4mobileSite();


        //  BrowserContext context = browser.newContext(options);
        //dis font media,can fast scrsht
        BrowserContext context =   getBrowserContextFastOptmz(browser);
        return context;
    }
    public  static Browser getBrowser4canvas() {
        log = LoggerFactory.getLogger(BotBroswer.class);
        System.out.println(log);
        log.info("fun getBrowser4canvas");
        Playwright playwright = Playwright.create();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(false).setDevtools(true);
        options.setArgs(List.of(


                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--enable-webgl",
                "--use-gl=desktop",
                "--ignore-gpu-blocklist",
                "--disable-web-security",

                "--disable-blink-features=AutomationControlled"
        ));
        Browser browser = playwright.chromium().launch(options);
        log.info("endfun getBrowser4canvas");
        return browser;
    }
}
