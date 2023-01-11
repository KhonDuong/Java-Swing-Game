abstract public class Entity
{
    String name;
    int STR, DEX, INT, AGI, maxHP, currentHP;

    public void getStats()
    {
        System.out.println("Strength: " + this.STR);
        System.out.println("Dexterity: " + this.DEX);
        System.out.println("Intellect: " + this.INT);
        System.out.println("Agility: " + this.AGI);
        System.out.println("HP: " + currentHP + "/" + maxHP);
    }

    public int getSTR()
    {
        return this.STR;
    }

    public void setSTR(int STR)
    {
        this.STR = STR;
    }

    public int getDEX()
    {
        return this.DEX;
    }

    public void setDEX(int DEX)
    {
        this.DEX = DEX;
    }

    public int getINT()
    {
        return this.INT;
    }

    public void setINT(int INT)
    {
        this.INT = INT;
    }

    public int getAGI()
    {
        return this.AGI;
    }

    public void setAGI(int AGI)
    {
        this.AGI = AGI;
    }

    public int getMaxHP()
    {
        return this.maxHP;
    }

    public void setMaxHP(int maxHP)
    {
        this.maxHP = maxHP;
    }

    public int getCurrentHP()
    {
        return this.currentHP;
    }

    public void setCurrentHP(int currentHP)
    {
        this.currentHP = currentHP;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    // Basic attack damage is anywhere from half, to the max strength value
    public int attackDamage()
    {
        int min = this.STR / 2;  
        int max = this.STR;  
        return (int) (Math.random() * (max - min + 1) + min);
    }

    // Basic magic damage is always int value
    public int magicDamage()
    {
        return this.INT;
    }

    // If attacker's dexterity is higher than defender's agi, always hits
    // If attacker's dexterity is lower, randomly determine if it hits based on accuracy
    // 0 dex is always a miss
    // 0 agi is always a hit
    // True is a hit, false is a miss
    public boolean rollForHit(int attackerDex, int defenderAgi, int attackType)
    {
        if (attackerDex <= 0) {
            System.out.println("Attacker can never hit.");
            return false;
        } else if (defenderAgi <= 0) {
            System.out.println("Defender can never dodge.");
            return true;
        } else {
            double percentToHit = (double) attackerDex / defenderAgi;
            double rng = Math.random();
            System.out.println("Attack chance: " + Double.toString(percentToHit));
            System.out.println("Rolled: " + Double.toString(rng));
            if (percentToHit > rng) {
                System.out.println("Hit");
                return true;
            } else {
                System.out.println("Missed");
                return false;
            }
        }
    }
}
