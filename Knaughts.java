import java.awt.*;
import java.awt.event.*;

import java.util.Random;

public class Knaughts extends KeyAdapter {
    static final int WINDOW_WIDTH = 300;
    static final int WINDOW_HEIGHT = 300;

    private Game_Window window;

    public char[][] board;
    Random r;
    private Game_Character player;
    //private Game_Character enemy;
    Projectile[] projectiles;
    Weapon [] weapons;

    public Knaughts(){
        r = new Random();
        board = new char[10][16];
        player = new Game_Character();
        projectiles = new Projectile[10];
        weapons = new Weapon[10];
        
        

        for(int i = 0; i < board.length; i++){
            if(i > 0 && i < 9){
                for(int j = 0; j < board[i].length; j++){
                    if(j == 0 || j == 15){
                        board[i][j] = '*';
                    }else{
                        board[i][j] = ' ';
                    }
                }
            }else{
                for(int j = 0; j < board[i].length; j++){
                    board[i][j] = '*';
                }
            }
        }

        place_weapon();

        board[player.current.y][player.current.x] = player.avatar;

        this.window = new Game_Window(this, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.addKeyListener(this);
    }

    public void keyPressed(KeyEvent e){
        int move = e.getKeyCode();
        switch(move){
            case 32:
                
                if(player.weapon_inventory[0] != null){
                    System.out.println("space");
                    player.use_weapon();
                }
                break;
            case 38:
                if(player.current.y > 1){
                    board[player.last.y][player.last.x] = ' ';
                    player.current.y -= 1;
                    board[player.last.y][player.last.x] = player.avatar;
                }
                
                break;
            case 37:
                if(player.current.x > 1){
                    board[player.last.y][player.last.x] = ' ';
                    player.current.x -= 1;
                    board[player.last.y][player.last.x] = player.avatar;
                }
                break;
            case 40:
                if(player.current.y < board.length - 2){
                    board[player.last.y][player.last.x] = ' ';
                    player.current.y += 1;
                    board[player.last.y][player.last.x] = player.avatar;
                }
                break;
            case 39:
                if(player.current.x < board[0].length - 2){
                    board[player.last.y][player.last.x] = ' ';
                    player.current.x += 1;
                    board[player.last.y][player.last.x] = player.avatar;
                }
                break;
            case 81:
                System.exit(0);
                break;
            default:
                
        }
    }

    public char[][] get_board(){
        return board;
    }

    public Projectile[] get_projectiles(){
        return projectiles;
    }

    public void play(){
        long start_time = System.currentTimeMillis();
        long elapsed = 0;
        long end_time = 0;

        while(player.is_alive){
            
            if(player.current.x == 1 && player.current.y == 1){
                player.is_alive = false;
            }
            for(int i = 0; i < projectiles.length; i++){
                if(projectiles[i] != null){
                    
                    
                    if(projectiles[i].current.y > 0){
                        board[projectiles[i].current.y][projectiles[i].current.x] = ' ';
                        projectiles[i].current.y--;
                        if(projectiles[i].current.y == 0){
                            projectiles[i] = null;
                            continue;
                        }
                        board[projectiles[i].current.y][projectiles[i].current.x] = projectiles[i].sprite;
                    } 
                    
                }
            }
            if(player.current.x == weapons[0].current.x && player.current.y == weapons[0].current.y && weapons[0].is_available == true){
                
                player.collect_weapon(weapons[0]);
            }

            try {
                Thread.sleep(125);
            } catch (Exception e) {
                e.printStackTrace();
            } 
            
            end_time = System.currentTimeMillis();
            elapsed += end_time - start_time;

            if(elapsed >= 150){
                window.surface.render();
                elapsed = 0;
            }
            start_time = System.currentTimeMillis();
            
        }
    }
    public static void main(String[] args) {
        new Knaughts().play();
    }

    class Game_Character {
        Location current;
        Location last;
        boolean is_alive;
        boolean is_hit;
        
    
        Weapon[] weapon_inventory;
    
        char avatar;
    
        public Game_Character(){
            current = new Location();
            last = new Location();
            weapon_inventory = new Weapon[3];
    
            do{
                current.x = r.nextInt(15);
            }while(current.x == 0 || current.x == 15);
            
            do{
                current.y = r.nextInt(9);
            }while(current.y == 0 || current.y == 9);
    
            last = current;
            is_alive = true;
            avatar = '@';
        }
    
        public void check_hit(){
    
        }
    
        public void use_weapon(){
            Projectile created = new Projectile();
            for(int i = 0; i < projectiles.length; i++){
                
                if(projectiles[i] == null){
                    projectiles[i] = created;
                    projectiles[i].current.x = player.current.x;
                    projectiles[i].current.y = player.current.y - 1;
                    board[projectiles[i].current.y][projectiles[i].current.x] = projectiles[i].sprite;
                    break;
                }

            }
        }

        public void collect_weapon(Weapon collected){
            for(int i = 0; i < weapon_inventory.length; i++){
                if(weapon_inventory[i] == null){
                    weapon_inventory[i] = collected;
                    collected.is_available = false;
                    break;
                }
            }
        }
    
    }

    public interface Item {

    }

    abstract class Weapon implements Item {
        char type;
        int power;
        boolean is_available;
    
        Location current;
        Location last;

        public char get_type(){
            return type;
        }
        
    }
    
    class Rifle extends Weapon{
        char type;
        
        boolean active_projectile;
        char[] ammo;
    
        public Rifle(){
            current = new Location();
            type = 'r';

            do{
                current.x = r.nextInt(15);
            }while(current.x == 0 || current.x == 15);
            
            do{
                current.y = r.nextInt(9);
            }while(current.y == 0 || current.y == 9);

            ammo = new char[5];
            for(int i = 0; i < ammo.length; i++){
                ammo[i] = '\'';
            }
        }
        public char get_type(){
            return 'r';
        }

    }

    private void place_weapon(){
        
        weapons[0] = new Rifle();

        board[weapons[0].current.y][weapons[0].current.x] = weapons[0].get_type();
        weapons[0].is_available = true;
    }
    
    class Projectile {
        Location current;
        Location last;
        char sprite;

        public Projectile(){
            current = new Location();
            current = new Location();
            sprite = '\'';
        }
    }
}

class Game_Window extends Frame{
    Game_Surface surface;

    public Game_Window(Knaughts state, int width, int height){
        surface = new Game_Surface(state, width, height);
        
        this.setSize(width, height);
        this.add(surface);
        this.setResizable(false);
        this.setVisible(true);
        surface.init_image();
        
    }
}

class Game_Surface extends Panel{
    Knaughts state;
    Graphics context;
    Graphics bcontext;
    Image bbuffer;
    char[][] board;
    

    public Game_Surface(Knaughts state, int width, int height){
        this.state = state;
        this.setSize(width, height);
        
        this.setFont(new Font("Monospaced", 1, 20));
    }

    protected void init_image(){
        bbuffer = this.createImage(this.getWidth(), this.getHeight());
        context = this.getGraphics();
        bcontext = bbuffer.getGraphics();
        render();
    }

    public void render(){
        //board = state.get_board();

        int ypos = 40;
        String curr_row = "";
        bcontext.clearRect(0, 0, 300, 300);

        for(char[] inner : state.board){
            for(char c : inner){
                curr_row += c;
            }
            bcontext.drawString(curr_row, 40, ypos);
            ypos += 22;
            curr_row = "";
        }
        context.drawImage(bbuffer, 0, 0, this);
        
        //context.dispose();
    }
}



class Location {
    int x;
    int y;
}



