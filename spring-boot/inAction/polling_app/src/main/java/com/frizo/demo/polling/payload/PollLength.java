package com.frizo.demo.polling.payload;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class PollLength {
    @NotNull
    @Max(7)
    private int days;

    @NotNull
    @Max(23)
    private int hours;

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}
