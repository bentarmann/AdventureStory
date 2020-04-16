/////////////////////////////////////////// FILE  HEADER /////////////////////////////////////////////
//
// Title: Adventure Story
// Files: AdventureStory.java, TestAdventureStory.java, Config.java
// This File: AdventureStory.java
// 
// Name: Benjamin Tarmann
// Email: btarmann@wisc.edu
//
///////////////////////////////////////// 100 COLUMNS WIDE /////////////////////////////////////////

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;

/**
 * This class contains the code to go through a choose your own adventure story. Users
 * input their own story from a story file and make decisions that have an effect on how
 * the story progresses.
 * 
 * @author Benjamin Tarmann
 */
public class AdventureStory {

    /**
     * Prompts the user for a value by displaying prompt. 
     *
     * After prompting the user, the method will consume an entire line of input while reading an
     * int. If the value read is between min and max (inclusive), that value is returned. Otherwise,
     * "Invalid value." terminated by a new line is output and the user is prompted again.
     *
     * @param sc     The Scanner instance to read from System.in.
     * @param prompt The name of the value for which the user is prompted.
     * @param min    The minimum acceptable int value (inclusive).
     * @param max    The maximum acceptable int value (inclusive).
     * @return Returns the value read from the user.
     */
    public static int promptInt(Scanner sc, String prompt, int min, int max) {
        boolean validInput = false;
        int intValue;

        while (validInput == false) {
            System.out.print(prompt);
            // checks that the next token exists and is an integer
            if (sc.hasNextInt()) {
                intValue = sc.nextInt();
            } else {
                System.out.println("Invalid value.");
                continue;
            }
            // checks the integer entered is between the min and max values
            if (intValue >= min && intValue <= max) {
                return intValue;
            } else {
                System.out.println("Invalid value.");
                continue;
            }
        }
        return -99; // only returns -99 if the loop is exited, which it should never
    }

    /**
     * Prompts the user for a char value by displaying prompt.
     *
     * After prompting the user, the method will read an entire line of input and return the first
     * non-whitespace character converted to lower case.
     *
     * @param sc The Scanner instance to read from System.in
     * @param prompt The user prompt.
     * @return Returns the first non-whitespace character (in lower case) read from the user. If 
     *         there are no non-whitespace characters read, the null character is returned.
     */
    public static char promptChar(Scanner sc, String prompt) {
        String userInput;
        char charValue;
        System.out.print(prompt);
        // checks that the next token exists
        if (sc.hasNextLine()) {
            userInput = sc.nextLine();
            userInput = userInput.trim().toLowerCase();
            charValue = userInput.charAt(0);
            return charValue;
        } else {
            return '\0';
        }
    }

    /**
     * Prompts the user for a string value by displaying prompt.
     *
     * After prompting the user, the method will read an entire line of input, removing any leading and 
     * trailing whitespace.
     *
     * @param sc The Scanner instance to read from System.in
     * @param prompt The user prompt.
     * @return Returns the string entered by the user with leading and trailing whitespace removed.
     */
    public static String promptString(Scanner sc, String prompt) {
        String userInput;
        System.out.print(prompt);
        userInput = sc.nextLine();
        userInput = userInput.trim();
        return userInput;
    }

    /**
     * Saves the current position in the story to a file.
     *
     * The format of the bookmark file is as follows:
     * Line 1: The value of Config.MAGIC_BOOKMARK
     * Line 2: The filename of the story file from storyFile
     * Line 3: The current room id from curRoom
     *
     * @param storyFile The filename containing the cyoa story.
     * @param curRoom The id of the current room.
     * @param bookmarkFile The filename of the bookmark file.
     * @return false on an IOException, and true otherwise.
     */
    public static boolean saveBookmark(String storyFile, String curRoom, String bookmarkFile) {
        try {
            File f = new File(bookmarkFile);
            PrintWriter p = new PrintWriter(f);
            p.println(Config.MAGIC_BOOKMARK);
            p.println(storyFile);
            p.println(curRoom);
            p.flush();
            p.close();
        } catch (IOException e) {
            return false;
        }
        return true; // returns true if no exceptions are thrown
    }

