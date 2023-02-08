import sys
import os
import random
import json
import logging
from typing import List, Literal, Set, Union, Tuple


DAY = 12
MONTH = 9
YEAR = 2002
MIN_POSITION = 1
FINAL_POSITION = DAY + MONTH + YEAR
MIN_MOVE = 1
MAX_MOVE = DAY + MONTH
LOG_FILES_DIR = 'log_files'
DATA_FILE = 'data_file.json'


def clear():
    '''Clears the terminal.
    '''
    os.system('clear')


def get_mode(
        mode: str
    ) -> Literal['smart', 'random', 'advisor']:
    '''Returns mode of play according to it's first letter.
    
    Args:
        mode: First letter of given play mode.
            Can take values 's', 'r', 'a'
    '''
    if mode == 's':
        return 'smart'
    elif mode == 'r':
        return 'random'
    elif mode == 'a':
        return 'advisor'


def find_fix_points(
        min_position: int,
        final_position: int,
        min_move: int,
        max_move: int
    ) -> Set[int]:
    '''Returns a set of fix points for a game with given parameters.
    '''
    w = [set()]

    w.append(set(i for i in range(final_position - max_move, final_position + max_move)))

    moves = set(i for i in range(min_move, max_move + 1))

    while True:
        new_w = set()
        
        least_current_fix_point = list(w[-1])[0]

        left_bound = least_current_fix_point - 2 * max_move
        left_bound = min_position if left_bound < min_position else left_bound

        right_bound = least_current_fix_point + 2 * max_move
        right_bound = final_position if right_bound > final_position else right_bound

        for number in range(left_bound, right_bound):
            for i in moves:
                if (number + i) not in w[-1] and number not in w[-1]:
                    entry = [(number + i + j) in w[-1] for j in moves]
                    if all(entry) and number not in new_w:
                        new_w.add(number)

        if len(new_w) == 0:
            break
        
        w.append(new_w | w[-1])

    return w[-1]


fix_points = find_fix_points(MIN_POSITION, FINAL_POSITION, MIN_MOVE, MAX_MOVE)


def find_lose_points(
        min_position: int,
        final_position: int,
        min_move: int,
        max_move: int
    ) -> List[int]:
    '''Returns points which are not in the fix_points set for a given game.
    '''
    fix = find_fix_points(min_position, final_position, min_move, max_move)
    all_points = set([i for i in range(min_position, final_position + max_move)])
    lose_points = list(all_points - fix)
    return sorted(lose_points)


lose_points = find_lose_points(MIN_POSITION, FINAL_POSITION, MIN_MOVE, MAX_MOVE)


def find_nearest_lose_point(position: int) -> Union[int, None]:
    '''Returns the nerest lose point that is bigger than given position.
    '''
    for point in lose_points:
        if point > position:
            return point
    return None


def choose_init_postion() -> int:
    '''Parses user input to define initial position for the play.

    Returns:
        Initial position for the play in range [MIN_POSITION, FINAL_POSITION).
    '''
    while True:
        try:
            mode = input('If you want to start from random position enter "r".\n' + 
                         'If you want to choose your own start position enter "c"\n')
            assert mode == 'r' or mode == 'c'
        except AssertionError:
            clear()
            print('Invalid input. Try again.\n')
        else:
            clear()
            break

    if mode == 'r':
        init_position = random.randrange(MIN_POSITION, FINAL_POSITION)
        return init_position
    
    while True:
        try:
            init_position = input(f'Enter an integer number from {MIN_POSITION} to {FINAL_POSITION - 1} as initial position for the play.\n')
            init_position = int(init_position)
            assert init_position >= MIN_POSITION and init_position < FINAL_POSITION
        except ValueError:
            clear()
            print('The input should be an integer number. Try again.\n')
        except AssertionError:
            clear()
            print('Position is out of range. Try again.\n')
        else:
            clear()
            return init_position


def choose_playing_mode() -> Literal['s', 'r', 'a']:
    '''Parses user input to define play mode.

    s - smart mode
    r - random mode
    a - advisor mode
    '''
    print('Now you should choose playing mode.\n')
    while True:
        try:
            mode = input('Enter\n' +
                         '      "s" to play in smart mode\n' +
                         '      "r" to play in random mode\n' +
                         '      "a" to play in advisor mode\n')
            assert mode == 's' or mode == 'r' or mode == 'a'
        except AssertionError:
            clear()
            print('Invalid input. Try again.\n')
        else:
            clear()
            return mode


def read_user_move() -> int:
    '''Parses user input to define user's move.

    Returns:
        Read user move as int in range [MIN_MOVE, MAX_MOVE].
    '''
    while True:
        try:
            move = input(f'Enter your move as number from {MIN_MOVE} to {MAX_MOVE}\n')
            move = int(move)
            assert move >= MIN_MOVE and move <= MAX_MOVE
        except ValueError:
            print('The input should be an integer number. Try again.\n')
        except AssertionError:
            print('Move is out of allowed range. Try again.\n')
        else:
            return move


