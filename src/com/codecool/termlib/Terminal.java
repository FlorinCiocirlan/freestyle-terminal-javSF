package com.codecool.termlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Terminal {
    /**
     * The beginning of control sequences.
     */
    // HINT: In \033 the '0' means it's an octal number. And 33 in octal equals 0x1B in hexadecimal.
    // Now you have some info to decode that page where the control codes are explained ;)
    private static final String CONTROL_CODE = "\033[";
    /**
     * Command for whole screen clearing.
     *
     * Might be partitioned if needed.
     */
    private static final String CLEAR = "2J";
    /**
     * Command for moving the cursor.
     */
    private static final String MOVE = "H";
    /**
     * Command for printing style settings.
     *
     * Handles foreground color, background color, and any other
     * styles, for example color brightness, or underlines.
     */
    private static final String STYLE = "m";

    /**
     * Reset printing rules in effect to terminal defaults.
     *
     * Reset the color, background color, and any other style
     * (i.e.: underlined, dim, bright) to the terminal defaults.
     */
    public void resetStyle() throws InterruptedException {
        String resetCode = "0m";
        command(resetCode);
    }

    public void savePosition()  {
        System.out.print("\033[s");
    }

    /**
     * Clear the whole screen.
     *
     * Might reset cursor position.
     */
    public void clearScreen() throws InterruptedException {
        System.out.print(CONTROL_CODE+MOVE);
        command(CLEAR);
    }

    /**
     * Move cursor to the given position.
     *
     * Positions are counted from one.  Cursor position 1,1 is at
     * the top left corner of the screen.
     *
     * @param x Column number.
     * @param y Row number.
     */
    public void moveTo(Integer x, Integer y) throws InterruptedException {
        String position= x + ";" + y + MOVE;
        command(position);
    }

    /**
     * Set the foreground printing color.
     *
     * Already printed text is not affected.
     *
     * @param color The color to set.
     */
    public void setColor(Color color) throws InterruptedException {
        int initialColorCode = 30;
        String colorCode = (initialColorCode + color.ordinal()) + STYLE;
        command(colorCode);
    }

    /**
     * Set the background printing color.
     *
     * Already printed text is not affected.
     *
     * @param color The background color to set.
     */
    public void setBgColor(Color color) throws InterruptedException {
        String colorCode = (40 + color.ordinal()) + STYLE;
        command(colorCode);
    }


    /**
     * Make printed text underlined.
     *
     * On some terminals this might produce slanted text instead of
     * underlined.  Cannot be turned off without turning off colors as
     * well.
     */

    public void setAttribute(Attribute attribute) throws InterruptedException {
        String styleCode = (attribute.ordinal() + STYLE);
        command(styleCode);
    }

    /**
     * Move the cursor relatively.
     *
     * Move the cursor amount from its current position in the given
     * direction.
     *
     * @param direction Step the cursor in this direction.
     * @param amount Step the cursor this many times.
     */
    public void moveCursor(Direction direction, Integer amount) throws InterruptedException {
        String moveCode;
        String saveCursorPosition = "s";
        String returnToSavedPosition = "u";
        command(returnToSavedPosition);
        String amountCode = String.valueOf(amount);
        if (direction == Direction.UP){
            moveCode = amountCode + "A";
        } else if (direction == Direction.DOWN){
            moveCode = amountCode + "B";
        } else if (direction == Direction.FORWARD){
            moveCode = amountCode + "C";
        } else {
            moveCode = amountCode + "D";
        }
        command(moveCode);
        command(saveCursorPosition);
    }

    /**
     * Set the character diplayed under the current cursor position.
     *
     * The actual cursor position after calling this method is the
     * same as beforehand.  This method is useful for drawing shapes
     * (for example frame borders) with cursor movement.
     *
     * @param c the literal character to set for the current cursor
     * position.
     */
    public void setChar(char c) throws InterruptedException {
        String saveCursorPosition = "s";
        String returnToSavedPosition = "u";
        command(saveCursorPosition);
        System.out.println(c);
        command(returnToSavedPosition);

    }

    /**
     * Helper function for sending commands to the terminal.
     *
     * The common parts of different commands shall be assembled here.
     * The actual printing shall be handled from this command.
     *
     * @param commandString The unique part of a command sequence.
     */
    private void command(String commandString) throws InterruptedException {
        System.out.print(CONTROL_CODE + commandString);
        savePosition();
    }

    private void validateInput (String input) throws InterruptedException {
        ArrayList<Color> colorList = new ArrayList<>(Arrays.asList(Color.values()));
        ArrayList<Direction> directionList = new ArrayList<>(Arrays.asList(Direction.values()));
        ArrayList<Attribute> attributeList = new ArrayList<>(Arrays.asList(Attribute.values()));
        if (input.contains("change")) {
            if (input.contains("background")){
                for (Color background : colorList){
                    if (input.contains(background.toString().toLowerCase())){
                        setBgColor(background);
                        Music.playlistPlay("change", "background",background.toString().toLowerCase());
                        break;
                    }
                }

            } else if(input.contains("color") && !input.contains("background")){
                for(Color color : colorList){
                    if(input.contains(color.toString().toLowerCase())){
                        setColor(color);
                        Music.playlistPlay("change", "color", color.toString().toLowerCase());
                        break;
                    }
                }
            }
        } else if (input.contains("move")) {
            if((input.contains("forward")) || (input.contains("backward") || (input.contains("up")) || (input.contains("down")))){
                for(Direction direction : directionList){
                    if(input.contains(direction.toString().toLowerCase())){
                        StringBuilder stringNumber = new StringBuilder();
                        for(int i=0 ; i<input.length(); i++){
                            char charCheck = input.charAt(i);
                            if (Character.isDigit(charCheck)){
                                stringNumber.append(charCheck);
                            }
                        }
                        int amount = Integer.parseInt(stringNumber.toString());
                        moveCursor(direction,amount);
                        Music.playlistPlay("move", "cursor", direction.toString().toLowerCase());
                    }
                }
            } else {System.out.println("Wrong way!");}
        } else if (input.contains("help")) {
            System.out.println("Help menu:");
            System.out.println("Change(Color || BgColor (Black, Red, Green, Yellow, Blue, Magenta, Cyan, White)) , " +
                    "Move (direction:amount) , Set(x:y), Reset, Clear ");
        } else if (input.contains(Keywords.retainCommonWithBase(input).get(0).toLowerCase())){
            for(Attribute attribute : attributeList){
                if(input.contains(attribute.toString().toLowerCase())){
                    setAttribute(attribute);
                    if (attribute == Attribute.RESET){
                        Music.playReset();
                    } else {
                    Music.playlistPlay("set", "attribute",attribute.toString().toLowerCase());
                    break;
                    }
                }
            }
        } else if (input.contains("clear")) {
            clearScreen();
            Music.playClean();
        }
         else {
            System.out.println("Please consult help menu (Write 'HELP' in console)");
            Music.playMenu();
        }
    }

    public static void main (String[] args) throws Exception {
        Terminal term = new Terminal();
        Music.playMusic("/Users/durlesteanu/codecool/OOP/tw_1/javaTerminal/music/welcome.wav");
        System.out.println("Hello, my name is Raja. How can I help you today?");
        while (true){
            Scanner test = new Scanner(System.in);
            String input = test.nextLine().toLowerCase();
            term.validateInput(input);

    }}}