    /**
     * Loads the story and current location from a file either a story file or a bookmark file.
     * 
     * The type of the file will be determined by reading the first line of the file.
     * 
     * If the first line is Config.MAGIC_STORY, then the file is parsed using the parseStory method.
     * If the first line is Config.MAGIC_BOOKMARK, the the file is parsed using the parseBookmark
     * method.
     * Otherwise, print an error message, terminated by a new line, to System.out, displaying: 
     * "First line: trimmedLineRead does not correspond to known value.", where trimmedLineRead is 
     * the trimmed value of the first line from the file. 
     * 
     * If there is an IOException, print an error message, terminated by a new line, to System.out,
     * saying "Error reading file: fName", where fName is the value of the parameter.
     * 
     * If there is an error reading the first line, print an error message, terminated by a new 
     * line, to System.out, displaying: "Unable to read first line from file: fName", where fName is
     * the value of the parameter. 
     *
     * Milestone #2: Open the file, handling the IOExceptions as described above. Do not read the
     * the first line: Assume the file is a story file and call the parseStory method.
     *
     * @param fName The name of the file to read.
     * @param rooms The ArrayList structure that will contain the room details. A parallel ArrayList
     *              trans.
     * @param trans The ArrayList structure that will contain the transition details. A parallel 
     *              ArrayList to rooms. Since the rooms can have multiple transitions, each room 
     *              will be an ArrayList<String[]> with one String[] per transition with the 
     *              overall structure being an ArrayList of ArrayLists of String[].
     * @param curRoom An array of at least length 1. The current room id will be stored in the cell
     *                at index 0.
     * @return false if there is an IOException or a parsing error. Otherwise, true. 
     */
    public static boolean parseFile(String fName, ArrayList<String[]> rooms,
        ArrayList<ArrayList<String[]>> trans, String[] curRoom) {
        String firstLine;
        try {
            // opens the file and wraps it in a Scanner object
            File f = new File(fName);
            Scanner s = new Scanner(f);
            // checks that the first line of the file exists
            if (s.hasNextLine()) {
                firstLine = s.nextLine().trim();
            } else {
                System.out.println("Unable to read first line from file: " + fName);
                s.close();
                return false;
            }
            // checks whether a file is a story or bookmark file
            if (firstLine.equals(Config.MAGIC_STORY)) {
                if (parseStory(s, rooms, trans, curRoom) != true) {
                    return false;
                }
            } else if (firstLine.equals(Config.MAGIC_BOOKMARK)) {
                if (parseBookmark(s, rooms, trans, curRoom) != true) {
                    return false;
                }
            } else {
                System.out
                    .println("First line: " + firstLine + " does not correspond to known value.");
                s.close();
                return false;
            }
        } catch (IOException e) {
            System.out.print("Error reading file: " + fName);
            System.out.println();
            return false;
        }
        return true; // returns true if no exceptions are thrown and the file is parsed successfully
    }

    /**
     * Loads the story and the current room from a bookmark file. This method assumes that the first
     * line of the file, containing Config.MAGIC_BOOKMARK, has already been read from the Scanner.
     *
     * The format of a bookmark file is as follows:
     * Line No: Contents
     *       1: Config.MAGIC_BOOKMARK
     *       2: Story filename
     *       3: Current room id
     *
     * As an example, the following contents would load the story Goldilocks.story and set the 
     * current room to id 7.
     *
     * #!BOOKMARK
     * Goldilocks.story
     * 7
     *
     * @param sc The Scanner object buffering the input file to read.
     * @param rooms The ArrayList structure that will contain the room details. A parallel ArrayList
     *              trans.
     * @param trans The ArrayList structure that will contain the transition details. A parallel 
     *              ArrayList to rooms.
     * @param curRoom An array of at least length 1. The current room id will be stored in the cell
     *                at index 0.
     * @return false if there is a parsing error. Otherwise, true. 
     */
    public static boolean parseBookmark(Scanner sc, ArrayList<String[]> rooms,
        ArrayList<ArrayList<String[]>> trans, String[] curRoom) {
        String fileName = sc.nextLine().trim();
        // checks that the story file listed in the bookmark is valid
        if (parseFile(fileName, rooms, trans, curRoom) != true) {
            curRoom[0] = sc.nextLine().trim();
            return false;
        } else {
            curRoom[0] = sc.nextLine().trim();
            return true;
        }

    }

