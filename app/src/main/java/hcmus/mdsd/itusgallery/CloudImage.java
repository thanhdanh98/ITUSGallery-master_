package hcmus.mdsd.itusgallery;

public class CloudImage {
    String NameImage;
    String LinkImage;

    public CloudImage(){

    }

    CloudImage(String nameImage, String linkImage){
        NameImage=nameImage;
        LinkImage=linkImage;
    }

    String getUrl(){
        return LinkImage;
    }

}
