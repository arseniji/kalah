package io.github.arseniji.kalah.desktop;

import io.github.arseniji.kalah.api.GameSession;
import io.github.arseniji.kalah.api.GameView;
import io.github.arseniji.kalah.api.LocalSession;
import io.github.arseniji.kalah.bot.Difficulty;
import io.github.arseniji.kalah.bot.MinimaxBot;
import io.github.arseniji.kalah.bot.Personality;
import io.github.arseniji.kalah.core.Side;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class KalahApp extends Application {

    private final BoardView boardView = new BoardView();
    private final Label status = new Label();
    private final ComboBox<Difficulty> difficulty = new ComboBox<>();

    private GameSession session;
    private boolean busy = false;

    @Override
    public void start(Stage stage) {
        difficulty.getItems().addAll(Difficulty.values());
        difficulty.setValue(Difficulty.Medium);

        Button newGame = new Button("Новая игра");
        newGame.setOnAction(e -> startNewGame());

        Label diffLabel = new Label("Сложность:");
        diffLabel.setStyle("-fx-text-fill: #9C8A6A;");
        HBox top = new HBox(10, diffLabel, difficulty, newGame);
        top.setPadding(new Insets(12, 16, 12, 16));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle("-fx-background-color: #1A130D;");

        status.setStyle("-fx-text-fill: #F3E7CF; -fx-font-size: 15px; -fx-font-weight: bold;");
        HBox bottom = new HBox(status);
        bottom.setPadding(new Insets(12, 16, 12, 16));
        bottom.setStyle("-fx-background-color: #1A130D;");

        BorderPane root = new BorderPane(boardView, top, null, bottom, null);
        boardView.setOnPit(this::humanMove);

        startNewGame();

        stage.setTitle("Калах");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    private void startNewGame() {
        // человек за SOUTH, бот за NORTH; вся партия считается в этом процессе
        session = new LocalSession(null, new MinimaxBot(difficulty.getValue(), Personality.Balanced));
        busy = false;
        GameView view = session.view();
        boardView.show(new int[14], List.of(), false);
        render(view);
    }

    private void humanMove(int pit) {
        if (busy) return;
        busy = true;
        boardView.show(boardView.snapshotBoard(), List.of(), false);
        status.setText("Думаю...");

        // поиск уходит с потока отрисовки, иначе на большой глубине окно подвиснет
        Thread.ofPlatform().name("kalah-move").daemon(true).start(() -> {
            GameView view = session.move(pit);
            Platform.runLater(() -> play(view));
        });
    }

    private void play(GameView view) {
        Animation animation = boardView.animateAll(view.steps());
        animation.setOnFinished(e -> { busy = false; render(view); });
        animation.play();
    }

    private void render(GameView view) {
        boolean playable = !view.gameOver();
        boardView.show(view.board(), playable ? view.legalMoves() : List.of(), playable);

        if (view.gameOver()) {
            int south = view.board()[Side.SOUTH.store];
            int north = view.board()[Side.NORTH.store];
            String who = south > north ? "Победил SOUTH" : north > south ? "Победил NORTH" : "Ничья";
            status.setText(who + "   —   SOUTH " + south + " : " + north + " NORTH");
        } else {
            status.setText("Ход " + view.current() + "   —   выбери лунку");
        }
    }
}
