// (Groovy script) <!-- Hide code from Dreamweaver
import java.awt.image.BufferedImage; 
import java.io.IOException; 
import javax.imageio.ImageIO; 

class OutfitImage extends Object {
  public static BufferedImage getImage(String name, ClassLoader loader) {
    URL url = loader.getResource("data/sprites/outfit/" + name + ".png");
    if(url!=null) {
      return(ImageIO.read(url));
    }
    return(null);
  }
}

response.set("Content-Type", "image/png");
response.set("Cache-Control", "public");
response.setDate("Expires",System.currentTimeMillis()+900000); // 15 minutes

BufferedImage myImage = null;
outfitString = request.getParameter("outfit");
int outfitNumber = new Integer(outfitString);

res = "player_base_" + (outfitNumber % 100);
imageList = ["dress_", "head_", "hair_"];

myImage = OutfitImage.getImage(res, this.getClass().getClassLoader());
if(myImage != null) {
  int width  = myImage.getWidth() / 3
  int height = myImage.getHeight() / 4
  int destwidth = (width/2).intValue();
  int destheight = (height/2).intValue();
  BufferedImage image = new BufferedImage(destwidth,destheight,BufferedImage.TYPE_INT_ARGB);
  imageCounter = 0;
  while(myImage != null ) {
    image.getGraphics().drawImage(myImage,0,0,destwidth - 1, destheight - 1, width , 2 * height, 2 * width -1, 3 * height -1 ,null);
    if(imageCounter >= imageList.size()) {
      break;
    }
    outfitNumber = (outfitNumber / 100).intValue();
    res = imageList[imageCounter++] +  (outfitNumber % 100);
    myImage = OutfitImage.getImage(res, this.getClass().getClassLoader()); 
  }
  ImageIO.write(image,"png",out);
} else {
  ImageIO.write(ImageIO.read(this.getClass().getClassLoader().getResource("data/sprites/failsafe.png")),"png",out);
}
//-->