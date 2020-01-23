package com.mygdx.jdrop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class PantallaGameOver implements Screen {

    final JDrop joc;
    OrthographicCamera camera;
    int puntuacio;
    public static Texture texturaFons;
    public static Sprite spriteFons;

    PantallaGameOver(final JDrop joc,int puntuacio) {
        this.joc = joc;
        this.puntuacio = puntuacio;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        texturaFons = new Texture(Gdx.files.internal(("cementiri.jpg")));
        spriteFons = new Sprite(texturaFons);

    }

    @Override
    public void show() {

    }

    void renderFons() {
        spriteFons.draw(joc.batch);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        joc.batch.setProjectionMatrix(camera.combined);

        joc.batch.begin();
        renderFons();
        joc.font.draw(joc.batch, "La teva puntuació ha sigut de: "+ puntuacio, 100, 150);
        joc.font.draw(joc.batch, "Si vols tornar a jugar, apreta on sigui!", 100, 100);
        joc.batch.end();

        if (Gdx.input.isTouched()) {
            joc.setScreen(new DropJoan(joc));
            dispose();
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

    }
}
