package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Shot {
	Sprite sprite;
	Animation<Object> lAnimation, rAnimation;
	TextureAtlas shotAtlas;
	Body body;
	BodyDef bodyDef;
	PolygonShape shape;
	Fixture fixture;
	FixtureDef fixtureDef;
	boolean dead;
	int height, width;
	float xPos, yPos, shotSpeed, angle,xSpeed,ySpeed, tDistance;
	final float PIXELS_TO_METERS = 100f;
	final short PLAYER_CATEGORY_BITS = 0x001, GROUND_ENEMY_CATEGORY_BITS = 0x002, FLOOR_CATEGORY_BITS = 0x003, WALL_CATEGORY_BITS = 0x004, FLYING_ENEMY_CATEGORY_BITS = 0x005, INVINCIBLE_CATEGORY_BITS = 0x006, SHOT_CATEGORY_BITS = 0x007;
	final short PLAYER_MASK_BITS =  ~PLAYER_CATEGORY_BITS, INVINCIBLE_MASK_BITS = FLOOR_CATEGORY_BITS | WALL_CATEGORY_BITS, GROUND_ENEMY_MASK_BITS = PLAYER_CATEGORY_BITS | FLOOR_CATEGORY_BITS | WALL_CATEGORY_BITS,  FLOOR_MASK_BITS = PLAYER_CATEGORY_BITS | GROUND_ENEMY_CATEGORY_BITS, WALL_MASK_BITS = -1, FLYING_ENEMY_MASK_BITS = WALL_CATEGORY_BITS | PLAYER_CATEGORY_BITS, SHOT_MASK_BITS = ~SHOT_CATEGORY_BITS;
	
	
	public Shot(float x, float y, float xSp, float ySp,float tD, float s,int h,int w,float a,String sA, String aLeft, String aRight, short group){
		xPos = x;
		yPos = y;
		xSpeed = xSp;
		ySpeed = ySp;
		tDistance = tD;
		shotSpeed = s;
		height = h;
		width = w;
		angle = a;
		sprite = new Sprite();
		shotAtlas = new TextureAtlas(sA);
		lAnimation = new Animation<Object>(1/8f, shotAtlas.findRegions(aLeft));
		rAnimation = new Animation<Object>(1/8f, shotAtlas.findRegions(aRight));
		sprite.setPosition(x, y);
		bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set((x + width/2) / PIXELS_TO_METERS, (y + height/2) / PIXELS_TO_METERS);
		shape = new PolygonShape();
		shape.setAsBox(width/2 / PIXELS_TO_METERS, height/2 / PIXELS_TO_METERS);
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 0f;
//        fixtureDef.filter.categoryBits = SHOT_CATEGORY_BITS;
//        fixtureDef.filter.maskBits = SHOT_MASK_BITS;
        fixtureDef.filter.groupIndex = -2;
        
        
	}
	public Shot() {

	}
	public Body getBody()  {return body;}
	public Sprite getShotSprite() {return sprite;}
	public BodyDef getBodyDef() {return bodyDef;}
	public FixtureDef getFixtureDef() {return fixtureDef;}
	public PolygonShape getShape() {return shape;}
	public Animation<Object> getRightAnimation() {return rAnimation;}
	public Animation<Object> getLeftAnimation() {return lAnimation;}
	public float getX() {return xPos;}
	public float getY() {return yPos;}
	public float getWidth() {return width;}
	public float getHeight() {return height;}
	public float getShotSpeed() {return shotSpeed;}
	public float getAngle() {return angle;}
	public boolean getDead() {return dead;}
	
	
	public void isDead(){
		dead = true;
	}
	
	public void update(){
		angle = -((float) (Math.atan(ySpeed/xSpeed)*(180/Math.PI)));
		body.setLinearVelocity(((xSpeed*10)*shotSpeed)/tDistance, ((ySpeed*10)*shotSpeed)/tDistance);
		sprite.setPosition((body.getPosition().x* PIXELS_TO_METERS) - width/2 , (body.getPosition().y * PIXELS_TO_METERS) - height/2 );
	}
	
	 public void createPShot(int counter){
	    	body = Physics6.getWorld().createBody(bodyDef);
	        body.setFixedRotation(true);
	        fixture = body.createFixture(fixtureDef);
	        body.setUserData("shot" + counter + " - player");
	        shape.dispose();
	 }
	 
	 public void createEShot(int counter){
	    	body = Physics6.getWorld().createBody(bodyDef);
	        body.setFixedRotation(true);
	        fixtureDef.filter.groupIndex = -1;
	        fixture = body.createFixture(fixtureDef);
	        body.setUserData("shot" + counter + " - enemy");
	        shape.dispose();
	 }
	 
	 public TextureRegion sAnimate(){
		TextureRegion region = new TextureRegion();
		if(xSpeed <0)
			region = (TextureRegion)lAnimation.getKeyFrame(Physics6.elapsedTime, true);
		else
			region = (TextureRegion)rAnimation.getKeyFrame(Physics6.elapsedTime, true);
    	update();
    	if(dead){
    		Physics6.pShots.remove(this);
    		Physics6.getWorld().destroyBody(body);
    	}
    	return region;
     }
	
}
