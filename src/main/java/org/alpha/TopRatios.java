package org.alpha;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class TopRatios {

    protected static void print(HashMap topRatios){
        for(Object key: topRatios.keySet()){
            System.out.println(key +" = "+topRatios.get(key));
        }
    }
    public static HashMap extractTopRatios(WebDriver driver){
        String name;
        String value;
        LinkedHashMap<String, String> topRatios = new LinkedHashMap<>();
        List<WebElement> tr_list = driver.findElement(By.id("top-ratios")).findElements(By.tagName("li"));
        for(WebElement tr_li: tr_list){
            name = tr_li.findElement(By.cssSelector(".name")).getText();
            value = tr_li.findElement(By.cssSelector(".nowrap")).getText();
            topRatios.put(name, value);
        }
        return topRatios;
    }
}
