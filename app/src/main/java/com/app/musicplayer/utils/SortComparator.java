package com.app.musicplayer.utils;

import com.app.musicplayer.db.SongModel;

import java.util.Comparator;

public class SortComparator {

    public static class SortByName {

        public static class Ascending implements Comparator<SongModel> {

            @Override
            public int compare(SongModel song1, SongModel song2) {
                // Catch Parse errors
                try {
                    return song1.getTitle().toLowerCase().compareToIgnoreCase(song2.getTitle().toLowerCase());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                return 0;
            }
        }

        /**
         * Class to sort list by Date in descending order
         */
        public static class Descending implements Comparator<SongModel> {

            @Override
            public int compare(SongModel song1, SongModel song2) {
                try {
                    // Descending
                    return song2.getTitle().toLowerCase().compareToIgnoreCase(song1.getTitle().toLowerCase());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                return 0;
            }
        }
    }

    public static class SortBySize {

        public static class Ascending implements Comparator<SongModel> {

            @Override
            public int compare(SongModel song1, SongModel song2) {
                // Catch Parse errors
                try {
                    return (song1.getSize() > song1.getSize() ? 1 : -1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                return 0;
            }
        }

        /**
         * Class to sort list by Date in descending order
         */
        public static class Descending implements Comparator<SongModel> {

            @Override
            public int compare(SongModel song1, SongModel song2) {
                try {
                    // Descending
                    return (song1.getSize() > song2.getSize() ? -1 : 1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                return 0;
            }
        }
    }

    public static class SortByDuration {

        public static class Ascending implements Comparator<SongModel> {

            @Override
            public int compare(SongModel song1, SongModel song2) {
                // Catch Parse errors
                try {
                    return (song1.getDuration() > song1.getDuration() ? 1 : -1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                return 0;
            }
        }

        /**
         * Class to sort list by Date in descending order
         */
        public static class Descending implements Comparator<SongModel> {

            @Override
            public int compare(SongModel song1, SongModel song2) {
                try {
                    // Descending
                    return (song1.getDuration() > song2.getDuration() ? -1 : 1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                return 0;
            }
        }
    }

    public static class SortByDate {

        /**
         * Class to sort list by Date in ascending order
         */
        public static class Ascending implements Comparator<SongModel> {

            @Override
            public int compare(SongModel song1, SongModel song2) {
                // Catch Parse errors
                try {
                    return (song1.getDateAdded() > song1.getDateAdded() ? 1 : -1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                return 0;
            }
        }

        /**
         * Class to sort list by Date in descending order
         */
        public static class Descending implements Comparator<SongModel> {

            @Override
            public int compare(SongModel song1, SongModel song2) {
                try {
                    // Descending
                    return (song1.getDateAdded() > song2.getDateAdded() ? -1 : 1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                return 0;
            }
        }
    }
}