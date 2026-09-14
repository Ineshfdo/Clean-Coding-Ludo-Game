package gamefacade;

import command.Command;
import command.MoveCommand;
import dice.Dice;
import dice.SixSidedDice;
import gamemessage.GameMessage;
import gamemessage.GameMessageCenter;
import gamemessage.GameMessagePublisher;
import gamemessage.GameMessageType;
import gameoutput.ConsoleGameObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ludoboard.Board;
import ludoboard.LudoBoard;
import ludoboard.PlayerColor;
import numbergenerator.SeededRandomNumberGenerator;
import player.BluePlayer;
import player.GreenPlayer;
import player.Piece;
import player.Player;
import player.RedPlayer;
import player.YellowPlayer;

// Facade: Main only ever calls startGame(). Game logic here only
// publishes GameMessages; ConsoleGameObserver decides the wording
// (Observer pattern), so this class never calls System.out itself.
public final class GameFacade {

    private static final int TEST_ROUND_COUNT = 20;

    private GameFacade() {
    }

    public static void startGame(long seed) {
        GameMessageCenter.getInstance().clearObservers();
        GameMessagePublisher messages = GameMessageCenter.getInstance();
        messages.addObserver(new ConsoleGameObserver());

        messages.publish(GameMessage.of(GameMessageType.GAME_INITIALIZING));
        SeededRandomNumberGenerator.getInstance().setSeed(seed);

        Board board = LudoBoard.getInstance();
        messages.publish(GameMessage.of(GameMessageType.BOARD_INITIALIZED));

        Dice dice = SixSidedDice.getInstance();
        messages.publish(GameMessage.of(GameMessageType.DICE_INITIALIZED));

        List<Player> players = buildPlayers();
        messages.publish(GameMessage.of(GameMessageType.PLAYERS_CREATED));

        messages.publish(GameMessage.of(GameMessageType.GAME_STARTING));

        // The toss itself also proceeds clockwise; Red started.
        List<Player> tossOrder = buildTurnOrder(PlayerColor.RED, players, board);
        Player firstPlayer = determineFirstPlayer(tossOrder, dice, messages);
        List<Player> turnOrder = buildTurnOrder(firstPlayer.getColor(), players, board);

        
        for (int roundNumber = 1; roundNumber <= TEST_ROUND_COUNT; roundNumber++) {
            messages.publish(GameMessage.roundStarted(roundNumber));
            for (Player player : turnOrder) {
                playTurn(player, dice, board, messages);
            }
        }
    }

    private static List<Player> buildPlayers() {
        return List.of(
                new RedPlayer(),
                new YellowPlayer(),
                new GreenPlayer(),
                new BluePlayer());
    }

    // Rerolls everyone whenever two or more players tie for the
    // highest roll, since a toss must produce exactly one winner.
    private static Player determineFirstPlayer(
            List<Player> players, Dice dice, GameMessagePublisher messages) {
        messages.publish(GameMessage.of(GameMessageType.TOSS_STARTING));

        while (true) {
            Player highestRoller = players.get(0);
            int highestRoll = 0;
            int highestRollerCount = 0;

            for (Player player : players) {
                int rollValue = dice.roll();
                messages.publish(GameMessage.diceRolled(player.getColor(), rollValue));

                if (rollValue > highestRoll) {
                    highestRoll = rollValue;
                    highestRoller = player;
                    highestRollerCount = 1;
                } else if (rollValue == highestRoll) {
                    highestRollerCount++;
                }
            }

            if (highestRollerCount == 1) {
                messages.publish(GameMessage.tossWon(highestRoller.getColor(), highestRoll));
                return highestRoller;
            }

            messages.publish(GameMessage.tossTied(highestRoll));
        }
    }

    // Play clockwise around the board starting from a given color - the toss's fixed start, or later the winner's.
    private static List<Player> buildTurnOrder(
            PlayerColor startingColor, List<Player> players, Board board) {
        List<Player> turnOrder = new ArrayList<>();
        PlayerColor color = startingColor;

        for (int position = 0; position < players.size(); position++) {
            turnOrder.add(findPlayerByColor(players, color));
            color = board.getNextColorClockwise(color);
        }

        return turnOrder;
    }

    private static Player findPlayerByColor(List<Player> players, PlayerColor color) {
        return players.stream()
                .filter(player -> player.getColor() == color)
                .findFirst()
                .orElseThrow();
    }

    // Rule 1: observe the dice face value, then move a piece that  many cells - represented as a MoveCommand so an invoker only needs to know it holds a legal Command.
    private static void playTurn(
            Player player, Dice dice, Board board, GameMessagePublisher messages) {
        messages.publish(GameMessage.turnStarted(player.getColor()));

        int rollValue = dice.roll();
        messages.publish(GameMessage.turnRolled(player.getColor(), rollValue));

        Optional<Piece> movablePiece = findMovablePiece(player);
        if (movablePiece.isPresent()) {
            Piece piece = movablePiece.get();
            Command moveCommand = new MoveCommand(player, piece, rollValue, board);
            moveCommand.execute();
            messages.publish(GameMessage.pieceMoved(piece.toString(), piece.getTrackPosition()));
        } else {
            messages.publish(GameMessage.noPieceMovable());
        }
    }

    private static Optional<Piece> findMovablePiece(Player player) {
        return player.getPieces().stream()
                .filter(piece -> !piece.isAtBase())
                .findFirst();
    }
}
