package group22.travelstories;

/**
 * Created by Nop on 27-Nov-16.
 */

public class ImageToUpload {
    private final String name;
    private final String compressedImage;

    public ImageToUpload(String name, String compressedImage) {
        this.name = name;
        this.compressedImage = compressedImage;
    }

    public String getName(){
        return name;
    }

    public String getCompressedImage(){
        return compressedImage;
    }
}
