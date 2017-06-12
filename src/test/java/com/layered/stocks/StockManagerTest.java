package com.layered.stocks;

import com.layered.stocks.model.Stock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class StockManagerTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();

    private StockManager unit = new StockManager();

    @Test
    public void testManagerHappyPath() {
        StockManager unit = new StockManager();

        // Set up stocks
        unit.buyStock(new Stock("AAPL", "Apple Computer", "117.10129"), 10);

        unit.buyStock(new Stock("GOOG", "Google", "530.8891"), 20);
        unit.buyStock(new Stock("GOOG", "Google", "529.1123"), 5);

        // Validate simple CRUD - remember "stock" represents a stock/price combination
        assertEquals("Should have stock of AAPL", 1, unit.findByTicker("aAPl").size());
        assertEquals("Should two stocks of GOOG", 2, unit.findByTicker("goog").size());
        assertEquals("Should zero of AMZN", 0, unit.findByTicker("AMZN").size());

        // Validate aggregating amounts
        assertEquals("Should have total of shares added together", new BigDecimal("13263.3435"), unit.getValueUnderManagerByTicker("Goog"));
        assertEquals("Should have total of shares added together", new BigDecimal("1171.0129"), unit.getValueUnderManagerByTicker("aapl"));
        assertEquals("Should have total of shares added together", new BigDecimal("0"), unit.getValueUnderManagerByTicker("amzn"));

        // Validate total stocks amounts
        assertEquals("Should have ten shares of AAPL", 10, unit.numberOfSharesByTicker("aAPl"));
        assertEquals("Should twenty-five shares of GOOG", 25, unit.numberOfSharesByTicker("goog"));
        assertEquals("Should zero shares of AMZN", 0, unit.numberOfSharesByTicker("AMZN"));

        // Validate selling stocks
        Optional<Set<BigDecimal>> result_sell = unit.sellStock("GOOG", 6, new BigDecimal("520.00"));
        assertTrue(result_sell.isPresent());
        assertTrue("Expected two prices for GOOG", result_sell.get().size() == 2);
        assertEquals("Should have sold some GOOG", 19, unit.numberOfSharesByTicker("goog  "));
        assertTrue("Should have sold cheapest ones first", new BigDecimal("10086.8929").equals(unit.getValueUnderManagerByTicker("  goog ")));

        // Test p&l
        Optional<Set<BigDecimal>> result_sell_loss = unit.sellStock("aapl ", 1, new BigDecimal("100.00"));
        assertTrue(result_sell_loss.isPresent());
        assertTrue("should reflect loss", new BigDecimal("-17.10129").equals(unit.getProfitForStockByTicker("aapl").get()));

        // These stocks do not exist in management
        assertFalse(unit.sellStock("AMZN", 10, new BigDecimal("100.00")).isPresent());
        assertFalse(unit.getProfitForStockByTicker("AMZN").isPresent());
    }

    @Test
    public void testInvalidBuyStockWithZeroShares() {
        expected.expect(IllegalArgumentException.class);
        unit.buyStock(new Stock("AMZN", "Test", "1234"), 0);
    }

    @Test
    public void testInvalidBuyStockWithNegativeShares() {
        expected.expect(IllegalArgumentException.class);
        unit.buyStock(new Stock("AMZN", "Test", "1234"), -1);
    }

    @Test
    public void testInvalidSellStockWithZeroShares() {
        expected.expect(IllegalArgumentException.class);
        unit.sellStock("AMZN", 0, new BigDecimal("100.00"));
    }

    @Test
    public void testInvalidSellStockWithNegativeShares() {
        expected.expect(IllegalArgumentException.class);
        unit.sellStock("AMZN", -1, new BigDecimal("100.00"));
    }

}
