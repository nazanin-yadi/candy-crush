public class Candy {

    private int xPosition;
    private int yPosition;
    private Candy up;
    private Candy down;
    private Candy left;
    private Candy right;
    private String type = "";
    private String color = "";

    public Candy(int x, int y, String type, String color){
        this.xPosition = x;
        this.yPosition = y;
        this.type = type;
        this.color = color;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setColor(String color){
        this.color = color;
    }

    public String getType(){
        return this.type;
    }

    public String getColor(){
        return this.color;
    }

    public String getValue(){
        return this.type+this.color;
    }

    public int getX(){
        return this.xPosition;
    }

    public int getY(){
        return this.yPosition;
    }

    public Candy getLeft(){
        return left;
    }

    public Candy getRight(){
        return right;
    }

    public Candy getUp(){
        return up;
    }

    public Candy getDown(){
        return down;
    }

    public void setLeft(Candy left){
        this.left = left;
    }

    public void setRight(Candy right){
        this.right = right;
    }

    public void setUp(Candy up){
        this.up = up;
    }

    public void setDown(Candy down){
        this.down = down;
    }

    public void setX(int x){
        this.xPosition = x;
    }

    public void setY(int y){
        this.yPosition = y;
    }

    public boolean equals(Candy c){
        if (c == null){
            return false;
        }
        if (c.getX() == this.xPosition && c.getY() == this.yPosition && 
        c.getColor().equals(this.color) && c.getType().equals(this.type)){
            return true;
        }else{
            return false;
        }
    }

    public boolean isNeighbor(Candy c){
        
        if ((this.left != null && this.left.equals(c)) || (this.right != null && this.right.equals(c))
        || (this.up != null && this.up.equals(c)) || (this.down != null && this.down.equals(c))){
            return true;
        }else{
            return false;
        }
    }

    public String toString(){
        return getValue()+getX()+","+getY();
    }
    
}
