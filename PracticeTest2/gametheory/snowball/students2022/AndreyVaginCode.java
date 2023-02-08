package gametheory.snowball.students2022;
import gametheory.snowball.Player;

/**
 * Implements modified GRUDGER strategy.
 * At last move sends snowballs to the opponent field even if it was not betrayed.
 */
public class AndreyVaginCode implements  Player{

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

        if (opponentLastShotToYourField != 0 || round == 60) {
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

            if (round == 56) {
                canShoot = Math.min(7, canShoot);
            }

            return canShoot;
        }

        return 0;
    }

    @Override
    public String getEmail() {
        
        return "a.vagin@innopolis.university";
    }
    
}
