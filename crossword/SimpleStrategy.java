/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crossword;
import crossword.dictionary.*;
import java.util.Random;
import java.util.LinkedList;

/**
 *
 * @author arturhebda
 */
public class SimpleStrategy extends Strategy {
    private Random randomGenerator;
    private int wordCount;

    public SimpleStrategy() {
        randomGenerator = new Random();
        wordCount = 0;
    }

    // TODO! ma znaleźć w Board miejsce do wstawienia nowego hasła
    // na podstawie tego, które komórki mogą być startowe.
    // długość losowa, stworzyć wzorzec, dopasować hasło i je zwrócić
    @Override
    public CwEntry findEntry(Crossword cw) {
        Board board = cw.getBoard();
        InteliCwDB cwdb = cw.getCwDB();
        LinkedList<BoardCell> startCells = board.getStartCells();
        BoardCell startCell = null;
        Entry entry;
        String pattern;
        int startCol = 0;
        int startRow = 0;

        int tries = 50;

        if (startCells.size() < 1)
            return null;

        if (wordCount > 0) {
            Direction direciton = Direction.HORIZ;

            startCell = startCells.get(randomGenerator.nextInt(startCells.size()));
            startCol = startCell.getCol();
            startRow = startCell.getRow();

            while ((tries--) > 0) {
                int length = randomGenerator.nextInt(board.getWidth() - 3) + 3;
                pattern = board.createPattern(startCol, startRow, startCol + length - 1, startRow);

                LinkedList<Entry> matchEntries = cwdb.findAll(pattern);

                if (matchEntries.size() == 0) {
                    continue;
                }
                else {
                    entry = matchEntries.get(randomGenerator.nextInt(matchEntries.size()));
                    CwEntry cwEntry = new CwEntry(entry.getWord(), entry.getClue());
                    cwEntry.setLocation(startCol, startRow, direciton);
                    wordCount++;

                    return cwEntry;
                }
            }
        }
        else { // pierwsze słowo
            Direction direction = Direction.VERT;

            while ((tries--) > 0) {
                int length = randomGenerator.nextInt(board.getHeight() - 3) + 3;
                LinkedList<Entry> matchEntries = cwdb.findAll(length);

                if (matchEntries.size() == 0) {
                    continue;
                }
                else {
                    entry = matchEntries.get(randomGenerator.nextInt(matchEntries.size()));
                    CwEntry cwEntry = new CwEntry(entry.getWord(), entry.getClue());
                    cwEntry.setLocation(0, 0, direction);
                    wordCount++;

                    return cwEntry;
                }
            }
        }

        return null;
    }

    // TODO! powinna dodać hasło do listy haseł i zaktualizować jego otoczenie
    @Override
    public void updateBoard(Board b, CwEntry e) {
        int startRow = e.getY();
        int startCol = e.getX();
        String word = e.getWord();

        int rowMax = b.getHeight() - 1;
        int colMax = b.getWidth() - 1;

        if (wordCount > 1) { // dodano więcej niż jedno słowo
            int endCol = startCol + e.getWord().length() - 1;

            for (int col = startCol; col <= endCol; col++) {
                b.getCell(col, startRow).disableAll();
                b.getCell(col, startRow).setContent(word.substring(col - startCol, col - startCol + 1));
            }

            for (int col = endCol + 1; col <= colMax; col++)
                b.getCell(col, startRow).disableAll();
        }
        else {
            int endRow = startRow + e.getWord().length() - 1;

            for (int col = 0; col <= colMax; col++)
                for (int row = endRow + 1; row <= rowMax; row++)
                    b.getCell(col, row).disableAll();

            for (int col = 1; col <= colMax; col++) {
                for (int row = startRow; row <= endRow; row++) {
                    b.getCell(col, row).disableVert();
                    b.getCell(col, row).disableStart();
                }
            }

            for (int row = startRow; row <= endRow; row++) {
                b.getCell(startCol, row).setContent(word.substring(row - startRow, row - startRow + 1));
                b.getCell(startCol, row).disableVert();
            }
        }
    }
}