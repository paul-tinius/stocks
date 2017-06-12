package com.layered.stocks;

import com.layered.stocks.model.Stock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class StockManager {

    // i10n/i18n?
    private static final String TICKER_NOT_NULL = "The specified stock ticker must not be null.";
    private static final String STOCK_NOT_NULL = "The specified stock must not be null.";
    private static final String SHARES_MUST_BE_GREATER_ZERO = "The specified number of shares must greater then zero.";
    private static final String SHARE_PRICE_NOT_NULL = "The specified share price must not be null.";

    private final Map<String,List<Stock>> stocks = new ConcurrentHashMap<>();
    private final Map<String,List<Integer>> shares = new ConcurrentHashMap<>();
    private final Map<String,List<BigDecimal>> profits = new ConcurrentHashMap<>();
    private final Map<String,List<BigDecimal>> losses = new ConcurrentHashMap<>();

    /**
     * For a given ticker symbol, add up the total stock values under management and return.
     */
    public BigDecimal getValueUnderManagerByTicker(final String ticker) {
        Optional<BigDecimal> value = stocks.getOrDefault(normalizeKey(ticker),new ArrayList<>())
                                           .stream()
                                           .map(s ->
                                                {
                                                    final List<Integer> sharesList = shares.getOrDefault(generateShareKey(s),
                                                                                                         new ArrayList<>() );
                                                    if(!sharesList.isEmpty()) {
                                                        return s.getPrice().multiply(BigDecimal.valueOf(sharesList.get(0)));
                                                    }
                                                    return s.getPrice().multiply(BigDecimal.valueOf(1));
                                                }
                                           ).reduce(BigDecimal::add);

        return value.map(BigDecimal::stripTrailingZeros).orElse(BigDecimal.ZERO);
    }

    /**
     * Get the list of "stocks" currently under management by its ticker symbol.
     * "Stock" in this case represents a stock/price combination that was purchased,
     * i.e. you can buy the same stock at different shares, and each of those is
     * represented by a "stock"
     */
    public List<Stock> findByTicker(final String ticker) {
        return stocks.getOrDefault(normalizeKey(ticker), new ArrayList<>());
    }

    /**
     * For a given ticker symbol, get the number of shares of stock under management.
     */
    public int numberOfSharesByTicker(final String ticker) {
        return findByTicker(ticker).stream()
                                   .map(s -> shares.getOrDefault(generateShareKey(s), new ArrayList<>()))
                                   .mapToInt(i ->
                                             {
                                                 if(!i.isEmpty()) {
                                                     return i.get(0);
                                                 }
                                                 return 0;
                                             })
                                   .sum();
    }

    /**
     * Add a new stock to be managed by our system (here 'buy' is equal to 'add').
     *
     * @param stock - The {@link com.layered.stocks.model.Stock} to buy
     * @param numberOfShares - The number of shares to purchase
     *
     * @throws java.lang.IllegalArgumentException if numberOfShares is <= 0
     */
    @SuppressWarnings("Duplicates")
    public void buyStock(final Stock stock, final int numberOfShares) {
        Objects.requireNonNull(stock, STOCK_NOT_NULL);
        if (numberOfShares <= 0) {
            throw new IllegalArgumentException(SHARES_MUST_BE_GREATER_ZERO);
        }

        final String key = normalizeKey(stock.getTicker());
        final String shareKey = generateShareKey(stock);
        final List<Stock> tmpStock = stocks.getOrDefault(key, new ArrayList<>());
        final List<Integer> tmpShares = shares.getOrDefault(shareKey, new ArrayList<>());
        final List<BigDecimal> tmpPandL = new ArrayList<>();
        tmpPandL.add(BigDecimal.ZERO);

        if(tmpStock.isEmpty()) {
            tmpStock.add(stock);
            stocks.put(key,tmpStock);
        } else {
            tmpStock.add(stock);
            stocks.replace(key,tmpStock);
        }

        profits.put(key, tmpPandL);
        losses.put(key, tmpPandL);

        if(tmpShares.isEmpty()) {
            tmpShares.add(numberOfShares);
            shares.put(shareKey,tmpShares);
        } else {
            tmpShares.add(numberOfShares);
            shares.replace(shareKey, tmpShares);
        }
    }

    /**
     * For a given ticker, sell the stock.
     * <p>
     * This method will attempt to sell stock starting with the lowest price.
     *
     * @param ticker - The stock ticker to sell
     * @param numberOfShares - The number of shares to sell
     * @param sharePrice - The price we are selling numberOfShares
     *
     * @return A list of the stock shares that were sold sorted from most to least expensive
     *
     * @throws java.lang.IllegalArgumentException if numberOfShares is <= 0
     */
    public Optional<Set<BigDecimal>> sellStock(final String ticker, final int numberOfShares, final BigDecimal sharePrice) {
        Objects.requireNonNull(sharePrice, SHARE_PRICE_NOT_NULL);
        if (numberOfShares <= 0) {
            throw new IllegalArgumentException(SHARES_MUST_BE_GREATER_ZERO);
        }

        final Set<BigDecimal> results = new HashSet<>();

        final List<Stock> list = findByTicker(ticker);
        list.sort(Comparator.comparing(Stock::getPrice));
        final List<Integer> leftOvers = new ArrayList<>();
        final List<Stock> exhausted = new ArrayList<>();
        int filled = 0;

        for(final Stock stock : list) {
            for(final Integer owned : shares.getOrDefault(generateShareKey(stock),new ArrayList<>())) {
                if(filled == numberOfShares) {
                    break;
                }

                final boolean isLoss = sharePrice.compareTo(stock.getPrice()) < 0;
                final int stillNeeded = (numberOfShares - filled);
                if(owned <= stillNeeded) {
                    filled += owned;
                    results.add(stock.getPrice());
                    exhausted.add(stock);
                } else {
                    int left = owned - stillNeeded;
                    filled += stillNeeded;
                    leftOvers.add(left);
                    results.add(stock.getPrice());
                }

                applyProfitLoss(isLoss,stock,sharePrice,stillNeeded);
            }

            if(!leftOvers.isEmpty()) {
                shares.replace(generateShareKey(stock), leftOvers);
            }
        }

        applyExhausted(exhausted);

        return !results.isEmpty() ? Optional.of(results) : Optional.empty();
    }

    /**
     * For a given stock ticker, get the P&L.
     *
     * @return The total profits made so far or an empty option if the stock is not under management
     */
    public Optional<BigDecimal> getProfitForStockByTicker(final String ticker) {
        List<BigDecimal> profitLoss = new ArrayList<>();
        if(!profits.isEmpty()) {
            profitLoss = profits.getOrDefault(normalizeKey(ticker), new ArrayList<>());
        } else if(!losses.isEmpty()) {
            profitLoss = losses.getOrDefault(normalizeKey(ticker), new ArrayList<>());
        }

        if(profitLoss.isEmpty()) {
            return Optional.empty();
        }

        return profitLoss.stream().reduce(BigDecimal::add);
    }

    private String normalizeKey(final String ticker) {
        Objects.requireNonNull(ticker, TICKER_NOT_NULL);
        return ticker.replaceAll("\\s", "").toUpperCase();
    }

    private String generateShareKey(final Stock stock) {
        Objects.requireNonNull(stock, STOCK_NOT_NULL);
        return String.format("%s:%s",
                             normalizeKey(stock.getTicker()),
                             stock.getPrice().toString().replaceAll("\\.","_"));
    }

    private void applyProfitLoss(final boolean isLoss, final Stock stock, final BigDecimal sharePrice, final int stillNeeded) {
        if(isLoss) {
            final BigDecimal sellPrice = sharePrice.subtract(stock.getPrice());
            losses.get(normalizeKey(stock.getTicker()))
                  .add(sellPrice.multiply(new BigDecimal(stillNeeded)));
        } else {
            profits.get(normalizeKey(stock.getTicker()))
                   .add(sharePrice.multiply(new BigDecimal(stillNeeded)));
        }
    }
    private void applyExhausted(final List<Stock> exhausted) {
        // remove zero quantity
        if(!exhausted.isEmpty()) {
            exhausted.forEach(s ->
                              {
                                  final List<Stock> modified =
                                      findByTicker(normalizeKey(s.getTicker())).stream()
                                                                               .filter(es -> !es.getPrice()
                                                                                                .equals(s.getPrice()))
                                                                               .collect(Collectors.toList());

                                  stocks.replace(normalizeKey(s.getTicker()), modified);
                              });
        }
    }
}
