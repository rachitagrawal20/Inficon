package infiniteconnections.inficon;

/**
 * Created by Rachit Agrawal on 2/13/2018.
 */

public class Signupdetails {
    private String name,facebookURL,contactno,codechef,linkedin,imageURL,email;
    public Signupdetails(){

    }
    public void setEmail(String name){
        this.email=name;
    }
    public void setImageURL(String name){
        this.imageURL=name;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setFacebookURL(String name){
        this.facebookURL=name;
    }
    public void setContactno(String name){
        this.contactno=name;
    }
    public void setCodechef(String name){
        this.codechef=name;
    }
    public void setLinkedin(String name){
        this.linkedin=name;
    }
    public String getName(){
        return name;
    }
    public String getFacebookURL(){
        return facebookURL;
    }
    public String getContactno(){
        return contactno;
    }
    public String getCodechef(){
        return codechef;
    }
    public String getLinkedin(){
        return linkedin;
    }
    public String getImageURL(){
        return imageURL;
    }
    public String getEmail(){
        return email;
    }
}
