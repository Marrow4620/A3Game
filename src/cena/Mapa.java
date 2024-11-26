package cena;

import com.jogamp.opengl.GL2;

public class Mapa {
   private final int[][] mapa = {
    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    {1, 2, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1},
    {1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1},
    {1, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1},
    {1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 1},
    {1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1},
    {1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1},
    {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1}
};

    // Retorna a largura do mapa
    public int getWidth() {
        return mapa[0].length;
    }

    // Retorna a altura do mapa
    public int getHeight() {
        return mapa.length;
    }

    private final float TILE_SIZE = 1.0f; // Tamanho de cada bloco do mapa

 public boolean isPositionValid(float x, float z, Bola bola) {
    int gridX = (int) Math.floor(x + mapa[0].length / 2.0f);
    int gridZ = (int) Math.floor(z + mapa.length / 2.0f);

    if (gridX < 0 || gridX >= mapa[0].length || gridZ < 0 || gridZ >= mapa.length) {
        return false;
    }

    if (mapa[gridZ][gridX] == 1) {
        bola.reset(this); // Passa o mapa para reposicionar a bola na posi��o inicial
        return false;
    }

    if (mapa[gridZ][gridX] == 3) {
        System.out.println("Parab�ns! Voc� completou o jogo.");
        bola.reset(this); // Reinicia a bola quando o jogador completa o jogo
    }

    return true;
}




   public void draw(GL2 gl, Bola bola) {
    gl.glDisable(GL2.GL_LIGHTING);

    for (int i = 0; i < mapa.length; i++) {
        for (int j = 0; j < mapa[i].length; j++) {
            gl.glPushMatrix();

            // Centraliza o mapa no espa�o
            gl.glTranslatef(j - mapa[0].length / 2.0f + 0.5f, 0, i - mapa.length / 2.0f + 0.5f);

            if (mapa[i][j] == 1) {
                gl.glColor3f(0.5f, 0.5f, 0.5f); // Cor da parede
                drawTile(gl, true);
            } else if (mapa[i][j] == 2 || mapa[i][j] == 3) {
                gl.glColor3f(0.0f, 0.0f, 1.0f); // Cor de in�cio e fim
                drawTile(gl, false);
            } else {
                gl.glColor3f(0.0f, 1.0f, 0.0f); // Caminho
                drawTile(gl, false);
            }

            gl.glPopMatrix();
        }
    }

    gl.glEnable(GL2.GL_LIGHTING); // Reativar ilumina��o para outros objetos

    // Desenhar a luz no centro do mapa
    drawLightSource(gl);
}
   public int[][] getMapa() {
    return mapa;
}



   private void drawTile(GL2 gl, boolean isWall) {
    gl.glBegin(GL2.GL_QUADS);

    // Piso
    gl.glVertex3f(-0.5f, 0, -0.5f);
    gl.glVertex3f(0.5f, 0, -0.5f);
    gl.glVertex3f(0.5f, 0, 0.5f);
    gl.glVertex3f(-0.5f, 0, 0.5f);

    // Se for parede, desenha as faces verticais
    if (isWall) {
        float height = 1.0f; // Altura da parede
        gl.glVertex3f(-0.5f, 0, -0.5f);
        gl.glVertex3f(0.5f, 0, -0.5f);
        gl.glVertex3f(0.5f, height, -0.5f);
        gl.glVertex3f(-0.5f, height, -0.5f);

        gl.glVertex3f(0.5f, 0, -0.5f);
        gl.glVertex3f(0.5f, 0, 0.5f);
        gl.glVertex3f(0.5f, height, 0.5f);
        gl.glVertex3f(0.5f, height, -0.5f);

        gl.glVertex3f(0.5f, 0, 0.5f);
        gl.glVertex3f(-0.5f, 0, 0.5f);
        gl.glVertex3f(-0.5f, height, 0.5f);
        gl.glVertex3f(0.5f, height, 0.5f);

        gl.glVertex3f(-0.5f, 0, 0.5f);
        gl.glVertex3f(-0.5f, 0, -0.5f);
        gl.glVertex3f(-0.5f, height, -0.5f);
        gl.glVertex3f(-0.5f, height, 0.5f);
    }

    gl.glEnd();
}


   private void drawLightSource(GL2 gl) {
    // Posi��o da luz (meio do mapa)
    float lightX = 0.0f;
    float lightY = 5.0f; // Altura da luz
    float lightZ = 0.0f;

    // Desenhar o halo da luz
    gl.glPushMatrix();
    gl.glTranslatef(lightX, lightY, lightZ);

    // Renderizar o brilho (halo)
    gl.glDisable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

    // Garantir que o halo esteja horizontal ao ch�o
    gl.glRotatef(-80.0f, 1.0f, 0.0f, 0.0f);

    gl.glColor4f(1.0f, 1.0f, 0.0f, 0.3f); // Cor amarela, semitransparente

    // Desenhar um c�rculo como halo
    int segments = 50; // Suavidade do c�rculo
    float haloSize = 1.0f; // Tamanho do halo
    gl.glBegin(GL2.GL_TRIANGLE_FAN);
    gl.glVertex3f(0.0f, 0.0f, 0.0f); // Centro do c�rculo
    for (int i = 0; i <= segments; i++) {
        double angle = 2 * Math.PI * i / segments;
        float x = (float) Math.cos(angle) * haloSize;
        float z = (float) Math.sin(angle) * haloSize;
        gl.glVertex3f(x, 0.0f, z);
    }
    gl.glEnd();

    gl.glDisable(GL2.GL_BLEND);

    // Desenhar a esfera da luz
    gl.glColor3f(1.0f, 1.0f, 0.0f); // Cor amarela brilhante
    com.jogamp.opengl.util.gl2.GLUT glut = new com.jogamp.opengl.util.gl2.GLUT();
    glut.glutSolidSphere(0.4f, 30, 30); // Esfera da luz

    gl.glEnable(GL2.GL_LIGHTING);

    gl.glPopMatrix();
}


    
}
