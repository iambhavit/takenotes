package com.anuntah.takenotes;

/**
 * Created by Bhavit Yadav on 18-02-2018.
 */

public class Constants {
    public static final int VERSION=1;
    public static final String DB_NAME="Notes";
    static class notes{
        public static final String NAME="notes";
        public static final String COL_ID="id";
        public static final String COL_TITLE="title";
        public static final String COL_DESCRIPTION="description";
        public static final String COL_TIME="time";
    }
    static class label{
        public static final String NAME="label";
        public static final String COL_ID="id";
        public static final String COL_LABEL="label";
        public static final String COL_NOTES_ID="notes_id";
        public static final String COL_ISCHECKED="checked";


    }
}
