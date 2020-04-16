/////////////////////////////////////////// FILE  HEADER /////////////////////////////////////////////
//
// Title: Adventure Story
// Files: AdventureStory.java, TestAdventureStory.java, Config.java
// This File: Config.java
// 
// Name: Benjamin Tarmann
// Email: btarmann@wisc.edu
//
///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////
public class Config {
    /**
     * Display maximum line length
     */
    public static int DISPLAY_WIDTH = 80; 

    /**
     * Room details String[] constants
     */
    public static int ROOM_DET_LEN = 3; // room details array length
    public static int ROOM_ID = 0; // index of room id
    public static int ROOM_TITLE = 1; // index of room title
    public static int ROOM_DESC = 2; // index of room desc

    /**
     * Transition details String[] constants
     */
    public static int TRAN_DET_LEN = 3; // transition details array length
    public static int TRAN_DESC = 0; // index of transition description
    public static int TRAN_ROOM_ID = 1; // index of transition destination (id of the room)
    public static int TRAN_PROB = 2; // index of transition probability weight

    /**
     * Terminal state constants
     */
    public static String SUCCESS = "=)";
    public static String FAIL = "=(";

    /**
     * File magic numbers
     */
    public static String MAGIC_STORY = "#!STORY";
    public static String MAGIC_BOOKMARK = "#!BOOKMARK";

    /**
     * Character used when printing a line
     */
    public static char LINE_CHAR = '-';

    /**
     * Random seed to use
     */
    public static long SEED = 6;
}
