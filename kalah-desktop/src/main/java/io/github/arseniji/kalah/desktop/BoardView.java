package io.github.arseniji.kalah.desktop;

import io.github.arseniji.kalah.api.MoveStep;
import io.github.arseniji.kalah.core.Side;
import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Доска. Про правила не знает: получает массив из 14 чисел, список доступных лунок
 * и путь посева, по которому проигрывает падение камней.
 */
public class BoardView extends Pane {

    private static final double W = 980, H = 420;
    private static final double PIT_R = 44, STORE_W = 76;
    private static final double STONE_FLIGHT_MS = 170;
    private static final double STONE_GAP_MS = 95;

    private static final Color PIT_FILL   = Color.web("#53412C");
    private static final Color PIT_LEGAL  = Color.web("#7E6435");
    private static final Color PIT_HOVER  = Color.web("#C79A46");
    private static final Color STORE_FILL = Color.web("#3A2C1D");
    private static final Color CAPTURE    = Color.web("#C24A38");
    private static final Color TEXT       = Color.web("#F3E7CF");

    private final Shape[] shape = new Shape[14];
    private final Text[] label = new Text[14];
    private final double[] cx = new double[14];
    private final double[] cy = new double[14];
    private final Group stoneLayer = new Group();

    private int[] board = new int[14];
    private List<Integer> legal = List.of();
    private boolean interactive = false;
    private IntConsumer onPit = pit -> { };

    public BoardView() {
        setPrefSize(W, H);
        setStyle("-fx-background-color: #241C14;");
        layoutPits();
        getChildren().add(stoneLayer);
    }

    public void setOnPit(IntConsumer onPit) { this.onPit = onPit; }

    private void layoutPits() {
        double midY = H / 2, rowGap = 108;
        double firstX = 200, stepX = 116;

        for (int c = 0; c < 6; c++) {
            addPit(Side.NORTH.last - c, firstX + c * stepX, midY - rowGap / 2, false);
            addPit(Side.SOUTH.first + c, firstX + c * stepX, midY + rowGap / 2, false);
        }
        addPit(Side.NORTH.store, 96, midY, true);
        addPit(Side.SOUTH.store, W - 96, midY, true);
    }

    private void addPit(int pit, double x, double y, boolean store) {
        cx[pit] = x; cy[pit] = y;

        Shape s;
        if (store) {
            Rectangle r = new Rectangle(x - STORE_W / 2, y - 150, STORE_W, 300);
            r.setArcWidth(56); r.setArcHeight(56);
            r.setFill(STORE_FILL);
            s = r;
        } else {
            Circle c = new Circle(x, y, PIT_R, PIT_FILL);
            s = c;
        }
        s.setStroke(Color.web("#1A130D"));
        s.setStrokeWidth(2);

        Text t = new Text(String.valueOf(0));
        t.setFill(TEXT);
        t.setFont(Font.font("System", store ? 30 : 24));
        t.setMouseTransparent(true);

        shape[pit] = s;
        label[pit] = t;
        getChildren().addAll(s, t);
        centerText(pit);

        if (!store) {
            s.setOnMouseClicked(e -> { if (clickable(pit)) onPit.accept(pit); });
            s.setOnMouseEntered(e -> { if (clickable(pit)) s.setFill(PIT_HOVER); });
            s.setOnMouseExited(e -> paintPit(pit));
        }
    }

    private boolean clickable(int pit) { return interactive && legal.contains(pit); }

    private void centerText(int pit) {
        Text t = label[pit];
        t.setX(cx[pit] - t.getLayoutBounds().getWidth() / 2);
        t.setY(cy[pit] + t.getLayoutBounds().getHeight() / 4);
    }

    private void setCount(int pit, int value) {
        board[pit] = value;
        label[pit].setText(String.valueOf(value));
        centerText(pit);
    }

    private void paintPit(int pit) {
        if (pit == Side.SOUTH.store || pit == Side.NORTH.store) { shape[pit].setFill(STORE_FILL); return; }
        shape[pit].setFill(interactive && legal.contains(pit) ? PIT_LEGAL : PIT_FILL);
    }

    /** Текущая показанная позиция — нужна, чтобы перерисовать доску без подсветки. */
    public int[] snapshotBoard() { return board.clone(); }

