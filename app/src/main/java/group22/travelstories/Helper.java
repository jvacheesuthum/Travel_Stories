package group22.travelstories;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.database.Cursor;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Nop on 28-Nov-16.
 */

public class Helper {

    private static Photo getPhoto(Cursor cursor, int dateColumn) {
        int path = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
        int latitude = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LATITUDE);
        int longitude = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LONGITUDE);

        Long date = cursor.getLong(dateColumn);
        Date d = new Date(date);
        String p = cursor.getString(path);
        Double lat = cursor.getDouble(latitude);
        Double longi = cursor.getDouble(longitude);
        Photo photo = new Photo(p, d, lat, longi);
        return photo;
    }

    private static Double calculateDistance(Photo photo, TimeLineEntry t) {
        Double photoLong = photo.longitude;
        Double photoLat = photo.latitude;
        Double entryLong = t.location.getLongitude();
        Double entryLat = t.location.getLatitude();

        int radius = 6371;
        //Haversine formula:
        // a = sin^2(latDiff/2) + cos(lat1)cos(lat2)sin^2(LongDiff/2)
        // c = 2 * theta of (x, y) converted to polar (r, theta)
        // distance = radius of sphere * c
        Double sinDistanceLong = Math.sin(Math.toRadians(entryLong - photoLong)/2);
        Double sinDistanceLat = Math.sin(Math.toRadians(entryLat - photoLat)/2);
        Double a = Math.pow(sinDistanceLat, 2) + Math.cos(Math.toRadians(photoLat))
                * Math.cos(Math.toRadians(entryLat)) * Math.pow(sinDistanceLong, 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double distance = radius * c;

        return distance;
    }

    protected static List<TimeLineEntry> populateList(List<TimeLineEntry> timeLine, Long initStart, Context context) {
        if (timeLine.isEmpty()) return timeLine;
        List<TimeLineEntry> result = timeLine;
        String[] projection = {MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.LATITUDE,
                MediaStore.Images.ImageColumns.LONGITUDE,
                MediaStore.Images.ImageColumns.DATE_TAKEN};
        String selection = MediaStore.Images.ImageColumns.DATE_TAKEN + " > ? AND " +
                MediaStore.Images.ImageColumns.DATE_TAKEN + " < ?";
//        Long start = timeLine.get(0).start.getTimeInMillis();
//        Long end = timeLine.get(timeLine.size() - 1).end.getTimeInMillis();
        Long start = initStart;
        Long end = System.currentTimeMillis();

        String[] selectionArgs = {start.toString(), end.toString()};
        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        cursor.moveToFirst();
        int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN);

        while(cursor.moveToNext()) {
            System.out.println("Curosr: " + cursor.getLong(dateColumn));
        }
        cursor.moveToFirst();

        for (TimeLineEntry e : result) {
            e.photos = new ArrayList<>();
        }

        int index = 0;
        TimeLineEntry prevEntry = null;
        TimeLineEntry currEntry;

        for (TimeLineEntry e : result) {
            System.out.println("TimeLine Time: " + e.getTime());
        }

        if (!cursor.moveToNext()) return result;
        cursor.moveToFirst();

        int count = cursor.getCount();

        do {

            ArrayList<Photo> photos = new ArrayList<>();
            currEntry = result.get(index);
            if (prevEntry != null) {
                start = end;
                end = currEntry.start.getTimeInMillis();
                while (cursor.getLong(dateColumn) < end) {
                    Photo photo = getPhoto(cursor, dateColumn);
                    if (calculateDistance(photo, prevEntry) >
                            calculateDistance(photo, currEntry)) {
                        photos.add(photo);
                    } else {
                        prevEntry.photos.add(photo);
                    }
                    if (!cursor.moveToNext()) {
                        break;
                    }
                }
            }
            start = currEntry.start.getTimeInMillis();
            end = currEntry.end.getTimeInMillis();

            while (cursor.getLong(dateColumn) <= end) {
                photos.add(getPhoto(cursor, dateColumn));

                if (!cursor.moveToNext()) {
                    break;
                }
//                cursor.moveToPrevious();
            }
            currEntry.photos = photos;
            prevEntry = currEntry;
            index++;
            if (cursor.isLast()) {
                break;
            }
        } while (index != timeLine.size());

        if (!cursor.isLast() && !cursor.isAfterLast()) {
            System.out.println("In last if");
            do {
                System.out.println("IN LAST LOOP: " + cursor.getLong(dateColumn));
                currEntry.photos.add(getPhoto(cursor, dateColumn));
                if (!cursor.moveToNext()) {
                    break;
                }
                cursor.moveToPrevious();
            } while (cursor.moveToNext());
        }

        cursor.close();
        return result;
    }
}
