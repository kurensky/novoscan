package ru.novaris.novoscan.server;

import javax.servlet.http.HttpServlet;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageServlet extends HttpServlet {
       /**
        *
        */
       private static final long serialVersionUID = 1L;
       private static final AffineTransform trans = new AffineTransform();
       private static BufferedImage thumbImage;

       public void doGet(HttpServletRequest request, HttpServletResponse response)  {
               String imagefile = new StringBuffer().append(getServletContext().getRealPath("/")).append(request.getParameter("file")).toString();
               String degree = request.getParameter("degree");
               //System.err.println(imagefile + ", " + degree);
               response.setContentType("image/png");
               try {
                       Image image = ImageIO.read(new File(imagefile));
                       int imageWidth = image.getWidth(null);
                       int imageHeight = image.getHeight(null);
                       // if width parameter is null, then just use it to scale
                       Double rotateDegree;
                       if (degree != null) {
                    	   try {
                               rotateDegree = new Double(degree);
                    	   } catch (Exception e) {
                    		   //System.out.print(e.getMessage());
                    		   rotateDegree = new Double(0);
                    	   }
                               // determine size from WIDTH and HEIGHT
                       } else {
                    	   rotateDegree = new Double(0);
                       }
                       double radians = Math.toRadians(rotateDegree);
                       double sin = Math.abs(Math.sin(radians));
                       double cos = Math.abs(Math.cos(radians));
                       int newWidth = (int)Math.floor(imageWidth * cos + imageHeight * sin);
                       int newHeight = (int)Math.floor(imageWidth * sin + imageHeight * cos);
                       if ((newWidth > 0) &&(newHeight > 0)) {
	                       thumbImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
	                       int x = (newWidth - imageWidth) / 2;
	                       int y = (newHeight - imageHeight) / 2;
	                       trans.setToRotation(radians ,x + (imageWidth / 2), y + (imageHeight / 2));
	                       trans.translate(x, y);
	                       Graphics2D graphics2D = thumbImage.createGraphics();                       
	                       graphics2D.setTransform(trans);
	                       graphics2D.drawImage(image, 0, 0, null);
	                       graphics2D.dispose();
	                       ImageIO.write(thumbImage, "png", response.getOutputStream());
                       }   
               } catch (Exception e) {
                       e.printStackTrace();

               }
       }
}
