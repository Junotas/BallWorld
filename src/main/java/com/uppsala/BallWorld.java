package com.uppsala;
// Eftersom koden är avsedd att läsas av nybörjare förklarar
// kommentarerna i koden även olika begrepp i programspråket.

// Denna klass implementerar ett enkelt program i Java. En boll
// studsar inom en given rektangel. Din uppgift blir att utöka detta
// program med möjlighet till fler bollar, möjlighet att ändra
// storleken på rektangeln etc.
//
// Följande rader är till för att Java skall kunna förstå och tolka
// användandet av standardklasser som Color och JFrame.
//
// Alla klasser och underpaket i paketen java.awt, java.awt.event samt
// javax.swing kan nu refereras direkt.

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Ball. Denna klass beskriver konceptet boll. En boll har en position
// (givet av en koordinat (x,y)), en hastighet (givet av en
// differential (dx,dy)) och en färg.

class Ball {
    // Standardkonstanter (for alla instanser av Ball)
    static int        defaultDiameter  = 10;
    static Color      defaultColor     = Color.yellow;

    // Position
    private int x, y;

    // Hastighet och riktning
    private int dx, dy;

    // Diameter (storlek)
    private int diameter;

    // Färg
    private Color color;

    // Begränsande rektangulära område inom vilket bollen studsar
    private Rectangle box;

    // Ny boolean för att hantera om bollen växer eller krymper
    private boolean growing = true;

    // Gränser för storleken på bollen
    private static final int MIN_DIAMETER = 10;
    private static final int MAX_DIAMETER = 60;

    // Konstruktion av nya bollar kräver position och riktning
    public Ball( int x0, int y0, int dx0, int dy0 ) {
        x = x0;
        y = y0;
        dx = dx0;
        dy = dy0;

        color = defaultColor;
        diameter = defaultDiameter;
    }

    // Sätt ny färg
    public void setColor( Color c ) {
        color = c;
    }

    // Sätt nytt begränsande rektangulärt område
    public void setBoundingBox( Rectangle r ) {
        box = r;
    }

    // Rita ut en boll på givet grafiskt område 
    public void paint( Graphics g ) {
        // Byt till bollens färg
        g.setColor( color );

        // Bollen representeras som en fylld cirkel, dvs en ellips (oval)
        // med lika höjd och bredd 
        g.fillOval( x, y, diameter, diameter );
    }

    // Begränsa bollen inom det rektangulära området. Uppdatera hastigheten
    // om det behövs.
    void constrain() {
        // Ge absoluta koordinater för det rektangulära området
        int x0 = box.x;
        int y0 = box.y;
        int x1 = x0 + box.width - diameter;
        int y1 = y0 + box.height - diameter;

        // Ändra hastighet och riktning om bollen är utanför det
        // rektangulära området
        if (x < x0) {
            dx = Math.abs(dx);
        }
        if (x > x1) {
            dx = -Math.abs(dx);
        }
        if (y < y0) {
            dy = Math.abs(dy);
        }
        if (y > y1) {
            dy = -Math.abs(dy);
        }
    }

    // Flytta bollen med aktuell riktning och hastighet ett steg 
    public void action() {
        x = x + dx;
        y = y + dy;

        // Puls-logik för att växa och krympa
        if (growing) {
            diameter++;
            if (diameter >= MAX_DIAMETER) {
                growing = false; // Börja krympa
            }
        } else {
            diameter--;
            if (diameter <= MIN_DIAMETER) {
                growing = true; // Börja växa
            }
        }

        constrain();
    }

    // Metod för uppdatering av bollens storlek
    public void setDiameter (int newDiameter) {
        diameter = newDiameter;
    }

}

// Klassen BallPanel definierar en rityta där bollarna ritas upp. Den
// ärver klassen JPanel och implementerar ActionListener. Genom att
// implementera ActionListener kan man låta en Timer med jämna
// mellanrum ge ett 'tick' då uppdatering av panelen ska göras.

class BallPanel extends JPanel implements ActionListener {
    // Bredd och höjd  
    private int width, height;

    // Array för flera bollar
    private final Ball[] balls;

    // Timer. Skickar en signal var 50e millisekund till panelen som
    // skickas med som ActionListener.

    // Initiera attributen
    public BallPanel(int width, int height) {
        // Ta reda på bredd och höjd för ritytan
        this.width = width;
        this.height = height;

        // Skapa flera bollar
        balls = new Ball[2];
        balls[0] = new Ball(width / 10, height / 5, 5, 5);
        balls[1] = new Ball(width / 2, height / 2, -3, 4);

        // Sätt färg och storlek för bollarna
        balls[0].setColor(Color.WHITE);
        balls[0].setDiameter(20);

        balls[1].setColor(Color.RED);
        balls[1].setDiameter(30);

        // Sätt varje bolls rektangulära begränsande område (bounding box)
        for (Ball ball : balls) {
            ball.setBoundingBox(new Rectangle(0, 0, width, height));
        }

        // Starta timern.
        Timer timer = new Timer(50, this);
        timer.start();
    }

    // Uppdatera (anropas vid omritning, repaint())
    public void paintComponent( Graphics g ) {
        // Rensa hela ritytan (med svart färg)

        g.setColor( Color.black );
        g.fillRect( 0, 0, width, height );

        // Rita ut alla bollar (på svart bakgrund)
        for (Ball ball : balls) {
            ball.paint(g);
        }
    }

    // När vi får en signal från timern... 
    public void actionPerformed(ActionEvent e) {
        if (width != getWidth() || height != getHeight())
            wasResized(getWidth(), getHeight());

        // Uppdatera alla bollar
        for (Ball ball : balls) {
            ball.action();
        }
        repaint(); // Gör automatiskt ett anrop till paintComponent()
    }

    // Anropas om fönstret ändrar storlek
    public void wasResized(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
        for (Ball ball : balls) {
            ball.setBoundingBox(new Rectangle(0, 0, width, height));
        }
    }
}

// Denna klass definierar det fönster som skapas av programmet. Ett
// fönster (JFrame) skapas där en instans av BallPanel (ritytan)
// placeras.
public class BallWorld extends JFrame {

    // Skapa en panel
    public BallWorld() {
        // Lägg till bollpanelen i mitten på ramen.
        Container c = getContentPane();

        BallPanel panel = new BallPanel(180, 180);
        c.add(panel, BorderLayout.CENTER);

        setSize(200, 200);     // Ramens storlek.
        setLocation(100, 100); // Så att ramen inte hamnar i ett hörn.
        setVisible(true);      // Gör så att ramen syns.

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // Denna metod startas av Javas virtuella maskin vid anropet java
    // BallWorld
    public static void main(String[] argv) {
        // Detta kommando ger bättre animering i en del
        // OS. Avkommentera om bollen rör sig ryckigt.
        // System.setProperty("sun.java2d.opengl", "true");

        BallWorld world = new BallWorld();
    }
}


