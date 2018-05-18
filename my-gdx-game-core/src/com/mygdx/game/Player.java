package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Player {
	Sprite playerSprite, gunSprite, lArm, rArm, lArmF, rArmF;
	Animation<Object> rightAnimation, leftAnimation;
	TextureAtlas atlas, armAtlas;
	Body body;
	BodyDef bodyDef;
	PolygonShape shape;
	Fixture fixture;
	FixtureDef fixtureDef;
	Animation<Object> lAnimation, rAnimation, rStanding, lStanding, rWalking, lWalking, rBored, lBored, rJump, lJump,rDash,lDash,rHurt,lHurt;
	float health, xPos, yPos, width, height, shotSpeed, damage, xVel, yVel, angle = 0,dashVel = 9;
	boolean moving, jump, canFall,dead,dashing, hit = false, invincible, aHit = false;
	int m = 0, aM = 0, h1 = -1, h2 = -1, h3 = -1, h4 = -1, h5 = -1, h6 = -1;
	final short PLAYER_CATEGORY_BITS = 0x001, GROUND_ENEMY_CATEGORY_BITS = 0x002, FLOOR_CATEGORY_BITS = 0x003, WALL_CATEGORY_BITS = 0x004, FLYING_ENEMY_CATEGORY_BITS = 0x005, INVINCIBLE_CATEGORY_BITS = 0x006, SHOT_CATEGORY_BITS = 0x007;
	final short PLAYER_MASK_BITS =  ~PLAYER_CATEGORY_BITS, INVINCIBLE_MASK_BITS = FLOOR_CATEGORY_BITS | WALL_CATEGORY_BITS, GROUND_ENEMY_MASK_BITS = PLAYER_CATEGORY_BITS | FLOOR_CATEGORY_BITS | WALL_CATEGORY_BITS,  FLOOR_MASK_BITS = PLAYER_CATEGORY_BITS | GROUND_ENEMY_CATEGORY_BITS, WALL_MASK_BITS = -1, FLYING_ENEMY_MASK_BITS = WALL_CATEGORY_BITS | PLAYER_CATEGORY_BITS, SHOT_MASK_BITS = ~SHOT_CATEGORY_BITS;
	final float PIXELS_TO_METERS = 100f;
	float animTime = 0;
	float idleTimer = 0;
	float dashTimer = 0;
	float invincibilityTime = 0;
	float dashWaitTimer = 100;
    float hitDirection;

	
	
	public Player(Sprite ps, Sprite gs, String atl, float h, float he, float wi, float x, float y, float ss, float d, float xv, float yv, boolean invinc)  {
		playerSprite = ps;
		gunSprite = gs;
		health = h;
		xPos = x;
		xPos = y;
		shotSpeed = ss;
		damage = d;
		height = he;
		width = wi;
		xVel = xv;
		yVel = yv;
		invincible = invinc;
		
		
		atlas = new TextureAtlas(atl);
		armAtlas = new TextureAtlas("assets/playerArm.atlas");
        
        rStanding = new Animation<Object>(1/6f, atlas.findRegions("idleRight"));
        lStanding = new Animation<Object>(1/6f, atlas.findRegions("idleLeft"));
        rWalking = new Animation<Object>(1/20f, atlas.findRegions("movingRight"));
        lWalking = new Animation<Object>(1/20f, atlas.findRegions("movingLeft"));
        rBored = new Animation<Object>(1/15f, atlas.findRegions("boredRight"));
        lBored = new Animation<Object>(1/15f, atlas.findRegions("boredLeft"));
        lJump = new Animation<Object>(1/15f, atlas.findRegions("jumpLeft"));
        rJump = new Animation<Object>(1/15f, atlas.findRegions("jumpRight"));
        rDash = new Animation<Object>(1/20f, atlas.findRegions("dashRight"));
        lDash = new Animation<Object>(1/20f, atlas.findRegions("dashLeft"));
        rHurt = new Animation<Object>(1/20f, atlas.findRegions("hurtRight"));
        lHurt = new Animation<Object>(1/20f, atlas.findRegions("hurtLeft"));
        
        lArm = new Sprite(armAtlas.createSprite("armLeft"));
        rArm = new Sprite(armAtlas.createSprite("armRight"));
        lArmF = new Sprite(armAtlas.createSprite("armLeft1"));
        rArmF = new Sprite(armAtlas.createSprite("armRight1"));
        
        lArm.setPosition(x+28,y+25);
        rArm.setPosition(x-2,y+25);
        lArmF.setPosition(x+28,y+25);
        rArmF.setPosition(x-2,y+25);
		playerSprite.setPosition(x, y);
		
		bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set((x + width/2) / PIXELS_TO_METERS, (y + height/2) / PIXELS_TO_METERS);
		
		shape = new PolygonShape();
		shape.setAsBox(width/2 / PIXELS_TO_METERS, height/2 / PIXELS_TO_METERS);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0f;
        
        if(invinc)  {
//        	fixtureDef.filter.categoryBits = INVINCIBLE_CATEGORY_BITS;
//        	fixtureDef.filter.maskBits = INVINCIBLE_MASK_BITS;
        	fixtureDef.filter.groupIndex = -1;
        }
        else  {
//        	fixtureDef.filter.categoryBits = PLAYER_CATEGORY_BITS;
//        	fixtureDef.filter.maskBits = PLAYER_MASK_BITS;
        	fixtureDef.filter.groupIndex = -2;
        }
        	
        for(int i = 1; i <= h; i++)  {
        	if(i == 1)
        		h1 = 3;
        	if(i == 2)
        		h2 = 3;
        	if(i == 3)
        		h3 = 3;
        	if(i == 4)
        		h4 = 3;
        	if(i == 5)
        		h5 = 3;
        	if(i == 6)
        		h6 = 3;
        		
        }
        
	}
	
	public Player() {
	}
	
	public Body getBody()  {return body;}
	public Sprite getPlayerSprite() {return playerSprite;}
	public Sprite getGunSprite() {return gunSprite;}
	public Sprite getLArmSprite() {return lArm;}
	public Sprite getRArmSprite() {return rArm;}
	public Sprite getLArmFSprite() {return lArmF;}
	public Sprite getRArmFSprite() {return rArmF;}
	public BodyDef getBodyDef() {return bodyDef;}
	public FixtureDef getFixtureDef() {return fixtureDef;}
	public PolygonShape getShape() {return shape;}
	public Animation getLeftAnimation() {return leftAnimation;}
	public Animation getRightAnimation() {return rightAnimation;}
	public float getHealth() {return health;}
	public float getX() {return xPos;}
	public float getY() {return yPos;}
	public float getWidth() {return width;}
	public float getHeight() {return height;}
	public float getDamage() {return damage;}
	public float getShotSpeed() {return shotSpeed;}
	public float getXVelocity() {return xVel;}
	public float getYVelocity() {return yVel;}
	public float getDashVelocity() {return dashVel;}
	public float getAngle() {return angle;}
	public int getDirection() {return m;}
	public int getAnimDirection() {return aM;}
	public boolean isMoving() {return moving;}
	public boolean isDead() {return dead;}
	public boolean isJumping() {return jump;}
	public boolean isntJumping() {return body.getLinearVelocity().y < 0.001 && body.getLinearVelocity().y > -0.001;}
	public boolean isDashing() {return dashing;}
	
	public float upgradeDamage(float modifier)  {
		damage *= modifier;
		return damage;
	}
	
	public float upgradeShotSpeed(float modifier)  {
		shotSpeed *= modifier;
		return shotSpeed;
	}
	
	public int changeDirection(int direction) {
		m = direction;
		return m;
	}
	
	public boolean startMoving(int direction)  {
		m = direction;
		moving = true;
		return moving;
	}
	
	public boolean endMoving()  {
		moving = false;
		return moving;
	}
	
	public boolean startJump()  {
		jump = true;
		return jump;
	}
	
	public boolean endJump()  {
		jump = false;
		return jump;
	}
	
	public boolean startDashing(int direction)  {
		m = direction;
		dashing = true;
		return dashing;
	}
	
	public boolean endDashing()  {
		dashing = false;
		return dashing;
	}
	
	public void hit(){
		health-=.5;
		hit = true;
	}
	
	public void assessHealth()  {
		if(health == 5)  
			h6 = -1;
		if(health == 4)  
			h5 = -1;
		if(health == 3)  
			h4 = -1;
		if(health == 2)  
			h3 = -1;
		if(health == 1)  
			h2 = -1;
		if(health == 0)  
			h1 = -1;
		
		Physics6.map[1][25] = h1;
		Physics6.map[1][26] = h2;
		Physics6.map[1][27] = h3;
		Physics6.map[2][25] = h4;
		Physics6.map[2][26] = h5;
		Physics6.map[2][27] = h6;
        	 
	}
	
	public void move()  {
    	if(getDirection() == 0 && isMoving()){
            body.setLinearVelocity(getXVelocity(), body.getLinearVelocity().y);
        } 
        else if(getDirection() == 1 && isMoving()){
            body.setLinearVelocity(-getXVelocity(), body.getLinearVelocity().y);
        }
        if(isJumping()){
    		body.setLinearVelocity(body.getLinearVelocity().x, getYVelocity());
    		canFall = true;
        	jump = false;
        }
        if(getDirection() == 0 && isDashing() && dashTimer < 10){
        	dashWaitTimer = 0;
        	dashTimer++;
        	body.setLinearVelocity(getDashVelocity(), body.getLinearVelocity().y);
        } 
        else if(getDirection() == 1 && isDashing() && dashTimer < 10){
        	dashWaitTimer = 0;
        	dashTimer++;
        	body.setLinearVelocity(-getDashVelocity(), body.getLinearVelocity().y);
        } else {
        	if(!isMoving()){
        		getBody().setLinearVelocity(0f, getBody().getLinearVelocity().y);
        	}
        	endDashing();
        	dashTimer = 0;
        }
	}
	
	
	public TextureRegion pAnimate(){
		TextureRegion region = new TextureRegion();
		if(aHit){
			if(body.getLinearVelocity().y==0){
				aHit = false;
			}
			if(aM==1)
    			region = (TextureRegion)lHurt.getKeyFrame(Physics6.getElapsedTime(), true);
    		else
    			region = (TextureRegion)rHurt.getKeyFrame(Physics6.getElapsedTime(), true);
		}
		else if((body.getLinearVelocity().x < -5 || body.getLinearVelocity().x > 5) && dashTimer < 10){
		idleTimer = 0;
    		animTime = 0;
    		dashWaitTimer = 0;
    		if(m==0) 
    			region = (TextureRegion)rDash.getKeyFrame(Physics6.getElapsedTime(), true);
    		else
    			region = (TextureRegion)lDash.getKeyFrame(Physics6.getElapsedTime(), true);
    	}
	else if(!(body.getLinearVelocity().y < 0.001 && body.getLinearVelocity().y > -0.001)){
		idleTimer = 0;
    		if(aM==0)
    			region = (TextureRegion)rJump.getKeyFrame(Physics6.getElapsedTime(), true); //,playerSprite.getX(),playerSprite.getY(),width*1.1f, height*1.1f);
    		else
    			region = (TextureRegion)lJump.getKeyFrame(Physics6.getElapsedTime(), true); //,playerSprite.getX(),playerSprite.getY(),width*1.1f, height*1.1f);
    	}
    	else if(body.getLinearVelocity().x < -0.001 || body.getLinearVelocity().x > 0.001){
    		idleTimer = 0;
    		animTime = 0;
    		if(aM==0) {
    			region = (TextureRegion)rWalking.getKeyFrame(Physics6.getElapsedTime(), true); //,playerSprite.getX(),playerSprite.getY(),width, height);
    		}else
    			region = (TextureRegion)lWalking.getKeyFrame(Physics6.getElapsedTime(), true); //playerSprite.getX(),playerSprite.getY(),width, height);
    	}
    	else if (!((body.getLinearVelocity().x < -0.001 || body.getLinearVelocity().x > 0.001))){
    		if(idleTimer >= 400){
    			animTime +=Gdx.graphics.getDeltaTime();
    			if(aM==0)
    				region = (TextureRegion)rBored.getKeyFrame(animTime, true); //,playerSprite.getX(),playerSprite.getY(),width, height);
	    		else
	    			region = (TextureRegion)lBored.getKeyFrame(animTime, true); //,playerSprite.getX(),playerSprite.getY(),width, height);
    			if(lBored.getKeyFrame(animTime,true).equals(lBored.getKeyFrame(lBored.getAnimationDuration()))){
    				idleTimer = 0;
    				animTime = 0;
    			}
    		}
    		else{
	    		if(aM==0)
	    			region = (TextureRegion)rStanding.getKeyFrame(Physics6.getElapsedTime(), true); //,playerSprite.getX(),playerSprite.getY(),width, height);
	    		else
	    			region = (TextureRegion)lStanding.getKeyFrame(Physics6.getElapsedTime(), true); //,playerSprite.getX(),playerSprite.getY(),width, height);
	    		idleTimer +=1;
    		}
    	}
    	dashWaitTimer++;
		return region;
    }
}

