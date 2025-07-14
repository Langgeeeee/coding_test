
# reverse list
def reverse_list(list_l):
    """
    Reverse a list without using any built-in functions.
    The function should return a reversed list.
    Input l is a list that may contain any type of data.
    """
    try:
        i = len(list_l)-1
        list_r = []
        while i >= 0:
            list_r.append(list_l[i])
            i -= 1
    except IndexError:
        print('Index out of range!')
        return False
    return list_r

# sudoku
def print_board(board):

    str_space = ' '
    for i in range(9):
        print(str_space.join(map(str, board[i])))

def find_empty_cell(board):

    for i in range(9):
        for j in range(9):
            if board[i][j] == 0:
                return i, j
    return None

# check rules
def is_valid(num, row, col, board):

#check row rules, make sure each colum contains 1 to 9
    for i in range(9):
        if board[row][i] == num:
            return False

#check colum rules, make sure each colum contains 1 to 9
    for j in range(9):
        if board[j][col] == num:
            return False

#check 3x3 section
    box3_row = row // 3
    box3_col = col // 3

    for i in range(box3_row*3, box3_row*3 + 3):
        for j in range(box3_col*3, box3_col*3 + 3):
            if board[i][j] == num and (i, j) != (row, col):
                return False

    return True

def solve_sudoku(board):
    """
    Write a program to solve a 9x9 Sudoku board.
    The board must be completed so that every row, column, and 3x3 section
    contains all digits from 1 to 9.
    Input: a 9x9 matrix representing the board.
    """

    is_empty = find_empty_cell(board)

    if not is_empty:
        return True     #if no empty cell, stop the process

    row, col = is_empty

    # print(f'checking the index now {is_empty}----------------------------')

    for num in range(1, 10):
        # print(f'checking {num}')
        if is_valid(num, row, col, board):
            board[row][col] = num

            #recusive call
            if solve_sudoku(board):
                return True

            #if recursive failed, go back
            # print(f'go back to {row, col}')
            board[row][col] = 0

    return False



if __name__ == "__main__":
# list reverse
    print('reverse list-------------------------------:')
    l = [1,2,3,'adc',4,5,6,'t']
    if reverse_list(l):
        print(reverse_list(l))

# sudoku
    print('sudoku algorithm---------------------------:')
    input_board = [
        [7, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 3, 6, 0, 0, 0, 0, 0],
        [0, 8, 0, 0, 9, 0, 2, 0, 0],
        [0, 5, 0, 0, 0, 7, 0, 0, 0],
        [0, 0, 2, 0, 4, 5, 7, 0, 0],
        [0, 0, 0, 1, 0, 0, 0, 3, 0],
        [0, 0, 1, 0, 0, 0, 0, 6, 8],
        [0, 0, 8, 5, 0, 2, 0, 1, 0],
        [0, 9, 0, 0, 0, 0, 4, 0, 0]
    ]
    print('Input Board:')
    print_board(input_board)
    print('After Solve Sudoku:')

    if solve_sudoku(input_board):
        print_board(input_board)
    else:
        print('No solution!')
