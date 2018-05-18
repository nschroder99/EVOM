package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class ControlScreenContactListener implements ContactListener {
	ControlScreen game;
	final short GROUP_NO_COLLISION = -1;
	final float PIXELS_TO_METERS = 100f;
	
	public ControlScreenContactListener(ControlScreen ga)  {
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
        
        for(ControlScreenShot s : game.pShots)  {
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
