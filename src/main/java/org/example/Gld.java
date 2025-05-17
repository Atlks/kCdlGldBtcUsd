package org.example;

import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.*;

//https://cn.investing.com/commodities/gold
public class Gld {

    public static void main(String[] args) throws InterruptedException {
        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
            launchOptions.setHeadless(false).setTimeout(60000);
            launchOptions.setDevtools(true); // 打开开发者工具，观察渲染状态
            launchOptions.setArgs(Arrays.asList(new String[]{
                    "--enable-webgl",
                    "--ignore-gpu-blocklist",
                    "--disable-web-security",
                    "--use-gl=desktop",
                    "--disable-blink-features=AutomationControlled"
            }));
            Browser browser = playwright.chromium().launch(
                    launchOptions);

            Map<String, String> headers = new HashMap<>();
            headers.put("Referer", "https://cn.investing.com/");
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                    .setExtraHTTPHeaders(headers);

            // Create a context with a custom user agent (Firefox user agent)
            Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Firefox/89.0 Safari/537.36")
                    .setLocale("en-US")
                    .setViewportSize(1280, 720)
                    .setExtraHTTPHeaders(headers)
                    .setTimezoneId("America/New_York");
            BrowserContext context = browser.newContext(newContextOptions

            );



            // Create a new page within this context
            Page page = context.newPage();
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
            String url = "https://cn.investing.com/commodities/gold";
            url="https://cn.investing.com/currencies/xau-usd-candlestick";
            System.out.println(url);
          //  page.navigate(url);

            try{
                page.navigate(url,
                        new Page.NavigateOptions()
                                .setTimeout(20000)
                                .setWaitUntil(WaitUntilState.NETWORKIDLE)); // 页面空闲时认为加载完成
            }catch (Exception e){
                System.out.println("------------cat e:"+e.getMessage());

                e.printStackTrace();
                System.out.println("-----------end catex");
            }


            //https://cn.investing.com/currencies/xau-usd-candlestick

            // Wait for the main element to load
//            page.waitForSelector("div[class*='cmc-homepage']");

            // Optional: print the title of the page
            System.out.println("Page title: " + page.title());
            // Wait for page content to load (adjust selector as needed)
            page.waitForSelector("h1");
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
            Locator element1 = page.locator("div#js_instrument_chart");
            element1.waitFor(new Locator.WaitForOptions().setTimeout(9*1000));
            System.out.println("targe elmt showed");
          //  page.waitForFunction("() => document.querySelector('canvas') && document.querySelector('canvas').clientHeight > 0");



            // 3. Take screenshot of that element
            System.out.println("start   save pic...");
            String gldpic = "gld" + System.currentTimeMillis() + ".png";

            // 创建 CDP 会话   连接 DevTools 协议（CDP
            CDPSession cdp = page.context().newCDPSession(page);

            // 执行截图命令
            JsonObject result = cdp.send("Page.captureScreenshot", new com.google.gson.JsonObject());

            // 保存为文件
            String base64 = result.get("data").toString();
            byte[] imageBytes = Base64.getDecoder().decode(base64);
            try (FileOutputStream fos = new FileOutputStream("forced-cdp-screenshot.png")) {
                fos.write(imageBytes);
            }

            System.out.println("✅ 截图完成（CDP方式，无需等待字体）");

            System.out.println("✅ 强制截图完成，不等待字体！");
//            page.screenshot(new Page.ScreenshotOptions()
//                    .setPath(Paths.get(gldpic))
//                    .setFullPage(true));

            // element1.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get(gldpic)));

            System.out.println("Screenshot saved as chart-element.png");


            // Keep browser open for a while to see the page
            Thread.sleep(3000 * 1000); // 10 seconds
            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread.sleep(3000 * 1000); // 10 seconds
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
