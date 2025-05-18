package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;

import java.nio.file.Paths;
import java.util.Arrays;

import static org.example.BotBroswer.*;
import static org.example.OpenCoinMarketCap.*;

public class Gld2 {
    public static void main(String[] args) {
        String url = "https://www.cashbackforex.com/chart?s=XAU.USD-30m";
        System.out.println(url);

        try (Playwright playwright = Playwright.create()) {

            Browser browser = getBrowser4canvas();

            Browser.NewContextOptions options = getNewContextOptions4mobileSite();

        //  BrowserContext context = browser.newContext(options);
            //dis font media,can fast scrsht
           BrowserContext context =   getBrowserContextFastOptmz(browser);

            // Create a new page within this context
            Page page = context.newPage();
            // 注入JS，隐藏 navigator.webdriver = true
            page.addInitScript("Object.defineProperty(navigator, 'webdriver', { get: () => undefined });");

            //  addstyle(page);

            //  disabFont(page);
            addWbsktMntr(page);

            nvgt(url,page);
//            try {
//                page.navigate(url,
//                        new Page.NavigateOptions()
//                                .setTimeout(10000)
//                                .setWaitUntil(WaitUntilState.NETWORKIDLE)); // 页面空闲时认为加载完成..
//            } catch (Throwable e) {
//                System.out.println("------------cat e:" + e.getMessage());
//
//                e.printStackTrace();
//                System.out.println("-----------end catex");
//            }


            // Optional: print the title of the page
            System.out.println("Page title: " + page.title());
            // Wait for page content to load (adjust selector as needed)
            page.waitForSelector("h1");
            System.out.println("aftr h1 " + page.title());
            String ctnBtn = "div.backdrop-continue";
            page.waitForSelector(ctnBtn);
            Locator btn = page.locator(ctnBtn);
           // btn.waitFor();
            btn.click();

            // 2. Wait for the element to appear
            // 等待目标 div 加载（最多 60 秒）
            // 等待表格元素出现在页面中

            //  Locator element1 = page.locator("table.chart-markup-table");
            // element1.waitFor(new Locator.WaitForOptions().setTimeout(12 * 1000));
         //   String targtElmt = "div#tv-chart-overlay";
            String   targtElmt = "table.chart-markup-table";

            page.evaluate("() => {" +
                    "  const el = document.querySelector('table.chart-markup-table');" +
                    "  if (el) el.scrollIntoView();" +
                    "}");
            Thread.sleep(300);
            letVisab(page);

            //  targtElmt="div.chart-gui-wrapper";
          //  targtElmt="div.chart-container-border";
            Locator element1 = page.locator(targtElmt);
            System.out.println("element1.isVisible:"+element1.isVisible());
           // element1.waitFor(new Locator.WaitForOptions().setTimeout(9 * 1000));
           Thread.sleep(3 * 1000); // 10 secon
            System.out.println("targe elmt showed");
            //  page.waitForFunction("() => document.querySelector('canvas') && document.querySelector('canvas').clientHeight > 0");


            // 3. Take screenshot of that element
            System.out.println("start   save pic...");
            mkdir("pics");
            String gldpic = "pics/cmdt" + System.currentTimeMillis() + ".png";
            element1.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get(gldpic)));

        //    scrshtByElmtHdlr(page, gldpic);

            System.out.println("Screenshot saved as chart-element.png");


            // Keep browser open for a while to see the page
            Thread.sleep(3000 * 1000); // 10 seconds

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void letVisab(Page page) {
        page.evaluate("() => {" +
                "  const el = document.querySelector('table.chart-markup-table');" +
                "  if (el) {" +
                "    el.style.display = 'block';" +       // 取消 display:none
                "    el.style.visibility = 'visible';" +  // 取消 visibility:hidden
                "    el.style.opacity = '1';" +           // 取消透明
                "  }" +
                "}");

        page.evaluate("() => {" +
                "  const el = document.querySelector('table.chart-markup-table');" +
                "  if (el) el.scrollIntoView();" +
                "}");
    }

    /**
     * Playwright 的 locator.screenshot() 默认会等待元素：
     *
     * 可见 (visible)
     *
     * 所有字体加载完毕
     *
     * 渲染完成
     *
     * scrshtByElmtHdlr 会跳过
     * @param page
     * @param gldpic
     */
    private static void scrshtByElmtHdlr(Page page, String gldpic) {
        ElementHandle element = page.querySelector("table.chart-markup-table");
        if (element != null) {
            element.screenshot(new ElementHandle.ScreenshotOptions().setPath(Paths.get(gldpic)));
        } else {
            System.out.println("元素未找到，跳过截图");
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