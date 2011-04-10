/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.applib.value;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.Period;

import org.apache.isis.applib.Defaults;
import org.apache.isis.applib.annotation.Value;
import org.apache.isis.applib.clock.Clock;

/**
 * Value object representing a date and time value. By default, the time is initialised to the current time, unless
 * otherwise specified.
 */
@Value(semanticsProviderName = "org.apache.isis.core.progmodel.facets.value.datetime.DateTimeValueSemanticsProvider")
public class DateTime extends Magnitude<DateTime> {

    private static final long serialVersionUID = 1L;
    private final org.joda.time.DateTime dateTime;

    /**
     * Create a Time object for storing a timeStamp set to the current time.
     */
    public DateTime() {
        org.joda.time.DateTime d = new org.joda.time.DateTime(Clock.getTime(), Defaults.getApplibTimeZone());
        // dateTime = d.secondOfMinute().setCopy(0);
        dateTime = d;
    }

    /**
     * Create a DateTime from the provided java.util.Date, assuming that the date is in UTC. If not, see {@link
     * DateTime#}
     * 
     * @param date
     */
    public DateTime(final Date date) {
        this.dateTime = new org.joda.time.DateTime(date, Defaults.getApplibTimeZone());
    }

    public DateTime(final long millisSinceEpoch) {
        this.dateTime = new org.joda.time.DateTime(millisSinceEpoch, Defaults.getApplibTimeZone());
    }

    public DateTime(final Date date, TimeZone timeZone) {
        DateTimeZone tz = DateTimeZone.forTimeZone(timeZone);
        this.dateTime = new org.joda.time.DateTime(date, tz);
    }

    public DateTime(final org.joda.time.DateTime dateTime) {
        this.dateTime = new org.joda.time.DateTime(dateTime);
    }

    /**
     * Creates a DateTime on the specified day/month/year, with the current time
     */
    public DateTime(final int year, final int month, final int day) {
        Time time = new Time();
        // this(year, month, day, time.getHour(), time.getMinute(), 0);

        // replace below with something more like the above..
        checkTime(year, month, day, 0, 0);
        dateTime =
            new org.joda.time.DateTime(year, month, day, time.getHour(), time.getMinute(), time.getSecond(), 0,
                Defaults.getApplibTimeZone());
    }

    /**
     * Create a Date object set to the specified day, month, year, hour, minute.
     */
    public DateTime(final int year, final int month, final int day, final int hour, final int minute) {
        this(year, month, day, hour, minute, 0);
    }

    /**
     * Create a Date object set to the specified day, month, year, hour, minute, and second.
     */
    public DateTime(final int year, final int month, final int day, final int hour, final int minute, final int second) {
        checkTime(year, month, day, hour, minute);
        dateTime = new org.joda.time.DateTime(year, month, day, hour, minute, second, 0, Defaults.getApplibTimeZone());
    }

    private void checkTime(final int year, final int month, final int day, final int hour, final int minute) {
        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Month must be in the range 1 - 12 inclusive " + month);
        }

        org.joda.time.DateTime dateTime = new org.joda.time.DateTime(year, month, 1, 0, 0, 0, 0);
        final int lastDayOfMonth = dateTime.dayOfMonth().getMaximumValue();

        if ((day < 1) || (day > lastDayOfMonth)) {
            throw new IllegalArgumentException("Day must be in the range 1 - " + lastDayOfMonth + " inclusive " + day);
        }

        if ((hour < 0) || (hour > 23)) {
            throw new IllegalArgumentException("Hour must be in the range 0 - 23 inclusive " + hour);
        }

