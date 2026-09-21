package view;

import java.util.List;
import message.GameMessage;
import model.board.Board;
import model.player.Player;
import model.player.strategy.blockdirection.BlockTravelDirectionStrategy;
import view.presenter.EventPresenter;
import view.presenter.cannotmove.BlockRollTooSmallPresenter;
import view.presenter.cannotmove.EffectRollTooSmallPresenter;
import view.presenter.cannotmove.PieceBlockedPresenter;
import view.presenter.cannotmove.PieceNeedsExactRollPresenter;
import view.presenter.capture.BlockCapturedPresenter;
import view.presenter.capture.PieceCapturedPresenter;
import view.presenter.game.BoardStateReportedPresenter;
import view.presenter.game.GameOverPresenter;
import view.presenter.game.GameStartingPresenter;
import view.presenter.game.PlayerRosterAnnouncedPresenter;
import view.presenter.game.RoundStartedPresenter;
import view.presenter.move.BlockMovedPresenter;
import view.presenter.move.PieceDirectionAssignedPresenter;
import view.presenter.move.PieceEnteredBoardPresenter;
import view.presenter.move.PieceEnteredHomeStraightPresenter;
import view.presenter.move.PieceLeftBlockPresenter;
import view.presenter.move.PieceMovedPresenter;
import view.presenter.move.PieceReachedHomePresenter;
import view.presenter.mystery.BetaRestrictionAppliedPresenter;
import view.presenter.mystery.BetaRestrictionTriggeredPresenter;
import view.presenter.mystery.BlockEffectAssignedPresenter;
import view.presenter.mystery.IndividualEffectAssignedPresenter;
import view.presenter.mystery.MysteryCellAppearedPresenter;
import view.presenter.mystery.MysteryCellRelocatedPresenter;
import view.presenter.mystery.PieceDirectionReversedPresenter;
import view.presenter.mystery.PieceTeleportedPresenter;
import view.presenter.toss.DiceRolledPresenter;
import view.presenter.toss.TossStartingPresenter;
import view.presenter.toss.TossTiedPresenter;
import view.presenter.toss.TossWonPresenter;
import view.presenter.turn.HomeGateOpenedPresenter;
import view.presenter.turn.NoPieceMovablePresenter;
import view.presenter.turn.ThirdSixVoidedPresenter;
import view.presenter.turn.TurnRolledPresenter;
import view.presenter.turn.TurnStartedPresenter;

// Lists every event presenter in one place; adding an event means adding its presenter here.
public final class PresenterCatalog {

    private final BoardStateReportedPresenter boardStatePresenter;
    private final List<EventPresenter<? extends GameMessage>> presenters;

    public PresenterCatalog(
            List<Player> allPlayers, Board board, BlockTravelDirectionStrategy blockTravelDirectionStrategy) {
        this.boardStatePresenter =
                new BoardStateReportedPresenter(allPlayers, board, blockTravelDirectionStrategy);
        this.presenters = List.of(
                // game
                new GameStartingPresenter(),
                new RoundStartedPresenter(),
                new PlayerRosterAnnouncedPresenter(),
                boardStatePresenter,
                new GameOverPresenter(),
                // toss
                new TossStartingPresenter(),
                new DiceRolledPresenter(),
                new TossTiedPresenter(),
                new TossWonPresenter(),
                // turn
                new TurnStartedPresenter(),
                new TurnRolledPresenter(),
                new NoPieceMovablePresenter(),
                new ThirdSixVoidedPresenter(),
                new HomeGateOpenedPresenter(),
                // move
                new PieceMovedPresenter(board),
                new BlockMovedPresenter(),
                new PieceEnteredBoardPresenter(),
                new PieceDirectionAssignedPresenter(),
                new PieceEnteredHomeStraightPresenter(),
                new PieceReachedHomePresenter(),
                new PieceLeftBlockPresenter(),
                // cannotmove
                new PieceBlockedPresenter(),
                new PieceNeedsExactRollPresenter(),
                new BlockRollTooSmallPresenter(),
                new EffectRollTooSmallPresenter(),
                // capture
                new PieceCapturedPresenter(),
                new BlockCapturedPresenter(),
                // mystery
                new MysteryCellAppearedPresenter(),
                new MysteryCellRelocatedPresenter(),
                new PieceTeleportedPresenter(),
                new IndividualEffectAssignedPresenter(),
                new BlockEffectAssignedPresenter(),
                new BetaRestrictionAppliedPresenter(),
                new BetaRestrictionTriggeredPresenter(),
                new PieceDirectionReversedPresenter());
    }

    public List<EventPresenter<? extends GameMessage>> getPresenters() {
        return presenters;
    }

    // Round reports list players in play order, starting from the toss winner.
    public void setTurnOrder(List<Player> turnOrder) {
        boardStatePresenter.setTurnOrder(turnOrder);
    }
}
