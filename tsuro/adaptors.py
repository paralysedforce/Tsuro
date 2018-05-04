from abc import ABC, abstractmethod
from admin import GameState, TsuroGame


class AdminAdaptorInterface(ABC):
    @abstractmethod
    def play_a_turn(self, deck, active_players, eliminated_players, board, tile_placement):
        pass

    @abstractmethod
    def legal_play(self):
        pass


class AdminAdaptor(AdminAdaptorInterface):
    """An adaptor class to adapt our TsuroGame to the class's standard Administrator interface."""

    def play_a_turn(self, deck, active_players, eliminated_players, board, tile_placement):
        state = self._convert_to_game_state(deck, active_players, eliminated_players, board, tile_placement)
        game = TsuroGame.from_state(state)
        game.play_turn(tile_placement)

        state = game.state()
        end_indicator = state.active_players if game.ended() else False
        return (
            list(state.deck_state),
            state.active_players,
            state.eliminated_players,
            state.board_state,
            end_indicator
        )

    def legal_play(self):
        pass

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


class PlayerAdaptorInterface(ABC):
    @abstractmethod
    def initialize(cls, color, other_colors):
        raise NotImplementedError

    @abstractmethod
    def place_pawn(self, board):
        raise NotImplementedError

    @abstractmethod
    def play_turn(self, board, tiles, tile_count):
        raise NotImplementedError

    @abstractmethod
    def end_game(self, board, color):
        raise NotImplementedError