def random_comp_move(position: int) -> int:
    '''Returns computer's move for random play mode.

    Args:
        position: Position from where computer is moving.
            Arument is needed for compatibility with smart_comp_move() interface.

    Returns:
        Random computer move as int in range [MIN_MOVE, MAX_MOVE].
    '''
    comp_move = random.randint(MIN_MOVE, MAX_MOVE)
    return comp_move


def smart_comp_move(position: int) -> int:
    '''Apllies winnig strategy for computer's move if possible.

    If there is no winnig strategy returns random move.

    Args:
        position: Position from whre computer should make a move.

    Returns:
        Computer move as int in range [MIN_MOVE, MAX_MOVE].
    '''
    if (FINAL_POSITION - position) <= MAX_MOVE:
        move = FINAL_POSITION - position

    elif position in fix_points:
        new_position = find_nearest_lose_point(position)
        move = new_position - position

    else:
        move = random_comp_move(position)

    return move


def advice_move(position: int):
    '''Prints advice for player's move.

    If there is exists a winning strategy from current postion advices needed move.
    Otherwise prints that there is no winning strategy from this position

    Args:
        position: User's current position.
    '''
    if position in lose_points:
        print('There is no winning strategy in this postion.')
        return

    if (FINAL_POSITION - position) <= MAX_MOVE:
        move = FINAL_POSITION - position

    else:
        new_position = find_nearest_lose_point(position)
        move = new_position - position
    
    print(f'Make a move of {move} to apply the winning strategy')


def get_play_index() -> int:
    '''Returns index of current play.

    This function is needed for naming log files. If directory for saved log
    files does not exists method creates such directory with .json file that
    keep number of already saved games (initially zero).

    On each call function reads .json file to give an index for a new play and
    increases the number of saved plays.
    '''
    if not os.path.exists(LOG_FILES_DIR):
        os.makedirs(LOG_FILES_DIR)
        with open(LOG_FILES_DIR + '/' + DATA_FILE, "w") as write_file:
            data = {
                'log_files_number': 0
            }
            json.dump(data, write_file)

    with open(LOG_FILES_DIR + '/' + DATA_FILE, "r") as read_file:
        data = json.load(read_file)

    data['log_files_number'] += 1

    with open(LOG_FILES_DIR + '/' + DATA_FILE, "w") as write_file:
        json.dump(data, write_file)

    index = data['log_files_number']
    return index


def play(
        init_position: int,
        mode: Literal['r', 's', 'a']
    ) -> Tuple[int, int]:
    '''Implementation of a play in the game.

    Args:
        init_position: Initial position of the play in range [MIN_POSITION, FINAL_POSITION).
        mode: Game mode (random, smart, advisor).

    Returns:
        Payoff of the play. (1, 0) if player wins, (0, 1) otherwise.
    '''
    play_index = get_play_index()
    logging.basicConfig(
        filename=LOG_FILES_DIR + f'/{play_index}_play.log',
        level=logging.INFO,
        filemode='w',
        force=True
    )

    if mode == 'r':
        make_move_comp = random_comp_move
    else:
        make_move_comp = smart_comp_move

    if mode == 'a':
        make_advice = advice_move
    else:
        make_advice = lambda position: None

    position = init_position

    print('This is the beginning of the play.\n')
    logging.info(f'This is the beginning of the play in {get_mode(mode)} mode.')
    logging.info(f'Final position is {FINAL_POSITION}')
    logging.info(f'Allowed moves are in range from {MIN_MOVE} to {MAX_MOVE}')
    logging.info(f'Initial position is {init_position}')
    while True:
        print('Now it is your turn.')
        print(f'To win you should reach {FINAL_POSITION} after your move.')
        print(f'Current position is {position}.')

        make_advice(position)
        user_move = read_user_move()
        position += user_move

        print(f'Postion after your move: {position}\n')
        logging.info(f'Player made a move of {user_move}.')
        logging.info(f'Current position is {position}.')

        if position >= FINAL_POSITION:
            print('Congratulations! You won.')
            logging.info('Player reached the final postion.')
            return (1, 0)
        
        print("Now it is computer's turn")

        comp_move = make_move_comp(position)
        position += comp_move

        print(f'Computer made a move for {comp_move} points')
        print(f"Position after computer's move is {position}\n")
        logging.info(f'Computer made a move of {comp_move}.')
        logging.info(f'Current position is {position}.')

        if position >= FINAL_POSITION:
            print('Computer reached final postion. You lost')
            logging.info('Computer reached the final postion.')
            return (0, 1)


def game():
    '''Implementation of the game.
    '''
    human_score = 0
    computer_score = 0

    clear()
    while True:
        try:
            command = input('Enter "s" to start a new play.\n\n' + 
                            'You can always press "Ctrl + C" to quit the game.\n')
            assert command == 's'
            clear()

            init_position = choose_init_postion()
            mode = choose_playing_mode()

            human_payoff, computer_payoff = play(init_position, mode)
            human_score += human_payoff
            computer_score += computer_payoff

            print('Current score:')
            print('you : computer')
            print(f'  {human_score} : {computer_score}\n\n')
            
        except AssertionError:
            clear()
            print('Invalid input. Try again.\n')

        except KeyboardInterrupt:
            clear()
            print('Goodbye\n')
            sys.exit(0)


if __name__ == '__main__':
    game()