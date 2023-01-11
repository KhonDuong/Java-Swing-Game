import java.util.Scanner;

public class Player extends Entity
{
    public void createPlayer(String name)
    {
        setName(name);
        setCurrentHP(15);
        setMaxHP(15);
        setSTR(5);
        setDEX(5);
        setINT(5);
        setAGI(5);
    }
}