    /**
     * Enum to track the state of parsing in the parseStory method
     */
    public enum parseState {
        DEFAULT, DESCRIPTION, TRANSITION, ERROR
    }
    
    /**
     * This method parses a story adventure file.
     *
     * The method will read the contents from the Scanner, line by line, and populate the parallel 
     * ArrayLists rooms and trans. As such the story files have a specific structure. The order of
     * the rooms in the story file correspond to the order in which they will be stored in the 
     * parallel ArrayLists.
     *
     * When reading the file line-by-line, whitespace at the beginning and end of the line should be
     * trimmed. The file format described below assumes that whitespace has been trimmed.
     *
     * Story file format:
     *
     * - Any line (outside of a room's description) that begins with a '#' is considered a comment 
     *   and should be ignored.
     * - Room details begin with a line starting with 'R' followed by the room id, terminated with 
     *   a ':'. Everything  after the first colon is the room title. The substrings of the room id 
     *   and the room title should be trimmed.
     * - The room description begins on the line immediate following the line prefixed with 'R',
     *   containing the room id, and continues until a line of ";;;" is read.
     *   - The room description may be multi-line. Every line after the first one, should be 
     *     prefixed with a newline character ('\n'), and concatenated to the previous description 
     *     lines read for the current room.
     * - The room transitions begin immediately after the line of ";;;", and continue until a line
     *   beginning with 'R' is encountered. There are 3 types of transition lines:
     *   - 1 -- Terminal Transition: A terminal transition is either Config.SUCCESS or 
     *                               Config.FAIL. This room is the end of the story. 
     *                               This value should be stored as a transition with the String at
     *                               index Config.TRAN_DESC set to the value read. The rest of the 
     *                               Strings in the transition String array should be null.
     *                               A room with a terminal transition can only have one transition 
     *                               associated with it. Any additional transitions should result in
     *                               a parse error.
     *   - 2 -- Normal Transition: The line begins with ':' followed by the transition description, 
     *                             followed by " -> " (note the spaces), followed by the room id to 
     *                             transition to. For normal transitions (those without a transition
     *                             weight), set the value at index Config.TRAN_PROB to null.
     *   - 3 -- Weighted Transition: Similar to a normal transition except that there is a 
     *                               probability weight associated with the transition. After the 
     *                               room id (as described in the normal transition) is a '?' 
     *                               followed by the probability weight. 
     *   - You can assume that room ids do not contain a '?'.
     *   - You can assume that Config.SUCCESS and Config.FAIL do not start with a ':'.
     *
     * In the parallel ArrayLists rooms and trans, the internal structures are as follows:
     *
     * The String array structure for each room has a length of Config.ROOM_DET_LEN. The entries in
     * the array are as follows:
     * Index              | Description
     * --------------------------------------------
     * Config.ROOM_ID     | The room id
     * Config.ROOM_TITLE  | The room's title
     * Config.ROOM_DESC   | The room's description
     *
     * The String array structure for each transition. Note that each room can have multiple 
     * transitions, hence, the ArrayList of ArrayLists of String[]. The length of the String[] is
     * Config.TRAN_DET_LEN. The entries in the String[] are as follows:
     * Index               | Description
     * ------------------------------------------------------------------
     * Config.TRAN_DESC    | The transition description
     * Config.TRAN_ROOM_ID | The transition destination (id of the room) 
     * Config.TRAN_PROB    | The probability weight for the transition
     *
     * If you encounter a line that violates the story file format, the method should print out an 
     * error message, terminated by a new line, to System.out displaying: 
     * "Error parsing file on line: lineNo: lineRead", where lineNo is the number of lines read
     * by the parseStory method (i.e. ignoring the magic number if Milestone #3), and lineRead is 
     * the offending trimmed line read from the Scanner.
     *
     * After parsing the file, if rooms or trans have zero size, or they have different sizes, print
     * out an error message, terminated by a new line, to System.out displaying:
     * "Error parsing file: rooms or transitions not properly parsed."
     *
     * After parsing the file, if curRoom is not null, store the reference of the id of the room at 
     * index 0 of the rooms ArrayList into the cell at index 0 of curRoom.
     *
     * @param sc The Scanner object buffering the input file to read.
     * @param rooms The ArrayList structure that will contain the room details.
     * @param trans The ArrayList structure that will contain the transition details.
     * @param curRoom An array of at least length 1. The current room id will be stored in the cell
     *                at index 0.
     * @return false if there is a parsing error. Otherwise, true. 
     */
    public static boolean parseStory(Scanner sc, ArrayList<String[]> rooms,
        ArrayList<ArrayList<String[]>> trans, String[] curRoom) {

        String test;
        parseState parseStatus = parseState.DEFAULT;

        int numLines = 0;
        while (sc.hasNextLine()) {
            test = sc.nextLine();
            test = test.trim();
            // checks if the line should be ignored
            if (!(parseStatus == parseState.DESCRIPTION)
                && (test.length() == 0 || test.charAt(0) == '#')) {
                numLines++;
                continue;
            }
            // checks if the line contains information for a room
            else if (parseStatus != parseState.DESCRIPTION && test.charAt(0) == 'R') {
                rooms.add(new String[Config.ROOM_DET_LEN]);
                rooms.get(rooms.size() - 1)[Config.ROOM_ID] =
                    test.substring(1, test.indexOf(':')).trim();
                rooms.get(rooms.size() - 1)[Config.ROOM_TITLE] =
                    test.substring(test.indexOf(':') + 1).trim();
                parseStatus = parseState.DESCRIPTION;
                numLines++;
                continue;
            }
            // checks if the following line/lines are transitions
            else if (test.equals(";;;")) {
                trans.add(new ArrayList<String[]>());
                parseStatus = parseState.TRANSITION;
                numLines++;
                continue;
            }
            // stores room description information
            else if (parseStatus == parseState.DESCRIPTION) {
                if (rooms.get(rooms.size() - 1)[Config.ROOM_DESC] == null) {
                    rooms.get(rooms.size() - 1)[Config.ROOM_DESC] = test;
                } else {
                    rooms.get(rooms.size() - 1)[Config.ROOM_DESC] += "\n" + test;
                }
            }
            // stores transition information
            else if (parseStatus == parseState.TRANSITION) {
                if (test.charAt(0) == ':') {
                    trans.get(trans.size() - 1).add(new String[Config.TRAN_DET_LEN]);
                    trans.get(trans.size() - 1)
                        .get(trans.get(trans.size() - 1).size() - 1)[Config.TRAN_DESC] =
                            test.substring(test.indexOf(":") + 1, test.indexOf(" -> ")).trim();
                    if (test.contains(" -> ")) {
                        // room ID found differently if the transitions contains a probability
                        // weight
                        if (test.contains("?")) {
                            trans.get(trans.size() - 1)
                                .get(trans.get(trans.size() - 1).size() - 1)[Config.TRAN_ROOM_ID] =
                                    test.substring(test.indexOf(" -> ") + 4, test.lastIndexOf("?"))
                                        .trim();
                        } else {
                            trans.get(trans.size() - 1)
                                .get(trans.get(trans.size() - 1).size() - 1)[Config.TRAN_ROOM_ID] =
                                    test.substring(test.indexOf(" -> ") + 4, test.length()).trim();
                        }
                    } else {
                        // error if test does not contain " -> "
                        parseStatus = parseState.ERROR;
                    }
                    if (test.contains("?")) {
                        trans.get(trans.size() - 1)
                            .get(trans.get(trans.size() - 1).size() - 1)[Config.TRAN_PROB] =
                                test.substring(test.lastIndexOf("?") + 2);
                    }
                } else if (test.equals(Config.SUCCESS) || test.equals(Config.FAIL)) {
                    trans.get(trans.size() - 1).add(new String[Config.TRAN_DET_LEN]);
                    trans.get(trans.size() - 1)
                        .get(trans.get(trans.size() - 1).size() - 1)[Config.TRAN_DESC] = test;
                } else {
                    // error if transition does not start with ":"
                    parseStatus = parseState.ERROR;
                }
            } else {
                // line does not follow story format
                parseStatus = parseState.ERROR;
            }
            if (parseStatus == parseState.ERROR) {
                System.out.print("Error parsing file on line: " + ++numLines + ": " + test);
                return false;
            }
            numLines++;
        }
        // checks that the size of rooms and trans are not 0 and that they are also the same
        if (rooms.size() < 1 || trans.size() < 1 || rooms.size() != trans.size()) {
            System.out.print("Error parsing file: rooms or transitions not properly parsed.");
            return false;
        }

        if (curRoom != null) {
            curRoom[0] = rooms.get(0)[Config.ROOM_ID];
        }
        return true; // returns true if parsed successfully
    }

