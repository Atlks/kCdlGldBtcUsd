package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

//https://cn.investing.com/commodities/gold
public class Gld {

    public static void main(String[] args) {
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

            // Create a context with a custom user agent (Firefox user agent)
            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Firefox/89.0 Safari/537.36")
                    .setLocale("en-US")
                    .setViewportSize(1280, 720)
                    .setTimezoneId("America/New_York")

            );


            // Create a new page within this context
            Page page = context.newPage();
            // 注入JS，隐藏 navigator.webdriver = true
            page.addInitScript("Object.defineProperty(navigator, 'webdriver', { get: () => undefined });");


            //
            // https://coinmarketcap.com/currencies/bitcoin/            // Open the CoinMarketCap website
            String url = "https://cn.investing.com/commodities/gold";
            url="https://cn.investing.com/currencies/xau-usd-candlestick";
            System.out.println(url);
          //  page.navigate(url);
            page.navigate("https://cn.investing.com/currencies/usd-cny-candlestick",
                    new Page.NavigateOptions()
                            .setTimeout(60000)
                            .setWaitUntil(WaitUntilState.NETWORKIDLE)); // 页面空闲时认为加载完成

            //https://cn.investing.com/currencies/xau-usd-candlestick

            // Wait for the main element to load
//            page.waitForSelector("div[class*='cmc-homepage']");

            // Optional: print the title of the page
            System.out.println("Page title: " + page.title());
            // Wait for page content to load (adjust selector as needed)
            page.waitForSelector("h1");
            System.out.println("aftr h1 " + page.title());

            randomDelay();
            page.mouse().move(200, 300);
            page.mouse().wheel(0, 500);  // 模拟滚动
            randomDelay();
            // Example: Click the second image in a list (adjust if needed)
            // This uses a general querySelector format. Adjust as necessary based on the site's structure.

            // Click the tab with data-index="tab-Candle"
            // Wait for the tab to be available and click it

            // 2. Wait for the element to appear
            Locator element = page.locator("div.stx-holder.stx-panel-chart");
            element.waitFor();

            page.waitForFunction("() => document.querySelector('canvas') && document.querySelector('canvas').clientHeight > 0");


            // 3. Take screenshot of that element
            String gldpic = "gld" + System.currentTimeMillis() + ".png";
            element.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get(gldpic)));

            System.out.println("Screenshot saved as chart-element.png");


            // Keep browser open for a while to see the page
            Thread.sleep(3 * 1000); // 10 seconds
            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
