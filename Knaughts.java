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
    private Enemy enemy;
    private Patrol patrol;
    Projectile[] projectiles;
    Weapon[] weapons;
    Land_Mine[] mines;

    public Knaughts(){
        r = new Random();
        board = new char[10][16];
        player = new Game_Character();
        enemy = new Enemy();
        patrol = new Patrol();
        projectiles = new Projectile[10];
        weapons = new Weapon[10];
        mines = new Land_Mine[5];
        
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

        // NEEDS TO BE CALLED RANDOMLY FOR DIFFERENT WEAPONS 
        // HARD CODED TO PLACE A RIFLE - JUST A TEST
        place_weapon();

         board[player.current.y][player.current.x] = player.avatar;
        // board[enemy.current.y][enemy.current.x] = enemy.avatar;
        board[patrol.current.y][patrol.current.x] = patrol.avatar;

        this.window = new Game_Window(this, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.addKeyListener(this);
    }

    public void keyPressed(KeyEvent e){
        int move = e.getKeyCode();
        switch(move){
            case 32:
            // HARD CODED TO LOOK AT ZERO
                if(player.weapon_inventory[0] != null){
                    player.use_weapon();
                }
                break;
            case 38:
                if(player.current.y > 1){
                    board[player.current.y][player.current.x] = ' ';
                    player.current.y -= 1;
                    board[player.current.y][player.current.x] = player.avatar;
                }
                
                break;
            case 37:
                if(player.current.x > 1){
                    board[player.current.y][player.current.x] = ' ';
                    player.current.x -= 1;
                    board[player.current.y][player.current.x] = player.avatar;
                }
                break;
            case 40:
                if(player.current.y < board.length - 2){
                    board[player.current.y][player.current.x] = ' ';
                    player.current.y += 1;
                    board[player.current.y][player.current.x] = player.avatar;
                }
                break;
            case 39:
                if(player.current.x < board[0].length - 2){
                    board[player.current.y][player.current.x] = ' ';
                    player.current.x += 1;
                    board[player.current.y][player.current.x] = player.avatar;
                }
                break;
            case 77:
                if(mines[0] != null){
                    mines[0].set();
                }
                break;
            case 81:
                System.exit(0);
                break;
            default:
                
        }
    }

    public void play(){
        long start_time = System.currentTimeMillis();
        long elapsed = 0;
        long end_time = 0;
        Enemy_AI ai = new Enemy_AI();
        
        
        while(player.is_alive){
            for(int i = 0; i < projectiles.length; i++){
                if(projectiles[i] != null){
                    if(projectiles[i].current.y > 0 && board[projectiles[i].current.y][projectiles[i].current.x] == projectiles[i].sprite){
                        board[projectiles[i].current.y][projectiles[i].current.x] = ' ';
                        projectiles[i].current.y += projectiles[i].direction;
                        if(projectiles[i].current.y == 0 || projectiles[i].current.y == 9){
                            projectiles[i] = null;
                            continue;
                        }
                        if(board[projectiles[i].current.y][projectiles[i].current.x] != 'r' && board[projectiles[i].current.y][projectiles[i].current.x] != 'm'){
                            board[projectiles[i].current.y][projectiles[i].current.x] = projectiles[i].sprite;
                            continue;
                        }
                        projectiles[i] = null;
                        
                    } 
                    
                }
            }

            if(!ai.isAlive()){
                ai.start();
            }
            // HARD CODED TO INDEX 0 - TEST
            if(player.current.x == weapons[0].current.x && player.current.y == weapons[0].current.y && weapons[0].is_available == true){
                player.collect_weapon(weapons[0]);
            }
            // HARD CODED TO INDEX 0 - TEST
            if(player.current.x == mines[0].current.x && player.current.y == mines[0].current.y && mines[0].is_available == true){
                player.collect_weapon(mines[0]);
            }

            if(player.current.x == mines[0].current.x && player.current.y == mines[0].current.y && mines[0].is_set == true){
                mines[0].tripped();
                // mines[0] = null;
                board[1][5] = 'D';
                board[1][6] = 'I';
                board[1][7] = 'E';
                board[1][8] = 'D';
                board[1][9] = '!';

                window.surface.render();
                patrol.is_alive = false;
                player.is_alive = false;
                break;
            }

            if(patrol.current.x == mines[0].current.x && patrol.current.y == mines[0].current.y && mines[0].is_set == true){
                mines[0].tripped();
                // mines[0] = null;
                board[1][5] = 'W';
                board[1][6] = 'I';
                board[1][7] = 'N';
                board[1][8] = '!';

                window.surface.render();
                patrol.is_alive = false;
                player.is_alive = false;
                break;
            }

            player.check_hit();
            patrol.check_hit();

            try {
                // TRYING TO KEEP GAME LOOP FROM HAPPENING TOO MANY UNNECESSARY TIMES
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            } 
            
            end_time = System.currentTimeMillis();
            elapsed += end_time - start_time;

            // 5 TIMES WORKS FOR ME, NOT SMOOTH BUT I LIKE IT FOR NOW
            if(elapsed >= 200){
                // System.out.println(elapsed);
                window.surface.render();
                elapsed = 0;
            }
            start_time = System.currentTimeMillis();
            /* if(player.is_alive == false){
                System.out.println("Yep");
            } */
        }
    }
    public static void main(String[] args) {
        new Knaughts().play();
    }

    class Game_Character {
        Location current;
        // Location last; MAYBE FOR FUTURE USE? TELEPORTATION?
        boolean is_alive;
        boolean is_hit;
        Weapon[] weapon_inventory;
    
        char avatar;
    
        public Game_Character(){
            current = new Location();
            
            // NEED TO IMPLEMENT PROTECTION AGAINST COLLECTING MO
            // MORE THAN 3 WEAPONS

            weapon_inventory = new Weapon[3];
    
            do{
                current.x = r.nextInt(15);
            }while(current.x == 0 || current.x == 15);
            
            /* do{
                current.y = r.nextInt(9);
            }while(current.y == 0 || current.y == 9); */
            current.y = 2;
    
            is_alive = true;
            avatar = '@';
        }
    
        // FOR CHECKING IF A CHARACTER WAS HIT, TO BE CALLED IN PLAY
        public void check_hit(){
            for(Projectile p : projectiles){
                if(p != null){
                    if(p.current.x == this.current.x && p.current.y == this.current.y){
                        if(p.sprite == patrol.bullet){
                            board[this.current.y][this.current.x] = '~';
    
                            board[1][5] = 'D';
                            board[1][6] = 'I';
                            board[1][7] = 'E';
                            board[1][8] = 'D';
                            board[1][9] = '!';
    
                            window.surface.render();
                            player.is_alive = false;
                        }
                    }
                }
                
            }
            
        }
    
        public void use_weapon(){
            for(int i = 0; i < projectiles.length; i++){
                if(projectiles[i] == null){
                    projectiles[i] = new Projectile();
                    projectiles[i].current.x = player.current.x;
                    projectiles[i].current.y = player.current.y - 1;
                    if(projectiles[i].current.y > 0){
                        board[projectiles[i].current.y][projectiles[i].current.x] = projectiles[i].sprite; // if at edge don't bother drawing bullet
                    }
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

    class Enemy extends Game_Character{
        char bullet = '"';
        public Enemy(){
            avatar = 'X';
        }

        @Override
        public void use_weapon(){
            for(int i = 0; i < projectiles.length; i++){
                if(projectiles[i] == null){
                    projectiles[i] = new Projectile();
                    projectiles[i].sprite = '"';
                    projectiles[i].direction = 1;
                    projectiles[i].current.x = enemy.current.x;
                    if(enemy.current.y < 8){
                        projectiles[i].current.y = enemy.current.y + 1;
                    }
                    if(projectiles[i].current.y < 9){
                        board[projectiles[i].current.y][projectiles[i].current.x] = this.bullet; // if at edge don't bother drawing bullet
                    }
                    break;
                }
            }
        }
    }

    class Patrol extends Enemy {
        char heading;
        public Patrol(){
            if(r.nextInt(11) % 2 == 0){
                heading = 'l';
            }else{
                heading = 'r';
            }

            do{
                current.x = r.nextInt(15);
            }while(current.x == 0 || current.x == 15);
            current.y = 3;
        }
        
        @Override
        public void use_weapon(){
            for(int i = 0; i < projectiles.length; i++){
                if(projectiles[i] == null){
                    projectiles[i] = new Projectile();
                    projectiles[i].sprite = '"';
                    projectiles[i].direction = 1;
                    projectiles[i].current.x = this.current.x;
                    if(this.current.y < 8){
                        projectiles[i].current.y = this.current.y + 1;
                    }
                    if(projectiles[i].current.y < 9){
                        board[projectiles[i].current.y][projectiles[i].current.x] = this.bullet; // if at edge don't bother drawing bullet
                    }
                    break;
                }
            }
        }

        public void check_hit(){
            for(Projectile p : projectiles){
                if(p != null){
                    if(p.current.x == this.current.x && p.current.y == this.current.y){
                        if(p.sprite == '\''){
                            board[this.current.y][this.current.x] = '\\';
    
                            board[1][5] = 'W';
                            board[1][6] = 'I';
                            board[1][7] = 'N';
                            board[1][8] = '!';
                            board[1][9] = '!';
    
                            window.surface.render();
                            player.is_alive = false;
                        }
                    }
                }
                
            }
            
        }
    }


    abstract class Weapon{
        char type;
        int power;
        boolean is_available;
    
        Location current;
        // Location last; MAYBE FOR FUTURE USE? TELEPORTATION?

        public char get_type(){
            return type;
        }
        
    }
    
    class Rifle extends Weapon{
        boolean active_projectile;
        char[] ammo;
    
        public Rifle(){
            current = new Location();
            type = 'r';

            do{
                current.x = r.nextInt(15);
            }while(current.x == 0 || current.x == 15); // HARD CODED TO 15 WIDE
            
            do{
                current.y = r.nextInt(9);
            }while(current.y == 0 || current.y == 9); // HARD CODED TO 9 HIGH

            ammo = new char[5];
            for(int i = 0; i < ammo.length; i++){
                ammo[i] = '\'';
            }
        }
        /* public char get_type(){
            return 'r';
        } */

    }

    private void place_weapon(){
        // HARD CODED TO PLACE RIFLE, NEED TO BE GENERAL 
        weapons[0] = new Rifle();
        mines[0] = new Land_Mine();

        board[weapons[0].current.y][weapons[0].current.x] = weapons[0].get_type();
        weapons[0].is_available = true;

        board[mines[0].current.y][mines[0].current.x] = mines[0].get_type();

    }

    class Land_Mine extends Weapon {
        boolean is_set;
        boolean tripped;


        public Land_Mine(){
            type = 'm';
            current = new Location();

            do{
                current.x = r.nextInt(15);
            }while(current.x == 0 || current.x == 15); // HARD CODED TO 15 WIDE
            
            do{
                current.y = r.nextInt(9);
            }while(current.y == 0 || current.y == 9 || current.y == 4 || current.y == 5); // HARD CODED TO 9 HIGH NO LINE 4 OR 5 TO COLLIDE WITH PATROL

        }

        public void set(){
            is_set = true;
            this.current.x = player.current.x;
            this.current.y = player.current.y; 
            player.current.y--;
            board[player.current.y][player.current.x] = player.avatar;
            board[this.current.y][this.current.x] = 'o';

        }

        public void tripped(){
            if(this.current.x > 1 && this.current.y > 1){
                board[this.current.y][this.current.x] = 'O';
                board[this.current.y + 1][this.current.x] = '#';
                board[this.current.y - 1][this.current.x] = '#';
                board[this.current.y][this.current.x - 1] = '#';
                board[this.current.y][this.current.x + 1] = '#';
            }
            this.is_set = false;
            this.tripped = true;
        }

    }
    
    class Projectile {
        Location current;
        // Location last; MAYBE FOR FUTURE USE? TELEPORTATION?
        char sprite;
        int power;
        int direction;

        public Projectile(){
            current = new Location();
            direction = -1;
            sprite = '\'';
        }
    }

    class Enemy_AI extends Thread {
        
        public void attack(){
            // SHOOT AT PLAYER
            if(enemy.current.x == player.current.x){
                enemy.use_weapon();
            }else{
                aim();
            }
        }
        // I DON'T LIKE MANIPULATING BOARD LIKE THIS
        // THERE SHOULD BE A SEPARATE METHOD THAT ONLY
        // SERVES TO UPDATE BOARD LEAVING METHODS
        // LIKE THIS TO JUST CHANGE DATA
        public void aim(){
            if(player.current.x > enemy.current.x && enemy.current.x != board[0].length - 1){
                board[enemy.current.y][enemy.current.x] = ' ';
                enemy.current.x++;
                board[enemy.current.y][enemy.current.x] = enemy.avatar;
            }else if(player.current.x < enemy.current.x && enemy.current.x != 1){
                board[enemy.current.y][enemy.current.x] = ' ';
                enemy.current.x--;
                board[enemy.current.y][enemy.current.x] = enemy.avatar;
            }
        }

        public void patrol(){
            if(patrol.current.x > 0 && patrol.heading == 'l'){
                board[patrol.current.y][patrol.current.x] = ' ';
                patrol.current.x--;
                if(patrol.current.x > 0){
                    board[patrol.current.y][patrol.current.x] = patrol.avatar;
                }else{
                    patrol.heading = 'r';
                    patrol.current.x++;
                    board[patrol.current.y][patrol.current.x] = patrol.avatar;
                }
            }else if(patrol.current.x < 15 && patrol.heading == 'r'){
                board[patrol.current.y][patrol.current.x] = ' ';
                patrol.current.x++;
                if(patrol.current.x < 15){
                    board[patrol.current.y][patrol.current.x] = patrol.avatar;
                }else{
                    patrol.heading = 'l';
                    patrol.current.x--;
                    board[patrol.current.y][patrol.current.x] = patrol.avatar;
                }
            }
            patrol.use_weapon();
        }

        public void run(){
            while(player.is_alive && patrol.is_alive){
                try {
                    Thread.sleep(r.nextInt(600));
                } catch (Exception e) {
                    //TODO: handle exception
                    e.printStackTrace();
                }
                // attack();
                patrol();
            }
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
    // NEED TO RESEARCH POSSIBLE WAYS TO BE MORE EFFICIENT
    public void render(){
        int ypos = 40;
        String curr_row = "";
        bcontext.clearRect(0, 0, 300, 300);

        // THIS SEEMS LIKE A PROBLEM AREA, COULD BE FASTER?
        for(char[] inner : state.board){
            for(char c : inner){
                curr_row += c;
            }
            bcontext.drawString(curr_row, 40, ypos);
            ypos += 22;
            curr_row = "";
        }
        context.drawImage(bbuffer, 0, 0, this);
        
        //context.dispose(); DONT KNOW IF THIS HELPS OR HURTS
    }
}
// UTILIY CLASS FOR SEMANTICS
class Location {
    int x;
    int y;
}



