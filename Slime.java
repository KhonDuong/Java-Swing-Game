public class Slime extends Monster {
    Slime()
    {
        setName("Slime");
        setCurrentHP(5);
        setMaxHP(5);
        setSTR(999);
        setDEX(0);
        setINT(0);
        setAGI(0);
        setDescription("Takes no damage from physical attacks, use magic!");
    }

    public int attackDamage() {
        return 999999999;
    }

    public boolean rollForHit(int attackerDex, int defenderAgi, int attackType)
    {
        if (attackType == 0) {
            System.out.println("Slimes don't take physical damage.");
            return false;
        } else {
            if (attackerDex <= 0) {
                return false;
            } else if (defenderAgi <= 0) {
                return true;
            } else {
                double percentToHit = attackerDex / defenderAgi;
                double rng = Math.random();
                if (percentToHit > rng) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
}
