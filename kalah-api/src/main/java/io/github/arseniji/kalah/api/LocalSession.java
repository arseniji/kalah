package io.github.arseniji.kalah.api;

import io.github.arseniji.kalah.bot.Bot;
import io.github.arseniji.kalah.core.Board;
import io.github.arseniji.kalah.core.Engine;
import io.github.arseniji.kalah.core.GameState;
import io.github.arseniji.kalah.core.Side;

import java.util.ArrayList;
import java.util.List;

public class LocalSession implements GameSession{
    private final Bot south;
    private final Bot north;
    private final Engine engine;
    private GameState state;
    private List<MoveStep> steps;

    public LocalSession(Bot south, Bot north){
        this.engine = new Engine();
        this.north = north;
        this.south = south;
        this.state = new GameState(new Board(), Side.SOUTH, false);
        this.steps = playBots(new ArrayList<>());
    }

    @Override
    public GameView view() {
        return toView(steps);
    }

    @Override
    public GameView move(int pit) {
        List<MoveStep> played = new ArrayList<>();

        Side whoMoved = state.current();
        state = engine.move(state, pit);
        played.add(new MoveStep(pit,whoMoved,state.board().getBoard()));

        steps = playBots(played);
        return toView(steps);
    }

    private List<MoveStep> playBots(List<MoveStep> played){
        while (!state.gameOver() && botFor(state.current()) != null) {
            int botPit = botFor(state.current()).chooseMove(state);
            Side whoMoved = state.current();
            state = engine.move(state, botPit);
            played.add(new MoveStep(botPit,whoMoved,state.board().getBoard()));
        }
        return List.copyOf(played);
    }

    private GameView toView(List<MoveStep> played){
        return new GameView(
                state.board().getBoard(),
                state.current(),
                state.gameOver(),
                engine.legalMoves(state),
                played
        );
    }

    private Bot botFor(Side side){
        return side == Side.SOUTH ? south : north;
    }
}
