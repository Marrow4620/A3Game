
package cena;

import com.jogamp.opengl.GL2;

public class Mapa {
    protected int[][] mapa; // Tornar a matriz protegida para acesso em subclasses

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

    private final float TILE_SIZE = 1.0f; // Tamanho de cada bloco do mapa

 private int mapaAtual = 1; // 1 = Mapa1, 2 = Mapa2, 3 = Mapa3

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
        if (mapaAtual == 1) { // Se estiver no primeiro mapa
            System.out.println("Parabéns! Você completou o primeiro mapa. Indo para o próximo...");
            Mapa novoMapa = new Mapa2();
            this.mapa = novoMapa.mapa;
            bola.resetPositionInNewMapa(novoMapa);
            mapaAtual = 2; // Atualiza para o segundo mapa
        } else if (mapaAtual == 2) { // Se estiver no segundo mapa
            System.out.println("Parabéns! Você completou o segundo mapa. Indo para o último...");
            Mapa novoMapa = new Mapa3();
            this.mapa = novoMapa.mapa;
            bola.resetPositionInNewMapa(novoMapa);
            mapaAtual = 3; // Atualiza para o terceiro mapa
        } else if (mapaAtual == 3) { // Se estiver no terceiro mapa
            System.out.println("Parabéns! Você chegou ao fim do jogo!");
            // Adicione lógica final (como encerrar o jogo ou exibir mensagem final)
        }

        return false; // Transição impede que continue no mapa atual
    }

    return true; // A posição é válida para movimentação
}



  

public Mapa getProximoMapa() {
    if (this instanceof Mapa) {
        return new Mapa2(); // Transição do primeiro para o segundo mapa
    } else if (this instanceof Mapa2) {
        return new Mapa3(); // Transição do segundo para o terceiro mapa
    } else if (this instanceof Mapa3) {
        System.out.println("Parabéns! Você completou o jogo!");
        return this; // Retorna o próprio mapa, pois não há mais transições
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

            if (mapa[i][j] == 1) {
                gl.glColor3f(0.0f, 0.0f, 0.0f); // Cor da parede
                drawTile(gl, true);
            } else if (mapa[i][j] == 2 || mapa[i][j] == 3) {
                gl.glColor3f(0.0f, 0.0f, 1.0f); // Cor de início e fim
                drawTile(gl, false);
            } else {
                gl.glColor3f(0.015686f, 0.992156f, 0.996078f); // Cor #04FDFE

                drawTile(gl, false);
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
