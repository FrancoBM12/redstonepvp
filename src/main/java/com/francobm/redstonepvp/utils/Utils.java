package com.francobm.redstonepvp.utils;

import java.util.Date;

public class Utils {

    public static String getTime(int time){
        int hours = time / 3600;
        int i = time - hours * 3600;
        int minutes = i / 60;
        int seconds = i - minutes * 60;
        String secondsMsg = "";
        String minutesMsg = "";
        String hoursMsg = "";
        if(seconds >= 0 && seconds <= 9){
            secondsMsg = "0"+seconds+"s";
        }else{
            secondsMsg = seconds+"s";
        }
        if(minutes >= 0 && minutes <= 9){
            minutesMsg = "0"+minutes+"m";
        }else{
            minutesMsg = minutes+"m";
        }
        if(hours >= 0 && hours <= 9){
            hoursMsg = "0"+hours+"h";
        }else{
            hoursMsg = hours+"h";
        }

        if(hours != 0)
        {
            return hoursMsg + " " + minutesMsg + " " + secondsMsg;
        }else if(minutes != 0) {
            return minutesMsg + " " + secondsMsg;
        }
        return secondsMsg;
    }

    public static String friendlyTimeDiff(Date d1, Date d2) {
        // d1, d2 are dates
        long diff = d2.getTime() - d1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        String day = diffDays + " days";
        String hour = diffHours + " hours";
        String minute = diffMinutes + " minutes";
        String second = diffSeconds + " seconds";
        if(diffDays <= 1){
            day = diffDays + " day";
        }
        if(diffHours <= 1){
            hour = diffHours + " hour";
        }
        if(diffMinutes <= 1){
            minute = diffMinutes + " minute";
        }
        if(diffSeconds <= 1){
            second = diffSeconds + " second";
        }

        //System.out.print(diffDays + " days, ");
        //System.out.print(diffHours + " hours, ");
        //System.out.print(diffMinutes + " minutes, ");
        //System.out.print(diffSeconds + " seconds.");
        return day + " " + hour + " " + minute + " " + second;
    }
}
