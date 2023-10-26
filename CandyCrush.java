import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;



import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

public class CandyCrush implements CandyInterface{

    protected int SCORE = 0;
    protected int X = 10;
    protected int Y = 10;
    private Random rand = new Random();
    protected ArrayList<ArrayList<Candy>> candyLists = new ArrayList<>();
    final private String[] candyTypes = {"SC", "LR", "LC", "RC"};
    final private String[] candyColors = {"Y", "R", "G", "B"};
    protected int[] Scores = new int[5];
    protected JButton[][] buttons = new JButton[X][Y]; 
    protected JLabel scoreLabel = new JLabel("0");
    private int xBuffer = -1;
    private int yBuffer = -1;
    protected JFrame firstPage = new JFrame();
    protected JFrame gamePage = new JFrame();

    public void mainMenu() {
        
        SpringLayout layout = new SpringLayout();
        firstPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        firstPage.setTitle("Candy Crush Game");
        firstPage.setPreferredSize(new Dimension(300, 440));
        firstPage.setResizable(false);

        Container pane = firstPage.getContentPane();

        pane.setBackground(Color.RED);
        pane.setLayout(layout);
        
        JButton randGame = new JButton("New Game");
        initializeButtonFirstPage(pane, randGame);
        randGame.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    createTable(10, 10, "");
                    connectCandies();
                    checkForCrush();
                    initializeGameBoard();
                    readScores();
                    firstPage.setVisible(false);
                    gamePanel();
                } catch (IOException exception){
                    System.out.println(exception);
                }
            }
        });
        
        JButton exitingGame = new JButton("Resume Game");
        initializeButtonFirstPage(pane, exitingGame);
        exitingGame.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                if( chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    try{
                    createTable(10, 10, chooser.getSelectedFile().getAbsolutePath());
                    connectCandies();
                    readScores();
                    firstPage.setVisible(false);
                    gamePanel();
                    } catch (IOException exception){
                        System.out.println(exception);
                    }
                }
            }
        });

        JButton scores = new JButton("Top Scores");
        initializeButtonFirstPage(pane, scores);
        scores.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    readScores();    
                } catch (Exception exception) {
                    System.out.println(exception);
                }
                firstPage.setVisible(false);
                mainScores();
            }
        });

        JLabel candy = new JLabel();
        candy.setPreferredSize(new Dimension(200, 200));
        setIconForLabel(candy, "candy_intro");
        pane.add(candy);

        layout.putConstraint(SpringLayout.NORTH, exitingGame, 0, SpringLayout.SOUTH, randGame);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, exitingGame, 0, SpringLayout.HORIZONTAL_CENTER, pane);
    
        layout.putConstraint(SpringLayout.NORTH, randGame, 10, SpringLayout.NORTH, pane);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, randGame, 0, SpringLayout.HORIZONTAL_CENTER, pane);

        layout.putConstraint(SpringLayout.NORTH, scores, 0, SpringLayout.SOUTH, exitingGame);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, scores, 0, SpringLayout.HORIZONTAL_CENTER, pane);

        layout.putConstraint(SpringLayout.SOUTH, candy, -20, SpringLayout.SOUTH, pane);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, candy, 0, SpringLayout.HORIZONTAL_CENTER, pane);

        firstPage.pack();
        firstPage.setVisible(true);
        
    }

    public void initializeButtonFirstPage(Container p, JButton btn){
        btn.setFont(new Font("Monospaced", Font.BOLD, 30));
        setDefaultSettingForButton(btn);
        btn.setPreferredSize(new Dimension(250,50));
        p.add(btn);
    }

    public void setIconForButton(JButton button, String iconName){
        try{
            Image img = ImageIO.read(getClass().getResource("resources/"+iconName+".png"));
            button.setIcon(new ImageIcon(img));
        }catch (Exception exception){
            System.out.println(exception);
        }
    }

    public void setIconForLabel(JLabel label, String iconName){
        try{
            Image img = ImageIO.read(getClass().getResource("resources/"+iconName+".png"));
            label.setIcon(new ImageIcon(img));
        }catch (Exception exception){
            System.out.println(exception);
        }
    }

    public void sortScores(){
        for(int i = 0; i < Scores.length; i++){
            for(int j = i+1; j < Scores.length; j++){
                if(Scores[i]<Scores[j]){
                    int temp = Scores[j];
                    Scores[j] = Scores[i];
                    Scores[i] = temp;
                }
            }
        }
    }

    public void writeScores() throws IOException{
        File f = new File("resources/Scores.txt");
        clearContenet(f);
        if (f.exists()){
            PrintStream p = new PrintStream(f);
            sortScores();
            for(int s : Scores){
                p.print(String.valueOf(s)+" ");
            }
            p.close();
        }
    }

    public void clearContenet(File f) throws IOException{
        PrintWriter pw = new PrintWriter(f);
        pw.print("");
        pw.close();
    }

    public void readScores() throws IOException{
        File f = new File("resources/Scores.txt");
        if (f.exists()){
            Scanner scoreReader = new Scanner(f);
            int counter = 0;
            while(counter < Scores.length && scoreReader.hasNextInt()){
                Scores[counter] = scoreReader.nextInt();
                counter++;
            }
            scoreReader.close();
            writeScores();
        }

    }

    public void addNewScore(int newScore) throws IOException{
        sortScores();
        for(int s = 0; s < Scores.length; s++){
            
            if(Scores[s] == 0){
                Scores[s] = newScore;
                break;
            }else if(newScore > Scores[s]){
                int counter = Scores.length-1;
                while(counter > s){
                    Scores[counter] = Scores[counter-1];
                    counter--;
                }
                Scores[s] = newScore;
                break;
            }
        }
        writeScores();
    }

    public void mainScores(){
        JFrame scoresFrame = new JFrame();
        scoresFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        scoresFrame.setTitle("Top Scores");
        scoresFrame.setPreferredSize(new Dimension(350, 600));
        scoresFrame.setResizable(false);

        Container panel = scoresFrame.getContentPane();
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);
        panel.setBackground(Color.RED);

        JLabel scorIcon = new JLabel();
        setIconForLabel(scorIcon, "scores");

        panel.add(scorIcon);
        layout.putConstraint(SpringLayout.NORTH, scorIcon, -15, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, scorIcon, 0, SpringLayout.HORIZONTAL_CENTER, panel);
        
        int counter = 0;
        JLabel lastLabel = new JLabel();
        while(counter < Scores.length && Scores[counter] != 0){
            
            JLabel sco = new JLabel(String.valueOf(Scores[counter]));
            sco.setFont(new Font("Monoscaped", Font.BOLD, 30));
            sco.setVerticalAlignment(SwingConstants.CENTER);
            panel.add(sco);
            if (counter == 0){
                layout.putConstraint(SpringLayout.NORTH, sco, -5, SpringLayout.SOUTH, scorIcon);
            }else{
                layout.putConstraint(SpringLayout.NORTH, sco, 15, SpringLayout.SOUTH, lastLabel);
            }
            layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, sco, 0, SpringLayout.HORIZONTAL_CENTER, panel);
            lastLabel = sco;

            counter++;
        }

        JButton backButton = new JButton("");
        backButton.setPreferredSize(new Dimension(100, 100));
        setIconForButton(backButton, "home");
        backButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                scoresFrame.setVisible(false);
                firstPage.setVisible(true);
            }
        });
        
        setDefaultSettingForButton(backButton);

        panel.add(backButton);
        layout.putConstraint(SpringLayout.EAST, backButton, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, backButton, -10, SpringLayout.SOUTH, panel);

        scoresFrame.pack();

        scoresFrame.setVisible(true);
    }

    public void setDefaultSettingForButton(JButton btn){
        btn.setBackground(null);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
    }

    public JPanel informationPanel(){
        JPanel infoPanel = new JPanel();
        infoPanel.setPreferredSize(new Dimension(900, 130));
        
        SpringLayout layout = new SpringLayout();
        
        infoPanel.setLayout(layout);
        infoPanel.setBackground(Color.PINK);
        infoPanel.add(scoreLabel);
        
        JLabel sc = new JLabel("Score:");
        sc.setFont(new Font("Verdana", Font.BOLD, 25));
        sc.setPreferredSize(new Dimension(100, 70));
        sc.setHorizontalAlignment(SwingConstants.CENTER);

        scoreLabel.setText(String.valueOf(this.SCORE));
        scoreLabel.setFont(new Font("Verdana", Font.BOLD, 30));
        scoreLabel.setPreferredSize(new Dimension(100, 40));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        infoPanel.add(sc);
        
        layout.putConstraint(SpringLayout.WEST, sc, 25, SpringLayout.WEST, infoPanel);
        layout.putConstraint(SpringLayout.WEST, scoreLabel, 10, SpringLayout.EAST, sc);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, sc, 0, SpringLayout.VERTICAL_CENTER, infoPanel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, scoreLabel, 0, SpringLayout.VERTICAL_CENTER, sc);
        
        JButton hint = new JButton("");
        setIconForButton(hint, "tips");
        hint.setPreferredSize(new Dimension(80, 80));
        hint.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // System.out.println("will be add soon...");
                ArrayList<Candy> hints = hintFinder();
                System.out.println(hints);
                if (hints == null){
                    showMessage("no sequence found...");
                }else{
                    for(Candy c : hints){
                        buttons[c.getX()][c.getY()].setBackground(Color.BLACK);
                        buttons[c.getX()][c.getY()].setBorderPainted(true);
                    }
                    
                }
            }
        });;
        setDefaultSettingForButton(hint);

        infoPanel.add(hint);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, hint, 0, SpringLayout.VERTICAL_CENTER, infoPanel);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, hint, 0, SpringLayout.HORIZONTAL_CENTER, infoPanel);
        
        int ss = this.SCORE;
        JButton backButton = new JButton("");
        setIconForButton(backButton, "home");
        backButton.setPreferredSize(new Dimension(80, 80));
        backButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                addNewScore(ss);    
                } catch (Exception exception){
                    System.out.println(exception);
                }
                gamePage.setVisible(false);
                firstPage.setVisible(true);
            }
        });
        
        setDefaultSettingForButton(backButton);

        infoPanel.add(backButton);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, backButton, 0, SpringLayout.VERTICAL_CENTER, infoPanel);
        layout.putConstraint(SpringLayout.EAST, backButton, -15, SpringLayout.EAST, infoPanel);
        
        return infoPanel;
    }

    public void initializeButtons(Container pane){
        
        for (int j = 0; j < Y; j++){
            for(int i = 0; i < X; i++){
                int xP = i;
                int yP = j;
                buttons[i][j] = new JButton();
                buttons[i][j].setPreferredSize(new Dimension(90, 90));
                buttons[i][j].addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        buttons[xP][yP].setBackground(Color.BLACK);
                        buttons[xP][yP].setContentAreaFilled(true);
                        buttons[xP][yP].setFocusPainted(true);
                        if (xBuffer == -1 && yBuffer == -1){
                            xBuffer = xP;
                            yBuffer = yP;
                        }else{
                            Candy c1 = candyLists.get(xP).get(yP);
                            Candy c2 = candyLists.get(xBuffer).get(yBuffer);
                            if (checkForSwap(c1, c2)){
                                swapTwoCandy(c1, c2);
                                checkForCrush();
                                for(int h = 0; h < X; h++){
                                    for(int k = 0; k < Y; k++){
                                        setIconForCandy(buttons[h][k], candyLists.get(h).get(k));
                                    }
                                }
                            }else{
                                showMessage("You can not swap these candies!!");
                            }

                            buttons[xP][yP].setBackground(null);
                            buttons[xBuffer][yBuffer].setBackground(null);
                            buttons[xP][yP].setContentAreaFilled(false);
                            buttons[xBuffer][yBuffer].setContentAreaFilled(false);
                            buttons[xP][yP].setContentAreaFilled(false);
                            buttons[xBuffer][yBuffer].setFocusPainted(false);
                            buttons[xP][yP].setContentAreaFilled(false);
                            buttons[xBuffer][yBuffer].setFocusPainted(false);
                            
                            xBuffer = -1;
                            yBuffer = -1;

                            scoreLabel.setText(String.valueOf(SCORE));

                            if (SCORE > 1500){
                                showMessage("You scored "+SCORE+"!!");
                                try{
                                addNewScore(SCORE);    
                                } catch (Exception exception){
                                    System.out.println(exception);
                                }
                                gamePage.setVisible(false);
                                firstPage.setVisible(true);
                            }
                        }
                    }
                });
                pane.add(buttons[i][j]);
            }
        }
    }

    public void showMessage(String message){
        JOptionPane.showMessageDialog(null, message);
    }

    public JPanel createCandiesPanel(){
        JPanel candyPanel = new JPanel();
        GridLayout grid = new GridLayout(X, Y);
        candyPanel.setLayout(grid);
        initializeButtons(candyPanel);
        for(int i = 0; i < X; i++){
            for(int j = 0; j < Y; j++){
                setDefaultSettingForButton(buttons[i][j]);
                setIconForCandy(buttons[i][j], candyLists.get(i).get(j));
            }
        }
        return candyPanel;
    }

    public void gamePanel(){
        
        gamePage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePage.setTitle("Candy Crush");
        gamePage.setBackground(Color.LIGHT_GRAY);
        gamePage.setPreferredSize(new Dimension(900, 1050));
        gamePage.setResizable(false);
        gamePage.setVisible(true);

        Container pane = gamePage.getContentPane();

        JPanel candyListsPanel = createCandiesPanel();

        JPanel infoPanel = informationPanel();

        pane.add(candyListsPanel, BorderLayout.NORTH);
        
        pane.add(infoPanel, BorderLayout.SOUTH);

        gamePage.pack();
    }

    public void setIconForCandy(JButton btn, Candy c){
        setIconForButton(btn, c.getValue());
        btn.setBackground(null);
        btn.setBorderPainted(false);
    }

    public boolean sequenseFinder(){
        int countVertical = 0;
        int countHorizonal = 0;

        for(int i = 0; i < X; i++){
            for(int j = 0; j < Y; j++){
                countVertical = sequencesCount(candyLists.get(i).get(j), true);
                countHorizonal = sequencesCount(candyLists.get(i).get(j), false);
                if((countHorizonal>2) || (countVertical>2)){
                    return true;
                }
            }
        }
        return false;
    }

    public void initializeGameBoard(){
        this.SCORE = 0;
        for(int i =0; i<X; i++){
            for(int j=0; j<Y; j++){
                if (!candyLists.get(i).get(j).getType().equals(candyTypes[0])){
                    candyLists.get(i).get(j).setType(candyTypes[0]);
                }
            }
        }
    }
    
    public boolean checkVerticalSequences(Candy c){
        int x = c.getX();
        int y = c.getY();
        int count;
        Candy nextCandy;
        int sequenceCount = sequencesCount(c, true);
        if (sequenceCount < 3){
            return false;
        }else if (sequenceCount == 3){
            nextCandy = c;
            count = 0;
            while(count < 3){
                explode(nextCandy);
                nextCandy = nextCandy.getDown();
                count++;
            }
            return true;
        }else if(sequenceCount == 4){
            candyLists.get(x).get(y+sequenceCount-1).setType(candyTypes[2]);
            nextCandy = c;
            count = 0;
            while(count < 3){
                explode(nextCandy);
                nextCandy = nextCandy.getDown();
                count++;
            }
            return true;
        }else{
            candyLists.get(x).get(y+sequenceCount-1).setType(candyTypes[3]);
            nextCandy = c;
            count = 0;
            while(count < 4){
                explode(nextCandy);
                nextCandy = nextCandy.getDown();
                count++;
            }
            return true;
        }
    }

    public boolean checkHorizonalSequenses(Candy c){
        Candy nextCandy;
        int count = 0;
        int sequenceCount = sequencesCount(c, false);

        if (sequenceCount < 3){
            return false;
        }else if (sequenceCount == 3){
            nextCandy = c;
            count = 0;
            while(count < 3){
                explode(nextCandy);
                nextCandy = nextCandy.getRight();
                count++;
            }
            return true;
        }else if(sequenceCount == 4){
            c.setType(candyTypes[2]);
            nextCandy = c.getRight();
            count = 0;
            while(count < 3){
                explode(nextCandy);
                nextCandy = nextCandy.getRight();
                count++;
            }
            return true;
        }else{
            c.setType(candyTypes[3]);
            nextCandy = c.getRight();
            count = 0;
            while(count < 4){
                explode(nextCandy);
                nextCandy = nextCandy.getRight();
                count++;
            }
            return true;
        }
    }

    public int sequencesCount(Candy c, boolean vertival){
        if (c == null)
            return -1;
        
        String currentColor = c.getColor();
        int counter = 0;
        Candy nextCandy = c;
        while(!(nextCandy == null) && nextCandy.getColor().equals(currentColor)){
            counter++;
            if (vertival)
                nextCandy = nextCandy.getDown();
            else
                nextCandy = nextCandy.getRight();
        }
        
        return counter;
    }

    public void explode(Candy c){
        if (c == null)
            return;
        String type = c.getType();
        if (type.equals(candyTypes[0])){
            this.SCORE+=5;
            Candy currentCandy = c;
            Candy nextCandy = c.getUp();
            while(!(nextCandy == null)){
                swapTwoCandy(currentCandy, nextCandy);
                currentCandy = nextCandy;
                nextCandy = nextCandy.getUp();
            }
            fillRand(candyLists.get(c.getX()).get(0));
        }else if (type.equals(candyTypes[1])){
            this.SCORE += 10;
            fillRand(c);
            for(int i = 0; i < this.X; i++){
                if (candyLists.get(i).get(c.getY()).equals(c))
                    continue;
                explode(candyLists.get(i).get(c.getY()));
            }
        }else if (type.equals(candyTypes[2])){
            this.SCORE += 10;
            fillRand(c);
            for(int i = 0; i < this.X; i++){
                if (candyLists.get(c.getX()).get(i).equals(c))
                    continue;
                explode(candyLists.get(c.getX()).get(i));
            }
        }else if(type.equals(candyTypes[3])){
            this.SCORE += 15;
            int startX;
            int startY;
            if (c.getX() - 2 > 0){
                startX = c.getX()-2;
            }else{
                startX = 0;
            }
            if (c.getY()-2>0){
                startY = c.getY()-2;
            }else{
                startY = 0;
            }
            fillRand(c);
            for(int i = startX; i < 5; i++){
                for(int j = startY; j < 5; j++){
                    if (candyLists.get(i).get(j).equals(c))
                        continue;
                    explode(candyLists.get(i).get(j));
                }
            }
        }
    }

    public void fillRand(Candy c){
        c.setColor(candyColors[rand.nextInt(candyColors.length)]);
        c.setType(candyTypes[0]);
    }

    public boolean checkForSwap(Candy c1, Candy c2){
        if (c1.isNeighbor(c2)){
            if (checkSurrondingCandies(c1, c2)){
                return true;
            }else if(checkSurrondingCandies(c2, c1)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public boolean checkSurrondingCandies(Candy c1, Candy c2){
        String current_color = c1.getColor();
        boolean left_check = !(c2.getLeft()==null) && !(c2.getLeft().equals(c1));
        boolean right_check = !(c2.getRight()==null) && !(c2.getRight().equals(c1));
        boolean up_check = !(c2.getUp()==null) && !(c2.getUp().equals(c1));
        boolean down_check = !(c2.getDown()==null) && !(c2.getDown().equals(c1));
        if (left_check){
            if (!(c2.getLeft().getLeft() == null) && 
            (c2.getLeft().getColor().equals(current_color) && c2.getLeft().getLeft().getColor().equals(current_color))){
                return true;
            }
        }
        if (right_check){
            if (!(c2.getRight().getRight()==null) &&
            (c2.getRight().getColor().equals(current_color) && c2.getRight().getRight().getColor().equals(current_color)))
                return true;
        }
        if (right_check && left_check){
            if((c2.getLeft().getColor().equals(current_color) && c2.getRight().getColor().equals(current_color)))
                return true;
        }
        if (up_check){
            if(!(c2.getUp().getUp()==null) &&
            (c2.getUp().getColor().equals(current_color) && c2.getUp().getUp().getColor().equals(current_color)))
                return true;
        }
        if (down_check){
            if (!(c2.getDown().getDown()==null) &&
            (c2.getDown().getColor().equals(current_color) && c2.getDown().getDown().getColor().equals(current_color)))
                return true;
        }
        if (up_check && down_check){
            if ((c2.getUp().getColor().equals(current_color) && c2.getDown().getColor().equals(current_color)))
                return true;
        }
        return false;
    }
    
    public void swapTwoCandy(Candy c1, Candy c2){
        String temp_type = c1.getType();
        String temp_color = c1.getColor();
        c1.setType(c2.getType());
        c1.setColor(c2.getColor());
        c2.setType(temp_type);
        c2.setColor(temp_color);
    }

    public void createTable(int xSize, int ySize, String fileAddress) throws IOException{
        this.X = xSize;
        this.Y = ySize;
        candyLists = new ArrayList<>();
        if (fileAddress.equals("")){
            Random rand = new Random();
            for(int i = 0; i < xSize; i++){
                ArrayList<Candy> candyList = new ArrayList<>();
                for(int j = 0; j < ySize; j++){
                    int p = rand.nextInt(candyColors.length);
                    Candy c = new Candy(i, j, candyTypes[0], candyColors[p]);
                    candyList.add(c);
                }
                candyLists.add(candyList);
            }
        }else{
            for(int i = 0; i < xSize; i++){
                ArrayList<Candy> l = new ArrayList<>();
                candyLists.add(l);
            }
            File f = new File(fileAddress);
            try (Scanner fileScanner = new Scanner(f)) {
                this.SCORE = fileScanner.nextInt();
                fileScanner.nextLine();
                int counter = 0;
                while(counter < ySize && fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine().replaceAll(" ", "");
                    String[] splited = line.split(",");
                    
                    for(int j = 0; j < splited.length; j++){
                        ArrayList<Candy> candyList = candyLists.get(j);
                        char[] ch = splited[j].toCharArray();
                        Candy c = new Candy(j, counter, String.valueOf(ch[0])+String.valueOf(ch[1]), String.valueOf(ch[2]));
                        candyList.add(c);
                    }    
                    
                    counter++;    
                }
            }
        }
        
    }
    
    public void connectCandies(){

        for (int i = 0; i < candyLists.size(); i++){
            ArrayList<Candy> candyList = candyLists.get(i);
            for(int j = 0; j < candyList.size(); j++){
                Candy c = candyList.get(j);
                if (i == 0){
                    c.setRight(candyLists.get(i+1).get(j));
                }else if (i == candyLists.size() - 1){
                    c.setLeft(candyLists.get(i-1).get(j));
                }else{
                    c.setRight(candyLists.get(i+1).get(j));
                    c.setLeft(candyLists.get(i-1).get(j));
                }
                
                if (j == 0){
                    c.setDown(candyLists.get(i).get(j+1));
                }else if(j == candyList.size() - 1){
                    c.setUp(candyLists.get(i).get(j-1));
                }else{
                    c.setUp(candyLists.get(i).get(j-1));
                    c.setDown(candyLists.get(i).get(j+1));
                }
            }
        }
    }

    public void printCandies(){
        for(int j = 0; j<this.Y; j++){
            for (int i =0; i<this.X; i++){
                System.out.print(candyLists.get(i).get(j)+"\t");
            }
            System.out.println();
        }
    }

    public void checkForCrush(){
        while(sequenseFinder()){        
            for(int i = 0; i < this.X; i++){
                for(int j = 0; j < this.Y; j++){
                    if (checkVerticalSequences(candyLists.get(i).get(j)) || checkHorizonalSequenses(candyLists.get(i).get(j))){
                        i = 0;
                        j = 0;
                        continue;
                    }
                }
            }
        }
    }

    public ArrayList<Candy> hintFinder(){
        ArrayList<Candy> result = new ArrayList<>();
        for(int i = 0; i<X; i++){
            for(int j = 0; j<Y; j++){
                String currentColor = candyLists.get(i).get(j).getColor();
                int countV = sequencesCount(candyLists.get(i).get(j), true);
                int countH = sequencesCount(candyLists.get(i).get(j), false);
                if (countV == 2){
                    boolean up_check = !(candyLists.get(i).get(j).getUp() == null);
                    boolean down_check = !(candyLists.get(i).get(j).getDown().getDown() == null);
                    if (up_check && (candyLists.get(i).get(j).getUp().getUp() != null) && 
                    candyLists.get(i).get(j).getUp().getUp().getColor().equals(currentColor)){
                        result.add(candyLists.get(i).get(j).getUp().getUp());
                        result.add(candyLists.get(i).get(j).getUp());
                        return result;
                    }
                    if (down_check && (candyLists.get(i).get(j).getDown().getDown().getDown() != null) &&
                    candyLists.get(i).get(j).getDown().getDown().getDown().getColor().equals(currentColor)){
                        result.add(candyLists.get(i).get(j).getDown().getDown());
                        result.add(candyLists.get(i).get(j).getDown().getDown().getDown());
                        return result;
                    }
                }
                if (countH == 2){
                    boolean left_check = candyLists.get(i).get(j).getLeft() != null;
                    boolean right_check = candyLists.get(i).get(j).getRight().getRight() != null;
                    if (left_check && candyLists.get(i).get(j).getLeft().getLeft() != null &&
                    candyLists.get(i).get(j).getLeft().getLeft().getColor().equals(currentColor)){
                        result.add(candyLists.get(i).get(j).getLeft());
                        result.add(candyLists.get(i).get(j).getLeft().getLeft());
                        return result;
                    }
                    if (right_check && candyLists.get(i).get(j).getRight().getRight().getRight() != null &&
                    candyLists.get(i).get(j).getRight().getRight().getRight().getColor().equals(currentColor)){
                        result.add(candyLists.get(i).get(j).getRight().getRight());
                        result.add(candyLists.get(i).get(j).getRight().getRight().getRight());
                        return result;
                    }
                }
            }
        }
        return null;
    }
}
