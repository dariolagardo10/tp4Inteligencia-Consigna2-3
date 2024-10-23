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

public class DetectorRectas extends JFrame {
    private static final int ANCHO = 500;
    private static final int ALTO = 500;
    
    public DetectorRectas() {
        setTitle("Detector de Rectas - TP4");
        setSize(ANCHO * 2, ALTO);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Crear panel para visualización
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
        // Imagen original con una línea
        BufferedImage imagenOriginal = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagenOriginal.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, ANCHO, ALTO);
        g2d.setColor(Color.BLACK);
        g2d.drawLine(100, 100, 400, 400);
        
        // Aplicar Hough
        int[][] acumulador = aplicarHough(imagenOriginal);
        
        // Dibujar resultados
        g.drawImage(imagenOriginal, 0, 0, null);
        dibujarAcumulador(g, acumulador, ANCHO, 0);
    }
    
    private int[][] aplicarHough(BufferedImage imagen) {
        int pasoTheta = 180;
        int pasoRho = 200;
        int[][] acumulador = new int[pasoTheta][pasoRho];
        
        // Por cada punto de la imagen
        for(int y = 0; y < ALTO; y++) {
            for(int x = 0; x < ANCHO; x++) {
                if((imagen.getRGB(x, y) & 0xFF) < 128) {  // Punto negro
                    // Para cada ángulo θ
                    for(int t = 0; t < pasoTheta; t++) {
                        double theta = Math.PI * t / pasoTheta;
                        double rho = x * Math.cos(theta) + y * Math.sin(theta);
                        int r = (int)((rho + ANCHO) * pasoRho / (2 * ANCHO));
                        if(r >= 0 && r < pasoRho) {
                            acumulador[t][r]++;
                        }
                    }
                }
            }
        }
        return acumulador;
    }
    
    private void dibujarAcumulador(Graphics g, int[][] acumulador, int x, int y) {
        // Encontrar máximo valor
        int maxVotos = 0;
        for(int[] fila : acumulador) {
            for(int valor : fila) {
                maxVotos = Math.max(maxVotos, valor);
            }
        }
        
        // Dibujar acumulador normalizado
        BufferedImage imagenAcumulador = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < acumulador.length; i++) {
            for(int j = 0; j < acumulador[0].length; j++) {
                int intensidad = (int)(255.0 * acumulador[i][j] / maxVotos);
                Color color = new Color(intensidad, intensidad, intensidad);
                imagenAcumulador.setRGB(j * ANCHO / acumulador[0].length, 
                                      i * ALTO / acumulador.length, 
                                      color.getRGB());
            }
        }
        g.drawImage(imagenAcumulador, x, y, null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DetectorRectas().setVisible(true);
        });
    }
}
