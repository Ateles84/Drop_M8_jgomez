package com.mygdx.jdrop;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class DropJoan implements Screen {
    final JDrop joc;

    private Texture dropImage;
    private Texture bucketImage;
    private Texture replayImage;
    private Sound dropSound;
    private Music rainMusic;
    private OrthographicCamera camera;
    private Rectangle bucket;
    private ImageButton botoGameOver;
    private Array<Rectangle> raindrops;
    private long lastDropTime;
    private BitmapFont textPuntuacio;
    private BitmapFont textGameOver;
    private BitmapFont textFPS;
    private int puntuacio;
    private boolean playing;
    private boolean musicPlaying;
    private Sound mort;
    private Rectangle hitboxGota;

    public static Texture texturaFons;
    public static Sprite spriteFons;


    public DropJoan(final JDrop joc) {
        this.joc = joc;

        //CONSTRUCTOR

        textPuntuacio = new BitmapFont();
        textGameOver = new BitmapFont();
        textFPS = new BitmapFont();

        puntuacio = 0;
        playing = true;
        musicPlaying = true;

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        replayImage = new Texture(Gdx.files.internal("replay.png"));
        texturaFons = new Texture(Gdx.files.internal(("minecraft.jpg")));
        spriteFons = new Sprite(texturaFons);

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        mort = Gdx.audio.newSound((Gdx.files.internal("death.wav")));

        // start the playback of the background music immediately
        rainMusic.setLooping(true);


        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
        bucket.y = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
        bucket.width = 64;
        bucket.height = 64;
        //Definim el hitbox de la gota
        hitboxGota = new Rectangle();
        hitboxGota.x = 800 / 2 - 64 / 2;
        hitboxGota.y = 20 + (bucket.getX() / 2); //Si la meva teoria es correcta, fem aixo per a que quedi a la Y 20 i per sobre de la meitat superior del cubell
        hitboxGota.width = 64;
        hitboxGota.height = 12; //Volem que tots els atributs siguin iguals per a que quedi per a sobre com una fina capa


        // definim el boto del replay
        botoGameOver = new ImageButton(new TextureRegionDrawable(new TextureRegion(replayImage)));
        botoGameOver.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                playing = true;
                musicPlaying = true;
                puntuacio = 0;
                botoGameOver.remove();
                System.out.println("Sordo");
            }
        });


        // create the raindrops array and spawn the first raindrop
        raindrops = new Array<Rectangle>();
        spawnRaindrop();
    }


    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }


    void pintarPuntuacio() {
        //Dibuixem la puntuacio
        joc.batch.begin();

        textPuntuacio.draw(joc.batch, "Puntuacio: " + puntuacio, 25f, 25f);
        textFPS.draw(joc.batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 75f, 75f);

        joc.batch.end();

    }


    @Override
    public void show() {

    }

    void renderFons() {     //Pintem el foncs constantment
        spriteFons.draw(joc.batch);
    }

    @Override
    public void render(float delta) {

        //RENDER

// clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        joc.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        joc.batch.begin();

        renderFons();

        joc.batch.draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle raindrop : raindrops) {
            joc.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        joc.batch.end();

        pintarPuntuacio();
        if (!musicPlaying) rainMusic.stop();
        else rainMusic.play();

        // process user input

        if (playing) {
            if (Gdx.input.isTouched()) {
                Vector3 touchPos = new Vector3();
                touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);
                bucket.x = touchPos.x - 64 / 2;
                hitboxGota.x = bucket.getX();
            }
            if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                bucket.x -= 200 * Gdx.graphics.getDeltaTime();
                hitboxGota.x = bucket.getX();
            }
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                bucket.x += 200 * Gdx.graphics.getDeltaTime();
                hitboxGota.x = bucket.getX();
            }
        }

        // make sure the bucket stays within the screen bounds
        if (bucket.x < 0) bucket.x = 0;
        if (bucket.x > 800 - 64) bucket.x = 800 - 64;

        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000 && playing) spawnRaindrop();

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the latter case we play back
        // a sound effect as well.
        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0) iter.remove();
            if (raindrop.overlaps(hitboxGota)) {
                puntuacio += 1;
                pintarPuntuacio();
                dropSound.play();
                iter.remove();
            }

            if (raindrop.getY() < 0) {
                iter.remove();
                System.out.println("RIP");
                playing = false;
                musicPlaying = false;


                if (!playing) rainMusic.stop();

                mort.play();

                joc.setScreen(new PantallaGameOver(joc, puntuacio));

            }
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        textPuntuacio.dispose();
        joc.batch.dispose();
    }
}
