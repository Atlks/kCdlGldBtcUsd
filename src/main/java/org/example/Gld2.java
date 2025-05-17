package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;

import java.nio.file.Paths;
import java.util.Arrays;

public class Gld2 {
    public static void main(String[] args) {
        String url = "https://www.cashbackforex.com/chart?s=XAU.USD-30m";
        System.out.println(url);

        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
            launchOptions.setHeadless(false);
                    //.setTimeout(30000);
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


            // Create a context with a custom user agent (Firefox user agent)
            Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Firefox/89.0 Safari/537.36")
                    .setLocale("en-US")
                    .setViewportSize(1280, 720)
                   // .setExtraHTTPHeaders(headers)
                    .setTimezoneId("America/New_York");
            BrowserContext context = browser.newContext(newContextOptions

            );


            // Create a new page within this context
            Page page = context.newPage();
            // 注入JS，隐藏 navigator.webdriver = true
            page.addInitScript("Object.defineProperty(navigator, 'webdriver', { get: () => undefined });");

            //  addstyle(page);

            //  disabFont(page);
            addWbsktMntr(page);


            try {
                page.navigate(url,
                        new Page.NavigateOptions()
                                .setTimeout(10000)
                                .setWaitUntil(WaitUntilState.NETWORKIDLE)); // 页面空闲时认为加载完成..
            } catch (Throwable e) {
                System.out.println("------------cat e:" + e.getMessage());

                e.printStackTrace();
                System.out.println("-----------end catex");
            }



            // Optional: print the title of the page
            System.out.println("Page title: " + page.title());
            // Wait for page content to load (adjust selector as needed)
            page.waitForSelector("h1");
            System.out.println("aftr h1 " + page.title());
            String ctnBtn="div.backdrop-continue";
            page.waitForSelector(ctnBtn);
            Locator btn = page.locator(ctnBtn);
            btn.click();

            // 2. Wait for the element to appear
            // 等待目标 div 加载（最多 60 秒）
            // 等待表格元素出现在页面中

          //  Locator element1 = page.locator("table.chart-markup-table");
           // element1.waitFor(new Locator.WaitForOptions().setTimeout(12 * 1000));
            String targtElmt="div#tv-chart-overlay";
            String selector = "table.chart-markup-table";
            Locator element1 = page.locator(targtElmt);

            Thread.sleep(9 * 1000); // 10 secon
            System.out.println("targe elmt showed");
            //  page.waitForFunction("() => document.querySelector('canvas') && document.querySelector('canvas').clientHeight > 0");


            // 3. Take screenshot of that element
            System.out.println("start   save pic...");
            String gldpic = "gld" + System.currentTimeMillis() + ".png";
            element1.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get(gldpic)));

            System.out.println("Screenshot saved as chart-element.png");


            // Keep browser open for a while to see the page
            Thread.sleep(3000 * 1000); // 10 seconds
            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addWbsktMntr(Page page) {
        page.onWebSocket(ws -> {
            System.out.println("✅ WebSocket 连接成功: " + ws.url());
        });
        page.onRequestFailed(request -> {
            if (request.url().startsWith("wss://")) {
                System.out.println("❌ WebSocket 连接失败: " + request.url());
            }
        });
    }


}