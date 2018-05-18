package com.mygdx.game;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;

public class Physics6 extends Game implements InputProcessor, Screen {
	Game game;
	private Stage stage;
	static Player player;
    SpriteBatch batch;
    Texture img, floorT, wallT, heart;
    static World world;
    Animation<Object> eFloor;
    TextureRegion floorRegion = new TextureRegion();
    BitmapFont font;
    Body box;
    Box2DDebugRenderer debugRenderer;
    ShapeRenderer shapeRenderer;
    Matrix4 debugMatrix;
    OrthographicCamera camera;
    static float elapsedTime = 0;
	float enemyTimer = 0;
	float enemySwitcher = 0;
    int playerWidth = 40, playerHeight = 52;
    Sound fireSound;
    ArrayList<Enemy> enemies;
    static ArrayList<Shot> pShots;
    ArrayList<Body> floors;
    final float PIXELS_TO_METERS = 100f;
    int enemyCount;
    int x = -300;
    int y = -300;
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
	boolean isPaused = false;
	Label paused;
	
    public float getXVelocity()  {return player.getBody().getLinearVelocity().x;}
    public float getYVelocity()  {return player.getBody().getLinearVelocity().y;}
    public static float getElapsedTime()  {return elapsedTime;}
    public static World getWorld()  {return world;}
    
    
    
