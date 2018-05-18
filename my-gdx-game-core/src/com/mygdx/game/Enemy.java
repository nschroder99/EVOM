package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Enemy {
	Sprite enemySprite, gunSprite;
	Animation<Object> rWalking, lWalking, rDeath, lDeath;
	TextureAtlas atlas;
	Body body;
	BodyDef bodyDef;
	PolygonShape shape;
	FixtureDef fixtureDef;
	Fixture fixture;
	float health, xPos, yPos, width, height, shotSpeed, damage, xVel, yVel, elapsedTime = 0, animTime = 0, idleTimer = 0,shotTimer = 0;
	boolean moving, jump,dead,onGround,hitWall, fly, aDead, respawn = false, beginADead;
	int m, shotTime; 
	final float PIXELS_TO_METERS = 100f;
	final short PLAYER_CATEGORY_BITS = 0x001, GROUND_ENEMY_CATEGORY_BITS = 0x002, FLOOR_CATEGORY_BITS = 0x003, WALL_CATEGORY_BITS = 0x004, FLYING_ENEMY_CATEGORY_BITS = 0x005, INVINCIBLE_CATEGORY_BITS = 0x006, SHOT_CATEGORY_BITS = 0x007;
	final short PLAYER_MASK_BITS =  ~PLAYER_CATEGORY_BITS, INVINCIBLE_MASK_BITS = FLOOR_CATEGORY_BITS | WALL_CATEGORY_BITS, GROUND_ENEMY_MASK_BITS = PLAYER_CATEGORY_BITS | FLOOR_CATEGORY_BITS | WALL_CATEGORY_BITS,  FLOOR_MASK_BITS = PLAYER_CATEGORY_BITS | GROUND_ENEMY_CATEGORY_BITS, WALL_MASK_BITS = -1, FLYING_ENEMY_MASK_BITS = WALL_CATEGORY_BITS | PLAYER_CATEGORY_BITS, SHOT_MASK_BITS = ~SHOT_CATEGORY_BITS;
	String groundImOn = "", atlasName, regionRightName, regionLeftName, regionDeathRightName, regionDeathLeftName;
	
	public Enemy(Sprite es, Sprite gs, String atl, String regionRight, String regionLeft, String regionDeathRight, String regionDeathLeft, float h, float he,float wi, float x, float y, float ss, float d, float xv, float yv, boolean flying, int di,float st,boolean ad, boolean bad, short gi)  {
		enemySprite = es;
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
		dead = false;
		fly = flying;
		shotTimer = st;
		aDead = ad;
		beginADead = bad;
		atlasName = atl;
		regionRightName = regionRight;
		regionLeftName = regionLeft;
		regionDeathRightName = regionDeathRight;
		regionDeathLeftName = regionDeathLeft;
		m=di;
		atlas = new TextureAtlas(atl);
        
        lWalking = new Animation<Object>(1/16f, atlas.findRegions(regionLeft));
        rWalking = new Animation<Object>(1/16f, atlas.findRegions(regionRight));
        lDeath = new Animation<Object>(1/30f, atlas.findRegions(regionDeathLeft));
        rDeath = new Animation<Object>(1/30f, atlas.findRegions(regionDeathRight));
        if(fly){
        	lWalking = new Animation<Object>(1/64f, atlas.findRegions(regionLeft));
            rWalking = new Animation<Object>(1/64f, atlas.findRegions(regionRight));
        }
		enemySprite.setPosition(x, y);
		
		bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set((x + width/2) / PIXELS_TO_METERS, (y + height/2) / PIXELS_TO_METERS);
		shape = new PolygonShape();
		shape.setAsBox(width/2 / PIXELS_TO_METERS, height/2 / PIXELS_TO_METERS);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0f;
//        if(fly)  {
//        	fixtureDef.filter.categoryBits = FLYING_ENEMY_CATEGORY_BITS;
//        	fixtureDef.filter.maskBits = FLYING_ENEMY_MASK_BITS;
//        }
//        else {
//        	fixtureDef.filter.categoryBits = GROUND_ENEMY_CATEGORY_BITS;
//        	fixtureDef.filter.maskBits = GROUND_ENEMY_MASK_BITS;
//        }
        fixtureDef.filter.groupIndex = gi;
        
	}
	
	public Enemy() {

	}

	public Body getBody()  {return body;}
	public Sprite getEnemySprite() {return enemySprite;}
	public Sprite getGunSprite() {return gunSprite;}
	public BodyDef getBodyDef() {return bodyDef;}
	public FixtureDef getFixtureDef() {return fixtureDef;}
	public PolygonShape getShape() {return shape;}
	public Animation<Object> getLeftAnimation() {return rWalking;}
	public Animation<Object> getRightAnimation() {return lWalking;}
	public float getHealth() {return health;}
	public float getX() {return xPos;}
	public float getY() {return yPos;}
	public float getWidth() {return width;}
	public float getHeight() {return height;}
	public float getDamage() {return damage;}
	public float getShotSpeed() {return shotSpeed;}
	public float getXVelocity() {return xVel;}
	public float getYVelocity() {return yVel;}
	public float getElapsedTime() {return elapsedTime;}
	public float getAnimTime() {return animTime;}
	public float getIdleTime() {return idleTimer;}
	public int getDirection() {return m;}
	public boolean isMoving() {return moving;}
	public boolean isJumping() {return jump;}
	public boolean isntJumping() {return body.getLinearVelocity().y < 0.001 && body.getLinearVelocity().y > -0.001;}
	public float getShotTimer(){return shotTimer;}
	
	
	public boolean isShooting(){
		if(shotTime>shotTimer){
			shotTime = 0;
			return true;
		}
		else
			return false;
	}
	
	public void makeDead(){
		aDead = true;
		beginADead = true;
	}
	
	public void isDead()  {
		dead = true;
	}
	
	public void hit()  {
		health -= 1;
	}
	
	public int changeDirection(int direction) {
		m = direction;
		return m;
	}
	public void flipDirection(){
		if(m==1)
			m=0;
		else
			m=1;
	}
	
	public TextureRegion eAnimate()  {
		TextureRegion region = new TextureRegion();
		shotTime ++;
		if(aDead){
			animTime += Gdx.graphics.getDeltaTime();
			xVel = 0;
			yVel = 0;
			if(lDeath.getKeyFrame(animTime,true).equals(lDeath.getKeyFrame(lDeath.getAnimationDuration()))){
				isDead();
			}
			else if(getDirection() == 0){
				region = (TextureRegion)rDeath.getKeyFrame(animTime, true);
			}
			else{
				region = (TextureRegion)lDeath.getKeyFrame(animTime, true);
			}
		}
		else if(!aDead && fly){
			if(Physics6.player.body.getPosition().x+0.2>body.getPosition().x)
				region = (TextureRegion)getRightAnimation().getKeyFrame(Physics6.getElapsedTime(), true);
			else
				region = (TextureRegion)getLeftAnimation().getKeyFrame(Physics6.getElapsedTime(), true);
		}
		else if(!aDead && getDirection() == 0)  
    		region = (TextureRegion)getRightAnimation().getKeyFrame(Physics6.getElapsedTime(), true);//, e.getEnemySprite().getX(),e.getEnemySprite().getY(),e.getWidth(), e.getHeight());
    	else
    		region = (TextureRegion)getLeftAnimation().getKeyFrame(Physics6.getElapsedTime(), true); //, e.getEnemySprite().getX(),e.getEnemySprite().getY(),e.getWidth(), e.getHeight());
    	
    	return region;
    }
	
	public void eMove(){
		if(!fly)  {
    		if(body.getLinearVelocity().y > 0.001 || body.getLinearVelocity().y < -0.001){
    			onGround = false;
    			body.setLinearVelocity(0, body.getLinearVelocity().y);
    		}
    		
    		if(onGround){
    			body.setLinearVelocity((getDirection() == 0) ? getXVelocity() : -getXVelocity(), body.getLinearVelocity().y);
    		}
    		if(hitWall){
    			if(getDirection() == 0){
    				changeDirection(1);
    			} else {
    				changeDirection(0);
    			}
    			body.setLinearVelocity((getDirection() == 0) ? getXVelocity() : -getXVelocity(), body.getLinearVelocity().y);
    			hitWall = false;
    		}
		}
		else {
			if(aDead){
				body.setLinearVelocity((getDirection() == 0) ? getXVelocity() : -getXVelocity(), (float) (body.getLinearVelocity().y));
				body.applyForceToCenter(0f, -0.5f, true);
				if(body.getLinearVelocity().y == 0)
					dead = true;
			}
			if(body.getPosition().y> 3.5){
				body.applyForceToCenter(0f, -.9f, true);
			}
			body.setLinearVelocity((getDirection() == 0) ? getXVelocity() : -getXVelocity(), body.getLinearVelocity().y);
			if(Physics6.player.body.getPosition().y+0.2>body.getPosition().y){
				body.applyForceToCenter(0f, 0.2f, true);
			}
			else{
				body.applyForceToCenter(0f, 0.175f, true);
			}
			if(Physics6.player.body.getPosition().x+0.2>body.getPosition().x){
				body.applyForceToCenter(0.3f, 0f, true);
			}
			else{
				body.applyForceToCenter(-0.3f, 0f, true);
			}
    		if(hitWall|| onGround){
    			if(onGround)
    				body.applyForceToCenter(0f, 0.19f, true);
    			if(hitWall)
    				flipDirection();
    			hitWall = false;
    			onGround = false;
    			if(Math.floor(Math.random()*2) == 1){
    				flipDirection();
    			} 
//    			body.setLinearVelocity((getDirection() == 0) ? getXVelocity() : -getXVelocity(), body.getLinearVelocity().y);
    		}
    		
		}
    }
	
	public void update()  {
		enemySprite.setPosition((body.getPosition().x * PIXELS_TO_METERS) - width/2 , (body.getPosition().y * PIXELS_TO_METERS) - height/2 );
	}
	
}
