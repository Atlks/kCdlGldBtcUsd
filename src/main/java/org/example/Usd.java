package org.example;

import com.microsoft.playwright.*;

import java.nio.file.Paths;

public class Usd {
//https://cn.investing.com/currencies/usd-cny-candlestick
public static void main(String[] args) {
    try (Playwright playwright = Playwright.create()) {
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setTimeout(60000));

        // Create a context with a custom user agent (Firefox user agent)
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Firefox/89.0 Safari/537.36")
        );

        // Create a new page within this context
        Page page = context.newPage();

        //
        // https://coinmarketcap.com/currencies/bitcoin/            // Open the CoinMarketCap website
        page.navigate("https://cn.investing.com/currencies/usd-cny-candlestick");

        // Wait for the main element to load
//            page.waitForSelector("div[class*='cmc-homepage']");

        // Optional: print the title of the page
        System.out.println("Page title: " + page.title());
        // Wait for page content to load (adjust selector as needed)
        page.waitForSelector("h1");

        // Example: Click the second image in a list (adjust if needed)
        // This uses a general querySelector format. Adjust as necessary based on the site's structure.

        // Click the tab with data-index="tab-Candle"
        // Wait for the tab to be available and click it

        // 2. Wait for the element to appear
        Locator element = page.locator("div.stx-holder.stx-panel-chart");
        element.waitFor();

        // 3. Take screenshot of that element
        String gldpic = "usd" + System.currentTimeMillis() + ".png";
        element.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get(gldpic)));

        System.out.println("Screenshot saved as chart-element.png");


        // Keep browser open for a while to see the page
        Thread.sleep(3*1000); // 10 seconds
        browser.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}


}
