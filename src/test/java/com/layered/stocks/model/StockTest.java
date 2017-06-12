package com.layered.stocks.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StockTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testHappyPathConstructor() {
        assertEquals("Should normalize stock symbols", "GOOG", new Stock("goog   ", "Google", "1234").getTicker());
        assertTrue("Should accept string value for price", new BigDecimal("1234.55").equals(new Stock("GOOG", "Google", "1234.55").getPrice()));
    }

    @Test
    public void testInvalidNumberForStringConstructor() {
        expected.expect(NumberFormatException.class);
        new Stock(null, null, "");
    }

    @Test
    public void testInvalidTicker() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Ticker is required!");
        new Stock(null, null, "1234");
    }

    @Test
    public void testInvalidTickerDescription() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Ticker is required!");
        new Stock("", null, "1234");
    }

    @Test
    public void testInvalidPrice() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Price must be greater than zero!");
        new Stock("GOOG", null, "-1234");
    }

}