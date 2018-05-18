package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Wall {
	
	FixtureDef fixtureDefinition;
	BodyDef bodyDefinition;
	EdgeShape edgeShape;
	
	public Wall(FixtureDef fix, BodyDef body, EdgeShape edge)  {
		fixtureDefinition = fix;
		bodyDefinition = body;
		edgeShape = edge;
	}
	
	public FixtureDef getFix()  {return fixtureDefinition;}
	public BodyDef getBody()  {return bodyDefinition;}
	public EdgeShape getShape()  {return edgeShape;}
	
	
	
	
}
