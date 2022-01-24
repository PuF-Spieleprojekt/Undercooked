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
import com.undercooked.game.entities.NetworkPlayer;
import com.undercooked.game.entities.Order;
import com.undercooked.game.entities.Player;
import com.undercooked.game.entities.Recipe;
import com.undercooked.game.utilities.enums.Direction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import grpc.gateway.protoc_gen_openapiv2.options.Openapiv2;

public class GameScreen implements Screen {

    // Game-Settings
    final Undercooked game;
    final Boolean multiplayer;
    final Boolean isHost;
    final Networking net;

   // Textures;
    Texture broccoliImage;
    Texture plateImage;
    Texture counterImage;
    Texture orderImage;

    //other game object
    Rectangle plate = new Rectangle(200,200,32,32);

    //Map properties
    TiledMap map;
    TiledMapRenderer tiledmaprenderer;
    MapObjects objects;
    int[] mapLayerIndices;

    RectangleMapObject servingArea;
    RectangleMapObject preparingArea;
    RectangleMapObject blockingObject;
    RectangleMapObject ingredientArea;
    RectangleMapObject currentLocation;

    Sound dropSound, choppingSound, punchSound;
    Music backgroundMusic;
    OrthographicCamera camera;
    int dropsGathered;
    Player player1;
    NetworkPlayer netPlayer1;
    NetworkPlayer netPlayer2;
    private ArrayList <NetworkPlayer> players = new ArrayList<NetworkPlayer>();
    private ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();

    double progress = 0;
    int dishesServed = 0;
    int highScore = GlobalUtilities.highscore;
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


    // timing
    public float elapsedTime = 0;
    final float GAMETIME = 120; // one round lasts 120 seconds
    float secondsLeft;

    // order and recipe logic
    Set<Ingredient> broccoliSoupIngredients = new HashSet<Ingredient>();
    Ingredient broccoli = new Ingredient("Broccoli", broccoliImage, new Rectangle(0,0, 32, 32));
    Recipe broccoliSoup = new Recipe("broccoli soup", broccoliSoupIngredients);
    List<Order> ordersToBeServed = new LinkedList<Order>();
    Order oneBroccoliSoupPlease = new Order(broccoliSoup, 60, elapsedTime);
    Order anotherBroccoliPlease = new Order(broccoliSoup, 60, elapsedTime);