    /** Показать позицию мгновенно, без анимации. */
    public void show(int[] board, List<Integer> legal, boolean interactive) {
        this.board = board.clone();
        this.legal = legal;
        this.interactive = interactive;
        stoneLayer.getChildren().clear();
        for (int pit = 0; pit < 14; pit++) { setCount(pit, board[pit]); paintPit(pit); }
    }

    /** Анимация одного хода: камни разлетаются по пути посева, затем доигрывается захват.
     *  Стартовая доска передаётся явно — при цепочке ходов её нельзя брать из поля,
     *  потому что анимации строятся заранее, а поле меняется только по ходу проигрывания. */
    public Animation animate(int[] from, MoveStep step) {
        int[] work = from.clone();
        SequentialTransition seq = new SequentialTransition();

        seq.getChildren().add(run(() -> { setCount(step.pit(), 0); pulse(step.pit()); }));
        work[step.pit()] = 0;

        for (int target : step.sowPath()) {
            work[target]++;
            final int tgt = target, value = work[tgt], src = step.pit();
            Animation tick = pause(STONE_GAP_MS);
            // счётчик растёт в момент приземления камня, а не в момент запуска
            tick.setOnFinished(e -> flyStone(src, tgt, () -> { setCount(tgt, value); pulse(tgt); }));
            seq.getChildren().add(tick);
        }
        seq.getChildren().add(pause(STONE_FLIGHT_MS));   // дать долететь последнему

        // захват и подметание доска уже посчитала — сверяем и доигрываем разницу
        int[] end = step.board();
        if (!java.util.Arrays.equals(work, end)) {
            seq.getChildren().add(pause(120));
            seq.getChildren().add(run(() -> captureFlash(work, end)));
            seq.getChildren().add(pause(420));
        }
        seq.getChildren().add(run(() -> { for (int p = 0; p < 14; p++) setCount(p, end[p]); }));
        return seq;
    }

    private void captureFlash(int[] before, int[] after) {
        for (int i = 0; i < 14; i++) {
            if (before[i] == after[i]) continue;
            final int pit = i;
            Shape s = shape[pit];
            FillTransition ft = new FillTransition(Duration.millis(200), s, (Color) s.getFill(), CAPTURE);
            ft.setCycleCount(2);
            ft.setAutoReverse(true);
            ft.setOnFinished(e -> paintPit(pit));
            ft.play();
        }
    }

    private void flyStone(int from, int to, Runnable onLand) {
        Circle stone = new Circle(cx[from], cy[from], 9, Color.web("#E8D3A2"));
        stone.setEffect(new DropShadow(6, Color.web("#00000088")));
        stoneLayer.getChildren().add(stone);

        Timeline fly = new Timeline(new KeyFrame(Duration.millis(STONE_FLIGHT_MS),
                new KeyValue(stone.centerXProperty(), cx[to], Interpolator.EASE_BOTH),
                new KeyValue(stone.centerYProperty(), cy[to], Interpolator.EASE_BOTH)));
        fly.setOnFinished(e -> { stoneLayer.getChildren().remove(stone); onLand.run(); });
        fly.play();
    }

    private void pulse(int pit) {
        ScaleTransition st = new ScaleTransition(Duration.millis(110), shape[pit]);
        st.setFromX(1); st.setFromY(1);
        st.setToX(1.12); st.setToY(1.12);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }

    private static PauseTransition pause(double ms) { return new PauseTransition(Duration.millis(ms)); }

    private static Animation run(Runnable r) {
        PauseTransition p = new PauseTransition(Duration.ONE);
        p.setOnFinished(e -> r.run());
        return p;
    }

    /** Полная анимация перехода: свой ход, затем каждый ход бота. */
    public Animation animateAll(List<MoveStep> steps) {
        List<Animation> parts = new ArrayList<>();
        int[] running = board.clone();
        for (MoveStep s : steps) {
            parts.add(animate(running, s));
            parts.add(pause(260));
            running = s.board().clone();   // следующий ход стартует с доски предыдущего
        }
        SequentialTransition all = new SequentialTransition();
        all.getChildren().addAll(parts);
        return all;
    }
}
