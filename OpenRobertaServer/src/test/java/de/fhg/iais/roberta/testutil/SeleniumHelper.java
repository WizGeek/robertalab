package de.fhg.iais.roberta.testutil;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.hibernate.Session;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.iais.roberta.main.ServerStarter;
import de.fhg.iais.roberta.persistence.util.DbSetup;
import de.fhg.iais.roberta.persistence.util.SessionFactoryWrapper;
import de.fhg.iais.roberta.util.Util1;

public class SeleniumHelper {
    private static final Logger LOG = LoggerFactory.getLogger(SeleniumHelper.class);

    public int port;
    public final ServerStarter serverStarter;
    public final Server server;
    public WebDriver driver;
    public final String baseUrl;
    public boolean browserVisibility;

    public SeleniumHelper(String baseUrl) throws Exception {
        Properties properties = Util1.loadProperties("classpath:openRoberta.properties");
        this.browserVisibility = Boolean.parseBoolean(properties.getProperty("browser.visibility"));
        this.serverStarter = new ServerStarter("classpath:openRoberta.properties");
        this.server = this.serverStarter.start("localhost", 1998);
        Session session = this.serverStarter.getInjectorForTests().getInstance(SessionFactoryWrapper.class).getNativeSession();
        new DbSetup(session).runDefaultRobertaSetup();
        this.driver = SeleniumHelper.runBrowser(this.browserVisibility);
        this.port = this.server.getURI().getPort();
        this.baseUrl = "http://localhost:" + this.port + "/" + baseUrl + "/";
        this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        this.driver.get(this.baseUrl);
    }

    public void click(Button b) {
        this.driver.findElement(By.id(b.toString())).click();
    }

    public void expectError() {
        awaitTextReadyInElementReady();
        Assert.assertEquals("error", this.driver.findElement(By.id("result")).getText());
    }

    public void expectSuccess() {
        awaitTextReadyInElementReady();
        Assert.assertEquals("asExpected", this.driver.findElement(By.id("result")).getText());
    }

    public void tearDown() throws Exception {
        this.driver.quit();
        this.server.stop();
    }

    public void assertText(String text, By id) {
        Assert.assertEquals(text, this.driver.findElement(id).getText());
    }

    private void awaitTextReadyInElementReady() {
        for ( int second = 0;; second++ ) {
            if ( second >= 60 ) {
                Assert.fail("timeout");
            }
            try {
                if ( "ready".equals(this.driver.findElement(By.id("ready")).getText()) ) {
                    break;
                }
            } catch ( Exception e ) {
            }
            try {
                Thread.sleep(1000);
            } catch ( InterruptedException e ) {
                // OK
            }
        }
    }

    public static enum Button {
        B1, B2, B3, B4, B5, B6, B7, B8, B9, B10;
    }

    public static WebDriver runBrowser(boolean browserVisibility) {
        WebDriver driver;
        if ( browserVisibility ) {
            LOG.info("browserVisibility: true");
            FirefoxProfile fp = new FirefoxProfile();
            fp.setEnableNativeEvents(false);
            fp.setPreference("xpinstall.signatures.required", false);
            driver = new FirefoxDriver(fp);
            driver.manage().window().maximize();
        } else {
            String phantomjsBinaryPath = System.getProperty("phantomjs.binary");
            LOG.info("browserVisibility: false; phantomjsBinaryPath: " + phantomjsBinaryPath);
            DesiredCapabilities caps = DesiredCapabilities.firefox();
            caps.setCapability("nativeEvents", false);
            caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjsBinaryPath);
            driver = new PhantomJSDriver(caps);
            driver.manage().window().setSize(new Dimension(1920, 1080));
        }
        return driver;
    }
}
