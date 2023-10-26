import java.io.IOException;
import javax.swing.*;

public interface CandyInterface {

    public void swapTwoCandy(Candy c1, Candy c2);

    public void connectCandies();

    public void createTable(int xSize, int ysize, String fileAddress) throws IOException;

    public void mainMenu();

    public void sortScores();

    public void mainScores();

    public void gamePanel();

    public JPanel informationPanel();

    public JPanel createCandiesPanel();

    public boolean checkForSwap(Candy c1, Candy c2);

    public void checkForCrush();
}