        if ((minute < 0) || (minute > 59)) {
            throw new IllegalArgumentException("Minute must be in the range 0 - 59 inclusive " + minute);
        }
    }

    /**
     * Add the specified time period to this date value.
     */
    public DateTime add(final int years, final int months, final int days, final int hours, final int minutes) {
        Period period = new Period(years, months, 0, days, hours, minutes, 0, 0);
        org.joda.time.DateTime dateTime = this.dateTime.plus(period);
        return new DateTime(dateTime);
    }

    /**
     * Add the specified days, years and months to this date value.
     */
    public DateTime add(int years, int months, int days) {
        return add(years, months, days, 0, 0);
    }

    public Calendar calendarValue() {
        return dateTime.toGregorianCalendar();
    }

    protected DateTime createDateTime(final Date date) {
        return new DateTime(date);
    }

    /**
     * Be careful - the TimeZone of the java.util.Date is based on the system default.
     */
    public java.util.Date dateValue() {
        return new Date(dateTime.getMillis());
    }

    public int getSeconds() {
        return dateTime.getSecondOfMinute();
    }

    public int getMinute() {
        return dateTime.getMinuteOfHour();
    }

    public int getHour() {
        return dateTime.getHourOfDay();
    }

    public int getDay() {
        return dateTime.getDayOfMonth();
    }

    public int getMonth() {
        return dateTime.getMonthOfYear();
    }

    public int getYear() {
        return dateTime.getYear();
    }

    /**
     * A DateTime that is at the start of the current week. Time is preserved.
     */
    public DateTime startOfWeek() {
        return new DateTime(dateTime.withDayOfWeek(1));
    }

    /**
     * A DateTime that represents the start of the current month. Time is preserved.
     */
    public DateTime startOfMonth() {
        return new DateTime(dateTime.withDayOfMonth(1));
    }

    /**
     * This DateTime, but on the first day of the year. Time is preserved.
     */
    public DateTime startOfYear() {
        return new DateTime(dateTime.withDayOfYear(1));
    }

    /**
     * Day of year (1 to 365 [366 in leap years]) for Gregorian calendar.
     * 
     * @return
     */
    public int getDayOfYear() {
        return dateTime.getDayOfYear();
    }

    /**
     * returns true if the time stamp of this object has the same value as the specified time
     */
    @Override
    public boolean isEqualTo(final DateTime timeStamp) {
        return this.dateTime.equals((timeStamp).dateTime);
    }

    /**
     * returns true if the timeStamp of this object is earlier than the specified timeStamp
     */
    @Override
    public boolean isLessThan(final DateTime timeStamp) {
        return dateTime.isBefore((timeStamp).dateTime);
    }

    public boolean isSameDayAs(DateTime dateTime2) {
        return dateTime2 == null ? false : getDayOfYear() == dateTime2.getDayOfYear();
    }

    public boolean sameDayOfWeekAs(DateTime dateTime2) {
        return dateTime2 == null ? false : dateTime.getDayOfWeek() == dateTime2.dateTime.getDayOfWeek();
    }

    public boolean sameDayOfMonthAs(DateTime dateTime2) {
        return dateTime2 == null ? false : dateTime.getDayOfMonth() == dateTime2.dateTime.getDayOfMonth();
    }

    public boolean sameDayOfYearAs(DateTime dateTime2) {
        return dateTime2 == null ? false : dateTime.getDayOfYear() == dateTime2.dateTime.getDayOfYear();
    }

    public boolean sameWeekAs(DateTime dateTime2) {
        return dateTime2 == null ? false : dateTime.getWeekOfWeekyear() == dateTime2.dateTime.getWeekOfWeekyear();
    }

    public boolean sameMonthAs(DateTime dateTime2) {
        return dateTime2 == null ? false : getMonth() == dateTime2.getMonth();
    }

    public boolean sameYearAs(DateTime dateTime2) {
        return dateTime2 == null ? false : getYear() == dateTime2.getYear();
    }

    @Deprecated
    /**
     * See millisSinceEpoch() 
     */
    public long longValue() {
        return millisSinceEpoch();
    }

    /**
     * Gets the milliseconds since the Java epoch of 1970-01-01T00:00:00Z
     */
    public long millisSinceEpoch() {
        return dateTime.getMillis();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DateTime other = (DateTime) obj;
        if (dateTime == null) {
            if (other.dateTime != null) {
                return false;
            }
        } else if (!dateTime.equals(other.dateTime)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
        return result;
    }

    @Override
    public String toString() {
        // return getYear() + "-" + getMonth() + "-" + getDay() + " " + getHour() + ":" + getMinute();
        return String.format("%04d-%02d-%02d %02d:%02d", getYear(), getMonth(), getDay(), getHour(), getMinute());
    }

}
