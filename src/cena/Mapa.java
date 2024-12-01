
package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.File;

public class Mapa {
    protected int[][] mapa; // Tornar a matriz protegida para acesso em subclasses
    private boolean exibirTelaFinal = false;
    private Texture telaFinalTexture; 
    public Mapa() {
        // Inicialize o mapa padrão (Mapa 1)
        mapa = new int[][]{
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
    }
    // Retorna a largura do mapa
    public int getWidth() {
        return mapa[0].length;
    }

    // Retorna a altura do mapa
    public int getHeight() {
        return mapa.length;
    }
    public int[][] getMapa() {
        return mapa;
    }
    // Tamanho de cada bloco do mapa


 private int mapaAtual = 1; // 1 = Mapa1, 2 = Mapa2, 3 = Mapa3
     public void carregarTelaFinal(String caminhoArquivo) {
        try {
            File textureFile = new File(caminhoArquivo);
            telaFinalTexture = TextureIO.newTexture(textureFile, true);
            System.out.println("Textura da tela final carregada com sucesso.");
        } catch (IOException e) {
            System.err.println("Erro ao carregar a textura da tela final: " + e.getMessage());
        }
    }

public boolean isPositionValid(float x, float z, Bola bola) {
    int gridX = (int) Math.floor(x + mapa[0].length / 2.0f);
    int gridZ = (int) Math.floor(z + mapa.length / 2.0f);  
   
    // Verifica se a posição está fora dos limites do mapa
    if (gridX < 0 || gridX >= mapa[0].length || gridZ < 0 || gridZ >= mapa.length) {
        return false;
    }

    // Verifica colisão com blocos sólidos (1)
    if (mapa[gridZ][gridX] == 1) {
        System.out.println("Colisão detectada! Reiniciando o jogo.");
        
        // Reseta para o mapa inicial
        Mapa mapaInicial = new Mapa();
        this.mapa = mapaInicial.mapa;
        bola.resetPositionInNewMapa(mapaInicial);
        mapaAtual = 1; // Volta para o primeiro mapa

        return false; // Evita que a bola continue se movendo
    }
    
    // Verifica se a bola atingiu o ponto de transição (bloco 3)
       if (mapa[gridZ][gridX] == 3) {
        switch (mapaAtual) {
            case 1 ->                 {
                    // Se estiver no primeiro mapa
                    System.out.println("Parabéns! Você completou o primeiro mapa. Indo para o próximo...");
                    Mapa novoMapa = new Mapa2();
                    this.mapa = novoMapa.mapa;
                    bola.resetPositionInNewMapa(novoMapa);
                    mapaAtual = 2; // Atualiza para o segundo mapa
                }
            case 2 ->                 {
                    // Se estiver no segundo mapa
                    System.out.println("Parabéns! Você completou o segundo mapa. Indo para o último...");
                    Mapa novoMapa = new Mapa3();
                    this.mapa = novoMapa.mapa;
                    bola.resetPositionInNewMapa(novoMapa);
                    mapaAtual = 3; // Atualiza para o terceiro mapa
                }
            case 3 -> {
                // Terceiro mapa (fim do jogo)
                System.out.println("Parabéns! Você chegou ao fim do jogo!");
               finalizarJogo();
            }
            default -> {
            }
        }

        return false; // Transição impede que continue no mapa atual
    }

    return true; // A posição é válida para movimentação
}

    private void tocarAudio(String caminhoAudio) {
        try {
            File audioFile = new File(caminhoAudio);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            System.err.println("Erro ao reproduzir áudio: " + e.getMessage());
        }
    }
     public void finalizarJogo() {
        exibirTelaFinal = true;
        tocarAudio("C:\\Users\\guilh\\OneDrive\\Documentos\\NetBeansProjects\\A3jogo\\src\\Audio.wav"); // Toca o áudio
        System.out.println("Jogador chegou ao final! Tela final exibida e som tocando.");
    }

    
 
public boolean deveExibirTelaFinal() {
        return exibirTelaFinal;
    }

   
public void desenharTelaFinal(GL2 gl) {
    if (telaFinalTexture == null) {
        System.err.println("Erro: Textura da tela final não foi carregada.");
        return;
    }

    // Configuração de projeção 2D
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPushMatrix();
    gl.glLoadIdentity();
    gl.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0);

    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glLoadIdentity();

    // Desativa iluminação e profundidade
    gl.glDisable(GL2.GL_LIGHTING);
    gl.glDisable(GL2.GL_DEPTH_TEST);

    // Ativa o blending para suavizar a textura
    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

    // Configuração de cor para brilho máximo
    gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

    // Ativa e vincula a textura
    telaFinalTexture.enable(gl);
    telaFinalTexture.bind(gl);

    // Renderiza o quad texturizado
    gl.glBegin(GL2.GL_QUADS);
    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(0.0f, 0.0f); // Inferior esquerdo
    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(1.0f, 0.0f); // Inferior direito
    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(1.0f, 1.0f); // Superior direito
    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(0.0f, 1.0f); // Superior esquerdo
    gl.glEnd();

    // Desativa a textura e o blending
    telaFinalTexture.disable(gl);
    gl.glDisable(GL2.GL_BLEND);

    // Restaura os estados anteriores
    gl.glPopMatrix();
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPopMatrix();
}

  
private Cena cena;

public void setCena(Cena cena) {
    this.cena = cena;
    System.out.println("Cena configurada no mapa.");
}


public Mapa getProximoMapa() {
    if (this instanceof Mapa) {
        return new Mapa2(); // Transição do primeiro para o segundo mapa
    } else if (this instanceof Mapa2) {
        return new Mapa3(); // Transição do segundo para o terceiro mapa
    } else if (this instanceof Mapa3) {
        
        
            }
    return null; // Apenas como fallback
}



   public void draw(GL2 gl, Bola bola) {
    gl.glDisable(GL2.GL_LIGHTING);

    for (int i = 0; i < mapa.length; i++) {
        for (int j = 0; j < mapa[i].length; j++) {
            gl.glPushMatrix();

            // Centraliza o mapa no espaço
            gl.glTranslatef(j - mapa[0].length / 2.0f + 0.5f, 0, i - mapa.length / 2.0f + 0.5f);

            switch (mapa[i][j]) {
                case 1 -> {
                    gl.glColor3f(0.0f, 0.0f, 0.0f); // Cor da parede
                    drawTile(gl, true);
                }
                case 2, 3 -> {
                    gl.glColor3f(0.0f, 0.0f, 1.0f); // Cor de início e fim
                    drawTile(gl, false);
                }
                default -> {
                    gl.glColor3f(0.015686f, 0.992156f, 0.996078f); // Cor #04FDFE
                    drawTile(gl, false);
                }
            }

            gl.glPopMatrix();
        }
    }

    gl.glEnable(GL2.GL_LIGHTING); // Reativar iluminação para outros objetos

    // Desenhar a luz no centro do mapa
    drawLightSource(gl);
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
    // Posição da luz (meio do mapa)
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

    // Garantir que o halo esteja horizontal ao chão
    gl.glRotatef(-80.0f, 1.0f, 0.0f, 0.0f);

    gl.glColor4f(1.0f, 1.0f, 0.0f, 0.3f); // Cor amarela, semitransparente

    // Desenhar um círculo como halo
    int segments = 50; // Suavidade do círculo
    float haloSize = 1.0f; // Tamanho do halo
    gl.glBegin(GL2.GL_TRIANGLE_FAN);
    gl.glVertex3f(0.0f, 0.0f, 0.0f); // Centro do círculo
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
