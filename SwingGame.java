import java.awt.Color;
import java.awt.Dimension;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException; // Import the FileNotFoundException class to handle errors
import java.io.FileWriter; // Import FileWriter to write to files
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;

public class SwingGame extends JFrame
{
    // Game is short, generates monsters with unique behaviors and attributes and goal is to get through the few listed to make for a short experience.
    // This game is intended to show off the core COP 3330C concepts by:
    //      Creating a working Swing UI with various components, like buttons, text fields
    //      Writing to and reading from a file, through saving and loading the game state
    //      Abstraction through the Entity class, from which both monsters and players are derived
    //      Inheritance is shown through some useful self-made UI classes, and also monster and player
    //      Encapsulation by getting the name or stats through get/set methods
    //      Polymorphism, because some monsters will override the default attack inherited from the Entity class
    //      Passing message between objects, through exchanging damage between player or monster
    //      And event handling, taking button presses and triggering things with it

    // Player and monster are "global" attributes for the game system
    // So that the same player or monster can be referenced in various different methods
    Player player;
    JLabel playerLabel;
    StatsLabel playerHPLabel;
    StatsLabel playerSTRLabel;
    StatsLabel playerDEXLabel;
    StatsLabel playerINTLabel;
    StatsLabel playerAGILabel;
    JButton attackButton;
    JButton magicButton;
    JButton saveButton;

    Monster monster;
    JLabel monsterLabel;
    StatsLabel monsterHPLabel;
    StatsLabel monsterSTRLabel;
    StatsLabel monsterDEXLabel;
    StatsLabel monsterINTLabel;
    StatsLabel monsterAGILabel;

    int monsterRound;
    JLabel playerTurnLabel;
    JLabel monsterTurnLabel;

