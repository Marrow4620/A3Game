package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import java.io.File;
import java.io.IOException;

public class Bola {
    private float x = 0.5f; // Posição inicial no centro de um bloco livre
    private float z = -2.0f; // Posição inicial
    private float radius = 0.3f; // Raio da bola
    private Texture texture; // Referência para a textura
    private GLU glu = new GLU(); // Para criar uma esfera texturizada

    // Variáveis para controlar a rotação da bola
    private float rotationX = 0.0f; // Rotação em torno do eixo X
    private float rotationZ = 0.0f; // Rotação em torno do eixo Z

    // Construtor para carregar a textura
    public Bola() {
        try {
            // Carregar a textura de um arquivo
            File textureFile = new File("C:\\Users\\guilh\\OneDrive\\Documentos\\NetBeansProjects\\A3jogo\\src\\obama3.jpg"); // Atualize com o caminho correto
            texture = TextureIO.newTexture(textureFile, true);
        } catch (IOException e) {
            System.err.println("Erro ao carregar a textura: " + e.getMessage());
        }
    }

    public float getRadius() {
        return radius;
    }

  public void draw(GL2 gl) {
    gl.glPushMatrix();

    // 1. Desenhar a sombra da bola no chão
    drawShadow(gl);

    // 2. Desenhar a bola com rotação
    gl.glTranslatef(x, radius, z);
    gl.glRotatef(rotationX, 1, 0, 0); // Rotação ao redor do eixo X
    gl.glRotatef(rotationZ, 0, 0, 1); // Rotação ao redor do eixo Z
    drawTexturedSphere(gl);

    gl.glPopMatrix();
}


private void drawShadow(GL2 gl) {
    // Posição da luz
    float lightX = 0.0f, lightY = 5.0f, lightZ = 0.0f;

    // Posição da bola
    float bolaX = getX();
    float bolaZ = getZ();

    // Configurar a matriz de projeção de sombra
    float[] shadowMatrix = {
        lightY, 0, 0, 0,
        -lightX, 0, -lightZ, -1,
        0, 0, lightY, 0,
        0, 0, 0, lightY
    };

    // Aplicar a matriz de projeção
    gl.glPushMatrix();
    gl.glMultMatrixf(shadowMatrix, 0);
    gl.glTranslatef(bolaX, 0.01f, bolaZ); // Ajuste para evitar clipping com o chão

    // Habilitar PolygonOffset para evitar z-fighting
    gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
    gl.glPolygonOffset(-1.0f, -1.0f);

    // Cor da sombra
    gl.glDisable(GL2.GL_LIGHTING); // Desligar iluminação para a sombra
    gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f); // Sombra semitransparente

    // Desenhar sombra (uma esfera achatada)
    com.jogamp.opengl.util.gl2.GLUT glut = new com.jogamp.opengl.util.gl2.GLUT();
    glut.glutSolidSphere(getRadius(), 30, 30);

    // Desativar PolygonOffset
    gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
    gl.glEnable(GL2.GL_LIGHTING); // Reativar iluminação
    gl.glPopMatrix();
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

        // Verificar se a nova posição é válida no mapa
        if (mapa.isPositionValid(newX, newZ)) {
            // Atualizar a posição
            x = newX;
            z = newZ;

            // Atualizar a rotação com base no deslocamento
            float distance = (float) Math.sqrt(dx * dx + dz * dz); // Distância percorrida
            float angle = (float) Math.toDegrees(distance / radius); // Ângulo baseado no raio da bola

            // Determinar os eixos de rotação
            if (dx != 0) {
                rotationZ += dx > 0 ? angle : -angle; // Rola no eixo Z
            }
            if (dz != 0) {
                rotationX += dz > 0 ? angle : -angle; // Rola no eixo X
            }
        } else {
            System.out.println("Colisão detectada em: (" + newX + ", " + newZ + ")");
        }
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }
}
