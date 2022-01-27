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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
    Texture boardImage;

    //other game object
    Rectangle plate = new Rectangle(200,200,32,32);

    //Map properties
    TiledMap map;
    TiledMapRenderer tiledmaprenderer;
    MapObjects objects;
    int[] mapLayerIndices;
    int i = 0;

    RectangleMapObject servingArea;
    RectangleMapObject preparingArea;
    RectangleMapObject blockingObject;
    RectangleMapObject ingredientArea;
    RectangleMapObject currentLocation;

    Map<String, String> matchData = new HashMap<>();

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
    int highScorePlayer2 = GlobalUtilities.highscorePlayer2;
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

    public float networkTimerClock;
    public float ingredientTimerCLock;
    public float playerMovementClock;
    public float plateUpdateClock;
    public float updateHighscoreClock;
    public float elapsedTime = 0;
    final float GAMETIME = 120; // one round lasts 120 seconds
    float secondsLeft;

    // order and recipe logic
    Set<Ingredient> broccoliSoupIngredients = new HashSet<Ingredient>();
    Ingredient broccoli = new Ingredient("Broccoli", broccoliImage, new Rectangle(0,0, 32, 32));
    Recipe broccoliSoup = new Recipe("broccoli soup", broccoliSoupIngredients);
    List<Order> ordersToBeServed = new LinkedList<Order>();


    // stuff to be able to use font in in-game UI
    protected FreeTypeFontGenerator fontGenerator;
    protected FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    protected BitmapFont font;
    protected Stage stage;
    protected Label timeScoreLabel;
    protected Label orderCompleteAddScoreLabel;
    protected Label.LabelStyle labelStyle;

    private int counter = 90;
    private int totalOrderCounter = 0;

    // simple vars to track an animation
    private float animationStartTime = 0;
    private Boolean animating = false;

    boolean created = false;

    public GameScreen(final Undercooked game, Networking net, Boolean multiplayer, Boolean isHost) {
        this.game = game;
        this.multiplayer = multiplayer;
        this.isHost = isHost;
        this.net = net;

        // recipe logic
        broccoliSoupIngredients.add(broccoli);


        // load the images for the droplet and the bucket, 64x64 pixels each
        broccoliImage = new Texture(Gdx.files.internal("textures/Broccoli.png"));
        plateImage = new Texture(Gdx.files.internal("textures/plate1.png"));
        counterImage = new Texture(Gdx.files.internal("counter.jpeg"));
        orderImage = new Texture(Gdx.files.internal("textures/order_broc.png"));
        boardImage = new Texture(Gdx.files.internal("textures/board.png"));

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

        // in-game UI (user interface)
        FitViewport viewport = new FitViewport( 800, 480, camera);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("AgentOrange.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 35;
        fontParameter.borderWidth = 2;
        fontParameter.borderColor = Color.FIREBRICK;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);
        Skin skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));

        labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        timeScoreLabel = new Label("Score " + highScore + " Time " + counter, labelStyle);
        orderCompleteAddScoreLabel = new Label("", labelStyle);
        timeScoreLabel.setPosition(300, 420);
        orderCompleteAddScoreLabel.setPosition(690, 300);
        stage.addActor(timeScoreLabel);
        stage.addActor(orderCompleteAddScoreLabel);


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
        int orderNumber = 0;
        for(Order order : ordersToBeServed) {
            game.batch.draw(orderImage, 30 + (orderNumber * 60), 350 );
            game.font.draw(game.batch, "" + (int)order.secondsLeft, 62 + (orderNumber * 60), 340);
            orderNumber++;
        }

        if (animating) {
            float animatedTime = elapsedTime - animationStartTime;

            orderCompleteAddScoreLabel.setPosition(690, 280 + (animatedTime *30));

            if (animatedTime > 2) {
                animating = false;
                animatedTime = 0;
                orderCompleteAddScoreLabel.setText("");
            }
        }

        plateUpdateClock += Gdx.graphics.getDeltaTime();

        if(isOnPlate) {
            if (!multiplayer) {
                game.batch.draw(plateImage, player1.holdingPosition.x, player1.holdingPosition.y - 10);
            } else {
                netPlayer1.setHasPlate(true);
                game.batch.draw(plateImage, netPlayer1.holdingPosition.x, netPlayer1.holdingPosition.y - 10);
                // send plate data every one second to the server
                if(plateUpdateClock>1){
                    updatePlateData(net,netPlayer1);
                    plateUpdateClock = 0;
                }
            }
        } else {
            if (multiplayer){
                netPlayer1.setHasPlate(false);
                if(plateUpdateClock>1){
                    updatePlateData(net,netPlayer1);
                    plateUpdateClock = 0;
                }
            }
        }

        if(!net.getPlateData().isEmpty()) {
            if (net.getPlateData().get("hasPlate").equals("true")) {
                game.batch.draw(plateImage, netPlayer2.holdingPosition.x, netPlayer2.holdingPosition.y - 10);
            }
        }

        // bigger / featured / headline font for the round / game timer
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // total game time counter
        elapsedTime += Gdx.graphics.getDeltaTime();

        // counter to restrict amount of network calls
        networkTimerClock += Gdx.graphics.getDeltaTime();
        ingredientTimerCLock += Gdx.graphics.getDeltaTime();
        playerMovementClock += Gdx.graphics.getDeltaTime();
        updateHighscoreClock += Gdx.graphics.getDeltaTime();

        // animation timer


        counter = (int)secondsLeft;

        if(!multiplayer){
            timeScoreLabel.setText("Score " + highScore + " Time " + counter);
        } else {
            timeScoreLabel.setText("Home: " + highScore + "|" + highScorePlayer2 + " : Away" + "\n" + "Time: " + counter);
            timeScoreLabel.setPosition(200, 420);


            if(updateHighscoreClock > 1){
                net.updateHighscore(String.valueOf(highScore));
            }
            if(!net.getUpdatedHighscore().isEmpty()){
                System.out.println(net.getUpdatedHighscore().get("highscore"));
                highScorePlayer2 = Integer.parseInt(net.getUpdatedHighscore().get("highscore"));
            }
        }


        //if host create Timer else get timerdata from network
        if (isHost) {
            secondsLeft = GAMETIME - elapsedTime;
            if(multiplayer && networkTimerClock > 1) {
                net.sendTimerData("globalTimer", String.valueOf(secondsLeft));
                networkTimerClock = 0;
            }

        } else {
            Map<String, String> timerData = net.getTimerData();
            if(!timerData.isEmpty()){
                if(timerData.get("timerPurpose").equals("globalTimer")) {
                    secondsLeft = Float.parseFloat(timerData.get("seconds"));
                }
            }
        }
        // for checking if the other client has any created ingredients
        Map<String, String> ingredientData = net.getIngredientData();
        Map<String, String> createCommand = net.getCreateIngredientCommand();


        // when the networklistener has data this data gets searched for a create command
        if(!ingredientData.isEmpty()){
            if(!createCommand.isEmpty() && createCommand.get("create").equals("true")){
                float x = Float.parseFloat(ingredientData.get("hitboxX"));
                float y = Float.parseFloat(ingredientData.get("hitboxY"));
                String ownerID = ingredientData.get("ownerID");
                ingredients.add(new Ingredient("Broccoli", broccoliImage, new Rectangle(x, y, 32, 32),ownerID));
                net.createIngredientCommand("false");
                net.resetCreateCommand();

            }
        }
        // IMPORTANT! To let the other Client know to stop producing more new ingredients
     /*   if(!createCommand.isEmpty() && createCommand.get("create").equals("false")){
            System.out.println("Create Command 2");
            net.createIngredientCommand("false");

        }*/

        

        // TODO do this for all orders: for(Order order : orders) ...
        // TODO fix bug, if order runs out of time the game crashes and if a dish is served without an order as well
        for (Order order: ordersToBeServed) {
            order.updateTimeLeft(elapsedTime);
            if (order.secondsLeft <= 0) {
                ordersToBeServed.remove(order);
            }
        }

        // create new orders

        if (elapsedTime / 15 > totalOrderCounter) {
            totalOrderCounter++;
            ordersToBeServed.add(new Order(broccoliSoup, 10, elapsedTime));
        }

        // end round / level / game
        if (elapsedTime > GAMETIME || counter < 0) {
            try {
                GlobalUtilities.highscore = highScore;
                backgroundMusic.stop();
                if(!multiplayer){
                    game.setScreen(new RoundScreen(game, net, false));
                } else {
                    game.setScreen(new RoundScreen(game, net, true));
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        // Map Objects get initialized
        // TODO why does this happen in every render? Shouldn't this be in the constructor?
        // I tried to put that into the constructor but then it didn't work anymore

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
                //multiplayer logic
                if(multiplayer){
                    if(ingredient.getPickUp()){
                        // compares the ingredients userID's
                        if(ingredient.getOwner().equals(netPlayer1.getUserID())){
                            game.batch.draw(ingredient.getTexture(), netPlayer1.holdingPosition.x, netPlayer1.holdingPosition.y);
                        } else {
                            game.batch.draw(ingredient.getTexture(), netPlayer2.holdingPosition.x, netPlayer2.holdingPosition.y);
                        }
                    }else{
                        // drwas ingredients in different areas
                        if(ingredient.getIsServed()){
                            drawInArea(servingArea, ingredient);
                            // after ingredient is served it is not allowed to send network data anymore, so further objects don't get confused by it
                            if(!ingredient.getBlockSending()){
                                net.sendIngredientData(ingredient);
                            }
                            ingredient.blockSending(true);

                        } else if (ingredient.getIsPreparing()){
                            drawInArea(preparingArea, ingredient);
                        }
                    }
                    // logic for behaviour in different areas
                    servingAreaAction(servingArea, netPlayer1.getHitbox(), ingredient);
                    preparingAreaAction(preparingArea, netPlayer1.getHitbox(), ingredient);

                    // update ingredientData every second
                    if(ingredientTimerCLock > 1 && ingredient.getOwner().equals(netPlayer1.getUserID()) && !ingredient.getBlockSending()) {
                        net.sendIngredientData(ingredient);
                        ingredientTimerCLock = 0;
                    }

                    // checks if ingredients are assigned to the other player and  treats them accordingly
                    // after an object is served the id is set to "", so this method doesn't get accesed anymore
                    // and prevents new ingredients that are assigned to the second player to kump directly to
                    // the serving area
                    if (ingredient.getOwner().equals(netPlayer2.getUserID()) && net.joinedMatch == true) {
                        Map<String, String> updatedIngredient = net.getIngredientData();
                        if (!updatedIngredient.isEmpty()) {
                            if (updatedIngredient.get("isPreparing").equals("true")) {
                                ingredient.putDown(preparingArea);
                            } else if (updatedIngredient.get("isPickedUp").equals("true")) {
                                ingredient.pickUp();
                            } else if (updatedIngredient.get("isServed").equals("true")) {
                                ingredient.putDown(servingArea);
                                ingredient.setOwner("");

                            }
                        }
                    }
                } else {
                    // singleplayer logic
                    if(ingredient.getPickUp()) {
                        game.batch.draw(ingredient.getTexture(), player1.holdingPosition.x, player1.holdingPosition.y);
                    } else {
                        // if ingredient is put down, draw it there
                        if(ingredient.getIsServed()){
                            drawInArea(servingArea, ingredient);
                        } else if (ingredient.getIsPreparing()){
                            drawInArea(preparingArea, ingredient);
                        }
                    }
                    servingAreaAction(servingArea, player1.getHitbox(), ingredient);
                    preparingAreaAction(preparingArea, player1.getHitbox(), ingredient);
                }
            }


        }



        //TODO: Fix Bug
        if(!multiplayer){
            if(Gdx.input.isKeyPressed(Keys.Q)){
                game.batch.draw((TextureRegion) player1.getCutAnimation().getKeyFrame(elapsedTime, true),player1.getHitbox().x, player1.getHitbox().y );
            }else {
                game.batch.draw(player1.getTexture(), player1.getHitbox().x, player1.getHitbox().y);
            }
        } else {
            if(Gdx.input.isKeyPressed(Keys.Q)){
                game.batch.draw((TextureRegion) netPlayer1.getCutAnimation().getKeyFrame(elapsedTime, true),netPlayer1.getHitbox().x, netPlayer1.getHitbox().y );
            }else {
                game.batch.draw(netPlayer1.getTexture(), netPlayer1.getHitbox().x, netPlayer1.getHitbox().y);
            }
        }

        // draw player2
        if(multiplayer && net.joinedMatch){
            //TODO: Maybe inject UserID through Constructor...
            while(i <= 1){
                String newUserID = net.getPlayerDataUserID();
                netPlayer2.setUserID(newUserID);
                if(!newUserID.isEmpty()) {i++;}
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
        // player movement
        desired_velocity.x = desired_velocity.y = 0.0f;
        if (Gdx.input.isKeyPressed(Keys.LEFT)){
            desired_velocity.x = -300 * Gdx.graphics.getDeltaTime();
            if(!multiplayer){
                player1.changeDirection(Direction.LEFT);
            }else{
                changeAndUpdatePlayerData(Direction.LEFT);
            }
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            desired_velocity.x = 300 * Gdx.graphics.getDeltaTime();
            if(!multiplayer){
                player1.changeDirection(Direction.RIGHT);
            }else{
                changeAndUpdatePlayerData(Direction.RIGHT);
            }
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)){
            desired_velocity.y = -300 * Gdx.graphics.getDeltaTime();
            if(!multiplayer){
                player1.changeDirection(Direction.DOWN);
            }else{
                changeAndUpdatePlayerData(Direction.DOWN);
            }
        }
        if (Gdx.input.isKeyPressed(Keys.UP)){
            desired_velocity.y = 300 * Gdx.graphics.getDeltaTime();
            if(!multiplayer){
                player1.changeDirection(Direction.UP);
            }else{
                changeAndUpdatePlayerData(Direction.UP);
            }
        }

        // get networkdata and change player2 position according to it
        if(net.joinedMatch){
            matchData = net.getPlayerData();

            if(matchData.size() > 1){
                netPlayer2.setNetworkPosition(matchData.get("hitboxX"), matchData.get("hitboxY"));
                netPlayer2.setNetworkDirection(matchData.get("direction"));
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
        net.resetPlayerData();
    }

    public void drawInArea(RectangleMapObject areaObject, Ingredient ingredient){
        game.batch.draw(ingredient.getTexture(), areaObject.getRectangle().x, areaObject.getRectangle().y);
    }

    public void changeAndUpdatePlayerData(Direction d){
        if(playerMovementClock > 0.05){
            netPlayer1.changeDirection(d);
            updatePlayerData(net, netPlayer1);
            playerMovementClock = 0;
        }

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
                  net.createIngredientCommand("true");
                  net.sendIngredientData(newIngredient);
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
                    Order orderProcessed = ordersToBeServed.remove(0); // dummy: remove the first order every time something is served. TODO replace this with sensible logic
                    int scoreEarned = (int)Math.ceil(orderProcessed.secondsLeft / 20);
                    highScore += scoreEarned;

                    // animate the little + score text that appears after delivering an order
                    orderCompleteAddScoreLabel.setText("+" + scoreEarned);
                    orderCompleteAddScoreLabel.setPosition(690, 280);
                    animating = true;
                    animationStartTime = elapsedTime;

                    // reset state variables
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
            net.sendPlayerData(player.getDirection(), player.getPositionStringX(), player.getPositionStringY(), player.getUserID());
        }
    }

  /*  public void updateIngredientData(Networking net, Ingredient ingredient, String ownerID){
        if(multiplayer){
            net.sendIngredientData(ingredient.getTexture().toString(), ingredient.getPositionStringX(), ingredient.getPositionStringY(), ownerID);
        }
    }*/

    public void updatePlateData(Networking net, NetworkPlayer player){
        net.sendPlateData(player.getPlate());
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
        counterImage.dispose();
        plateImage.dispose();
        orderImage.dispose();

        dropSound.dispose();
        backgroundMusic.dispose();

        fontGenerator.dispose();
        font.dispose();
        map.dispose();


    }

}