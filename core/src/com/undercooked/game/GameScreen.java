package com.undercooked.game;

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
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.undercooked.game.entities.Ingridient;

public class GameScreen implements Screen {

    final Undercooked game;

   // Texture dropImage;
    Texture broccoliImage;
    Texture bucketImage;
    Texture counterImage;

    //Rectangle counterBounds = new Rectangle(430,164,90,200);
    //Rectangle servingArea = new Rectangle(700, 170, 50, 130);

    //Map properties
    TiledMap map;
    TiledMapRenderer tiledmaprenderer;
    MapObjects objects;
    int[] mapLayerIndices;

    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    //Rectangle raindrops;
    RectangleMapObject servingArea;
    long lastDropTime;
    int dropsGathered;
    Ingridient broc;

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

    boolean soundLooping = false;

    public GameScreen(final Undercooked game) {
        this.game = game;

        // load the images for the droplet and the bucket, 64x64 pixels each
       // dropImage = new Texture(Gdx.files.internal("droplet.png"));
        broccoliImage = new Texture(Gdx.files.internal("textures/Broccoli.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        counterImage = new Texture(Gdx.files.internal("counter.jpeg"));
        // mapImage = new Texture(Gdx.files.internal("map.jpeg"));

        // load Tiled Map and generate Layerindex;
        map = new TmxMapLoader().load("map/map_v.0.1.tmx");
        tiledmaprenderer = new OrthogonalTiledMapRenderer(map);
        MapLayers mapLayers = map.getLayers();
        objects = mapLayers.get("Object Layer 1").getObjects();
        mapLayerIndices = new int[]{
                mapLayers.getIndex("Tile Layer 1"),
                mapLayers.getIndex("Tile Layer 2"),
                mapLayers.getIndex("Tile Layer 3")
        };


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
       // raindrops = new Rectangle(46,96,64,64);
        broc = new Ingridient("Broccoli", broccoliImage, new Rectangle(46,46,64,64));

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
        // render map
        tiledmaprenderer.setView(camera);
        tiledmaprenderer.render(mapLayerIndices);


        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
        game.font.draw(game.batch, "progress: " + progress, 0, 465);
        game.font.draw(game.batch, "Dishes served: " + dishesServed, 0, 450);
        game.font.draw(game.batch, "picked up ingredient: " + pickedUp, 0, 435);
        game.font.draw(game.batch, "put down ingredient / ready to process: " + putDown, 0, 420);
        //game.batch.draw(dropImage, raindrops.x, raindrops.y);
        game.batch.draw(broc.texture, broc.hitbox.x, broc.hitbox.y);
        game.batch.draw(bucketImage, bucket.x, bucket.y);

        // while carrying, draw the ingredient over the player
        if (pickedUp) {
            game.batch.draw(broc.texture, bucket.x, bucket.y);
        }
        // if ingredient is put down, draw it there
        drawInServingArea((RectangleMapObject) servingArea, broccoliImage);
        game.batch.end();

        // draw progressbar
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);
        game.shape.setColor(Color.BLUE);
        //game.shape.rect(counterBounds.x + 12, counterBounds.y + 70, (float) (0.7 * progress), 20);
        game.shape.end();


        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
            bucket.y = touchPos.y - 64 / 2;
        }

        desired_velocity.x = desired_velocity.y = 0.0f;
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

        // collision detection
        for (MapObject object : objects){

            if(object.getProperties().containsKey("blocked")) {
                collisionDetection((RectangleMapObject) object, bucket);

            } else if(object.getProperties().containsKey("Preparing Area")){
                servingArea =(RectangleMapObject) object;
                preparingAreaAction(servingArea, bucket);


            } else if (object.getProperties().containsKey("Serving Area")){
                servingAreaAction((RectangleMapObject) object, bucket);
            }
        }

        if (progress > 100) {
            progress = 0;
            putDown = false;
            dishesServed += 1;
            dropSound.stop();
            soundLooping = false;
        }


        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we play back
        // a sound effect as well.
        if (broc.hitbox.overlaps(bucket)) {
            // pick up food
            if (Gdx.input.isKeyJustPressed(Keys.A) && !pickedUp) {
                dropsGathered++;
                dropSound.play();
                pickedUp = true;
            }
        }
        // Closes the window using ecs button.
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }

    }

    public void servingAreaAction(RectangleMapObject areaObject, Rectangle playerObject){
        if (areaObject.getRectangle().overlaps(playerObject)){
            if (pickedUp && Gdx.input.isKeyJustPressed(Keys.A)) {
                pickedUp = false;
                putDown = true;
                dishesServed ++;
            }
        }
    }

    public void drawInServingArea(RectangleMapObject areaObject, Texture objectImage){
        if (putDown) {
            game.batch.draw(objectImage, areaObject.getRectangle().x + 12, areaObject.getRectangle().y + 100);
        }
    }
    public void preparingAreaAction(RectangleMapObject areaObject, Rectangle playerObject ){
        if (((RectangleMapObject) areaObject).getRectangle().overlaps(playerObject)){
            // put down food in order to process it
            if (pickedUp && Gdx.input.isKeyJustPressed(Keys.A)) {
                pickedUp = false;
                putDown = true;
                dropSound.play();
            }

            // process the food that is put down
            if (Gdx.input.isKeyPressed(Keys.Q) && putDown) {
                if(!soundLooping) {
                    dropSound.loop();
                    soundLooping = true;
                }
                progress += 40 * Gdx.graphics.getDeltaTime();
            }
        }
    }
    public void collisionDetection(RectangleMapObject blockingObject, Rectangle playerObject){
        // bucket can't cross objects with propertie "blocked
        if (blockingObject.getRectangle().overlaps(playerObject)) {
            /*System.out.print("Obere Kante : " +blockingObject.getRectangle().y + blockingObject.getRectangle().height + "\n Untere Kante: "
            +blockingObject.getRectangle().y+ "\n Eimer: " +bucket.y);*/
            if (blockingObject.getRectangle().x > playerObject.x) {
               // System.out.print("right");
                playerObject.x = playerObject.x - 10;
            } else if (blockingObject.getRectangle().x + blockingObject.getRectangle().width - 5 < playerObject.x) {
               // System.out.print("left");
                playerObject.x = playerObject.x + 10;
            } else if (blockingObject.getRectangle().y > playerObject.y) {
               // System.out.print("down");
                playerObject.y = playerObject.y - 10;
            } else if (blockingObject.getRectangle().y + blockingObject.getRectangle().height - 5 < playerObject.y) {
               // System.out.print("up");
                playerObject.y = playerObject.y + 10;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
       // rainMusic.play();
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
        broccoliImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}