    /**
     * Returns the index of the given room id in an ArrayList of rooms. 
     *
     * Each entry in the ArrayList contain a String array, containing the details of a room. The 
     * String array structure, which has a length of Config.ROOM_DET_LEN, and has the following 
     * entries:
     * Index              | Description
     * --------------------------------------------
     * Config.ROOM_ID     | The room id
     * Config.ROOM_TITLE  | The room's title
     * Config.ROOM_DESC   | The room's description
     *
     * @param id The room id to search for.
     * @param rooms The ArrayList of rooms.
     * @return The index of the room with the given id if found in rooms. Otherwise, -1.
     */
    public static int getRoomIndex(String id, ArrayList<String[]> rooms) {
        for (int i = 0; i < Config.ROOM_DET_LEN - 1; i++) {
            if (id.equals(rooms.get(i)[Config.ROOM_ID])) {
                return i;
            }
        }
        return -1; // -1 is returned if the index of the room with the given id is not found
    }

    /**
     * Returns the room String array of the given room id in an ArrayList of rooms.
     *
     * @param id The room id to search for.
     * @param rooms The ArrayList of rooms.
     * @return The reference to the String array in rooms with the room id of id. Otherwise, null.
     */
    public static String[] getRoomDetails(String id, ArrayList<String[]> rooms) {
        for (int i = 0; i < rooms.size(); i++) {
            if (id.equals(rooms.get(i)[Config.ROOM_ID])) {
                return rooms.get(i);
            }
        }
        return null; // null returned if room id is not found
    }

