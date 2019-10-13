package com.example.moviediscovery;

/**
 * Singleton that contains a range of dates
 */
public class DateRange {
    private String startDate;
    private String endDate;
    private static DateRange instance;

    /**
     * Access point to this class
     *
     * @return This instance or a new one if one does not exist
     */
    public static DateRange getInstance() {
        if (instance == null)
            instance = new DateRange();

        return instance;
    }

    private DateRange() {
    }


    // ACCESSORS AND MUTATORS


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
