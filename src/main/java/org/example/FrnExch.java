package org.example;

import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.example.BotBroswer.*;
import static org.example.OpenCoinMarketCap.*;
import static org.example.Util.iniLogCfg;


//https://cn.investing.com/commodities/gold
public class FrnExch {
    public static Logger log= LoggerFactory.getLogger(FrnExch.class);
    public static void main(String[] args) throws InterruptedException {
        iniLogCfg();
        log = LoggerFactory.getLogger(FrnExch.class);

            Browser browser = getBrowser4disabGpu();

//            Map<String, String> headers = new HashMap<>();
//            headers.put("Referer", "https://cn.investing.com/");
//            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
//                    .setExtraHTTPHeaders(headers);

            // Create a context with a custom user agent (Firefox user agent)
            Browser.NewContextOptions newContextOptions =     getNewContextOptions4mobileSite();


          //  newContextOptions .setExtraHTTPHeaders(headers);
                  //  .setTimezoneId("America/New_York");
            BrowserContext context =   getBrowserContextFastOptmz(browser);
    String url="https://www.baidu.com/s?ie=utf-8&wd=人民币美元汇率k线\n";

            // Create a new page within this context
            Page page = context.newPage();

            byte[] imgBytes=getImgBytes(url,page);


        mkdir("pics");
        String picpath = "pics/frnExchg" + System.currentTimeMillis() + ".png";

        //
        Thread.startVirtualThread(() -> {
            save2file(imgBytes,picpath);
            //  page.close();
        });

            //   byte[] imageBytes = Files.readAllBytes(Path.of(picpath));



            System.out.println("Screenshot saved as chart-element.png");


            // Keep browser open for a while to see the page
            Thread.sleep(3000 * 1000); // 10 seconds

        Thread.sleep(3000 * 1000); // 10 seconds
    }

    static byte[] getImgBytes(String url, Page page) throws InterruptedException {

        page.addInitScript("document.fonts.ready.then(() => console.log('Fonts ready'));");

        // 注入JS，隐藏 navigator.webdriver = true
        page.addInitScript("Object.defineProperty(navigator, 'webdriver', { get: () => undefined });");

        //  addstyle(page);

        //  disabFont(page);
        page.onWebSocket(ws -> {
            System.out.println("✅ WebSocket 连接成功: " + ws.url());
        });
        page.onRequestFailed(request -> {
            if (request.url().startsWith("wss://")) {
                System.out.println("❌ WebSocket 连接失败: " + request.url());
            }
        });


        //
        // https://coinmarketcap.com/currencies/bitcoin/            // Open the CoinMarketCap website
        //  String url = "https://cn.investing.com/commodities/gold";
        //    url = "https://cn.investing.com/currencies/xau-usd-candlestick";
        System.out.println(url);
        //  page.navigate(url);
        nvgt(url,page);
        //   nvgtThrw(page, url);


        //https://cn.investing.com/currencies/xau-usd-candlestick

        // Wait for the main element to load
//            page.waitForSelector("div[class*='cmc-homepage']");

        // Optional: print the title of the page
        System.out.println("Page title: " + page.title());
        // Wait for page content to load (adjust selector as needed)
        //  page.waitForSelector("h1");
        System.out.println("aftr h1 " + page.title());

//            randomDelay();
//            page.mouse().move(200, 300);
//            page.mouse().wheel(0, 500);  // 模拟滚动
//            randomDelay();
        // Example: Click the second image in a list (adjust if needed)
        // This uses a general querySelector format. Adjust as necessary based on the site's structure.

        // Click the tab with data-index="tab-Candle"
        // Wait for the tab to be available and click it


        // 2. Wait for the element to appear
        // 等待目标 div 加载（最多 60 秒）
        // Locator el = page.locator("div:has-text(\"日K\")");
//            Locator el22 =page.locator("div.WA_LOG_TAB:has-text(\"周K\")");
//
//            el22.waitFor();    Thread.sleep(1000);
//            el22.click(); // 如果找到了且可见，就点击
//            Thread.sleep(20000);
        Locator el =page.locator("div.WA_LOG_TAB:has-text(\"日K\")");
        System.out.println("匹配元素数: " + el.count());
        System.out.println("是否可见: " + el.isVisible());
        el.waitFor();    Thread.sleep(1000);
        el.click(); // 如果找到了且可见，就点击
        System.out.println("targe elmt showed");
        //  page.waitForFunction("() => document.querySelector('canvas') && document.querySelector('canvas').clientHeight > 0");


        // 3. Take screenshot of that element


        Locator chartBlock = page.locator("div.feedback__2xRet");
        //  chartBlock.waitFor();
        // 等待图表区域出现在页面上，并完成渲染
        chartBlock.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000)); // 最多等10秒

        Thread.sleep(3000);
        log.info("aft chartBlock  visibale ok");
        System.out.println("start   save pic...");
        //也需要wait font,, page scrsht
//            page.screenshot(new Page.ScreenshotOptions()
//                    .setPath(Paths.get("fullpage.png"))
//                    .setFullPage(true));
        //scrsave(page);
        byte[] imageBytes = chartBlock.screenshot();
return imageBytes;
    }

    private static void scrsave(Page page) throws IOException {
        // 创建 CDP 会话   连接 DevTools 协议（CDP
        CDPSession cdp = page.context().newCDPSession(page);

        // 执行截图命令
        JsonObject result = cdp.send("Page.captureScreenshot", new JsonObject());

        // 保存为文件
        String base64 = result.get("data").toString();
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        try (FileOutputStream fos = new FileOutputStream("forced-cdp-screenshot.png")) {
            fos.write(imageBytes);
        }

        System.out.println("✅ 截图完成（CDP方式，无需等待字体）");

        System.out.println("✅ 强制截图完成，不等待字体！");
    }

    private static void nvgtThrw(Page page, String url) {
        try {
            page.navigate(url,
                    new Page.NavigateOptions()
                            .setTimeout(20000)
                            .setWaitUntil(WaitUntilState.NETWORKIDLE)); // 页面空闲时认为加载完成
        } catch (Exception e) {
            System.out.println("------------cat e:" + e.getMessage());

            e.printStackTrace();
            System.out.println("-----------end catex");
        }
    }

    private static void addstyle(Page page) {
        // 注入字体策略 CSS
        page.addStyleTag(new Page.AddStyleTagOptions().setContent("""
                    * {
                        font-display: swap !important;
                    }
                """));
//        page.addStyleTag(new Page.AddStyleTagOptions().setContent("""
//* {
//    font-display: swap !important;
//}
//@font-face {
//    font-family: 'FakeFont';
//    src: local('Arial');
//}
//"""));
    }

    private static void disabFont(Page page) {
        // 禁止字体加载
        page.route("**/*", route -> {
            String url = route.request().url();
            if (url.endsWith(".woff") || url.endsWith(".woff2") || url.endsWith(".ttf") || url.contains("font")) {
                route.abort();
            } else {
                route.resume();
            }
        });
    }

    // 随机延迟模拟人类思考/操作间隔
    private static void randomDelay() {
        try {
            int delay = new Random().nextInt(2000) + 1000; // 1~3 秒
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}