    /**
     * Prints out a line of characters to System.out. The line should be terminated by a new line.
     *
     * @param len The number of times to print out c. 
     * @param c The character to print out.
     */
    public static void printLine(int len, char c) {
        for (int i = 0; i < len; i++) {
            System.out.print(c);
        }
        System.out.println("");
    }

    /**
     * Prints out a String to System.out, formatting it into lines of length no more than len 
     * characters.
     * 
     * This method will need to print the string out character-by-character, counting the number of
     * characters printed per line. 
     * If the character to output is a newline, print it out and reset your counter.
     * If it reaches the maximum number of characters per line, len, and the next character is:
     *   - whitespace (as defined by the Character.isWhitespace method): print a new line 
     *     character, and move onto the next character.
     *   - NOT a letter or digit (as defined by the Character.isLetterOrDigit method): print out the
     *     character, a new line, and move onto the next character.
     *   - Otherwise:
     *       - If the previous character is whitespace, print a new line then the character.
     *       - Otherwise, print a '-', a new line, and then the character.
     *
     * After printing out the characters in the string, a new line is output.
     *
     * @param len The maximum number of characters to print out.
     * @param val The string to print out.
     */
    public static void printString(int len, String val) {
        int counterVal = 0;
        for (int i = 0; i < val.length(); i++) {
            // checks for a newline
            if (val.charAt(i) == '\n') {
                System.out.print(val.charAt(i));
                counterVal = 0;
                continue;
            }
            
            // if the number of characters printed meets the max per line
            if (counterVal >= len - 1) {
                if (Character.isWhitespace(val.charAt(i))) {
                    System.out.print("\n");
                } else if (!Character.isLetterOrDigit(val.charAt(i))) {
                    System.out.print(val.charAt(i) + "\n");
                } else {
                    if (Character.isWhitespace(val.charAt(i - 1))) {
                        System.out.print("\n" + val.charAt(i));
                        counterVal = 1;
                        continue;
                    } else {
                        System.out.print("-" + "\n" + val.charAt(i));
                        counterVal = 1;
                        continue;
                    }
                }
                counterVal = 0;
                continue;
            // if the number of characters has not yet met the max per line
            } else {
                System.out.print(val.charAt(i));
                counterVal++;
            }
        }
        
        System.out.println(""); // prints a newline following all of the characters in val
    }

