package com.undercooked.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {

    final Undercooked game;

    Texture dropImage;
    Texture bucketImage;
    Texture counterImage;
    Texture mapImage;
    Rectangle counterBounds = new Rectangle(430,164,90,200);
    Rectangle servingArea = new Rectangle(700, 170, 50, 130);

    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> raindrops;
    long lastDropTime;
    int dropsGathered;

    double progress = 0;
    int dishesServed = 0;
    boolean pickedUp = false;
    boolean putDown = false;

    Vector2 playerMovementVector = new Vector2(0.0f, 0.0f);
    float speed = 0;
    double direction = 0;
    float dt;
    Vector2 desired_velocity = new Vector2(0.0f,0.0f);
    double transition_speed = 16;

    // with certain actions, we only want to execute when the button is let go again, like when picking up and dropping of


    public GameScreen(final Undercooked gam) {
        this.game = gam;

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        counterImage = new Texture(Gdx.files.internal("counter.jpeg"));
        mapImage = new Texture(Gdx.files.internal("map.jpeg"));

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
        bucket.y = 20; // bottom left corner of the bucket is 20 pixels above
        // the bottom screen edge
        bucket.width = 64;
        bucket.height = 64;

        // create the raindrops array and spawn the first raindrop
        raindrops = new Array<Rectangle>();
        spawnRaindrop();

    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(120, 800 - 64);
        raindrop.y = MathUtils.random(0, 480 - 64);
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(0, 0, 0.2f, 1);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();
        game.batch.draw(mapImage, 0, 0);
        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
        game.font.draw(game.batch, "progress: " + progress, 0, 465);
        game.font.draw(game.batch, "Dishes served: " + dishesServed, 0, 450);
        game.font.draw(game.batch, "picked up ingredient: " + pickedUp, 0, 435);
        game.font.draw(game.batch, "put down ingredient / ready to process: " + putDown, 0, 420);
        for (Rectangle raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.draw(bucketImage, bucket.x, bucket.y);
        // while carrying, draw the ingredient over the player
        if (pickedUp) {
            game.batch.draw(dropImage, bucket.x, bucket.y);
        }
        // if ingredient is put down, draw it there
        if (putDown) {
            game.batch.draw(dropImage, counterBounds.x + 12, counterBounds.y + 100);
        }

        game.batch.end();

        // draw progressbar
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);
        game.shape.setColor(Color.BLUE);
        game.shape.rect(10,10, (float) (2 * progress),20);
        game.shape.end();


        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
            bucket.y = touchPos.y - 64 / 2;
        }

        desired_velocity. x = desired_velocity.y = 0.0f;
        if (Gdx.input.isKeyPressed(Keys.LEFT))
            desired_velocity.x = -300 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            desired_velocity.x = 300 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.DOWN))
            desired_velocity.y = -300 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.UP))
            desired_velocity.y = 300 * Gdx.graphics.getDeltaTime();

        // a little mainstream formula from video game development. source: https://www.reddit.com/r/gamedev/comments/1eg21z/how_do_you_implement_acceleration/
        // New Velocity = old_velocity * (1 - delta_time * transition_speed) + desired_velocity * (delta_time * transition_speed)
        playerMovementVector.x = (float) (playerMovementVector.x * (1 - Gdx.graphics.getDeltaTime() * transition_speed) + desired_velocity.x * (Gdx.graphics.getDeltaTime() * transition_speed));
        playerMovementVector.y = (float) (playerMovementVector.y * (1 - Gdx.graphics.getDeltaTime() * transition_speed) + desired_velocity.y * (Gdx.graphics.getDeltaTime() * transition_speed));
        speed = playerMovementVector.len2();
        if (speed >= 300) {
            playerMovementVector.scl(100 / speed);
        }

        bucket.x += playerMovementVector.x;
        bucket.y += playerMovementVector.y;

        // make sure the bucket stays within the screen bounds
        if (bucket.x < 0)
            bucket.x = 0;
        if (bucket.x > 800 - 64)
            bucket.x = 800 - 64;
        if (bucket.y < 0)
            bucket.y = 0;
        if (bucket.y > 480 - 64)
            bucket.y = 480 - 64;


        // put down food in order to process it
        if (counterBounds.overlaps(bucket)) {
            if (pickedUp && Gdx.input.isKeyJustPressed(Keys.A)) {
                pickedUp = false;
                putDown = true;
            }

            // process the food that is put down
            if (Gdx.input.isKeyPressed(Keys.Q) && putDown)
                progress += 35 * Gdx.graphics.getDeltaTime();
        }

        // serve it and then the food isn't there anymore
        // TODO @Elena insert the image of the "serving area" from Jan's graphic at the right end
        //              of the screen and add variables so that the bucket has to walk over
        //              to the serving area and when it overlaps, only then the dish is served
        //              and the counter goes up
        // No image used for serving area, just a rectangle
        if (servingArea.overlaps(bucket)){
            if (pickedUp && Gdx.input.isKeyJustPressed(Keys.A)) {
                pickedUp = false;
                putDown = true;
                dishesServed ++;
            }
        }
        if (progress > 100) {
            progress = 0;
            putDown = false;
//            dishesServed += 1;
        }


        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime > 4000000000L)
            spawnRaindrop();

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we play back
        // a sound effect as well.
        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            if (raindrop.overlaps(bucket)) {
                // pick up food
                if (Gdx.input.isKeyJustPressed(Keys.A) && !pickedUp) {
                    dropsGathered++;
                    dropSound.play();
                    iter.remove();
                    pickedUp = true;
                }
            }
        }
        // Closes the window using ecs button.
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}