    // We've created a class that extends JFrame so we can use "this" to refer to the game window
    // Methods:
    //      SwingFrame(): Constructor, create the game window and start/load buttons
    //      newPlayerPrompt(): Creates the UI required to begin a new game
    //      instantiatePlayerUI(): Creates the UI for the player, either from new or loaded game
    //      instantiateMonsterUI(): Creates the UI for the monster, either from new or loaded game
    //      refreshPlayerUI(): Updates the current player UI rather than making a new one
    //      refreshMonsterUI(): Updates the current monster UI rather than making a new one
    //      monsterSpawner(): Controls what monster will spawn
    //      gameWon(): Clears the UI and leaves a victory message
    //      gameLost(): Clears the UI and leaves a defeat message
    //      saveGame(): Writes down the details for player and monster in savegame.txt
    //      loadGame(): Loads the details for player and monster from savegame.txt and calls instantiateUI functions
    SwingGame()
    {
        // Initialize Game Window
        this.setTitle("Swing Portfolio Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setSize(600, 400);
        this.setLayout(null);
        this.setVisible(true);
        this.getContentPane().setBackground(Color.white);

        // Create a start button and a load button
        JButton startButton = new JButton();
        startButton.setBounds(150, 125, 100, 75);
        startButton.setText("Start Game");

        JButton loadButton = new JButton();
        loadButton.setBounds(350, 125, 100, 75);
        loadButton.setText("Load Game");
        loadButton.setToolTipText("Looks for a savegame.txt, loads in the progress if found.");
        
        // Start button will prompt the user for a name
        startButton.addActionListener(e -> {
            startButton.setVisible(false);
            loadButton.setVisible(false);
            newPlayerPrompt();
            this.repaint();
            this.revalidate();
        });

        // Load button will look for savegame.txt and load the game if it exists
        loadButton.addActionListener(e -> {
            startButton.setVisible(false);
            loadButton.setVisible(false);
            loadGame();
            this.repaint();
            this.revalidate();
        });
        
        this.add(startButton);
        this.add(loadButton);
    }

    // Prompt the player to enter a name
    // And then once confirmed, the player and monster UI are created
    private void newPlayerPrompt()
    {
        JLabel enterNameLabel = new JLabel();
        enterNameLabel.setBounds(200, 100, 200, 30);
        enterNameLabel.setVerticalAlignment(JLabel.CENTER);
        enterNameLabel.setHorizontalAlignment(JLabel.CENTER);
        enterNameLabel.setText("Choose a new player name: ");
        this.add(enterNameLabel);

        JTextField enterNameField = new JTextField();
        enterNameField.setBounds(200, 140, 200, 30);
        this.add(enterNameField);

        // Once confirmed, will get rid of the username prompt
        // and then create the player and monster UI elements
        JButton confirmNameButton = new JButton();
        confirmNameButton.setBounds(225, 190, 150, 30);
        confirmNameButton.setText("Submit Name");
        confirmNameButton.addActionListener(e -> {
            enterNameLabel.setVisible(false);
            enterNameField.setVisible(false);
            confirmNameButton.setVisible(false);

            player = new Player();
            player.createPlayer(enterNameField.getText());
            instantiatePlayerUI();

            monsterRound = 1;
            monsterSpawner();
            this.repaint();
            this.revalidate();
        });

        this.add(confirmNameButton);
    }

    // The player UI consists of their name, stats, and buttons for the user to interact with
    private void instantiatePlayerUI()
    {
        // Player Name Text
        playerLabel = new JLabel();
        playerLabel.setText("Player: " + player.getName());
        playerLabel.setToolTipText("The goal of the game is to beat the few programmed monsters. Not difficult at all.");
        playerLabel.setBounds(25,250,120,50);
        playerLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
        playerLabel.setVerticalAlignment(JLabel.CENTER);
        playerLabel.setHorizontalAlignment(JLabel.LEFT);
        playerLabel.setOpaque(true);
        this.add(playerLabel);

        // StatsLabel is an extension of JLabel. They're all styled the same way,
        // so it's easier to move all the styling into its own class to improve readability.
        playerHPLabel = new StatsLabel(145,250,100,50, "HP: " + Integer.toString(player.getCurrentHP()) + "/" + Integer.toString(player.getMaxHP()));
        playerHPLabel.setToolTipText("You'll heal to full HP every new monster.");
        this.add(playerHPLabel);
        playerSTRLabel = new StatsLabel(25,300,90,30, "Strength: " + Integer.toString(player.getSTR()));
        playerSTRLabel.setToolTipText("How much physical damage you can deal.");
        this.add(playerSTRLabel);
        playerDEXLabel = new StatsLabel(25,330,90,30, "Dexterity: " + Integer.toString(player.getDEX()));
        playerDEXLabel.setToolTipText("How accurate your attacks are.");
        this.add(playerDEXLabel);
        playerINTLabel = new StatsLabel(105,300,90,30, "Intellect: " + Integer.toString(player.getINT()));
        playerINTLabel.setToolTipText("How much magic damage you can deal.");
        this.add(playerINTLabel);
        playerAGILabel = new StatsLabel(105,330,90,30, "Agility: " + Integer.toString(player.getAGI()));
        playerAGILabel.setToolTipText("How well you can dodge.");
        this.add(playerAGILabel);

        playerTurnLabel = new JLabel();
        playerTurnLabel.setBounds(150, 115, 300, 50);
        playerTurnLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
        playerTurnLabel.setVerticalAlignment(JLabel.CENTER);
        playerTurnLabel.setHorizontalAlignment(JLabel.LEFT);
        playerTurnLabel.setOpaque(true);
        this.add(playerTurnLabel);

        // Sword
        attackButton = new JButton();
        attackButton.setBounds(260, 260, 100, 30);
        attackButton.setText("Attack");
        attackButton.setToolTipText("<html>A basic physical attack that does damage<br>ranging from half your strength to your full strength.</html>");
        attackButton.addActionListener(e -> {
            if (monster.rollForHit(player.getDEX(), monster.getAGI(), 0)) {
                int damage = player.attackDamage();
                monster.setCurrentHP(monster.getCurrentHP() - damage);
                playerTurnLabel.setText("You attack the " + monster.getName() + " for " + Integer.toString(damage) + " physical damage.");
            } else {
                playerTurnLabel.setText("You swing at the " + monster.getName() + " but deal no damage.");
            }
            if (monster.getCurrentHP() <= 0) {
                monsterSpawner();
                player.setCurrentHP(player.getMaxHP());
                refreshPlayerUI();
            } else {
                refreshMonsterUI();
                if (player.rollForHit(monster.getDEX(), player.getAGI(), 0)) {
                    int damage = monster.attackDamage();
                    player.setCurrentHP(player.getCurrentHP() - damage);
                    monsterTurnLabel.setText("The " + monster.getName() + " hits you for " + Integer.toString(damage) + " damage.");
                } else {
                    monsterTurnLabel.setText("The " + monster.getName() + " tries to hit you but you dodge.");
                }
            }
            if (player.getCurrentHP() <= 0) {
                gameLost();
            } else {
                refreshPlayerUI();
            }
            this.repaint();
            this.revalidate();
        });
        this.add(attackButton);

        // Magic
        magicButton = new JButton();
        magicButton.setBounds(375, 260, 100, 30);
        magicButton.setText("Magic");
        magicButton.setToolTipText("A magical attack that does damage equal to your intellect.");
        magicButton.addActionListener(e -> {
            if (monster.rollForHit(player.getDEX(), monster.getAGI(), 1)) {
                int damage = player.attackDamage();
                monster.setCurrentHP(monster.getCurrentHP() - damage);
                playerTurnLabel.setText("You attack the " + monster.getName() + " for " + Integer.toString(damage) + " magic damage.");
            } else {
                playerTurnLabel.setText("You swing at the " + monster.getName() + " but deal no damage.");
            }
            if (monster.getCurrentHP() <= 0) {
                monsterSpawner();
                player.setCurrentHP(player.getMaxHP());
                refreshPlayerUI();
            } else {
                refreshMonsterUI();
                if (player.rollForHit(monster.getDEX(), player.getAGI(), 0)) {
                    int damage = monster.attackDamage();
                    player.setCurrentHP(player.getCurrentHP() - damage);
                    monsterTurnLabel.setText("The " + monster.getName() + " hits you for " + Integer.toString(damage) + " damage.");
                } else {
                    monsterTurnLabel.setText("The " + monster.getName() + " tries to hit you but you dodge.");
                }
            }
            if (player.getCurrentHP() <= 0) {
                gameLost();
            } else {
                refreshPlayerUI();
            }
            this.repaint();
            this.revalidate();
        });
        this.add(magicButton);

        // Save Game Button
        saveButton = new JButton();
        saveButton.setBounds(460, 315, 100, 30);
        saveButton.setText("Save Game");
        saveButton.setToolTipText("Saves current progress to savegame.txt");
        saveButton.addActionListener(e -> saveGame());
        this.add(saveButton);
    }

    private void instantiateMonsterUI()
    {
         // Monster Name Text
         monsterLabel = new JLabel();
         monsterLabel.setText("Monster: " + monster.getName());
         monsterLabel.setToolTipText(monster.getDescription());
         monsterLabel.setBounds(25,0,120,50);
         monsterLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
         monsterLabel.setVerticalAlignment(JLabel.CENTER);
         monsterLabel.setHorizontalAlignment(JLabel.LEFT);
         monsterLabel.setOpaque(true);
         this.add(monsterLabel);
 
        // StatsLabel is an extension of JLabel. They're all styled the same way,
        // so it's easier to move all the styling into its own class to improve readability.
         monsterHPLabel = new StatsLabel(145,0,100,50, "HP: " + Integer.toString(monster.getCurrentHP()) + "/" + Integer.toString(monster.getMaxHP()));
         monsterHPLabel.setToolTipText("A new monster will spawn when the current monster eaches zero HP.");
         this.add(monsterHPLabel);
         monsterSTRLabel = new StatsLabel(25,50,90,30, "Strength: " + Integer.toString(monster.getSTR()));
         monsterSTRLabel.setToolTipText("How much physical damage a monster can deal.");
         this.add(monsterSTRLabel);
         monsterDEXLabel = new StatsLabel(25,80,90,30, "Dexterity: " + Integer.toString(monster.getDEX()));
         monsterDEXLabel.setToolTipText("How accurate a monster is.");
         this.add(monsterDEXLabel);
         monsterINTLabel = new StatsLabel(105,50,90,30, "Intellect: " + Integer.toString(monster.getINT()));
         monsterINTLabel.setToolTipText("How much magic damage a monster can deal.");
         this.add(monsterINTLabel);
         monsterAGILabel = new StatsLabel(105,80,90,30, "Agility: " + Integer.toString(monster.getAGI()));
         monsterAGILabel.setToolTipText("How well a monster can dodge.");
         this.add(monsterAGILabel);

        monsterTurnLabel = new JLabel();
        monsterTurnLabel.setBounds(150, 185, 300, 50);
        monsterTurnLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
        monsterTurnLabel.setVerticalAlignment(JLabel.CENTER);
        monsterTurnLabel.setHorizontalAlignment(JLabel.LEFT);
        monsterTurnLabel.setOpaque(true);
        this.add(monsterTurnLabel);
    }

    private void refreshPlayerUI()
    {
        playerHPLabel.updateLabel("HP: " + Integer.toString(player.getCurrentHP()) + "/" + Integer.toString(player.getMaxHP()));;
        playerSTRLabel.updateLabel("Strength: " + Integer.toString(player.getSTR()));
        playerDEXLabel.updateLabel("Dexterity: " + Integer.toString(player.getDEX()));
        playerINTLabel.updateLabel("Intellect: " + Integer.toString(player.getINT()));
        playerAGILabel.updateLabel("Agility: " + Integer.toString(player.getAGI()));
    }

    private void refreshMonsterUI()
    {
        monsterLabel.setText("Monster: " + monster.getName());
        monsterHPLabel.updateLabel("HP: " + Integer.toString(monster.getCurrentHP()) + "/" + Integer.toString(monster.getMaxHP()));;
        monsterSTRLabel.updateLabel("Strength: " + Integer.toString(monster.getSTR()));
        monsterDEXLabel.updateLabel("Dexterity: " + Integer.toString(monster.getDEX()));
        monsterINTLabel.updateLabel("Intellect: " + Integer.toString(monster.getINT()));
        monsterAGILabel.updateLabel("Agility: " + Integer.toString(monster.getAGI()));
    }

    private void monsterSpawner()
    {
        if (monsterRound == 1) {
            monsterRound++;
            monster = new Slime();
            instantiateMonsterUI();
        } else if (monsterRound == 2) {
            monsterRound++;
            monster = new Goblin();
            refreshMonsterUI();
        } else if (monsterRound == 3) {
            monsterRound++;
            monster = new Orc();
            refreshMonsterUI();
        } else {
            gameWon();
        }
    }

    private void gameWon()
    {
        clearGameUI();
        JLabel wonLabel = new JLabel();
        wonLabel.setBounds(200, 150, 200, 30);
        wonLabel.setText("You won, thanks for playing.");
        this.add(wonLabel);
    }

    private void gameLost()
    {
        clearGameUI();
        JLabel lostLabel = new JLabel();
        lostLabel.setBounds(200, 150, 200, 30);
        lostLabel.setText("You lost, try again next time.");
        this.add(lostLabel);
    }

    private void clearGameUI()
    {
        playerLabel.setVisible(false);
        playerHPLabel.setVisible(false);
        playerSTRLabel.setVisible(false);
        playerDEXLabel.setVisible(false);
        playerINTLabel.setVisible(false);
        playerAGILabel.setVisible(false);
        attackButton.setVisible(false);
        magicButton.setVisible(false);
        saveButton.setVisible(false);
        monsterLabel.setVisible(false);
        monsterHPLabel.setVisible(false);
        monsterSTRLabel.setVisible(false);
        monsterDEXLabel.setVisible(false);
        monsterINTLabel.setVisible(false);
        monsterAGILabel.setVisible(false);
        playerTurnLabel.setVisible(false);
        monsterTurnLabel.setVisible(false);
    }
    // Save Game attempts to creates a file named savegame.txt
    // If it works, it'll overwrite the name and stats of both player and monster
    private void saveGame()
    {
        // Create file, check if it already exists
        try {
            File myObj = new File("savegame.txt");
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
            } else {
              System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // If it's created or exists, overwrite whatever is there
        try {
            FileWriter myWriter = new FileWriter("savegame.txt");

            // Write player information
            myWriter.write(player.getName() + System.lineSeparator());
            myWriter.write(Integer.toString(player.getCurrentHP()) + System.lineSeparator());
            myWriter.write(Integer.toString(player.getMaxHP()) + System.lineSeparator());
            myWriter.write(Integer.toString(player.getSTR()) + System.lineSeparator());
            myWriter.write(Integer.toString(player.getDEX()) + System.lineSeparator());
            myWriter.write(Integer.toString(player.getINT()) + System.lineSeparator());
            myWriter.write(Integer.toString(player.getAGI()) + System.lineSeparator());

            // Write monster information
            myWriter.write(monsterRound + System.lineSeparator());
            myWriter.write(monster.getName() + System.lineSeparator());
            myWriter.write(Integer.toString(monster.getCurrentHP()) + System.lineSeparator());
            myWriter.write(Integer.toString(monster.getMaxHP()) + System.lineSeparator());
            myWriter.write(Integer.toString(monster.getSTR()) + System.lineSeparator());
            myWriter.write(Integer.toString(monster.getDEX()) + System.lineSeparator());
            myWriter.write(Integer.toString(monster.getINT()) + System.lineSeparator());
            myWriter.write(Integer.toString(monster.getAGI()) + System.lineSeparator());
            myWriter.write(monster.getDescription() + System.lineSeparator());

            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    // loadGame attempts to load a file named savegame.txt
    // If it works, it'll restore the UI using the data available.
    // Can be used to manipulate stats by manually editting savegame.txt
    private void loadGame() {
        try {
            File myObj = new File("savegame.txt");
            Scanner scanner = new Scanner(myObj);
            player = new Player();
            player.setName(scanner.nextLine());
            player.setCurrentHP(Integer.parseInt(scanner.nextLine()));
            player.setMaxHP(Integer.parseInt(scanner.nextLine()));
            player.setSTR(Integer.parseInt(scanner.nextLine()));
            player.setDEX(Integer.parseInt(scanner.nextLine()));
            player.setINT(Integer.parseInt(scanner.nextLine()));
            player.setAGI(Integer.parseInt(scanner.nextLine()));

            monsterRound = Integer.parseInt(scanner.nextLine());
            monster = new Monster();
            monster.setName(scanner.nextLine());
            monster.setCurrentHP(Integer.parseInt(scanner.nextLine()));
            monster.setMaxHP(Integer.parseInt(scanner.nextLine()));
            monster.setSTR(Integer.parseInt(scanner.nextLine()));
            monster.setDEX(Integer.parseInt(scanner.nextLine()));
            monster.setINT(Integer.parseInt(scanner.nextLine()));
            monster.setAGI(Integer.parseInt(scanner.nextLine()));
            monster.setDescription(scanner.nextLine());
            System.out.println("Game successfully loaded from savegame.txt");
            scanner.close();
            instantiatePlayerUI();
            instantiateMonsterUI();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
