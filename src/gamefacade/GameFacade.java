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
import java.util.List;
import java.util.Optional;
import ludoboard.Board;
import ludoboard.LudoBoard;
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

    private GameFacade() {
    }

    public static void startGame(long seed) {
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

        Player firstPlayer = determineFirstPlayer(players, dice, messages);
        playTurn(firstPlayer, dice, board, messages);
    }

    private static List<Player> buildPlayers() {
        return List.of(
            new RedPlayer(),
            new YellowPlayer(),
            new GreenPlayer(),
            new BluePlayer());
    }

    private static Player determineFirstPlayer(
            List<Player> players, Dice dice, GameMessagePublisher messages) {
        messages.publish(GameMessage.of(GameMessageType.TOSS_STARTING));

        Player firstPlayer = players.get(0);
        int highestRoll = 0;

        for (Player player : players) {
            int rollValue = dice.roll();
            messages.publish(GameMessage.diceRolled(player.getColor(), rollValue));

            if (rollValue > highestRoll) {
                highestRoll = rollValue;
                firstPlayer = player;
            }
        }

        messages.publish(GameMessage.tossWon(firstPlayer.getColor(), highestRoll));
        return firstPlayer;
    }

    // Rule 1: observe the dice face value, then move a piece that
    // many cells - represented as a MoveCommand so an invoker only
    // needs to know it holds a legal Command, not how it works.
    private static void playTurn(
            Player player, Dice dice, Board board, GameMessagePublisher messages) {
        messages.publish(GameMessage.turnStarted(player.getColor()));

        int rollValue = dice.roll();
        messages.publish(GameMessage.diceRolled(player.getColor(), rollValue));

        Optional<Piece> movablePiece = findMovablePiece(player);
        if (movablePiece.isPresent()) {
            Command moveCommand = new MoveCommand(player, movablePiece.get(), rollValue, board);
            moveCommand.execute();
        }
    }

    private static Optional<Piece> findMovablePiece(Player player) {
        return player.getPieces().stream()
            .filter(piece -> !piece.isAtBase())
            .findFirst();
    }
}