    /**
     * This method prints out the room title and description to System.out. Specifically, it first
     * loads the room details, using the getRoomDetails method. If no room is found, the method
     * should return, avoiding any runtime errors.
     *
     * If the room is found, first a line of Config.LINE_CHAR of length Config.DISPLAY_WIDTH is 
     * output. Followed by the room's title, a new line, and the room's description. Both the title
     * and the description should be printed using the printString method with a maximum length of
     * Config.DISPLAY_WIDTH. Finally, a line of Config.LINE_CHAR of length Config.DISPLAY_WIDTH is 
     * output.
     *
     * @param id Room ID to display
     * @param rooms ArrayList containing the room details.
     */
    public static void displayRoom(String id, ArrayList<String[]> rooms) {
        String[] roomDetails = new String[Config.ROOM_DET_LEN];

        // checks if the details for the room can be found
        if (getRoomDetails(id, rooms) != null) {
            roomDetails = getRoomDetails(id, rooms);
            printLine(Config.DISPLAY_WIDTH, Config.LINE_CHAR); // divider line
            
            // if a title for the room exists
            if (roomDetails[Config.ROOM_TITLE] != null) {
                printString(Config.DISPLAY_WIDTH, roomDetails[Config.ROOM_TITLE]);
            } else {
                System.out.println("");
            }
            System.out.println("");
            
            // if a description for the room exists
            if (roomDetails[Config.ROOM_DESC] != null) {
                printString(Config.DISPLAY_WIDTH, roomDetails[Config.ROOM_DESC]);
            } else {
                System.out.println("");
            }
            printLine(Config.DISPLAY_WIDTH, Config.LINE_CHAR); // divider line
        
        } else {
            return; // method is exited if room details cannot be found
        }
    }

