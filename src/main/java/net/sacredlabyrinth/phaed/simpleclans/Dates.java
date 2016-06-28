package net.sacredlabyrinth.phaed.simpleclans;

public class Dates {
	
	private Dates() {}

	public static double differenceInDays(long date1, long date2) {
	    return differenceInHours( date1, date2 ) / 24;
	}
    public static double differenceInHours(long date1, long date2) {
        return differenceInMinutes(date1, date2) / 60.0;
    }
    public static double differenceInMinutes(long date1, long date2) {
        return differenceInSeconds(date1, date2) / 60.0;
    }
    public static double differenceInSeconds(long date1, long date2) {
        return differenceInMilliseconds(date1, date2) / 1000.0;
    }
    private static double differenceInMilliseconds(long date1, long date2) {
        return Math.abs(date1 - date2);
    }
}
