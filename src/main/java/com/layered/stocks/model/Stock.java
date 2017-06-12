package com.layered.stocks.model;

import java.math.BigDecimal;
import java.util.StringJoiner;

/**
 *  The Stock class represents a particular stock/price combination for the specified ticker
 */
public class Stock {
    public final String ticker;
    public final String description;
    public final BigDecimal price;

    /**
     * Create a new one.
     * <p>
     * Will throw a {@link java.lang.IllegalArgumentException} if fields are invalid.
     */
    public Stock(String ticker, String description, BigDecimal price) {
        if (ticker == null || "".equals(ticker)) {
            throw new IllegalArgumentException("Ticker is required!");
        } else if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be greater than zero!");
        }

        this.ticker = ticker.replaceAll("\\s", "").toUpperCase(); // Ticker should have no white space
        this.description = description;
        this.price = price;
    }

    /**
     * Create a new one using a String as the amount.
     * <p>
     * Will throw a {@link java.lang.NumberFormatException} if amount is an invalid numeric value.
     */
    public Stock(String ticker, String description, String price) {
        this(ticker, description, new BigDecimal(price));
    }

    /**
     * Get the ticker symbol.
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * Get the stock description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the amount.
     */
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
            .add("description = " + description)
            .add("price = " + price)
            .add("ticker = " + ticker)
            .toString();
    }
}