    /**
     * Prints out and returns the transitions for a given room. 
     *
     * If the room ID of id cannot be found, nothing should be output to System.out and null should
     * be returned.
     *
     * If the room is a terminal room, i.e., the transition list is consists of only a single 
     * transition with the value at index Config.TRAN_DESC being either Config.SUCCESS or 
     * Config.FAIL, nothing should be printed out.
     *
     * The transitions should be output in the same order in which they are in the ArrayList, and 
     * only if the transition probability (String at index TRAN_PROB) is null. Each transition 
     * should be output on its own line with the following format:
     * idx) transDesc
     * where idx is the index in the transition ArrayList and transDesc is the String at index 
     * Config.TRAN_DESC in the transition String array.
     *
     * See parseStory method for the details of the transition String array.
     *
     * @param id The room id of the transitions to output and return.
     * @param rooms The ArrayList structure that contains the room details.
     * @param trans The ArrayList structure that contains the transition details.
     * @return null if the id cannot be found in rooms. Otherwise, the reference to the ArrayList of
     *         transitions for the given room.
     */
    public static ArrayList<String[]> displayTransitions(String id, ArrayList<String[]> rooms,
        ArrayList<ArrayList<String[]>> trans) {
        // searches through rooms for the room id
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i)[Config.ROOM_ID].equals(id)) {

                // if the room is a terminal room
                if ((trans.get(i).size() == 1)
                    && (trans.get(i).get(0)[Config.TRAN_DESC].equals(Config.SUCCESS)
                        || trans.get(i).get(0)[Config.TRAN_DESC].equals(Config.FAIL))) {
                    return trans.get(i);

                    // if the room is not a terminal room
                } else {
                    // displays the transition description and index
                    for (int j = 0; j < trans.get(i).size(); j++) {
                        if (trans.get(i).get(j)[Config.TRAN_PROB] == null) {
                            System.out.println(j + ") " + trans.get(i).get(j)[Config.TRAN_DESC]);
                        }
                    }
                    return trans.get(i);
                }
            }
        }
        
        return null; // null is returned if the room id cannot be found in the rooms list
    }

    /**
     * Returns the next room id, selected randomly based on the transition probability weights.
     *
     * If curTrans is null or the total sum of all the probability weights is 0, then return null. 
     * Use Integer.parseInt to convert the Strings at index Config.TRAN_PROB of the transition
     * String array to integers. If there is a NumberFormatException, return null.
     *
     * The random transition work as follows:
     *   - Let totalWeight be the sum of the all the transition probability weights in curTrans.
     *   - Draw a random integer between 0 and totalWeight - 1 (inclusive) from rand.
     *   - From the beginning of the ArrayList curTrans, start summing up the transition probability 
     *     weights.
     *   - Return the String at index Config.TRAN_ROOM_ID of the first transition that causes the 
     *     running sum of probability weights to exceed the random integer.   
     *
     * See parseStory method for the details of the transition String array.
     *
     * @param rand The Random class from which to draw random values.
     * @param curTrans The ArrayList structure that contains the transition details.
     * @return The room id that was randomly selected if the sum of probabilities is greater than 0.
     *         Otherwise, return null. Also, return null if there is a NumberFormatException. 
     */
    public static String probTrans(Random rand, ArrayList<String[]> curTrans) {
        if (curTrans == null) {
            return null;
        }

        int totalWeight = 0;
        int currentWeight = 0;
        int randomValue;

        try {
            // sums the total probability weight for all transitions
            for (int i = 0; i < curTrans.size(); i++) {
                totalWeight += Integer.parseInt(curTrans.get(i)[Config.TRAN_PROB]);
            }

            if (totalWeight < 1) {
                return null;
            }

            // Sums up the probability weights again until they exceed the random value.
            // The room ID of the first room that causes the weight to exceed the random value is
            // returned.
            randomValue = rand.nextInt(totalWeight);
            for (int i = 0; i < curTrans.size(); i++) {
                currentWeight += Integer.parseInt(curTrans.get(i)[Config.TRAN_PROB]);
                if (currentWeight > randomValue) {
                    return curTrans.get(i)[Config.TRAN_ROOM_ID];
                }
            }

        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }

    /**
     * This is the main method for the Story Adventure game. It consists of the main game loop and
     * play again loop with calls to the various supporting methods. This method will evolve over 
     * the 3 milestones.
     * 
     * The Scanner object to read from System.in and the Random object with a seed of Config.SEED 
     * will be created in the main method and used as arguments for the supporting methods as 
     * required.
     *
     * @param args Unused
     */
    public static void main(String[] args) {
        // welcome message
        System.out.println("Welcome to this choose your own adventure system!");

        boolean playAgain = true;
        Scanner userInput = new Scanner(System.in);
        char continuePlaying;
        String fileName;
        int transitionChoice;
        String transitionProbID;

        // game loop
        while (playAgain) {
            Random rand = new Random(Config.SEED);
            ArrayList<String[]> rooms = new ArrayList<>();
            ArrayList<ArrayList<String[]>> trans = new ArrayList<>();
            String[] curRoom = new String[Config.ROOM_DET_LEN];
            ArrayList<String[]> currentTrans = new ArrayList<>();

            // prompts for a file name
            fileName = promptString(userInput, "Please enter the story filename: ");

            // checks if the story file is found and successfully parsed
            if (parseFile(fileName, rooms, trans, curRoom) == true) {

                // story loop
                while (!curRoom[0].equals(Config.FAIL) && !curRoom[0].equals(Config.SUCCESS)) {

                    // displays room details
                    displayRoom(curRoom[0], rooms);

                    // stores the transitions for the given room
                    currentTrans = displayTransitions(curRoom[0], rooms, trans);

                    // checks if the transition is not a terminal transition
                    if (((currentTrans != null) && (currentTrans.size() != 1))
                        && (!(currentTrans.get(0)[Config.TRAN_DESC].equals(Config.SUCCESS))
                            || !(currentTrans.get(0)[Config.TRAN_DESC].equals(Config.FAIL)))) {

                        transitionProbID = probTrans(rand, currentTrans);
                        // checks if the transitions have a probability
                        if (transitionProbID == null) {
                            transitionChoice =
                                promptInt(userInput, "Choose: ", -2, currentTrans.size() - 1);
                            userInput.nextLine();

                            // if the user would like to exit the story
                            if (transitionChoice == -1) {
                                continuePlaying = promptChar(userInput,
                                    "Are you sure you want to quit the adventure? ");
                                if (continuePlaying == 'y') {
                                    curRoom[0] = Config.FAIL;
                                }
                            }

                            // if the user would like to create a bookmark file
                            if (transitionChoice == -2) {
                                String bookmarkFile;
                                bookmarkFile =
                                    promptString(userInput, "Bookmarking current location: "
                                        + curRoom[0] + ". Enter bookmark filename: ");
                                if (saveBookmark(fileName, curRoom[0], bookmarkFile)) {
                                    System.out.println("Bookmark saved in " + bookmarkFile);
                                    break;
                                } else {
                                    System.out.println("Error saving bookmark in " + bookmarkFile);
                                    break;
                                }

                                // if the user enters a room
                            } else {
                                curRoom[0] =
                                    currentTrans.get(transitionChoice)[Config.TRAN_ROOM_ID];
                            }
                            // if the transition does have a probability
                        } else {
                            curRoom[0] = transitionProbID;
                        }

                    }
                    // if the transition is terminal, sets the current room to either pass or fail
                    else {
                        curRoom[0] = currentTrans.get(0)[Config.TRAN_DESC];
                    }

                }
                // determines if the user completed the adventure of not, and prints a winning or
                // losing message
                if (curRoom[0].equals(Config.FAIL)) {
                    System.out
                        .println("You failed to complete the adventure. Better luck next time!");
                } else if (curRoom[0].equals(Config.SUCCESS)) {
                    System.out
                        .println("Congratulations! You successfully completed the adventure!");
                }
            }

            // prompts to user for if they would like to play again
            continuePlaying = promptChar(userInput, "Do you want to try again? ");
            if (continuePlaying == 'n') {
                playAgain = false;
            }
        }

        // closing message
        System.out.println("Thank you for playing!");
    }
}
