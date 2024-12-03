package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bola {
    private float x = 0.5f; // Posi��o inicial no centro de um bloco livre
    private float z = -2.0f; // Posi��o inicial
    private float y = 0.3f;
    private float radius = 0.3f; // Raio da bola
    private Texture texture; // Refer�ncia para a textura
    private GLU glu = new GLU(); // Para criar uma esfera texturizada
    private List<Texture> skins = new ArrayList<>();
    private int currentSkinIndex = 0; // �ndice da textura atual
    // Vari�veis para controlar a rota��o da bola
    private float rotationX = 0.0f; // Rota��o em torno do eixo X
    private float rotationZ = 0.0f; // Rota��o em torno do eixo Z

    // Construtor para carregar a textura
 public Bola(Mapa mapa) {
         carregarSkins(); // Carregar todas as skins
        setTexture(0); // Define a primeira textura como padr�o
        findValidStartPosition(mapa); // Localiza posi��o inicial v�lida
    }

    private void carregarSkins() {
        String[] skinPaths = {
            "C:\\Users\\guilh\\OneDrive\\Documentos\\NetBeansProjects\\A3jogo\\src\\obama3.jpg",
            "C:\\Users\\guilh\\OneDrive\\Documentos\\NetBeansProjects\\A3jogo\\src\\color.png",
            "C:\\Users\\guilh\\OneDrive\\Documentos\\NetBeansProjects\\A3jogo\\src\\teste.png"
        };

        for (String path : skinPaths) {
            try {
                skins.add(TextureIO.newTexture(new File(path), true));
            } catch (IOException e) {
                System.err.println("Erro ao carregar a textura: " + path + " - " + e.getMessage());
            }
        }
    }
 
 
private void findValidStartPosition(Mapa mapa) {
    int[][] layout = mapa.getMapa(); // Obt�m a matriz do mapa

    for (int i = 0; i < layout.length; i++) {
        for (int j = 0; j < layout[i].length; j++) {
            if (layout[i][j] == 2) { // Valor 2 indica posi��o inicial
                this.x = j - layout[0].length / 2.0f + 0.5f; // Calcula posi��o X
                this.z = i - layout.length / 2.0f + 0.5f;    // Calcula posi��o Z
                return; // Sai ap�s encontrar a posi��o
            }
        }
    }

    throw new IllegalStateException("N�o foi poss�vel encontrar uma posi��o inicial v�lida para a bola.");
}

public void resetPositionInNewMapa(Mapa novoMapa) {
    findValidStartPosition(novoMapa); // Localiza o bloco 2 no novo mapa e reposiciona a bola
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
public void reset(Mapa novoMapa) {
    findValidStartPosition(novoMapa); // Define a nova posi��o inicial baseada no mapa
}








    public void setTexture(int index) {
        if (index >= 0 && index < skins.size()) {
            currentSkinIndex = index;
            texture = skins.get(index);
            System.out.println("Skin alterada para: " + index);
        } else {
            System.err.println("�ndice de skin inv�lido: " + index);
        }
    }

    public void nextTexture() {
        setTexture((currentSkinIndex + 1) % skins.size());
    }

    public void previousTexture() {
        setTexture((currentSkinIndex - 1 + skins.size()) % skins.size());
    }

    private void drawTexturedSphere(GL2 gl) {
        if (texture != null) {
            texture.enable(gl);
            texture.bind(gl);
        }

        GLUquadric quad = glu.gluNewQuadric();
        glu.gluQuadricTexture(quad, true);
        glu.gluSphere(quad, radius, 30, 30);
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
