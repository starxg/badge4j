package com.starxg.badge4j;

import org.junit.Assert;
import org.junit.Test;

/**
 * BadgeTest
 * 
 * @author huangxingguang
 */
public class BadgeTest {
    @Test
    public void test() {
        Assert.assertTrue(Badge.create("&", "1234").contains("&amp;"));
        Assert.assertTrue(Badge.create("&", "1234", "red", "pink", null, 0, 5).contains("#E5B"));
    }

    @Test
    public void calcWidth() {
        Assert.assertEquals(Badge.calcWidth("test"), Badge.calcWidth("tset"), 0);
        Assert.assertEquals(Badge.calcWidth("你好"), Badge.calcWidth("好你"), 0);
    }
}