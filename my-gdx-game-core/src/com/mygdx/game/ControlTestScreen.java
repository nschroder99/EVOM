package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MyGdxGame;

/**
 * Created by julienvillegas on 17/01/2017.
 */
public class ControlTestScreen implements Screen {

    private Stage stage;
    private Game game;

    public ControlTestScreen(Game aGame) {
        game = aGame;
        stage = new Stage(new ScreenViewport());
        
        Label body = new Label("Have you checked over the controls?", MyGdxGame.gameSkin);
        body.setAlignment(Align.center);
        body.setY(Gdx.graphics.getHeight()*2/3);
        body.setWidth(Gdx.graphics.getWidth());
        stage.addActor(body);

        TextButton playButton = new TextButton("Yes",MyGdxGame.gameSkin);
        playButton.setWidth(Gdx.graphics.getWidth()/2);
        playButton.setPosition(Gdx.graphics.getWidth()/2-playButton.getWidth()/2,300);
        playButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            		game.setScreen(new LevelLoadScreen(game, 1, 1));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(playButton);
        
        TextButton controlsButton = new TextButton("No",MyGdxGame.gameSkin);
        controlsButton.setWidth(Gdx.graphics.getWidth()/2);
        controlsButton.setPosition(Gdx.graphics.getWidth()/2-controlsButton.getWidth()/2, 100);
        controlsButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                	game.setScreen(new ControlScreen(game));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(controlsButton);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
    	Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
