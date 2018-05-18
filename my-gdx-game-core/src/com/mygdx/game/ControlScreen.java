package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ControlScreen extends Game implements InputProcessor, Screen {
	Game game;
	private Stage stage;
	static ControlScreenPlayer player;
    SpriteBatch batch;
    Texture img, floorT, wallT, heart;
    static World world;
    TextureRegion floorRegion = new TextureRegion();
    Body box;
    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;
    OrthographicCamera camera;
    static float elapsedTime = 0;
	float enemyTimer = 0;
	float enemySwitcher = 0;
    int playerWidth = 40, playerHeight = 52;
    Sound fireSound;
    static ArrayList<ControlScreenShot> pShots;
    ArrayList<Body> floors;
    final float PIXELS_TO_METERS = 100f;
    int enemyCount;
    int x = 350;
    int y = -96;
    int worldNum;
    int levelNum;
    float xDistance;
    float yDistance;
    float tDistance;
    float deadX, deadY;
    int mouseX;
    int mouseY;
    int mouseDown=0;
    int enemyCounter = 1, botsKilled = 0, botGoal =0, shotCounter = 1;
    float enemyDelay;
    int clusterSize = 3;
    int clusterCount = 0;
    float eShotDelay = 3, eShotTimer = 0;
    public static int hudWidth = 5;
    ArrayList<String> clusters;
    static int[][] map;
    final short PLAYER_CATEGORY_BITS = 0x001, GROUND_ENEMY_CATEGORY_BITS = 0x002, FLOOR_CATEGORY_BITS = 0x003, WALL_CATEGORY_BITS = 0x004, FLYING_ENEMY_CATEGORY_BITS = 0x005, INVINCIBLE_CATEGORY_BITS = 0x006, SHOT_CATEGORY_BITS = 0x007;
	final short PLAYER_MASK_BITS =  ~PLAYER_CATEGORY_BITS, INVINCIBLE_MASK_BITS = FLOOR_CATEGORY_BITS | WALL_CATEGORY_BITS, GROUND_ENEMY_MASK_BITS = PLAYER_CATEGORY_BITS | FLOOR_CATEGORY_BITS | WALL_CATEGORY_BITS,  FLOOR_MASK_BITS = PLAYER_CATEGORY_BITS | GROUND_ENEMY_CATEGORY_BITS, WALL_MASK_BITS = -1, FLYING_ENEMY_MASK_BITS = WALL_CATEGORY_BITS | PLAYER_CATEGORY_BITS, SHOT_MASK_BITS = ~SHOT_CATEGORY_BITS;
	
    public float getXVelocity()  {return player.getBody().getLinearVelocity().x;}
    public float getYVelocity()  {return player.getBody().getLinearVelocity().y;}
    public static float getElapsedTime()  {return elapsedTime;}
    public static World getWorld()  {return world;}
    
    
    
    public ControlScreen(final Game aGame) {
	    	game = aGame;
	    	world = new World(new Vector2(0, -14f),true);
	    	world.setContactListener(new ControlScreenContactListener(this));
	    	batch = new SpriteBatch();
        floorT = new Texture("assets/floor.png");
		wallT = new Texture("assets/mossStone.png");
		pShots = new ArrayList<ControlScreenShot>();
		floors = new ArrayList<Body>();
		fireSound = Gdx.audio.newSound(Gdx.files.internal("assets/pew.mp3"));
        createPlayer(x, y, false, 6);
        map = new int[][]{
    		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, -1},
    		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 0, 0, 1, -1},
    		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 0, 0, 1, -1},
    		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 0, 0, 1, -1},
    		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 0, 0, 1, -1},
    		  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 0, 0, 1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 0, 0, 1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 0, 0, 1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 0, 0, 1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, 0, 0, 0, 0, 0, 1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    	      { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
    		};
    		stage = new Stage(new ScreenViewport());
    		InputProcessor minimap = this;
    		InputProcessor buttons = stage;
    		InputMultiplexer inputMultiplexer = new InputMultiplexer();
    		inputMultiplexer.addProcessor(buttons);
    		inputMultiplexer.addProcessor(minimap);
    		Gdx.input.setInputProcessor(inputMultiplexer);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        batch.begin();
        buildMap();
        batch.end();
        buildBodies();
        camera.translate(hudWidth*16, 0);
        
        Label title = new Label("Controls", MyGdxGame.gameSkin);
        title.setAlignment(Align.center);
        title.setY(Gdx.graphics.getHeight()*6/7);
        title.setWidth(Gdx.graphics.getWidth());
        stage.addActor(title);
        
        Label wKey = new Label("Press         to jump", MyGdxGame.gameSkin);
        wKey.setAlignment(Align.center);
        wKey.setY(560);
        wKey.setX(-150);
        wKey.setWidth(Gdx.graphics.getWidth());
        stage.addActor(wKey);
        
        Label aordKey = new Label("Press         or         to move left or right", MyGdxGame.gameSkin);
        aordKey.setAlignment(Align.center);
        aordKey.setY(480);
        aordKey.setX(-Gdx.graphics.getHeight()*2/11);
        aordKey.setWidth(Gdx.graphics.getWidth());
        stage.addActor(aordKey);
        
        Label spaceKey = new Label("Press                          to dash (while moving)", MyGdxGame.gameSkin);
        spaceKey.setAlignment(Align.center);
        spaceKey.setY(400);
        spaceKey.setX(-140);
        spaceKey.setWidth(Gdx.graphics.getWidth());
        stage.addActor(spaceKey);
        
        Label clickKey = new Label("Press            to shoot", MyGdxGame.gameSkin);
        clickKey.setAlignment(Align.center);
        clickKey.setY(320);
        clickKey.setX(-150);
        clickKey.setWidth(Gdx.graphics.getWidth());
        stage.addActor(clickKey);
        
        Label rKey = new Label("Press 'R' to\nrestart", MyGdxGame.gameSkin);
        rKey.setAlignment(Align.center);
        rKey.setY(130);
        rKey.setX(-350);
        rKey.setWidth(Gdx.graphics.getWidth());
        stage.addActor(rKey);
        
        Label qKey = new Label("Press 'Q' to\nquit", MyGdxGame.gameSkin);
        qKey.setAlignment(Align.center);
        qKey.setY(130);
        qKey.setX(350);
        qKey.setWidth(Gdx.graphics.getWidth());
        stage.addActor(qKey);
        
        Label pKey = new Label("Press 'P' to pause", MyGdxGame.gameSkin);
        pKey.setAlignment(Align.center);
        pKey.setY(30);
        pKey.setX(Align.center);
        pKey.setWidth(Gdx.graphics.getWidth());
        stage.addActor(pKey);
        
        Label leftClickNotice = new Label("(left click)", MyGdxGame.gameSkin);
        leftClickNotice.setAlignment(Align.center);
        leftClickNotice.setY(290);
        leftClickNotice.setX(-285);
        leftClickNotice.setWidth(Gdx.graphics.getWidth());
        stage.addActor(leftClickNotice);
        
        TextButton wButton = new TextButton("W",MyGdxGame.gameSkin);
        wButton.setWidth(60);
        wButton.setHeight(60);
        wButton.setPosition(265, 545);
        wButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            	if(player.canFall == true && (player.getBody().getLinearVelocity().y > 0.001)){
            		player.getBody().applyForceToCenter(0f,player.getBody().getLinearVelocity().y, true);
            		player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 0f);
            		player.canFall = false;
            	}
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) { 
            	if(player.isntJumping()) {
        			player.startJump();
        		}
            	return true;
            }
        });
        stage.addActor(wButton);
        
        TextButton aButton = new TextButton("A",MyGdxGame.gameSkin);
        aButton.setWidth(60);
        aButton.setHeight(60);
        aButton.setPosition(140, 465);
        aButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            		player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, player.getBody().getLinearVelocity().y);
        			player.endMoving();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) { 
            		player.startMoving(1);
            		return true;
            }
        });
        stage.addActor(aButton);
        
        TextButton dButton = new TextButton("D",MyGdxGame.gameSkin);
        dButton.setWidth(60);
        dButton.setHeight(60);
        dButton.setPosition(240, 465);
        dButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            		player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, player.getBody().getLinearVelocity().y);
        			player.endMoving();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) { 
            		player.startMoving(0);
            		return true;
            }
        });
        stage.addActor(dButton);
        
        TextButton spaceButton = new TextButton("",MyGdxGame.gameSkin);
        spaceButton.setWidth(180);
        spaceButton.setHeight(70);
        spaceButton.setPosition(117, 375);
        spaceButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            		player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, player.getBody().getLinearVelocity().y);
        			player.endMoving();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) { 
            		if(!player.isDashing() && player.dashWaitTimer > 50 && ((player.body.getLinearVelocity().x > 0.001 || player.body.getLinearVelocity().x < -0.001) || (player.body.getLinearVelocity().y > 0.001 || player.body.getLinearVelocity().y < -0.001))){
            			player.startDashing(player.getDirection());
            		}
            		return true;
            }
        });
        stage.addActor(spaceButton);
        
        TextButton mouseButton2 = new TextButton("",MyGdxGame.gameSkin);
        mouseButton2.setWidth(37);
        mouseButton2.setHeight(50);
        mouseButton2.setPosition(252, 310);
        mouseButton2.addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            		pShoot();
            		return true;
            }
        });
        stage.addActor(mouseButton2);
        
        TextButton mouseButton3 = new TextButton("",MyGdxGame.gameSkin);
        mouseButton3.setWidth(37);
        mouseButton3.setHeight(50);
        mouseButton3.setPosition(287, 310);
        stage.addActor(mouseButton3);
        
        TextButton mouseButton1 = new TextButton("",MyGdxGame.gameSkin);
        mouseButton1.setWidth(75);
        mouseButton1.setHeight(75);
        mouseButton1.setPosition(250, 251);
        stage.addActor(mouseButton1);

        TextButton backButton = new TextButton("Go Back",MyGdxGame.gameSkin);
        backButton.setWidth(Gdx.graphics.getWidth()/2);
        backButton.setPosition(Gdx.graphics.getWidth()/2-backButton.getWidth()/2,Gdx.graphics.getHeight()/5-backButton.getHeight()/2);
        backButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            		game.setScreen(new TitleScreen(game));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(backButton);
    }
    
    public void render(float delta) {
		super.render();
        camera.update();
        world.step(1f/60f, 6, 2);

        player.playerSprite.setPosition((player.getBody().getPosition().x * PIXELS_TO_METERS) - playerWidth/2 , (player.getBody().getPosition().y * PIXELS_TO_METERS) - playerHeight/2 );
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
        batch.begin();
        buildMap();
        float testCol = 25.5f;
        float testRow = 4;
        elapsedTime += Gdx.graphics.getDeltaTime();
        
        move();
        animate();
        
        if(mouseDown == 4){
        		pShoot();
        }
        enemyTimer++;
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
    			player.startMoving(0);
        }
	    	if(Gdx.input.isKeyPressed(Input.Keys.A)){
	    		player.startMoving(1);
	    	}
	    	batch.end();
	    	stage.act();
	    stage.draw();
	}
  
    public void createPlayer(float x, float y, boolean invincible, float health)  {
    	if(!invincible)  {
	    	player = new ControlScreenPlayer(new Sprite(), new Sprite(), "assets/player.atlas", health, playerHeight, playerWidth, x, y, 1, 0, 3, 7, false);
			player.body = world.createBody(player.getBodyDef());
	        player.getBody().setFixedRotation(true);
	        player.getBody().createFixture(player.getFixtureDef());
	        player.fixture = player.getBody().createFixture(player.getFixtureDef());
	        player.body.setUserData(new Player());
	        player.getShape().dispose();
    	 }
    	else  {
    		player = new ControlScreenPlayer(new Sprite(), new Sprite(), "assets/player.atlas", health, playerHeight, playerWidth, x, y, 1, 0, 3, 7, true);
			player.body = world.createBody(player.getBodyDef());
	        player.getBody().setFixedRotation(true);
	        player.getBody().createFixture(player.getFixtureDef());
	        player.fixture = player.getBody().createFixture(player.getFixtureDef());
	        player.body.setUserData(new Player());
	        player.getShape().dispose();
    	}
    	
    }
     
    public void pShoot(){
		if(player.getAnimDirection()==0)
			if(player.invincible){
				pShots.add(new ControlScreenShot(player.getPlayerSprite().getX()+25, player.getPlayerSprite().getY()+26,xDistance,yDistance, tDistance,player.getShotSpeed(),6,6,player.getAngle(),"assets/shot1.atlas", "shot1","shot1",(short)-2));
			} else {
				pShots.add(new ControlScreenShot(player.getPlayerSprite().getX()+15, player.getPlayerSprite().getY()+26,xDistance,yDistance, tDistance,player.getShotSpeed(),6,6,player.getAngle(),"assets/shot1.atlas", "shot1","shot1",(short)-2));
			}
		else {
			if(player.invincible){
				pShots.add(new ControlScreenShot(player.getPlayerSprite().getX()+5, player.getPlayerSprite().getY()+26,xDistance,yDistance, tDistance ,player.getShotSpeed(),6,6,player.getAngle(),"assets/shot1.atlas","shot1","shot1", (short)-2));
			} else {
				pShots.add(new ControlScreenShot(player.getPlayerSprite().getX()+15, player.getPlayerSprite().getY()+26,xDistance,yDistance, tDistance ,player.getShotSpeed(),6,6,player.getAngle(),"assets/shot1.atlas","shot1","shot1", (short)-2));
			}
		}
		pShots.get(pShots.size()-1).createPShot(shotCounter);
		shotCounter++;
		fireSound.play();
    }
    
    public void move()  {
    		player.move();
    }
    
    public void animate()  {
    	for(int i = 0; i < pShots.size(); i++)  {
    		ControlScreenShot s = pShots.get(i);
        	batch.draw(s.sAnimate(), s.getShotSprite().getX(),s.getShotSprite().getY(),s.getWidth()/2,s.getHeight()/2,s.getWidth(), s.getHeight(),1,1,-s.getAngle());
        	//s.getBody().applyAngularImpulse(-s.getAngle(),true);
    }
    	aDraw();
    	if(player.invincibilityTime == 0 || player.invincibilityTime % 3 !=0) {
    		batch.draw(player.pAnimate(),(player.getBody().getPosition().x* PIXELS_TO_METERS)- playerWidth/2,(player.getBody().getPosition().y* PIXELS_TO_METERS)- playerHeight/2,player.width, player.height);
     }
    }
    
    public void aDraw(){
    	TextureRegion currentSkin;
    	if(player.getAnimDirection() ==0){
    		if(mouseDown>0){
    			currentSkin = player.getRArmFSprite();
    			mouseDown -= 1;
    		}
    		else
    			currentSkin = player.getRArmSprite();
    	}
    	else{
    		if(mouseDown>0){
    			currentSkin = player.getLArmFSprite();
    			mouseDown -= 1;
    		}
    		else
    			currentSkin = player.getLArmSprite();
    	}
    	xDistance = (-384-(player.getPlayerSprite().getX()+5)+(float)mouseX);
    	yDistance = (384-(float)mouseY-(player.getPlayerSprite().getY()+25));
    	tDistance =(float) Math.hypot(xDistance, yDistance);
    	if(player.aM==0){
    		 xDistance = (-384-(player.getPlayerSprite().getX()+22)+(float)mouseX);
        	 yDistance = (384-(float)mouseY-(player.getPlayerSprite().getY()+25));
    	}
    	player.angle =((float) (Math.atan(yDistance/xDistance)*(180/Math.PI)));
    	if(xDistance > 10){
    		player.aM = 0;
    		batch.draw(currentSkin, player.getPlayerSprite().getX()+20, player.getPlayerSprite().getY()+22, 0, 0, player.getWidth()/2, player.getHeight()/3,1,1, player.getAngle());
    	}
    	else if(xDistance < -10){
    		player.aM = 1;
    		batch.draw(currentSkin,player.getPlayerSprite().getX()+3,player.getPlayerSprite().getY()+22,20,0,player.getWidth()/2, player.getHeight()/3,1,1,player.angle);
    	}
    	else{
    		if(player.aM==0){
    			if(yDistance > 0)
    				batch.draw(currentSkin, player.getPlayerSprite().getX()+22, player.getPlayerSprite().getY()+24, 0, 0, player.getWidth()/2, player.getHeight()/3,1,1, 90);
    			else
    				batch.draw(currentSkin, player.getPlayerSprite().getX()+22, player.getPlayerSprite().getY()+24, 0, 0, player.getWidth()/2, player.getHeight()/3,1,1, -90);
    		}
    		else{
    			if(yDistance > 0)
    				batch.draw(currentSkin, player.getPlayerSprite().getX()+20, player.getPlayerSprite().getY()+50, 0, 0, player.getWidth()/2, player.getHeight()/3,1,1, -90);
    			else
    				batch.draw(currentSkin, player.getPlayerSprite().getX()+25, player.getPlayerSprite().getY()+5, 0, 0, player.getWidth()/2, player.getHeight()/3,1,1, 90);
    		}
    	}
    }
    
    private void buildBodies(){
    	int start = 0;
    	int end = 0;
    	for(int c = 0; c < map[0].length; c++){
			for(int r = 0; r < map.length - 1; r++){
				if(map[r][c] == 1 && map[r + 1][c] == 1){
					start = r;
					while(r < map.length && map[r][c] == 1){
						map[r][c] = 5;
						r++;
					}
					end = r;
					batch.begin();
					BodyDef boxDef = new BodyDef();
			        boxDef.type = BodyDef.BodyType.StaticBody; 
			        boxDef.position.set(((c * 32) - 368) / PIXELS_TO_METERS, (-(start * 32) + 368 - ((end - start > 1) ? ((end - start) / 2) * 32 : 0) + (((end - start + 1) % 2 == 0) ? 0 : 16)) / PIXELS_TO_METERS);    	
			        box = world.createBody(boxDef);
			        PolygonShape boxShape = new PolygonShape();
			        boxShape.setAsBox(0.16f, 0.16f * (end - start));        
			        FixtureDef fixtureDefBox = new FixtureDef();    
			        fixtureDefBox.shape = boxShape;  
			        fixtureDefBox.filter.groupIndex = 0;
			        box.createFixture(fixtureDefBox);
			        box.setUserData("wall");
			        boxShape.dispose();
			        batch.end();
				}
			}
		}

    	for(int r = 0; r < map.length; r++){
			for(int c = 0; c < map[r].length - 1; c++){
				if(map[r][c] == 1 && map[r][c + 1] == 1){
					start = c;
					while(map[r][c] == 1 && c < map[r].length - 1){
						map[r][c] = 5;
						c++;
					}
					end = c;
					batch.begin();
					BodyDef boxDef = new BodyDef();
			        boxDef.type = BodyDef.BodyType.StaticBody;   	
			        boxDef.position.set(((start * 32) - 368 + ((end - start > 1) ? ((end - start) / 2) * 32 : 0) - + (((end - start + 1) % 2 == 0) ? 0 : 16)) / PIXELS_TO_METERS, (-(r * 32) + 368) / PIXELS_TO_METERS);    	
			        box = world.createBody(boxDef);
			        PolygonShape boxShape = new PolygonShape();
			        boxShape.setAsBox(0.16f * (end - start), 0.16f);        
			        FixtureDef fixtureDefBox = new FixtureDef();    
			        fixtureDefBox.shape = boxShape;     
			        fixtureDefBox.filter.groupIndex = 0;
			        box.createFixture(fixtureDefBox);  
			        box.setUserData("floor");
			        floors.add(box);
			        boxShape.dispose();
			        batch.end();
				}
			}
		}
    	
    	for(int r = 0; r < map.length; r++){
			for(int c = 0; c < map[r].length; c++){
				if(map[r][c] == 1){
					batch.begin();
					BodyDef boxDef = new BodyDef();
			        boxDef.type = BodyDef.BodyType.StaticBody;   	
			        boxDef.position.set(((c * 32) - 368) / PIXELS_TO_METERS, (-(r * 32) + 368) / PIXELS_TO_METERS);    	
			        box = world.createBody(boxDef);
			        PolygonShape boxShape = new PolygonShape();
			        boxShape.setAsBox(0.16f , 0.16f);        
			        FixtureDef fixtureDefBox = new FixtureDef();    
			        fixtureDefBox.shape = boxShape;      
			        fixtureDefBox.filter.groupIndex = 0;
			        box.createFixture(fixtureDefBox);       
			        box.setUserData("floor");
			        floors.add(box);
			        boxShape.dispose();
			        batch.end();
				}
				if(map[r][c] == 7){
					batch.begin();
					BodyDef boxDef = new BodyDef();
			        boxDef.type = BodyDef.BodyType.StaticBody;   	
			        boxDef.position.set(((c * 32) - 368) / PIXELS_TO_METERS, (-(r * 32) + 368) / PIXELS_TO_METERS);    	
			        box = world.createBody(boxDef);
			        PolygonShape boxShape = new PolygonShape();
			        boxShape.setAsBox(0.16f , 0.16f);        
			        FixtureDef fixtureDefBox = new FixtureDef();    
			        fixtureDefBox.shape = boxShape;      
			        fixtureDefBox.filter.groupIndex = 0;
			        box.createFixture(fixtureDefBox);       
			        box.setUserData("lastFloor");
			        floors.add(box);
			        boxShape.dispose();
			        batch.end();
				}
			}
		}
    	for(int r = 0; r<map.length;r++){
    		for(int c = 0; c < map[r].length; c++){
    			if(map[r][c] == 5){
    				map[r][c] = 1;
    			}
    		}
    	};
    }
    
    private void buildMap(){
    		
		for(int r = 0; r < map.length; r++){
			for(int c = 0; c < map[r].length; c++){
				if(map[r][c] == 0){
					batch.draw(wallT, c * 32 - 384, (19 - r) * 32 - 256, 32, 32);
				}	
				if(map[r][c] == 1){
					batch.draw(wallT, c * 32 - 384, (19 - r) * 32 - 256, 32, 32);
					batch.draw(floorT, c * 32 - 384, (19 - r) * 32 - 256);
				}
			}
		}
	}
      
    @Override
    public void dispose() {
        world.dispose();
       // fireSound.dispose();
    }
   
    @Override
    public boolean keyDown(int keycode) {
    	if(keycode == Input.Keys.W && player.isntJumping()){
    		player.startJump();
    	}
    	if(keycode == Input.Keys.SPACE && !player.isDashing() && player.dashWaitTimer > 50 && ((player.body.getLinearVelocity().x > 0.001 || player.body.getLinearVelocity().x < -0.001) || (player.body.getLinearVelocity().y > 0.001 || player.body.getLinearVelocity().y < -0.001))){
    		player.startDashing(player.getDirection());
    	}
    	if(keycode == Input.Keys.R) {
    		player.dead = true;
    	}
    	
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
    	if(keycode == Input.Keys.D){
    		if(player.getDirection() == 1)
    			player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, player.getBody().getLinearVelocity().y);
    		else{
    			player.getBody().setLinearVelocity(0f, player.getBody().getLinearVelocity().y);
    			player.endMoving();
    		}
    	}
    	if(keycode == Input.Keys.A){
    		if (player.getDirection() == 0)
    			player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, player.getBody().getLinearVelocity().y);
    		else{
    			player.getBody().setLinearVelocity(0f,player.getBody().getLinearVelocity().y);
    			player.endMoving();
    		}
    	}
    	
    	if(keycode == Input.Keys.W  && player.canFall == true && (player.getBody().getLinearVelocity().y > 0.001)){
    		player.getBody().applyForceToCenter(0f,player.getBody().getLinearVelocity().y, true);
    		player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 0f);
    		player.canFall = false;
    	}
    	
        return true;
    }
    
    

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//    	if(screenX >= 210 && screenX <= 310 && screenY >= 130 && screenY <= 230) {
//    		System.out.println("TEST");
//    	}
//    	
    	mouseDown = 5;
    	player.idleTimer=0;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    	mouseDown = 0;
    	player.idleTimer=0;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
    	mouseX = screenX;
    	mouseY = screenY;
    	//mouseDown = 5;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
    	mouseX = screenX;
    	mouseY = screenY;
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
	
    @Override
    public void create() {
    }
    
    @Override
    public void render() {
    	
    }
    
}