package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class Cena implements GLEventListener {
    public float anguloX = 0;
    public float anguloY = 0;
    public float anguloZ = 0;

    private Bola bola;
    private Mapa mapa;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);

        bola = new Bola();
        mapa = new Mapa();
    }

public void display(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    gl.glLoadIdentity();

    // Configurar a luz
    float[] lightPosition = {0.0f, 5.0f, 0.0f, 1.0f}; // Luz acima da cena
    float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};  // Luz branca
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse, 0);
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);

    // Câmera segue a bola
    gl.glTranslatef(-bola.getX(), -3.0f, -12.0f);
    gl.glRotatef(10.0f, 1.0f, 0.0f, 0.0f);

    // Desenhar o mapa
    mapa.draw(gl, bola);

    // Desenhar a bola (com sombra)
    bola.draw(gl);

    gl.glFlush();
}



    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        if (height <= 0) height = 1;
        float aspect = (float) width / height;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        com.jogamp.opengl.glu.GLU glu = new com.jogamp.opengl.glu.GLU();
        glu.gluPerspective(45.0, aspect, 1.0, 20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    public Bola getBola() {
    return bola;
}
    public Cena() {
    mapa = new Mapa();
}

    public Mapa getMapa() {
    return mapa;
}



    @Override
    public void dispose(GLAutoDrawable drawable) {}
}
