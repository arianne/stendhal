// (Groovy script) <!-- Hide code from Dreamweaver
import java.awt.image.BufferedImage; 
import java.io.IOException; 
import javax.imageio.ImageIO; 
response.set("Content-Type", "image/png");
response.set("Cache-Control", "public");
response.setDate("Expires",System.currentTimeMillis()+900000); // 15 minutes
//cut a frame out of creature / npc sprite sets
cut = true;
BufferedImage myImage = null;
String ref = "data/sprites/npc/angelnpc.png"
res = "data/sprites/" + request.getParameter("sprite");
URL url = this.getClass().getClassLoader().getResource(res);
if(url==null) {
  url = this.getClass().getClassLoader().getResource("data/sprites/failsafe.png");
  cut = false;
}
myImage = ImageIO.read(url);
if(cut) {
  int width  = myImage.getWidth() / 3
  int height = myImage.getHeight() / 4
  int destwidth = (width/2).intValue();
  int destheight = (height/2).intValue();
  BufferedImage image = new BufferedImage(destwidth,destheight,BufferedImage.TYPE_INT_ARGB);
  image.getGraphics().drawImage(myImage,0,0,destwidth-1,destheight-1, width , 2 * height, 2 * width -1, 3 * height -1 ,null);
  ImageIO.write(image,"png",out);
} else {
  ImageIO.write(myImage,"png",out);
}
//-->