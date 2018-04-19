from board import Board
from board import BOARD_HEIGHT, BOARD_WIDTH


def test_top_border_terminal():
    board = Board()
    for square in board._squares[0]:
        assert square._spots[0]._is_terminal_spot and square._spots[1]._is_terminal_spot


def test_bottom_border_terminal():
    board = Board()
    for square in board._squares[BOARD_HEIGHT - 1]:
        assert square._spots[4]._is_terminal_spot and square._spots[5]._is_terminal_spot


def test_left_border_terminal():
    board = Board()
    for row in board._squares:
        assert row[0]._spots[6]._is_terminal_spot and row[0]._spots[7]._is_terminal_spot


def test_right_border_terminal():
    board = Board()
    for row in board._squares:
        assert row[BOARD_WIDTH -
                   1]._spots[2]._is_terminal_spot and row[BOARD_WIDTH -
                                                          1]._spots[3]._is_terminal_spot


def test_horizontal_connections():
    board = Board()
    for row in board._squares:
        prev = None
        for square in row:
            if prev is not None:
                assert square._spots[6]._next_card == prev._spots[3]
                assert square._spots[7]._next_card == prev._spots[2]
                assert prev._spots[2]._next_card == square._spots[7]
                assert prev._spots[3]._next_card == square._spots[6]
            prev = square


def test_vertical_connections():
    board = Board()
    prev_row = []
    for row in board._squares:
        for i in range(BOARD_WIDTH):
            square = row[i]
            if len(prev_row) == BOARD_WIDTH:
                prev = prev_row[i]
                assert prev._spots[4]._next_card == square._spots[1]
                assert prev._spots[5]._next_card == square._spots[0]
                assert square._spots[0]._next_card == prev._spots[5]
                assert square._spots[1]._next_card == prev._spots[4]
        prev_row = row