    public GameScreen(final Undercooked game, Networking net, Boolean multiplayer, Boolean isHost) {
        this.game = game;
        this.multiplayer = multiplayer;
        this.isHost = isHost;
        this.net = net;

        // order and recipe logic
        broccoliSoupIngredients.add(broccoli);
        ordersToBeServed.add(oneBroccoliSoupPlease);
        ordersToBeServed.add(anotherBroccoliPlease);


        // load the images for the droplet and the bucket, 64x64 pixels each
        broccoliImage = new Texture(Gdx.files.internal("textures/Broccoli.png"));
        plateImage = new Texture(Gdx.files.internal("textures/plate1.png"));
        counterImage = new Texture(Gdx.files.internal("counter.jpeg"));
        orderImage = new Texture(Gdx.files.internal("textures/order_sushi.png"));

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
        //dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("backgroundMusic.mp3"));
        choppingSound = Gdx.audio.newSound(Gdx.files.internal("chopping.wav"));
        dropSound = Gdx.audio.newSound(Gdx.files.internal("punch.wav"));
        backgroundMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the bucket


        if(multiplayer){
            players.add(new NetworkPlayer(net));
            players.add(new NetworkPlayer(net));
            netPlayer1 = players.get(0);
            netPlayer2 = players.get(1);
        } else {
            player1 = new Player("Player1");
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

        game.batch.draw(plateImage, plate.x, plate.y);
        game.batch.draw(orderImage, 700, 400 );

        if(isOnPlate) {
            if(!multiplayer) {
                game.batch.draw(plateImage, player1.holdingPosition.x, player1.holdingPosition.y - 10);
            } else {
                game.batch.draw(plateImage, netPlayer1.holdingPosition.x, netPlayer1.holdingPosition.y - 10);
            }
        }

        game.font.draw(game.batch, "incoming orders: " + ordersToBeServed, 0, 480);
        game.font.draw(game.batch, "time left: " + (int)secondsLeft, 0, 465);
        game.font.draw(game.batch, "Dishes served: " + dishesServed, 0, 450);
        game.font.draw(game.batch, "highscore: " + highScore, 0, 435);


        

        elapsedTime += Gdx.graphics.getDeltaTime();

        //if host create Timer else get timerdata from network
        if (isHost) {
            secondsLeft = GAMETIME - elapsedTime;
            if(multiplayer) {
                net.sendTimerData("globalTimer", String.valueOf(secondsLeft));
            }

        } else {
            Map<String, String> timerData = net.getTimerData();
            if(timerData.get("timerPurpose").equals("globalTimer")) {
                secondsLeft = Float.parseFloat(timerData.get("seconds"));
            }
        }
        Map<String, String> ingredientData = net.getIngredientData();
        if(!ingredientData.isEmpty()){
            System.out.println(ingredientData);
            if(ingredientData.get("create").equals("true")){
                float x = Float.parseFloat(ingredientData.get("hitboxX"));
                float y = Float.parseFloat(ingredientData.get("hitboxY"));
                String ownerID = ingredientData.get("ownerID");
                System.out.println("NetworIngridenet method is accessed");
                ingredients.add(new Ingredient("Broccoli", broccoliImage, new Rectangle(x, y, 32, 32),ownerID));
            }
        }

        

        // TODO do this for all orders: for(Order order : orders) ...
        oneBroccoliSoupPlease.updateTimeLeft(elapsedTime);
        anotherBroccoliPlease.updateTimeLeft(elapsedTime);


        // end round / level / game
        if (elapsedTime > GAMETIME) {
            try {
                GlobalUtilities.highscore = highScore;
                backgroundMusic.stop();
                game.setScreen(new RoundScreen(game, net));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        // Map Objects get initialized
        // TODO why does this happen in every render? Shouldn't this be in the constructor?

        for (MapObject object : objects){

            if(object.getProperties().containsKey("blocked")) {
                blockingObject = (RectangleMapObject) object;

                if(!multiplayer){
                    player1.collisionDetection(blockingObject);
                } else {
                    netPlayer1.collisionDetection(blockingObject);
                }


            } else if(object.getProperties().containsKey("Preparing Area")){

                preparingArea = (RectangleMapObject) object;
               // currentLocation = getLocation((RectangleMapObject) object, player1.getHitbox());

            } else if (object.getProperties().containsKey("Serving Area")){
                //Set servingArea to be able to acces it in batch.draw
                servingArea =(RectangleMapObject) object;
              //  currentLocation = getLocation((RectangleMapObject) object, player1.getHitbox());

            } else if (object.getProperties().containsKey("ingredient")){ // TODO let's fix this, seems wrong to look for a key like this, no?
                ingredientArea = (RectangleMapObject) object;
                if(!multiplayer){
                    createIngredient(ingredientArea, player1.getHitbox());
                } else {
                    createNetworkIngredient(ingredientArea, netPlayer1);
                }

            }
        }


        //game.batch.draw(dropImage, raindrops.x, raindrops.y);
        //game.batch.draw(broc.texture, broc.hitbox.x, broc.hitbox.y);

        //for loop through ingredient array
        for (Ingredient ingredient : ingredients){
            if(ingredient != null) {
                if(ingredient.getPickUp()) {
                    if(multiplayer){
                        if(ingredient.getOwner().equals(netPlayer1.getUserID())){
                            game.batch.draw(ingredient.getTexture(), netPlayer1.holdingPosition.x, netPlayer1.holdingPosition.y);
                        } else {
                            game.batch.draw(ingredient.getTexture(), netPlayer2.holdingPosition.x, netPlayer2.holdingPosition.y);
                        }
                        updateIngredientData(net, ingredient, "false", ingredient.getOwner());

                    } else {
                        game.batch.draw(ingredient.getTexture(), player1.holdingPosition.x, player1.holdingPosition.y);
                    }

                } else{
                    // if ingredient is put down, draw it there
                    if(ingredient.getIsServed()){
                        drawInArea(servingArea, ingredient);
                    }else if (ingredient.getIsPreparing()){
                        drawInArea(preparingArea, ingredient);
                    }
                }
                if(!multiplayer){
                    servingAreaAction(servingArea, player1.getHitbox(), ingredient);
                    preparingAreaAction(preparingArea, player1.getHitbox(), ingredient);
                } else {
                    servingAreaAction(servingArea, netPlayer1.getHitbox(), ingredient);
                    preparingAreaAction(preparingArea, netPlayer1.getHitbox(), ingredient);
                }

            }

        }



        //TODO: Fix Bug
        if(!multiplayer){
            if(Gdx.input.isKeyPressed(Keys.SPACE)){
                game.batch.draw((TextureRegion) player1.getCutAnimation().getKeyFrame(elapsedTime, true),player1.getHitbox().x, player1.getHitbox().y );
            }else {
                game.batch.draw(player1.getTexture(), player1.getHitbox().x, player1.getHitbox().y);
            }
        } else {
            if(Gdx.input.isKeyPressed(Keys.SPACE)){
                game.batch.draw((TextureRegion) netPlayer1.getCutAnimation().getKeyFrame(elapsedTime, true),netPlayer1.getHitbox().x, netPlayer1.getHitbox().y );
            }else {
                game.batch.draw(netPlayer1.getTexture(), netPlayer1.getHitbox().x, netPlayer1.getHitbox().y);
            }
        }

        
        if(multiplayer && net.joinedMatch){
            //TODO: Maybe inject UserID through Constructor...
            int i = 0;
            while(i >= 1){
                String newUserID = net.getPlayerData().get("userID");
                netPlayer2.setUserID(newUserID);
                i++;
            }
            game.batch.draw(netPlayer2.getTexture(), netPlayer2.getHitbox().x, netPlayer2.getHitbox().y);
          }

        game.batch.end();

        // draw progressbar
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Filled);
        game.shape.setColor(Color.BLUE);
        game.shape.rect(preparingArea.getRectangle().x + 12, preparingArea.getRectangle().y + 70, (float) (0.7 * progress), 20);
        game.shape.end();


        // plate logic
        if(!multiplayer){
            if (plate.overlaps(player1.getHitbox())) {
                if (Gdx.input.isKeyPressed(Keys.A)){
                    isOnPlate = true;
                }
            }
        } else {
            if (plate.overlaps(netPlayer1.getHitbox())) {
                if (Gdx.input.isKeyPressed(Keys.A)){
                    isOnPlate = true;
                }
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
            if(!multiplayer){
                player1.changeDirection(Direction.LEFT);
            }else{
                netPlayer1.changeDirection(Direction.LEFT);
                updatePlayerData(net, netPlayer1);
            }
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            desired_velocity.x = 300 * Gdx.graphics.getDeltaTime();
            if(!multiplayer){
                player1.changeDirection(Direction.RIGHT);
            }else{
                netPlayer1.changeDirection(Direction.RIGHT);
                updatePlayerData(net, netPlayer1);
            }
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)){
            desired_velocity.y = -300 * Gdx.graphics.getDeltaTime();
            if(!multiplayer){
                player1.changeDirection(Direction.DOWN);
            }else{
                netPlayer1.changeDirection(Direction.DOWN);
                updatePlayerData(net, netPlayer1);
            }
        }
        if (Gdx.input.isKeyPressed(Keys.UP)){
            desired_velocity.y = 300 * Gdx.graphics.getDeltaTime();
            if(!multiplayer){
                player1.changeDirection(Direction.UP);
            }else{
                netPlayer1.changeDirection(Direction.UP);
                updatePlayerData(net, netPlayer1);
            }
        }

        if(net.joinedMatch){
            Map<String, String> matchData =  net.getPlayerData();
            if(matchData.size() > 1){
                netPlayer2.setPosition(matchData.get("hitboxX"), matchData.get("hitboxY"));
                // netPlayer2.checkBoundaries();
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

        if(!multiplayer){
            player1.getHitbox().x += playerMovementVector.x;
            player1.getHitbox().y += playerMovementVector.y;
            player1.checkBoundaries();
        } else {
            netPlayer1.getHitbox().x += playerMovementVector.x;
            netPlayer1.getHitbox().y += playerMovementVector.y;
            netPlayer1.checkBoundaries();
        }


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
            //TODO: Create logic for quiting gane without closing app
            Gdx.app.exit();
        }

    }

    public void drawInArea(RectangleMapObject areaObject, Ingredient ingredient){
        game.batch.draw(ingredient.getTexture(), areaObject.getRectangle().x, areaObject.getRectangle().y);
    }

    public RectangleMapObject getLocation(RectangleMapObject object, Rectangle playerObject){
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
        //TODO: Maybe other way to determine which ingredient will be created?
        if (object.getProperties().containsKey("broccoli")){
            if (object.getRectangle().overlaps(playerObject)){
                if(Gdx.input.isKeyJustPressed(Keys.A)){
                    ingredients.add(new Ingredient("Broccoli", broccoliImage, new Rectangle(playerObject.x, playerObject.y, 32, 32)));
                    holdingSomething = true;
                }
            }
        }

    }

    public void createNetworkIngredient(RectangleMapObject object, NetworkPlayer player){
        //TODO: Maybe other way to determine which ingredient will be created?
        if (object.getProperties().containsKey("broccoli")){
            if (object.getRectangle().overlaps(player.getHitbox())){
                if(Gdx.input.isKeyJustPressed(Keys.A)){
                  Ingredient newIngredient = new Ingredient("Broccoli", broccoliImage, new Rectangle(player.getHitbox().x, player.getHitbox().y, 32, 32), player.getUserID());
                  ingredients.add(newIngredient);
                  System.out.println("NetworkIngredient gets created");
                  updateIngredientData(net,newIngredient, "true", newIngredient.getOwner());
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
                    Order orderProcessed;
                    if (oneBroccoliSoupPlease.secondsLeft > anotherBroccoliPlease.secondsLeft) {
                        orderProcessed = anotherBroccoliPlease;
                    } else {
                        orderProcessed = oneBroccoliSoupPlease;
                    }
                    highScore += Math.ceil(orderProcessed.secondsLeft / 20);

                    // reset state variables
                    holdingSomething = false;
                    holdingSomethingProcessed = false;
                    isOnPlate = false;

                    // in this case reset order but really, this should be its own method randomly spawning new orders
                    oneBroccoliSoupPlease.orderTime = elapsedTime;
                    anotherBroccoliPlease.orderTime = elapsedTime;
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
                    dropSound.play();
                    dropSound.loop();
                    soundLooping = true;
                }
                if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
                    choppingSound.play();
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
            } else if(soundLooping && !Gdx.input.isKeyPressed(Keys.Q)) {
                dropSound.stop();
                soundLooping = false;
            }
        }
    }

    public void updatePlayerData(Networking net, NetworkPlayer player){
        if(multiplayer){
            net.sendPlayerData(player.getTextureName(), player.getPositionStringX(), player.getPositionStringY(), player.getUserID());
        }
    }


    public void updateIngredientData(Networking net, Ingredient ingredient, String create, String ownerID){
        if(multiplayer){
            net.sendIngredientData(create,ingredient.getTexture().toString(), ingredient.getPositionStringX(), ingredient.getPositionStringY(), ownerID);
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
       backgroundMusic.play();
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
        plateImage.dispose();
        dropSound.dispose();
        backgroundMusic.dispose();

    }

}