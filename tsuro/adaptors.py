from admin import GameState, TsuroGame


class CPlayerInterface:
    def initialize(cls, color, other_colors):
        raise NotImplementedError

    def place_pawn(self, board):
        raise NotImplementedError

    def play_turn(self, board, tiles, tile_count):
        raise NotImplementedError

    def end_game(self, board, color):
        raise NotImplementedError


class PlayerAdaptor(CPlayerInterface):
    """An adaptor class to adapt our Player to the class's Player."""

    def __init__(self, player):
        self.player = player
        self.other_colors =

    def get_name(self):
        return self.player.name

    def initialize(self, color, other_colors):
        self.player.color = color

    def place_pawn(self, board):
        pass

    def play_turn(self, board, tiles, tile_count):
        pass

    def end_game(self, board, color):
        pass


class AdministratorAdaptor:
    """An adaptor class to adapt our TsuroGame to the class's Administrator."""

    def play_a_turn(self, deck, active_players, eliminated_players, board, tile_placement):
        state = self._convert_to_game_state(deck, active_players, eliminated_players, board, tile_placement)
        game = TsuroGame.from_state(state)
        game.play_turn(tile_placement)

        (_deck_state, _active_players, _dragon_holder, _eliminated_players, _board_state) = game.state()

        end_indicator = _active_players if game.ended() else False
        return (
            list(_deck_state),
            _active_players,
            _eliminated_players,
            _board_state,
            end_indicator
        )

    def legal_play(self):
        pass

    # To convert to and from different interfaces, override _convert_to_{state} methods in a subclass.
    def _convert_to_game_state(self, deck, active_players, eliminated_players, board, tile_placement):
        return GameState(
            active_players=active_players,
            eliminated_players=eliminated_players,
            dragon_holder=None,
            board_state=self._convert_to_board_state(board),
            deck_state=self._convert_to_deck_state(deck),
        )

    def _convert_to_deck_state(self, deck):
        return deck

    def _convert_to_board_state(self, board):
        return board
