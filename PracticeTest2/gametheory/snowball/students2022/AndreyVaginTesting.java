package gametheory.snowball.students2022;

import gametheory.snowball.students2022.AndreyVaginCode;
import gametheory.snowball.Player;


public class AndreyVaginTesting {
    
    /**
     * This method simulates the battle between two agents.
     * 
     * @param firsPlayer   the first agent
     * @param secondPlayer the second agent
     * @return             tuple that contains payoffs of 
     *                     the agents after end of the game
     * 
     */
    static Tuple simulateGame(Player firsPlayer, Player secondPlayer){
        firsPlayer.reset();
        secondPlayer.reset();

        int snowFirst = 99;
        int snowSecond = 99;

        int minutesFirst = 0;
        int minutesSecond = 0;

        int attackFirst = 0;
        int attackSecond = 0;

        for (int i = 0; i < 60; i++) {
            int tempAttackFirst = firsPlayer.shootToOpponentField(attackSecond, snowFirst, minutesFirst);
            int tempAttackSecond = secondPlayer.shootToOpponentField(attackFirst, snowSecond, minutesSecond);
            
            int hotFirst = firsPlayer.shootToHotField(attackSecond, snowFirst, minutesFirst);
            int hotSecond = secondPlayer.shootToHotField(attackFirst, snowSecond, minutesSecond);
            
            if (tempAttackFirst != 0 || hotFirst != 0) {
                minutesFirst = 0;
            }

            if (tempAttackSecond != 0 || hotSecond != 0) {
                minutesSecond = 0;
            }

            snowFirst = snowFirst + 1 + tempAttackSecond - hotFirst - tempAttackFirst;
            snowSecond = snowSecond + 1 + tempAttackFirst - hotSecond - tempAttackSecond;

            minutesFirst++;
            minutesSecond++;
            
            attackFirst = tempAttackFirst;
            attackSecond = tempAttackSecond;
            
            // if (i % 4 == 3) {
            //     System.out.format("Round %d\n", i + 1);
            //     System.out.format("First attack %d\n", tempAttackFirst);
            //     System.out.format("First hot %d\n", hotFirst);
            //     System.out.format("First snow %d\n", snowFirst);
            //     System.out.println();
            // }
        }


        return new Tuple(snowFirst, snowSecond);
    }

    public static void main(String[] args) {
        
        Player[] players1 = new Player[4];
        players1[0] = new Cooperator();
        players1[1] = new Cheater();
        players1[2] = new Grudger();
        players1[3] = new AndreyVaginCode();

        Player[] players2 = new Player[4];
        players2[0] = new Cooperator();
        players2[1] = new Cheater();
        players2[2] = new Grudger();
        players2[3] = new AndreyVaginCode();

        int[] finalPayoffs = new int[4];
        int[] numberAgents = new int[4];
        numberAgents[0] = 7;
        numberAgents[1] = 3;
        numberAgents[2] = 3;
        numberAgents[3] = 5;

        /* Calculate payoffs for agents by counting all-with-all tournament. */
        for (int i = 0; i < 4; i++) {
            for (int j = 0; numberAgents[i] > 0 && j < 4; j++) {

                Tuple payoffs = simulateGame(players1[i], players2[j]);

                int numPlays = numberAgents[j];
                if (i == j && numPlays != 0) {
                    numPlays--;
                }

                finalPayoffs[i] += payoffs.firstPayoff * numPlays;
            }
        }

        System.out.format("Cooperator: %d\n", finalPayoffs[0]);
        System.out.format("Cheater: %d\n", finalPayoffs[1]);
        System.out.format("Grudger: %d\n", finalPayoffs[2]);
        System.out.format("My Agent: %d\n", finalPayoffs[3]);

        // Player firstPlayer = new Cooperator();
        // Player secondPlayer = new Cheater();

        // Tuple payoffs = simulateGame(firstPlayer, secondPlayer);

        // System.out.println(payoffs.firstPayoff);
        // System.out.println(payoffs.secondPayoff);
    }
}


/**
 * Implements COOPERATOR strategy.
 * Always sends snowballs to the hotfield.
 */
class Cooperator implements Player {

    int round = 0;

    @Override
    public void reset() {
        round = 0;

    }

    @Override
    public int shootToOpponentField(int opponentLastShotToYourField, int snowballNumber, int minutesPassedAfterYourShot) {
        round++;


        return 0;
    }

    @Override
    public int shootToHotField(int opponentLastShotToYourField, int snowballNumber, int minutesPassedAfterYourShot) {

        if (round % 4 == 0) {
            return Math.min(snowballNumber, maxSnowballsPerMinute(minutesPassedAfterYourShot));
        }

        return 0;
    }

    @Override
    public String getEmail() {
        // TODO Auto-generated method stub
        return null;
    }
    
}


/**
 * Implements CHEATER strategy.
 * Always shoots to the opponent field.
 */
class Cheater implements Player {

    int round = 0;

    @Override
    public void reset() {
        round = 0;
        
    }

    @Override
    public int shootToOpponentField(int opponentLastShotToYourField, int snowballNumber, int minutesPassedAfterYourShot) {
        round++;

        if (round % 4 == 0) {
            return Math.min(snowballNumber, maxSnowballsPerMinute(minutesPassedAfterYourShot));
        }

        return 0;
    }

    @Override
    public int shootToHotField(int opponentLastShotToYourField, int snowballNumber, int minutesPassedAfterYourShot) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getEmail() {
        // TODO Auto-generated method stub
        return null;
    }
    
}


/**
 * Implements GRUDGER strategy.
 * Always cooperates until onece is being betrayed.
 */
class Grudger implements Player {

    int round = 0;
    boolean betrayed = false;

    @Override
    public void reset() {
        
        round = 0;
        betrayed = false;
    }

    @Override
    public int shootToOpponentField(int opponentLastShotToYourField, int snowballNumber, int minutesPassedAfterYourShot) {
        round++;

        if (opponentLastShotToYourField != 0) {
            betrayed = true;
        }

        if (betrayed == true && round % 4 == 0) {
            return Math.min(snowballNumber, maxSnowballsPerMinute(minutesPassedAfterYourShot));
        }

        return 0;
    }

    @Override
    public int shootToHotField(int opponentLastShotToYourField, int snowballNumber, int minutesPassedAfterYourShot) {

        if (betrayed == false && round % 4 == 0) {
            int canShoot = Math.min(snowballNumber, maxSnowballsPerMinute(minutesPassedAfterYourShot));

            return canShoot;
        }

        return 0;
    }

    @Override
    public String getEmail() {
        
        return "a.vagin@innopolis.university";
    }
    
}

/**
 * Used to represent players payoffs in the snowball game.
 */
class Tuple {
    public final int firstPayoff;
    public final int secondPayoff;

    public Tuple(int firstPayoff, int secondPayoff) {
      this.firstPayoff = firstPayoff;
      this.secondPayoff = secondPayoff;
    }

    public boolean equals(Tuple tuple) {
        if (tuple.firstPayoff == this.firstPayoff && tuple.secondPayoff == this.secondPayoff) {
            return true;
        }
        return false;
    }
}