    public Physics6(Game aGame, int w, int l, int[][] m, int bG, int eD, ArrayList<String> c) {
	    	game = aGame;
	    	map = m;
	    	worldNum = w;
	    	levelNum = l;
	    	enemyDelay = eD;
	    	for(String s : c){
	    		botGoal += s.length();
	    	}
	    	clusters = c;
	    	world = new World(new Vector2(0, -14f),true);
	    	world.setContactListener(new MyContactListener(this));
	    	FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/font1.ttf"));
	    	FreeTypeFontParameter parameter = new FreeTypeFontParameter();
	    	shapeRenderer = new ShapeRenderer();
	    	parameter.size = 18;
	    	font = generator.generateFont(parameter);
	    	font.setColor(new Color(0x930000ff));
	    	generator.dispose();
	    	batch = new SpriteBatch();
        floorT = new Texture("assets/floor.png");
		wallT = new Texture("assets/mossStone.png");
		heart = new Texture("assets/heart.png");
		eFloor = new Animation<Object>(1/6f, new TextureAtlas("assets/eFloor.atlas").findRegions("sprite"));
		enemies = new ArrayList<Enemy>();
		pShots = new ArrayList<Shot>();
		floors = new ArrayList<Body>();
		fireSound = Gdx.audio.newSound(Gdx.files.internal("assets/pew.mp3"));
        createPlayer(x, y, false, 6);
        map = m;
        Gdx.input.setInputProcessor(this);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        map = m;
        stage = new Stage(new ScreenViewport());
        batch.begin();
        buildMap();
        batch.end();
        buildBodies();
        camera.translate(hudWidth*16, 0);
        
        paused = new Label("PAUSED", MyGdxGame.gameSkin);
        Color pauseC = paused.getColor();
		paused.setColor(pauseC.r, pauseC.g, pauseC.b, 0);
        paused.setX(Gdx.graphics.getWidth()*6/17);
        paused.setY(Gdx.graphics.getHeight()*1/2);
        paused.setWidth(Gdx.graphics.getWidth());
        stage.addActor(paused);
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
        font.draw(batch, botsKilled + "/" + botGoal, testCol * 32 - 384, (19 - testRow) * 32 - 256);
        electrifyFloor();

        elapsedTime += Gdx.graphics.getDeltaTime();
        
        animate();
        if(isPaused) {
        		pauseGame();
        }
        else {
	        for(Contact c : world.getContactList()){
		    		if((c.getFixtureA().getBody().getUserData().equals("eFloor") && c.getFixtureB().getBody().getUserData() instanceof Player) || (c.getFixtureB().getBody().getUserData().equals("eFloor") && c.getFixtureA().getBody().getUserData() instanceof Player) && !player.invincible){
		    			if(player.health >= 1)  {
		            		player.hit();
		            		player.assessHealth();
		            	}
		            	if(player.health < 1){
		            		player.dead = true;
		            	}
		    		}
	        }
        
        		
	        move();
	        sweepTheBodies();
	        invincible();
	        player.assessHealth();
	        
	        if(mouseDown == 4){
	        	pShoot();
	        }
	        eShoot();
	        enemyTimer++;
	        if(clusters.size() > 0 && !clusters.get(0).substring(0, 1).equals("o") && enemyTimer % (60*enemyDelay) == 0)  {
	        		clusterSize = clusters.get(0).length();
	        		clusterCount ++;
	        		if(clusters.get(0).substring(0, 1).equals("m")){
	        			clusters.set(0, clusters.get(0).substring(1));
	        			enemySwitcher = 0;
	        		} else if(clusters.get(0).substring(0, 1).equals("t")){
	        			clusters.set(0, clusters.get(0).substring(1));
	        			enemySwitcher = 1;
	        		} else if(clusters.get(0).substring(0, 1).equals("f")){
	        			clusters.set(0, clusters.get(0).substring(1));
	        			enemySwitcher = 2;
	        		}
	        		if(clusters.get(0).length() < 1){
	        			clusters.remove(0);
	        			enemyTimer = 0;
	        			clusterCount = 0;
	        		} else {
	        			enemyTimer -= 25;
	        		}
	        		spawnEnemy(enemySwitcher);
	        }
	        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
	        		player.startMoving(0);
	        	}
	    		if(Gdx.input.isKeyPressed(Input.Keys.A)){
	    			player.startMoving(1);
	    		}
        }
    if(botGoal == botsKilled){
    	game.setScreen(new LevelLoadScreen(game,worldNum, levelNum + 1));
    }
    else
    	batch.end();
	stage.act();
    stage.draw();
    //debugRenderer.render(world, debugMatrix);
		
	}
  
    public void createPlayer(float x, float y, boolean invincible, float health)  {
    	if(!invincible)  {
	    	player = new Player(new Sprite(), new Sprite(), "assets/player.atlas", health, playerHeight, playerWidth, x, y, 1, 0, 3, 7, false);
			player.body = world.createBody(player.getBodyDef());
	        player.getBody().setFixedRotation(true);
	        player.getBody().createFixture(player.getFixtureDef());
	        player.fixture = player.getBody().createFixture(player.getFixtureDef());
	        player.body.setUserData(new Player());
	        player.getShape().dispose();
    	 }
    	else  {
    		player = new Player(new Sprite(), new Sprite(), "assets/player.atlas", health, playerHeight, playerWidth, x, y, 1, 0, 3, 7, true);
			player.body = world.createBody(player.getBodyDef());
	        player.getBody().setFixedRotation(true);
	        player.getBody().createFixture(player.getFixtureDef());
	        player.fixture = player.getBody().createFixture(player.getFixtureDef());
	        player.body.setUserData(new Player());
	        player.getShape().dispose();
    	}
    	
    }
    
    public void createEnemy(Enemy e)  {
    	e.body = world.createBody(e.getBodyDef());
        e.getBody().setFixedRotation(true);
        e.fixture = e.getBody().createFixture(e.getFixtureDef());
        e.fixture.setFilterData(e.fixtureDef.filter);
        e.body.setUserData(new Enemy());
        e.getShape().dispose();
        
        
    }
    
    public void spawnEnemy(float enemySwitcher2)  {
    	int direction = enemyCounter % 2;
    	if(enemySwitcher2 == 0){
    		enemies.add(new Enemy(new Sprite(), new Sprite(), "assets/basicBot.atlas", "mainBotWalkingLeft", "mainBotWalkingRight", "mainBotDeath", "mainBotDeath", 3, 48, 32, -32, 400, 0, 1, 2, 0, false,direction,0,false,false,(short)-1));
        	createEnemy(enemies.get(enemies.size()-1));
        	enemies.get(enemies.size()-1).getBody().setUserData("bot" + enemyCounter + " - basic");
        	enemyCounter++;
        	enemySwitcher = 1;
    	} else if(enemySwitcher2 == 1){
    		enemies.add(new Enemy(new Sprite(), new Sprite(), "assets/tesla.atlas", "teslaLeft", "teslaRight", "teslaDeathRight", "teslaDeathLeft", 1, 41, 32, -32, 400, 0, 1, 2, 0, false,direction,0,false,false,(short)-1));
        	createEnemy(enemies.get(enemies.size()-1));
        	enemies.get(enemies.size()-1).getBody().setUserData("bot" + enemyCounter + " - tesla");
        	enemyCounter++;
        	enemySwitcher = 2;
    	}
    	else if(enemySwitcher2 == 2){
    		enemies.add(new Enemy(new Sprite(), new Sprite(), "assets/FlyGuy.atlas", "FlyGuyLeft", "FlyGuyRight", "FlyGuyDeath", "FlyGuyDeath",3, 41, 32, -32, 400, 0, 1, 1, 0, true,direction,100,false,false,(short)-1));
        	createEnemy(enemies.get(enemies.size()-1));
        	enemies.get(enemies.size()-1).getBody().setUserData("bot" + enemyCounter + " - fly");
        	enemyCounter++;
        	enemySwitcher = 0;
    	}
    }
     
    public void pShoot(){
		if(player.getAnimDirection()==0)
			if(player.invincible){
				pShots.add(new Shot(player.getPlayerSprite().getX()+25, player.getPlayerSprite().getY()+26,xDistance,yDistance, tDistance,player.getShotSpeed(),6,6,player.getAngle(),"assets/shot1.atlas", "shot1","shot1",(short)-2));
			} else {
				pShots.add(new Shot(player.getPlayerSprite().getX()+15, player.getPlayerSprite().getY()+26,xDistance,yDistance, tDistance,player.getShotSpeed(),6,6,player.getAngle(),"assets/shot1.atlas", "shot1","shot1",(short)-2));
			}
		else {
			if(player.invincible){
				pShots.add(new Shot(player.getPlayerSprite().getX()+5, player.getPlayerSprite().getY()+26,xDistance,yDistance, tDistance ,player.getShotSpeed(),6,6,player.getAngle(),"assets/shot1.atlas","shot1","shot1", (short)-2));
			} else {
				pShots.add(new Shot(player.getPlayerSprite().getX()+15, player.getPlayerSprite().getY()+26,xDistance,yDistance, tDistance ,player.getShotSpeed(),6,6,player.getAngle(),"assets/shot1.atlas","shot1","shot1", (short)-2));
			}
		}
		pShots.get(pShots.size()-1).createPShot(shotCounter);
		shotCounter++;
		fireSound.play();
    }
    
    public void eShoot(){
    	eShotTimer++;
    	for(int i = 0; i < enemies.size(); i++)  {
    		Enemy e = enemies.get(i);
    		if(e.getShotTimer() != 0 && e.isShooting()){
	    		if(e.fly)  {
	    			float x = -e.getBody().getPosition().x + (float)player.getBody().getPosition().x;
	    	    	float y =-e.getBody().getPosition().y + player.getBody().getPosition().y;
					pShots.add(new Shot(e.getEnemySprite().getX()+e.getEnemySprite().getWidth()/2, e.getEnemySprite().getY()+e.getEnemySprite().getHeight()/2,x,y, (float)Math.hypot(x, y),0.3f,14,14,player.getAngle(),"assets/redShot.atlas","leftRedShot","rightRedShot", (short) -1));
					pShots.get(pShots.size()-1).createEShot(shotCounter);
					shotCounter++;
	    		}
	    	}	
    	}
		if(eShotTimer > eShotDelay * 60)
			eShotTimer = 0;
    	
    }
    
    public void move()  {
	    	player.move();
	    	for(Enemy e : enemies)  {
	    		e.eMove();
	    	}
    }
    
    public void animate()  {
    	for(int i = 0; i < pShots.size(); i++)  {
        	Shot s = pShots.get(i);
        	batch.draw(s.sAnimate(), s.getShotSprite().getX(),s.getShotSprite().getY(),s.getWidth()/2,s.getHeight()/2,s.getWidth(), s.getHeight(),1,1,-s.getAngle());
        	//s.getBody().applyAngularImpulse(-s.getAngle(),true);
        }
    	aDraw();
    	if(player.invincibilityTime == 0 || player.invincibilityTime % 3 !=0) {
    		batch.draw(player.pAnimate(),(player.getBody().getPosition().x* PIXELS_TO_METERS)- playerWidth/2,(player.getBody().getPosition().y* PIXELS_TO_METERS)- playerHeight/2,player.width, player.height);
    	}
        for(Enemy e : enemies)  {
            e.enemySprite.setPosition((e.getBody().getPosition().x * PIXELS_TO_METERS) - e.getWidth()/2 , (e.getBody().getPosition().y * PIXELS_TO_METERS) - e.getHeight()/2 );
            TextureRegion temp = e.eAnimate();
            if(!e.dead) {
            	batch.draw(temp, (e.getBody().getPosition().x* PIXELS_TO_METERS)- e.getWidth()/2,(e.getBody().getPosition().y* PIXELS_TO_METERS)- e.getHeight()/2, e.getWidth(), e.getHeight());
            }
        }
     }
    
    public void aDraw(){
    	TextureRegion currentSkin;
    	if(player.getAnimDirection() ==0){
    		if(mouseDown>0 && !isPaused){
    			currentSkin = player.getRArmFSprite();
    			mouseDown -= 1;
    		}
    		else
    			currentSkin = player.getRArmSprite();
    	}
    	else{
    		if(mouseDown>0 && !isPaused){
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

    public void sweepTheBodies(){
    	if(player.getBody().getPosition().y <= -4.2) {
    		player.dead = true;
    }
    	
    	if(player.dead){
    		world.destroyBody(player.body);
    		game.setScreen(new DeathScreen(game));
    	}
    	for(int i = 0; i < enemies.size(); i++){
    		Enemy e = enemies.get(i);
    		if(e.beginADead && !e.respawn){
    			Sprite es = enemies.get(i).enemySprite;
    			Sprite gs = enemies.get(i).gunSprite;
    			String atl = enemies.get(i).atlasName;
    			String regionRight = enemies.get(i).regionRightName;
    			String regionLeft = enemies.get(i).regionLeftName;
    			String regionDeathRight = enemies.get(i).regionDeathRightName;
    			String regionDeathLeft = enemies.get(i).regionDeathLeftName;
    			float h = enemies.get(i).health;
    			float he = enemies.get(i).height;
    			float wi = enemies.get(i).width;
    			float x = enemies.get(i).getBody().getPosition().x * PIXELS_TO_METERS - 22;
    			float y = enemies.get(i).getBody().getPosition().y * PIXELS_TO_METERS - 22;
    			float ss = enemies.get(i).shotSpeed;
    			float d = enemies.get(i).damage;
    			float xv = 0;
    			float yv = 0;
    			float st = 0;
    			boolean flying = enemies.get(i).fly;
    			boolean ad = true;
    			int di = e.m;
    			world.destroyBody(enemies.remove(i).getBody());
    			enemies.add(new Enemy(es, gs, atl, regionRight, regionLeft, regionDeathRight, regionDeathLeft, h, he, wi, x, y, ss, d, xv, yv, flying,  di, st, ad, false, (player.invincible) ? (short)-1 : (short)-2));
    			createEnemy(enemies.get(enemies.size()-1));
            	enemies.get(enemies.size()-1).getBody().setUserData("dyingBot");
    		}
        	if(e.dead  && !e.respawn)  {
        		botsKilled += 1;
        		world.destroyBody(enemies.remove(i).getBody());
        		electrifyFloor();
        		drawEFloor();
        	}
        	else if(e.dead  && e.respawn)  {
        		
        		if(((String)e.getBody().getUserData()).indexOf("basic") != -1)  {
        			spawnEnemy(0);
        		}
        		else if(((String)e.getBody().getUserData()).indexOf("tesla") != -1)  {
        			spawnEnemy(1);
        		}
        		else if(((String)e.getBody().getUserData()).indexOf("fly") != -1)  {
        			spawnEnemy(2);
        		}
        		world.destroyBody(enemies.remove(i).getBody());
        		electrifyFloor();
        		drawEFloor();
        	}
        }
    }
    
    public void invincible()  {
    	 if(player.hit)  {
         	player.invincibilityTime = 0;
         	float h = player.getHealth();
         	float deadX = player.playerSprite.getX();
         	float deadY = player.playerSprite.getY();
         	float deadXVel = player.getBody().getLinearVelocity().x;
         	float deadYVel = player.getBody().getLinearVelocity().y;
         	world.destroyBody(player.body);
         	createPlayer(deadX, deadY, true, h);
         	player.aHit = true;
         	player.body.setLinearVelocity(deadXVel, 4f);
         	
         		
         	player.hit = false;
         	player.invincible = true;
         }
         else if(player.invincibilityTime > 100  && player.invincible)  {
         	float h = player.getHealth();
         	float deadX = player.playerSprite.getX();
         	float deadY = player.playerSprite.getY();
         	float deadXVel = player.getBody().getLinearVelocity().x;
         	float deadYVel = player.getBody().getLinearVelocity().y;
         	world.destroyBody(player.body);
         	createPlayer(deadX, deadY, false, h);
         	player.invincible = false;
         	player.body.setLinearVelocity(deadXVel, deadYVel);
         }
    	 if(player.invincible){
    		 player.invincibilityTime += 1;
    	 }
    }
   
    public void drawEFloor(){
    	for(int r = 0; r < map.length; r++){
			for(int c = 0; c < map[r].length; c++){
				if(map[r][c] == 2){
					map[r][c] = 1;
				}
			}
		}
    	
    	
    	
    	for(Enemy e : enemies){
    		if(e.onGround && ((String)e.getBody().getUserData()).indexOf("tesla") >= 0){
    			int r = Integer.valueOf(e.groundImOn.substring(e.groundImOn.indexOf(",") + 2, e.groundImOn.indexOf(")")));
    			int c = Integer.valueOf(e.groundImOn.substring(1, e.groundImOn.indexOf(",")));
    			map[r][c] = 2;
    			c++;
    			while(c < map[0].length - hudWidth - 2 && map[r][c + 1] == 1){
    				c++;
    			}
    			while(c > 0 && (map[r][c] == 1 || map[r][c] == 2)){
    				map[r][c] = 2;
    				c--;
    			}
    		}
    	}
    }

    public void electrifyFloor(){
    	drawEFloor();
    	for(Enemy e : enemies){	
    		if(e.getBody().getLinearVelocity().y>0.001 || e.getBody().getLinearVelocity().y < -0.001){
    			if(!e.onGround && ((String)e.getBody().getUserData()).indexOf("tesla") >= 0){
    				
    				for(Body f : floors){
					if(f.getUserData().equals("eFloor")){ 
						f.setUserData("floor");
					}
				}
				for(Contact c : world.getContactList()){
			    		if((c.getFixtureA().getBody().getUserData().equals("floor") && ((c.getFixtureB().getBody().getUserData() instanceof String) ? ((String)c.getFixtureB().getBody().getUserData()).indexOf("tesla") >= 0 : false)) || (c.getFixtureB().getBody().getUserData().equals("eFloor") && ((c.getFixtureA().getBody().getUserData() instanceof String) ? ((String)c.getFixtureA().getBody().getUserData()).indexOf("tesla") >= 0 : false))){
			    			if(c.getFixtureA().getBody().getUserData().equals("floor")){
			    				c.getFixtureA().getBody().setUserData("eFloor");
			    			} else {
			    				c.getFixtureB().getBody().setUserData("eFloor");
			    			}
			    		}
			    }
					drawEFloor();
				}
    		}
    	}
    	int tCount = 0;
    	for(Enemy e : enemies){
    		if(((String)e.getBody().getUserData()).indexOf("tesla") >= 0){
    			tCount++;
    		}
    	}
    	if(tCount == 0){
    		for(Body f : floors){
				if(f.getUserData().equals("eFloor")){
					f.setUserData("floor");
				}
			}
			drawEFloor();
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
				} else if(map[r][c] == 2){
					floorRegion = (TextureRegion)eFloor.getKeyFrame(Physics6.getElapsedTime(), true);
					batch.draw(wallT, c * 32 - 384, (19 - r) * 32 - 256, 32, 32);
					batch.draw(floorRegion, c * 32 - 384, (19 - r) * 32 - 256, 32, 32);
				}
				else if(map[r][c] == 3){
					batch.draw(heart, c * 32 - 384, (19 - r) * 32 - 256, 32, 32);
				}
				
			}
		}
	}
    
    private void pauseGame() {
    		Color pauseC = paused.getColor();
		paused.setColor(pauseC.r, pauseC.g, pauseC.b, 1);
		batch.draw(player.pAnimate(),(player.getBody().getPosition().x* PIXELS_TO_METERS)- playerWidth/2,(player.getBody().getPosition().y* PIXELS_TO_METERS)- playerHeight/2,player.width, player.height);
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0,0,0,0.5f);
    		shapeRenderer.rect(0, 0, 1000, 1000);
    		shapeRenderer.end();	
    		batch.begin();
    		
    		if(player.getBody().getLinearVelocity().y > 0.01 || player.getBody().getLinearVelocity().y < -0.01) {
    			player.getBody().setLinearVelocity(0, 0.2433333479f);
    		} else {
    			player.getBody().setLinearVelocity(0, player.getBody().getLinearVelocity().y);
    		}
    		player.getBody().setTransform(player.getBody().getPosition().x, player.getBody().getPosition().y, player.getBody().getAngle());
    		for(Enemy e : enemies) {
			e.getBody().setLinearVelocity(0, 0.235f);
			e.getBody().setTransform(e.getBody().getPosition().x, e.getBody().getPosition().y, e.getBody().getAngle());
    		}
    		for(Shot s : pShots) {
    			s.getBody().setLinearVelocity(0, 0.235f);
    			s.getBody().setTransform(s.getBody().getPosition().x, s.getBody().getPosition().y, s.getBody().getAngle());
    		}
    }
    
    private void unPauseGame() {
    		Color pauseC = paused.getColor();
		paused.setColor(pauseC.r, pauseC.g, pauseC.b, 0);
    		player.getBody().setLinearVelocity(0,0);
		for(Enemy e : enemies) {
    			e.getBody().setLinearVelocity(0, 0);
    		}
    }
      
    @Override
    public void dispose() {
        world.dispose();
       // fireSound.dispose();
    }
   
    @Override
    public boolean keyDown(int keycode) {
    	if(keycode == Input.Keys.W && player.isntJumping() && !isPaused){
    		player.startJump();
    	}
    	if(keycode == Input.Keys.SPACE && !isPaused && !player.isDashing() && player.dashWaitTimer > 50 && ((player.body.getLinearVelocity().x > 0.001 || player.body.getLinearVelocity().x < -0.001) || (player.body.getLinearVelocity().y > 0.001 || player.body.getLinearVelocity().y < -0.001))){
    		player.startDashing(player.getDirection());
    	}
    	if(keycode == Input.Keys.R) {
    		game.setScreen(new LevelLoadScreen(game, 1, 1));
    	}
    	if(keycode == Input.Keys.Q) {
    		game.setScreen(new TitleScreen(game));
    	}
    	if(keycode == Input.Keys.P) {
    		isPaused = (isPaused) ? false : true;
    		if(!isPaused) {
    			unPauseGame();
    		}
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