package com.travelplanner.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for price calculations, comparisons, and formatting.
 * Handles price alerts, discount calculations, and price statistics.
 */
@Slf4j
@Component
public class PriceCalculatorUtil {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Format price to 2 decimal places.
     *
     * @param price the price to format
     * @return formatted price with 2 decimal places
     */
    public BigDecimal formatPrice(BigDecimal price) {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        return price.setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * Format price to currency string.
     *
     * @param price the price to format
     * @param currency the currency code (e.g., "USD", "EUR")
     * @return formatted price string
     */
    public String formatPriceString(BigDecimal price, String currency) {
        if (price == null) {
            return "0.00 " + (currency != null ? currency : "");
        }
        BigDecimal formatted = formatPrice(price);
        return String.format("%s %s", formatted, currency != null ? currency : "");
    }

    /**
     * Calculate percentage change between old and new price.
     *
     * @param oldPrice the original price
     * @param newPrice the new price
     * @return percentage change (positive = increase, negative = decrease)
     */
    public BigDecimal calculatePercentageChange(BigDecimal oldPrice, BigDecimal newPrice) {
        if (oldPrice == null || newPrice == null || oldPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal difference = newPrice.subtract(oldPrice);
        BigDecimal percentage = difference.divide(oldPrice, SCALE + 2, ROUNDING_MODE)
                .multiply(new BigDecimal("100"));

        return formatPrice(percentage);
    }

    /**
     * Calculate discount percentage.
     *
     * @param originalPrice the original price
     * @param discountedPrice the discounted price
     * @return discount percentage
     */
    public BigDecimal calculateDiscount(BigDecimal originalPrice, BigDecimal discountedPrice) {
        if (originalPrice == null || discountedPrice == null || 
            originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = originalPrice.subtract(discountedPrice);
        BigDecimal percentage = discount.divide(originalPrice, SCALE + 2, ROUNDING_MODE)
                .multiply(new BigDecimal("100"));

        return formatPrice(percentage);
    }

    /**
     * Check if current price meets alert threshold.
     * Alert triggered when: currentPrice <= targetPrice
     *
     * @param currentPrice the current price
     * @param targetPrice the target/threshold price for alert
     * @return true if alert should be triggered, false otherwise
     */
    public boolean shouldTriggerAlert(BigDecimal currentPrice, BigDecimal targetPrice) {
        if (currentPrice == null || targetPrice == null) {
            return false;
        }
        return currentPrice.compareTo(targetPrice) <= 0;
    }

    /**
     * Calculate average price from a list of prices.
     *
     * @param prices list of prices to average
     * @return average price, or ZERO if list is empty
     */
    public BigDecimal calculateAveragePrice(List<BigDecimal> prices) {
        if (prices == null || prices.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = prices.stream()
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = sum.divide(
                new BigDecimal(prices.size()),
                SCALE + 2,
                ROUNDING_MODE
        );

        return formatPrice(average);
    }

    /**
     * Get minimum price from a list of prices.
     *
     * @param prices list of prices
     * @return minimum price, or empty if list is empty
     */
    public Optional<BigDecimal> getMinPrice(List<BigDecimal> prices) {
        if (prices == null || prices.isEmpty()) {
            return Optional.empty();
        }

        return prices.stream()
                .filter(p -> p != null)
                .min(BigDecimal::compareTo);
    }

    /**
     * Get maximum price from a list of prices.
     *
     * @param prices list of prices
     * @return maximum price, or empty if list is empty
     */
    public Optional<BigDecimal> getMaxPrice(List<BigDecimal> prices) {
        if (prices == null || prices.isEmpty()) {
            return Optional.empty();
        }

        return prices.stream()
                .filter(p -> p != null)
                .max(BigDecimal::compareTo);
    }

    /**
     * Calculate price range.
     *
     * @param prices list of prices
     * @return price range as string (e.g., "$100.00 - $500.00")
     */
    public String getPriceRange(List<BigDecimal> prices, String currency) {
        Optional<BigDecimal> min = getMinPrice(prices);
        Optional<BigDecimal> max = getMaxPrice(prices);

        if (min.isEmpty() || max.isEmpty()) {
            return "N/A";
        }

        return String.format("%s - %s",
                formatPriceString(min.get(), currency),
                formatPriceString(max.get(), currency));
    }

    /**
     * Round up to nearest value.
     *
     * @param price the price to round
     * @param roundTo the value to round to (e.g., 0.10 for nearest dime)
     * @return rounded price
     */
    public BigDecimal roundUpToNearest(BigDecimal price, BigDecimal roundTo) {
        if (price == null || roundTo == null || roundTo.compareTo(BigDecimal.ZERO) == 0) {
            return formatPrice(price);
        }

        BigDecimal divided = price.divide(roundTo, SCALE + 2, RoundingMode.UP);
        return divided.multiply(roundTo).setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * Apply percentage markup/discount to a price.
     *
     * @param price the base price
     * @param percentageChange percentage change (positive = markup, negative = discount)
     * @return new price after percentage change
     */
    public BigDecimal applyPercentageChange(BigDecimal price, BigDecimal percentageChange) {
        if (price == null || percentageChange == null) {
            return formatPrice(price);
        }

        BigDecimal multiplier = BigDecimal.ONE.add(percentageChange.divide(new BigDecimal("100"), SCALE + 2, ROUNDING_MODE));
        return formatPrice(price.multiply(multiplier));
    }

    /**
     * Compare two prices and return comparison result.
     *
     * @param price1 first price
     * @param price2 second price
     * @return -1 if price1 < price2, 0 if equal, 1 if price1 > price2
     */
    public int comparePrice(BigDecimal price1, BigDecimal price2) {
        if (price1 == null || price2 == null) {
            return 0;
        }
        return price1.compareTo(price2);
    }
}
