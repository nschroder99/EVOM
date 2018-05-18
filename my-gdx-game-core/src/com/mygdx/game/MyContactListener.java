package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class MyContactListener implements ContactListener {
	Physics6 game;
	final short GROUP_NO_COLLISION = -1;
	final float PIXELS_TO_METERS = 100f;
	
	public MyContactListener(Physics6 ga)  {
		game = ga;
	}
	@Override
	public void beginContact(Contact contact) {
		
		Body a=contact.getFixtureA().getBody();
        Body b=contact.getFixtureB().getBody();
        
        
        if((a.getUserData() instanceof Player && ((b.getUserData() instanceof String) ? b.getUserData().toString().substring(0, 3).equals("bot") : false)) || (b.getUserData() instanceof Player && ((a.getUserData() instanceof String) ? a.getUserData().toString().substring(0, 3).equals("bot") : false))) {
			if(game.player.health > 1)
				game.player.hit();
			else
				game.player.dead = true;
        		
        	
        }
        
        if((a.getUserData() instanceof Player && ((b.getUserData() instanceof String) ? b.getUserData().toString().indexOf("enemy") >= 0 : false)) || (b.getUserData() instanceof Player && ((a.getUserData() instanceof String) ? a.getUserData().toString().indexOf("enemy") >= 0 : false))) {
        	if(game.player.health > 1)
				game.player.hit();
			else
				game.player.dead = true;
        }
        
        for(Enemy e : game.enemies)  {
        	if((a.getUserData().equals("wall") && ((b.getUserData() instanceof String) ? b.getUserData().toString().substring(0, 3).equals("bot") : false)) || (((b.getUserData() instanceof String) ? b.getUserData().toString().substring(0, 3).equals("bot") : false) && b.getUserData().equals("wall")))  {
        		if(e.getBody().getUserData().equals(a.getUserData()) || e.getBody().getUserData().equals(b.getUserData())){
        			e.hitWall = true;
        		}
        	 }
        	if(((a.getUserData().equals("lastFloor")) && ((b.getUserData() instanceof String) ? b.getUserData().toString().substring(0, 3).equals("bot") : false)) || (((a.getUserData() instanceof String) ? a.getUserData().toString().substring(0, 3).equals("bot") : false) && (b.getUserData().equals("floor"))))  {
        		if(e.getBody().getUserData().equals(a.getUserData()) || e.getBody().getUserData().equals(b.getUserData()))  {
        			e.makeDead();
        			e.respawn = true;
        		}
        	}
        	if(((a.getUserData().equals("floor") || a.getUserData().equals("eFloor")) && ((b.getUserData() instanceof String) ? b.getUserData().toString().substring(0, 3).equals("bot") : false)) || (((a.getUserData() instanceof String) ? a.getUserData().toString().substring(0, 3).equals("bot") : false) && (b.getUserData().equals("floor") || b.getUserData().equals("eFloor"))))  {	 
        		if(e.getBody().getUserData().equals(a.getUserData()) || e.getBody().getUserData().equals(b.getUserData())){
        			e.onGround = true;
        			if(a.getUserData().equals("floor") || a.getUserData().equals("eFloor")){
        				e.groundImOn = "(" + (((int)(((a.getPosition().x * PIXELS_TO_METERS) + 384) / 32))) + ", " + (int)(((384 - (a.getPosition().y * PIXELS_TO_METERS)) / 32) + 0.4) + ")";
        			} else {
        				e.groundImOn =  "(" + ", " + (int)(((384 - (b.getPosition().y * PIXELS_TO_METERS)) / 32) + 0.4) + ")";
        			}
         			if(e.fly)
         				e.body.applyForceToCenter(0f, 1f, true);
         		 }
        		 if(((String)a.getUserData()).indexOf("tesla") >= 0 ||((String)b.getUserData()).indexOf("tesla") >= 0 )  {
	        		if(a.getUserData().equals("floor") || a.getUserData().equals("eFloor")){
	            		a.setUserData("eFloor");
	            	} else {
	            		b.setUserData("eFloor");
	            	}
        		 }	
            	game.electrifyFloor();
        	 }
        	 if((((a.getUserData() instanceof String) ? a.getUserData().toString().indexOf("player") >= 0 : false) && ((b.getUserData() instanceof String) ? b.getUserData().toString().substring(0, 3).equals("bot") : false)) || (((b.getUserData() instanceof String) ? b.getUserData().toString().indexOf("player") >= 0 : false) && ((a.getUserData() instanceof String) ? a.getUserData().toString().substring(0, 3).equals("bot") : false))) {
        		 if(e.getBody().getUserData().equals(a.getUserData())||e.getBody().getUserData().equals(b.getUserData())){
         			if(e.health > 1)
         				e.hit();
         			else
         				e.makeDead();
         		}
        	 }
        }
        
        for(Shot s : game.pShots)  {
        	 if((((a.getUserData() instanceof String) ? a.getUserData().toString().indexOf("player") >= 0 : false) && ((b.getUserData() instanceof String) ? b.getUserData().toString().substring(0, 3).equals("bot") : false)) || (((b.getUserData() instanceof String) ? b.getUserData().toString().indexOf("player") >= 0 : false) && ((a.getUserData() instanceof String) ? a.getUserData().toString().substring(0, 3).equals("bot") : false))) {
        		 if(s.getBody().getUserData().equals(a.getUserData())||s.getBody().getUserData().equals(b.getUserData())){
         			s.isDead();
         		}
        	 }
        	 if((((a.getUserData() instanceof String) ? a.getUserData().equals("wall") : false)&& ((b.getUserData() instanceof String) ? b.getUserData().toString().indexOf("shot") >= 0 : false)) || (((b.getUserData() instanceof String) ? b.getUserData().equals("wall") : false)&& ((a.getUserData() instanceof String) ? a.getUserData().toString().indexOf("shot") >= 0 : false)) || (((a.getUserData() instanceof String) ? a.getUserData().equals("floor") : false)&& ((b.getUserData() instanceof String) ? b.getUserData().toString().indexOf("shot") >= 0 : false)) || (((b.getUserData() instanceof String) ? b.getUserData().equals("floor") : false)&& ((a.getUserData() instanceof String) ? a.getUserData().toString().indexOf("shot") >= 0 : false))||(((a.getUserData() instanceof String) ? a.getUserData().equals("eFloor") : false)&& ((b.getUserData() instanceof String) ? b.getUserData().toString().indexOf("shot") >= 0 : false)) ||((b.getUserData() instanceof String) ? b.getUserData().equals("eFloor") : false)&& ((a.getUserData() instanceof String) ? a.getUserData().toString().indexOf("shot") >= 0 : false)) {
        		 if(s.getBody().getUserData().equals(a.getUserData())||s.getBody().getUserData().equals(b.getUserData())){
         			s.isDead();
         		}
        	 }
        	 if((a.getUserData() instanceof Player && ((b.getUserData() instanceof String) ? b.getUserData().toString().indexOf("enemy") >= 0 : false)) || (b.getUserData() instanceof Player && ((a.getUserData() instanceof String) ? a.getUserData().toString().indexOf("enemy") >= 0 : false))) {
        		 if(s.getBody().getUserData().equals(a.getUserData())||s.getBody().getUserData().equals(b.getUserData())){
          			s.isDead();
          		}
             }
        }
   }

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
