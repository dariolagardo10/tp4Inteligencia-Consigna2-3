/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabajopractico2inteligencia;

/**
 *
 * @author Dario
 */
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class DetectorCirculos extends JFrame {
    private static final int ANCHO = 500;
    private static final int ALTO = 500;
    private static final int RADIO_MIN = 50;
    private static final int RADIO_MAX = 70;
    
    public DetectorCirculos() {
        setTitle("Detector de Círculos - TP4");
        setSize(ANCHO * 2, ALTO);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarDemostracion(g);
            }
        };
        add(panel);
    }
    
    private void dibujarDemostracion(Graphics g) {
        // Imagen original con un círculo
        BufferedImage imagenOriginal = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagenOriginal.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, ANCHO, ALTO);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(ANCHO/4, ALTO/4, ANCHO/2, ALTO/2);
        
        // Aplicar Hough
        int[][][] acumulador = aplicarHough(imagenOriginal);
        
        // Dibujar resultados
        g.drawImage(imagenOriginal, 0, 0, null);
        dibujarAcumulador(g, acumulador, ANCHO, 0);
    }
    
    private int[][][] aplicarHough(BufferedImage imagen) {
        int rangoRadio = RADIO_MAX - RADIO_MIN + 1;
        int[][][] acumulador = new int[ALTO][ANCHO][rangoRadio];
        
        // Por cada punto de borde
        for(int y = 0; y < ALTO; y++) {
            for(int x = 0; x < ANCHO; x++) {
                if((imagen.getRGB(x, y) & 0xFF) < 128) {  // Punto negro
                    // Para cada radio posible
                    for(int r = 0; r < rangoRadio; r++) {
                        int radio = r + RADIO_MIN;
                        // Para cada ángulo
                        for(int theta = 0; theta < 360; theta++) {
                            double rad = Math.toRadians(theta);
                            int a = (int)(x - radio * Math.cos(rad));
                            int b = (int)(y - radio * Math.sin(rad));
                            
                            if(a >= 0 && a < ANCHO && b >= 0 && b < ALTO) {
                                acumulador[b][a][r]++;
                            }
                        }
                    }
                }
            }
        }
        return acumulador;
    }
    
    private void dibujarAcumulador(Graphics g, int[][][] acumulador, int x, int y) {
        // Proyectar acumulador 3D a 2D
        int[][] proyeccion = new int[ALTO][ANCHO];
        for(int i = 0; i < ALTO; i++) {
            for(int j = 0; j < ANCHO; j++) {
                for(int r = 0; r < acumulador[0][0].length; r++) {
                    proyeccion[i][j] += acumulador[i][j][r];
                }
            }
        }
        
        // Encontrar máximo valor
        int maxVotos = 0;
        for(int[] fila : proyeccion) {
            for(int valor : fila) {
                maxVotos = Math.max(maxVotos, valor);
            }
        }
        
        // Dibujar proyección normalizada
        BufferedImage imagenProyeccion = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < ALTO; i++) {
            for(int j = 0; j < ANCHO; j++) {
                int intensidad = (int)(255.0 * proyeccion[i][j] / maxVotos);
                Color color = new Color(intensidad, intensidad, intensidad);
                imagenProyeccion.setRGB(j, i, color.getRGB());
            }
        }
        g.drawImage(imagenProyeccion, x, y, null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DetectorCirculos().setVisible(true);
        });
    }
}