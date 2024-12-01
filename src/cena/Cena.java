package cena;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;



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
    
    mapa.setCena(this); // 'this' é a instância de Cena atual
    
    // Inicializa o mapa e a bola
    mapa = new Mapa();
    mapa.setCena(this);
    bola = new Bola(mapa);
    

    // Carrega a textura da tela final
    mapa.carregarTelaFinal("C:\\Users\\guilh\\OneDrive\\Documentos\\NetBeansProjects\\A3jogo\\src\\Labirinto.jpg");

    // Inicia com a tela final desativada
}





    @Override

public void display(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    gl.glLoadIdentity();
     

    // Verifica se deve exibir a tela final
    if (mapa.deveExibirTelaFinal()) {
        mapa.desenharTelaFinal(gl);
        return;
    }

    // Configurar a luz
    float[] lightPosition = {0.0f, 5.0f, 0.0f, 1.0f}; // Luz acima da cena
    float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};  // Luz branca
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse, 0);
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);

    // Ajustar a posição da câmera
    float cameraDistance = 5.0f;          // Distância atrás da bola (eixo Z)
    float cameraHeightOffset = 10.5f;      // Deslocamento vertical da câmera (eixo Y)

    // Posição da câmera calculada relativa à bola
    float cameraX = bola.getX();          // Segue o X da bola
    float cameraY = bola.getY() + cameraHeightOffset; // Ajusta a altura
    float cameraZ = bola.getZ() + cameraDistance;     // Atrás da bola no Z

    // Posicionar a câmera usando gluLookAt
    GLU glu = new GLU();
    glu.gluLookAt(
        cameraX, cameraY, cameraZ,    // Posição da câmera
        bola.getX(), bola.getY(), bola.getZ(),  // Ponto que a câmera observa (bola)
        0.0f, 1.0f, 0.0f             // Vetor "para cima"
    );

    // Desenhar o mapa
    mapa.draw(gl, bola);

    // Desenhar a bola
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
