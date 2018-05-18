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

public class TitleScreen implements Screen {

    private Stage stage;
    private Game game;

    public TitleScreen(final Game aGame) {
        game = aGame;
        stage = new Stage(new ScreenViewport());

        Label title = new Label("EVOM", MyGdxGame.gameSkin);
        
        title.setY(Gdx.graphics.getHeight()*6/7);
        title.setWidth(Gdx.graphics.getWidth());
//        title.setSize(100, 40);
        title.setAlignment(Align.center);
        stage.addActor(title);

        TextButton playButton = new TextButton("Play",MyGdxGame.gameSkin);
        playButton.setWidth(Gdx.graphics.getWidth()/2);
        playButton.setPosition(Gdx.graphics.getWidth()/2-playButton.getWidth()/2,Gdx.graphics.getHeight()/2+playButton.getHeight());
        playButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            		game.setScreen(new ControlTestScreen(aGame));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(playButton);

        TextButton optionsButton = new TextButton("Options",MyGdxGame.gameSkin);
        optionsButton.setWidth(Gdx.graphics.getWidth()/2);
        optionsButton.setPosition(Gdx.graphics.getWidth()/2-optionsButton.getWidth()/2,Gdx.graphics.getHeight()/2-(optionsButton.getHeight()*1/6));
        optionsButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new OptionScreen(game));
                System.out.println("x: " + x + " y: " + y);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(optionsButton);
        
        TextButton controlsButton = new TextButton("Controls",MyGdxGame.gameSkin);
        controlsButton.setWidth(Gdx.graphics.getWidth()/2);
        controlsButton.setPosition(Gdx.graphics.getWidth()/2-controlsButton.getWidth()/2,Gdx.graphics.getHeight()/2-(controlsButton.getHeight()*4/3));
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
        
        TextButton quitButton = new TextButton("Quit",MyGdxGame.gameSkin);
        quitButton.setWidth(Gdx.graphics.getWidth()/2);
        quitButton.setPosition(Gdx.graphics.getWidth()/2-quitButton.getWidth()/2,Gdx.graphics.getHeight()/5-quitButton.getHeight()/2);
        quitButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(quitButton);

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
