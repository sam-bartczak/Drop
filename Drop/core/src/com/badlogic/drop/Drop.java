package com.badlogic.drop;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Drop extends ApplicationAdapter {
	private Texture BucketImage;
	private Texture DropImage;
	private Sound DropSound;
	private Music RainSounds;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long LastDropTime;
	
	
	@Override
	public void create () {
		//creating the camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false,800,480);
		
		//making spriteBatch
		batch = new SpriteBatch();
		//loading the images 
		DropImage = new Texture(Gdx.files.internal("drop.png"));
		BucketImage = new Texture(Gdx.files.internal("bucket.png"));
		
		//loading the sounds
		DropSound = Gdx.audio.newSound(Gdx.files.internal("DropSound.wav"));
		RainSounds = Gdx.audio.newMusic(Gdx.files.internal("RainSound.mp3"));
		
		//Start the playing of the rain music
		RainSounds.setLooping(true);
		RainSounds.play();
		
		//setting bucket rectangle's size and position
		bucket = new Rectangle();
		bucket.x = 800/2 - 32;
		bucket.y = 20;
		bucket.width = 64;
		
		//creating raindrop list and spawning first raindrop
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
		
		
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		LastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		if(Gdx.input.isTouched()) {
			Vector3 position = new Vector3();
			position.set(Gdx.input.getX(),Gdx.input.getY(),0);
			camera.unproject(position);
			bucket.x = position.x - 64/2;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			bucket.x = bucket.x + 350*Gdx.graphics.getDeltaTime();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			bucket.x = bucket.x - 350*Gdx.graphics.getDeltaTime();
		}
		if (bucket.x <0) {
			bucket.x = 0;
		}
		if (bucket.x > 800-64) {
			bucket.x = 800-64;
		}
		//if it has been more than a second, we will spawn a raindrop
		if (TimeUtils.nanoTime() - LastDropTime>1000000000) {
			spawnRaindrop();
		}
		//removing raindrop rectangle from list if it exits the screen
		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle rect = iter.next();
			rect.y = rect.y - 200 *Gdx.graphics.getDeltaTime();
			if (rect.y +64<0) {
				iter.remove();
			}
			if (rect.overlaps(bucket)){
				iter.remove();
				DropSound.play();
				
			}
		}
		//drawing each raindrop and setting its position to the required place.
		batch.begin();
		batch.draw(BucketImage,bucket.x,bucket.y);
		for (Rectangle raindrop: raindrops) {
			batch.draw(DropImage,raindrop.x,raindrop.y);
		}
		
		batch.end();
	}
	
	
	@Override
	public void dispose () {
		DropImage.dispose();
		BucketImage.dispose();
		DropSound.dispose();
		RainSounds.dispose();
		batch.dispose();
	}
}
