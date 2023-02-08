package gametheory.snowball;

public interface Player {

    /**
     * This method is called to reset the agent before the match
     * with each player containing several rounds
     */
    void reset();

    /**
     * This method returns the number of snowballs the player will shoot
     * to opponent’s field based on the last shot of the opponent to your
     * field and the number of minutes passed after your previous shot.
     * Initially, previous opponent's shot to your field is 0, the number
     * of balls is equal to 100 and 0 minutes passed after your previous shot
     *
     * @param opponentLastShotToYourField the last shot of the opponent to your
     *                                    field (0 – if this is the first shot)
     * @param snowballNumber              the number of snowballs on your field
     * @param minutesPassedAfterYourShot  the number of minutes passed after
     *                                    your last shot to any field
     *                                    (0 – if this is the first shot)
     * @return                            the number of snowballs the player
     *                                    will shoot to opponent's field
     */
    int shootToOpponentField(int opponentLastShotToYourField, int snowballNumber,
                             int minutesPassedAfterYourShot);

    /**
     * This method returns the number of snowballs the player will shoot
     * to hot field based on the last shot of the opponent to your
     * field and the number of minutes passed after your previous shot.
     * Initially, previous opponent's shot to your field is 0, the number
     * of balls is equal to 100 and 0 minutes passed after your previous shot
     *
     * @param opponentLastShotToYourField the last shot of the opponent to your
     *                                    field (0 – if this is the first shot)
     * @param snowballNumber              the number of snowballs on your field
     * @param minutesPassedAfterYourShot  the number of minutes passed after
     *                                    your last shot to any field
     *                                    (0 – if this is the first shot)
     * @return the number of snowballs the player will shoot to hot field
     */
    int shootToHotField(int opponentLastShotToYourField, int snowballNumber,
                        int minutesPassedAfterYourShot);

    /**
     * This method returns your Innopolis email
     *
     * @return your Innopolis email
     */
    String getEmail();

    /**
     * This default method calculates the maximum number of snowballs
     * which can be shot by a player per minute based on the number of minutes
     * passed after the previous shot
     *
     * @param minutesPassedAfterYourShot the number of minutes passed after
     *                                   your last shot to any field
     *                                   (0 – if this is the first shot)
     * @return the maximum number of snowballs which can be shot by a player
     *         per minute
     */
    default int maxSnowballsPerMinute(int minutesPassedAfterYourShot) {
        double exp = Math.exp(minutesPassedAfterYourShot);
        return (int) (15 * exp / (15 + exp));
    }
}
