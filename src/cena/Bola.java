package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import java.io.File;
import java.io.IOException;

public class Bola {
    private float x = 0.5f; // Posi��o inicial no centro de um bloco livre
    private float z = -2.0f; // Posi��o inicial
    private float y = 0.3f;
    private float radius = 0.3f; // Raio da bola
    private Texture texture; // Refer�ncia para a textura
    private GLU glu = new GLU(); // Para criar uma esfera texturizada

    // Vari�veis para controlar a rota��o da bola
    private float rotationX = 0.0f; // Rota��o em torno do eixo X
    private float rotationZ = 0.0f; // Rota��o em torno do eixo Z

    // Construtor para carregar a textura
    public Bola(Mapa mapa) {
    try {
        // Carregar a textura de um arquivo
        File textureFile = new File("C:\\Users\\guilh\\OneDrive\\Documentos\\NetBeansProjects\\A3jogo\\src\\obama3.jpg");
        texture = TextureIO.newTexture(textureFile, true);
    } catch (IOException e) {
        System.err.println("Erro ao carregar a textura: " + e.getMessage());
    }

    // Procurar uma posi��o v�lida no mapa para a bola
    findValidStartPosition(mapa);
}
    private void findValidStartPosition(Mapa mapa) {
    int[][] layout = mapa.getMapa(); // Obter a matriz do mapa
    for (int i = 0; i < layout.length; i++) {
        for (int j = 0; j < layout[i].length; j++) {
            if (layout[i][j] == 2) { // Considera '2' como a posi��o inicial
                this.x = j - layout[0].length / 2.0f + 0.5f;
                this.z = i - layout.length / 2.0f + 0.5f;
                return;
            }
        }
    }
    throw new IllegalStateException("N�o foi poss�vel encontrar uma posi��o inicial v�lida para a bola.");
}



    public float getRadius() {
        return radius;
    }

  public void draw(GL2 gl) {
        gl.glPushMatrix();

        // 1. Desenhar a sombra da bola no ch�o
        drawShadow(gl);

        // 2. Desenhar a bola com rota��o
        gl.glTranslatef(x, y, z);  // Usar a vari�vel 'y' para a altura
        gl.glRotatef(rotationX, 1, 0, 0); // Rota��o ao redor do eixo X
        gl.glRotatef(rotationZ, 0, 0, 1); // Rota��o ao redor do eixo Z
        drawTexturedSphere(gl);

        gl.glPopMatrix();
    }
  


private void drawShadow(GL2 gl) {
    // Posi��o da luz
    float lightX = 0.0f, lightY = 5.0f, lightZ = 0.0f;

    // Posi��o da bola
    float bolaX = getX();
    float bolaZ = getZ();

    // Configurar a matriz de proje��o de sombra
    float[] shadowMatrix = {
        lightY, 0, 0, 0,
        -lightX, 0, -lightZ, -1,
        0, 0, lightY, 0,
        0, 0, 0, lightY
    };

    // Aplicar a matriz de proje��o
    gl.glPushMatrix();
    gl.glMultMatrixf(shadowMatrix, 0);
    gl.glTranslatef(bolaX, 0.01f, bolaZ); // Ajuste para evitar clipping com o ch�o

    // Habilitar PolygonOffset para evitar z-fighting
    gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
    gl.glPolygonOffset(-1.0f, -1.0f);

    // Cor da sombra
    gl.glDisable(GL2.GL_LIGHTING); // Desligar ilumina��o para a sombra
    gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f); // Sombra semitransparente

    // Desenhar sombra (uma esfera achatada)
    com.jogamp.opengl.util.gl2.GLUT glut = new com.jogamp.opengl.util.gl2.GLUT();
    glut.glutSolidSphere(getRadius(), 30, 30);

    // Desativar PolygonOffset
    gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
    gl.glEnable(GL2.GL_LIGHTING); // Reativar ilumina��o
    gl.glPopMatrix();
}
    public void reset(Mapa mapa) {
    // Reposicionar a bola na posi��o inicial
    findValidStartPosition(mapa); // Chama o mesmo m�todo para encontrar a posi��o inicial

    // Resetar a rota��o
    rotationX = 0.0f;
    rotationZ = 0.0f;
}





    private void drawTexturedSphere(GL2 gl) {
        if (texture != null) {
            texture.enable(gl);
            texture.bind(gl);
        }

        GLUquadric quad = glu.gluNewQuadric();
        glu.gluQuadricTexture(quad, true); // Ativa texturas na esfera
        glu.gluSphere(quad, radius, 30, 30); // Desenha a esfera texturizada
        glu.gluDeleteQuadric(quad);

        if (texture != null) {
            texture.disable(gl);
        }
    }

    public void move(float dx, float dz, Mapa mapa) {
    float newX = x + dx;
    float newZ = z + dz;

    // Verificar se a nova posi��o � v�lida no mapa
    if (mapa.isPositionValid(newX, newZ, this)) {
        x = newX;
        z = newZ;

        float distance = (float) Math.sqrt(dx * dx + dz * dz);
        float angle = (float) Math.toDegrees(distance / radius);

        if (dx != 0) {
            rotationZ += dx > 0 ? angle : -angle;
        }
        if (dz != 0) {
            rotationX += dz > 0 ? angle : -angle;
        }
    } else {
        System.out.println("Colis�o detectada! Reiniciando o jogo.");
         System.out.println("Colis�o detectada em: (" + newX + ", " + newZ + ")");
    }
}
     public float getY() {
        return y;
    }
    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }
}
