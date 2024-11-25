package cena;

import com.jogamp.opengl.GL2;

public class Mapa {
    private final int[][] mapa = {
   {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    {1, 0, 0, 0, 1, 0, 0, 0, 0, 1},
    {1, 0, 1, 0, 1, 0, 1, 1, 0, 1},
    {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
    {1, 0, 1, 1, 1, 1, 0, 1, 0, 1},
    {1, 0, 0, 0, 1, 0, 0, 0, 0, 1},
    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    private final float TILE_SIZE = 1.0f; // Tamanho de cada bloco do mapa

   public boolean isPositionValid(float x, float z) {
    // Transformar as coordenadas da bola em coordenadas da grade
    int gridX = (int) Math.floor(x + mapa[0].length / 2.0f);
    int gridZ = (int) Math.floor(z + mapa.length / 2.0f);

    // Verificar se a posição está fora dos limites do mapa
    if (gridX < 0 || gridX >= mapa[0].length || gridZ < 0 || gridZ >= mapa.length) {
        return false;
    }

    // Verificar se o bloco correspondente é uma parede
    return mapa[gridZ][gridX] == 0;
}


  public void draw(GL2 gl, Bola bola) {
      gl.glDisable(GL2.GL_LIGHTING);
      for (int i = 0; i < mapa.length; i++) {
        for (int j = 0; j < mapa[i].length; j++) {
            gl.glPushMatrix();

            // Centraliza o mapa no espaço
            gl.glTranslatef(j - mapa[0].length / 2.0f + 0.5f, 0, i - mapa.length / 2.0f + 0.5f);

            if (mapa[i][j] == 1) {
                gl.glColor3f(0.5f, 0.5f, 0.5f); // Parede
                drawTile(gl);

                // Se a bola colide com o bloco, destacar em vermelho
                if (isBallCollidingWithBlock(bola, j, i)) {
                    gl.glColor3f(1.0f, 0.0f, 0.0f); // Cor de colisão
                    drawTile(gl);
                }
            } else {
                gl.glColor3f(0.0f, 1.0f, 0.0f); // Caminho
                drawTile(gl);
            }

            gl.glPopMatrix();
        }
    }
      gl.glEnable(GL2.GL_LIGHTING); // Reativar iluminação para outros objetos
}


    private void drawTile(GL2 gl) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-0.5f, 0, -0.5f);
        gl.glVertex3f(0.5f, 0, -0.5f);
        gl.glVertex3f(0.5f, 0, 0.5f);
        gl.glVertex3f(-0.5f, 0, 0.5f);
        gl.glEnd();
    }

private boolean isBallCollidingWithBlock(Bola bola, int gridX, int gridZ) {
    float bolaRadius = bola.getRadius(); // Raio da bola
    float bolaX = bola.getX();          // Posição da bola no eixo X
    float bolaZ = bola.getZ();          // Posição da bola no eixo Z

    // Calcula a posição do bloco no mundo
    float blockX = gridX - (mapa[0].length / 2.0f) + 0.5f; // Centralizar o bloco
    float blockZ = gridZ - (mapa.length / 2.0f) + 0.5f;

    // Verificar colisão com o bloco
    return bolaX + bolaRadius > blockX - 0.5f &&
           bolaX - bolaRadius < blockX + 0.5f &&
           bolaZ + bolaRadius > blockZ - 0.5f &&
           bolaZ - bolaRadius < blockZ + 0.5f;
}






}
