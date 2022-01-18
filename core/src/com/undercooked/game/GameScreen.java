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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.undercooked.game.entities.Ingredient;
import com.undercooked.game.entities.Order;
import com.undercooked.game.entities.Player;
import com.undercooked.game.entities.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GameScreen implements Screen {

    final Undercooked game;

   // Textures;
    Texture broccoliImage;
    Texture bucketImage;
    Texture counterImage;

    //other game object
    Rectangle plate = new Rectangle(200,200,64,64);

    //Map properties
    TiledMap map;
    TiledMapRenderer tiledmaprenderer;
    MapObjects objects;
    int[] mapLayerIndices;

    RectangleMapObject servingArea;
    RectangleMapObject preparingArea;
    RectangleMapObject blockingObject;
    RectangleMapObject currentLocation;

    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    int dropsGathered;
    Player player1;
    float elapsedTime = 0;
    Player player2;
    private ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();

    double progress = 0;
    int dishesServed = 0;
    int highScore = 0;
    boolean holdingSomething = false;
    boolean putDown = false;
    boolean isOnPlate = false;
    boolean holdingSomethingProcessed = false; // this would later be a property of each of the Set of ingredients being held

    Vector2 playerMovementVector = new Vector2(0.0f, 0.0f);
    float speed = 0;
    double direction = 0;
    float dt;
    Vector2 desired_velocity = new Vector2(0.0f,0.0f);
    double transition_speed = 16;

    boolean soundLooping = false;
    final Boolean multiplayer;
    final Networking net;

    // order and recipe logic
    Set<Ingredient> broccoliSoupIngredients = new HashSet<Ingredient>();
    Ingredient broccoli = new Ingredient("Broccoli", broccoliImage, new Rectangle(0,0, 32, 32));
    Recipe broccoliSoup = new Recipe("broccoli soup", broccoliSoupIngredients);
    List<Order> ordersToBeServed = new LinkedList<Order>();
    Order oneBroccoliSoupPlease = new Order(broccoliSoup, 60, elapsedTime);
    float secondsLeft;

    public GameScreen(final Undercooked game, Networking net, Boolean multiplayer) {
        this.game = game;
        this.multiplayer = multiplayer;
        this.net = net;

        // order and recipe logic
        broccoliSoupIngredients.add(broccoli);
        ordersToBeServed.add(oneBroccoliSoupPlease);


        // load the images for the droplet and the bucket, 64x64 pixels each
        broccoliImage = new Texture(Gdx.files.internal("textures/Broccoli.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        counterImage = new Texture(Gdx.files.internal("counter.jpeg"));

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
        player1 = new Player("Player1");

        if(multiplayer){
            player2 = new Player("Player2");
        }


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
        game.batch.begin();
        // render map
        tiledmaprenderer.setView(camera);
        tiledmaprenderer.render(mapLayerIndices);

        game.batch.draw(bucketImage, plate.x, plate.y);
        if(isOnPlate) game.batch.draw(bucketImage, player1.getHitbox().getX(), player1.getHitbox().getY());

        game.font.draw(game.batch, "incoming orders: " + ordersToBeServed, 0, 480);
        game.font.draw(game.batch, "time left: " + secondsLeft, 0, 465);
        game.font.draw(game.batch, "progress: " + progress, 0, 450);
        game.font.draw(game.batch, "Dishes served: " + dishesServed, 0, 435);
        game.font.draw(game.batch, "player holding something processed: " + holdingSomethingProcessed, 0, 420);
        game.font.draw(game.batch, "player holding something: " + holdingSomething, 0, 405);
        game.font.draw(game.batch, "put down ingredient / ready to process: " + putDown, 0, 390);
        game.font.draw(game.batch, "highscore: " + highScore, 0, 375);

        elapsedTime += Gdx.graphics.getDeltaTime();
        secondsLeft = 60 - elapsedTime;

        //game.batch.draw(dropImage, raindrops.x, raindrops.y);
        //game.batch.draw(broc.texture, broc.hitbox.x, broc.hitbox.y);
        for (Ingredient ingredient : ingredients){
            if(ingredient != null) {
                //TODO make draw in Area responsive to current Position
                if(ingredient.getPickUp()) {
                    game.batch.draw(ingredient.getTexture(), player1.getHitbox().x, player1.getHitbox().y);
                } else{
                    // if ingredient is put down, draw it there
                    System.out.println(ingredient.getIsPreparing());
                    if(ingredient.getIsServed()){
                        drawInArea(servingArea, ingredient);
                    }else if (ingredient.getIsPreparing()){
                        drawInArea(preparingArea, ingredient);
                    }

                }
            }
        }
        game.batch.draw(player1.getTexture(), player1.getHitbox().x, player1.getHitbox().y);
        if(multiplayer && net.joinedMatch){
            game.batch.draw(player2.getTexture(), player2.getHitbox().x + 100, player2.getHitbox().y + 100);
        }


        if(Gdx.input.isKeyPressed(Keys.SPACE)){
            game.batch.draw((TextureRegion) player1.getCutAnimation().getKeyFrame(elapsedTime, true),player1.getHitbox().x, player1.getHitbox().y );
        }else {
            game.batch.draw(player1.getTexture(), player1.getHitbox().x, player1.getHitbox().y);
        }
            if(multiplayer && net.joinedMatch){
            game.batch.draw(player2.getTexture(), player2.getHitbox().x + 100, player2.getHitbox().y + 100);
        }

        game.batch.end();

        // draw progressbar
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);
        game.shape.setColor(Color.BLUE);
        //game.shape.rect(counterBounds.x + 12, counterBounds.y + 70, (float) (0.7 * progress), 20);
        game.shape.end();

        // Map Objects get initialized
        for (MapObject object : objects){

            if(object.getProperties().containsKey("blocked")) {
                blockingObject = (RectangleMapObject) object;
                player1.collisionDetection(blockingObject);

            } else if(object.getProperties().containsKey("Preparing Area")){

                preparingArea = (RectangleMapObject) object;
                currentLocation = getLocation((RectangleMapObject) object, player1.getHitbox());

            } else if (object.getProperties().containsKey("Serving Area")){
                //Set servingArea to be able to acces it in batch.draw
                servingArea =(RectangleMapObject) object;
                currentLocation = getLocation((RectangleMapObject) object, player1.getHitbox());

            } else if (object.getProperties().containsKey("ingredient")){ // TODO let's fix this, seems wrong to look for a key like this, no?
                createIngredient((RectangleMapObject) object, player1.getHitbox());
            }
        }
            //System.out.println(currentLocation.getProperties().containsKey("Serving Area"));

        //Iterator for interaction with Ingredients
        for (Iterator<Ingredient> iter = ingredients.iterator(); iter.hasNext();){
            Ingredient ingredient = iter.next();
            servingAreaAction(servingArea, player1.getHitbox(), ingredient);
            preparingAreaAction(preparingArea, player1.getHitbox(), ingredient);
        }

        // plate logic
        if (plate.overlaps(player1.getHitbox())) {
            if (Gdx.input.isKeyPressed(Keys.A)){
                isOnPlate = true;
            }
        }


        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
        }

        desired_velocity.x = desired_velocity.y = 0.0f;
        if (Gdx.input.isKeyPressed(Keys.LEFT)){
            desired_velocity.x = -300 * Gdx.graphics.getDeltaTime();
            player1.changeTexture("left");
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            desired_velocity.x = 300 * Gdx.graphics.getDeltaTime();
            player1.changeTexture("right");
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)){
            desired_velocity.y = -300 * Gdx.graphics.getDeltaTime();
            player1.changeTexture("down");
        }
        if (Gdx.input.isKeyPressed(Keys.UP)){
            desired_velocity.y = 300 * Gdx.graphics.getDeltaTime();
            player1.changeTexture("up");
        }

        if(net.joinedMatch){
            String[] matchData =  net.getMatchdata();
            if(matchData.length > 1){
                player2.setPosition(matchData[1], matchData[2]);
            }

        }


        // a little mainstream formula from video game development. source: https://www.reddit.com/r/gamedev/comments/1eg21z/how_do_you_implement_acceleration/
        // New Velocity = old_velocity * (1 - delta_time * transition_speed) + desired_velocity * (delta_time * transition_speed)
        playerMovementVector.x = (float) (playerMovementVector.x * (1 - Gdx.graphics.getDeltaTime() * transition_speed) + desired_velocity.x * (Gdx.graphics.getDeltaTime() * transition_speed));
        playerMovementVector.y = (float) (playerMovementVector.y * (1 - Gdx.graphics.getDeltaTime() * transition_speed) + desired_velocity.y * (Gdx.graphics.getDeltaTime() * transition_speed));
        speed = playerMovementVector.len2();
        if (speed >= 300) {
            playerMovementVector.scl(100 / speed);
        }

        player1.getHitbox().x += playerMovementVector.x;
        player1.getHitbox().y += playerMovementVector.y;

        player1.checkBoundaries();

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we play back
        // a sound effect as well.
/*        if (broc.hitbox.overlaps(player1.hitbox)) {
            // pick up food
            if (Gdx.input.isKeyJustPressed(Keys.A) && !pickedUp) {
                dropsGathered++;
                dropSound.play();
                pickedUp = true;
            }
        }*/
        // Closes the window using ecs button.
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }

    }

    public void drawInArea(RectangleMapObject areaObject, Ingredient ingredient){
        game.batch.draw(ingredient.getTexture(), areaObject.getRectangle().x, areaObject.getRectangle().y);
    }

    public RectangleMapObject getLocation(RectangleMapObject object, Rectangle playerObject){
        //System.out.println(currentLocation.getProperties().containsKey("blocked") + "-----Outside");
        if(object.getProperties().containsKey("blocked")){
            if (object.getRectangle().overlaps(playerObject)){
                return object;
            }

        }else if(object.getProperties().containsKey("Preparing Area")){
            if (object.getRectangle().overlaps(playerObject)){
                return object;
            }

        }else if(object.getProperties().containsKey("Serving Area")){
            if (object.getRectangle().overlaps(playerObject)){
                return object;
            }

        }return new RectangleMapObject();
    }

    //Create an ingredient according to the area the player is standing in
    public void createIngredient(RectangleMapObject object, Rectangle playerObject){
        if (object.getProperties().containsKey("broccoli")){
            if (object.getRectangle().overlaps(playerObject)){
                if(Gdx.input.isKeyJustPressed(Keys.A)){
                    ingredients.add(new Ingredient("Broccoli", broccoliImage, new Rectangle(playerObject.x, playerObject.y, 32, 32)));
                    holdingSomething = true;
                }
            }
        }

    }

    public void servingAreaAction(RectangleMapObject areaObject, Rectangle playerObject, Ingredient ingredient){
            if (areaObject.getRectangle().overlaps(playerObject)){
                if (ingredient.getPickUp() && Gdx.input.isKeyJustPressed(Keys.A)) {

                    // I guess this would just result in no points or minus points for the player
                    if(!holdingSomethingProcessed) {
                        System.out.println("you can't serve raw ingredients");
                        return;
                    }
                    // can't serve without a plate. TODO later: if holding.contains(plate) or something like that
                    if(!isOnPlate) {
                        System.out.println("must be served on a plate = bucket");
                        return;
                    }


                    ingredient.putDown(areaObject);
                    dishesServed ++;
                    highScore += Math.ceil(secondsLeft / 20);
                    holdingSomething = false;
                    holdingSomethingProcessed = false;
                    isOnPlate = false;
                }
        }

    }



    public void preparingAreaAction(RectangleMapObject areaObject, Rectangle playerObject, Ingredient ingredient){
        if (((RectangleMapObject) areaObject).getRectangle().overlaps(playerObject)){
            // put down food in order to process it
            if (ingredient.getPickUp() && Gdx.input.isKeyJustPressed(Keys.A)) {
                ingredient.putDown(areaObject);
                dropSound.play();
                putDown = true;
                holdingSomething = false;
            }

            // process the food that is put down
            if (Gdx.input.isKeyPressed(Keys.Q) && ingredient.getIsPreparing()) {
                if(!soundLooping) {
                    dropSound.loop();
                    soundLooping = true;
                }
                progress += 40 * Gdx.graphics.getDeltaTime();

                if (progress > 100) {
                    progress = 0;
                    ingredient.pickUp();
                    holdingSomething = true;
                    holdingSomethingProcessed = true;
                    putDown = false;
                    dropSound.stop();
                    soundLooping = false;
                }
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