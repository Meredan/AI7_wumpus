import java.util.*;

public class Main {
	

    private static ArrayList<Node> nodes = new ArrayList<>(); // масив усіх кімнат карти
    private static boolean wumpusAlive = true; // відстежує, чи є вампус живим чи мертвим
    private static boolean arrow = true; // чи має агент стрілу
	public static void main(String[] args) {


            int size = 5;
            int sizeMap=size+2;
            System.out.println("Size: " + sizeMap + "x" + sizeMap);
            System.out.println("W - Wumpus\nP - Pit\nB - Breeze\nS - Stench\nG - Glitter\nX - Wall\n\n");
            Node[][] rooms = new Node[size][size]; // матриця кімнат
            Random rand = new Random();
            int iter = 0;
            int startId = -1;
            Node playerRoom = null; // точка, в якій знаходиться гравець

            for (int i = 0; i < size; i++) { // проходить по кімнатам
                for (int j = 0; j < size; j++) {
                    rooms[i][j] = new Node(iter, j, i);
                    nodes.add(rooms[i][j]);
                    if(i == size-1 && j == 0) {
                        startId = rooms[i][j].id; // встановлення стартової точки
                        rooms[i][j].start = true;
                        playerRoom = rooms[i][j]; // встановлення точки із гравцем
                    }
                    iter++;
                }
            }

            int goldRoom = rand.nextInt(size * size); // де золото
            int wumpusRoom = rand.nextInt(size * size); // де вампус
            while(goldRoom == startId || wumpusRoom == startId) { // і золото, і вампус
                goldRoom = rand.nextInt(size * size);
                wumpusRoom = rand.nextInt(size * size);
            }

            /*
             * ініціалізація карти
             */

            for (int X = 0; X < size; X++) {
                for (int Y = 0; Y < size; Y++) {
                    Node current = rooms[X][Y];
                    // перевірка всіх можливих напрямків з огляду на те, що рух по діагоналям і крізь стінки - неможливий
                    try {
                        current.friends.add(rooms[X+1][Y]);
                        current.friends.add(rooms[X][Y+1]);
                        current.friends.add(rooms[X-1][Y]);
                        current.friends.add(rooms[X][Y-1]);
                    } catch(ArrayIndexOutOfBoundsException e) {
                        try {
                            current.friends.add(rooms[X][Y+1]);
                            current.friends.add(rooms[X-1][Y]);
                            current.friends.add(rooms[X][Y-1]);
                        } catch(ArrayIndexOutOfBoundsException r) {
                            try {
                                current.friends.add(rooms[X-1][Y]);
                                current.friends.add(rooms[X][Y-1]);
                            } catch(ArrayIndexOutOfBoundsException t) {
                                try {
                                    current.friends.add(rooms[X][Y-1]);
                                } catch(ArrayIndexOutOfBoundsException u) {

                                }
                            }
                        }
                    }



                    if(rooms[X][Y].id == goldRoom){
                        rooms[X][Y].hasGold = true;
                    }
                    if(rooms[X][Y].id == wumpusRoom){
                        rooms[X][Y].hasWumpus = true;
                    }
                }
            }



            for (int i = 0; i < size; i++) { // ями
                for (int j = 0; j < size; j++){
                    if(i == size-1 && j == 0 || rooms[i][j].hasGold){// гравець праворуч внизу
                        continue;
                    }else{
                        if(rand.nextInt(10) < 2) {
                            rooms[i][j].hasPit = true;
                        }
                    }
                }
            }

            for (int i = 0; i < size; i++) { // кімнати із протягами та запахом
                for (int j = 0; j < size; j++) {
                    if(rooms[i][j].hasPit){
                        for(Node n : rooms[i][j].friends){
                            n.breeze = true;
                        }
                    }
                    if(rooms[i][j].hasWumpus){
                        for(Node n : rooms[i][j].friends){
                            n.smell = true;
                        }
                    }
                }
            }

            for(int i = 0; i < size+2; i++) {System.out.print("X    ");}
            System.out.println();
            System.out.println();

            for (int i = 0; i < size; i++) { // вивід стартової карти, запах має приорітет перед протягами
                System.out.print("X    ");
                for (int j = 0; j < size; j++) {
                    int g = 0;



                    if (rooms[i][j].hasGold) {
                        System.out.print("G");
                        g++;
                    }
                    if (rooms[i][j].hasWumpus) {

                        System.out.print("W");
                        g++;

                    }
                    if (rooms[i][j].hasPit) {

                        System.out.print("P");
                        g++;

                    }
                    if (rooms[i][j].smell) {

                        System.out.print("S");
                        g++;
                    }
                    if (rooms[i][j].breeze) {

                        System.out.print("B");
                        g++;

                    }
                    if (g == 0) {
                        System.out.print("_    ");
                    } else {
                        g = 5 - g;
                        for (int f = 0; f < g; f++)
                            System.out.print(" ");
                    }

                }
                System.out.print("X    ");
                System.out.println();
                System.out.println();
            }

             for(int i = 0; i < size+2; i++) {System.out.print("X    ");}
             System.out.println();
             System.out.println();


             //початок гри

            ArrayList<Node> badSquares = new ArrayList<>();
            int steps = 0;
            int score = 0;
            boolean collectedGold = false;
            boolean killChecker = false;
            while (true) {
                System.out.println((playerRoom.X+2) +":"+ (playerRoom.Y+2) + ", Steps: " + steps); // покроковий вивід дій агента

                playerRoom.safe = true;
                if (playerRoom.hasPit || (playerRoom.hasWumpus && wumpusAlive)) { // перевірка чи живий агент
                    score -= 1000;
                    score -= steps;

                    System.out.println("\nPlayer Died");
                    System.out.println("Steps: " + steps);
                    System.out.println("Points: " + score);

                    break;
                }

                if (playerRoom.hasGold) { // якщо кімната із золотом, забираємо його
                    collectedGold = true;
                    steps += 1;
                    System.out.println("\nWe took the gold, run!");
                }

                /*
                 * Перевірка небезпек
                 */

                if (playerRoom.breeze) {
                    System.out.println("feels breeze");// якщо агент відчуває протяг, небезпека кімнати підвищується
                    for (Node spot : playerRoom.friends) {
                        if (!spot.safe) {
                            spot.pitNum++;
                            if(!badSquares.contains(spot)){
                                badSquares.add(spot);
                            }
                        }
                    }
                } else { // якщо поточна кімната без протягу, декрементуємо небезпеку
                    for (Node spot : playerRoom.friends) {
                        if (!spot.safe) {
                            spot.pitNum--;
                            if(badSquares.contains(spot) && spot.wumpusNum <= 0 && spot.pitNum <= 0){
                                badSquares.remove(spot);
                            }
                        }
                    }
                }
                if (playerRoom.smell) {
                    System.out.println("feels smell");// так само збільщуємо показник небезпеки при запаху
                    for (Node spot : playerRoom.friends) {
                        if (!spot.safe) {
                            spot.wumpusNum++;
                            if(!badSquares.contains(spot)){ // додання до списку пошуку
                                badSquares.add(spot);
                            }
                        }
                    }
                } else { // декремент у разі безпечного перебування
                    for (Node spot : playerRoom.friends) {
                        if (!spot.safe) {
                            spot.wumpusNum--;
                            if(badSquares.contains(spot) && spot.wumpusNum <= 0 && spot.pitNum <= 0){
                                badSquares.remove(spot);
                            }
                        }
                    }
                }

                /*
                 * кінець перевірки небезпек
                 */
                boolean checker = false;

                if(collectedGold){ // перевірка чи є золото
                    //пошук із початку
                    System.out.println("feels glitter");
                    breadthFirstEnd(playerRoom);//встановлення кроків
                    steps += nodes.get(startId).tail.size();
                    score += 1000;
                    killChecker = true; // закінчення гри
                }else {

                    for (Node spot : playerRoom.friends) { // якщо поруч жодного натяка на небезпеку, йти сюди
                        if (spot.wumpusNum <= 0 && spot.pitNum <= 0 && !spot.safe) {
                            playerRoom = spot;
                            checker = true;
                            steps++;
                            break;
                        }
                    }
                    if (!checker) {
                        int temp = breadthFirstZero(playerRoom);
                        if (temp != -1) {
                            playerRoom = nodes.get(temp); //встановлення положення гравця

                            steps += playerRoom.tail.size();
                        } else { //пошук найкращої позиції і перехід туди

                            if (findSafestSquare(badSquares).wumpusNum > 0 && arrow) { //перевірка на найкращій варіант поведінки, враховуючи наявні фактори
                                Node past = playerRoom;
                                playerRoom = findSafestSquare(badSquares);
                                breadthFirstNoZero(past, playerRoom);
                                badSquares.remove(playerRoom);
                                score-=10;
                                steps += playerRoom.tail.size();
                                killWumpus(playerRoom);
                            } else { // перехід до точки без вбивства
                                Node past = playerRoom;
                                playerRoom = findSafestSquare(badSquares);
                                breadthFirstNoZero(past, playerRoom);
                                badSquares.remove(playerRoom);
                                steps += playerRoom.tail.size();
                            }


                        }
                    }
                }
                if(killChecker){
                    System.out.println("Found gold");
                    score -= steps;
                    System.out.println("Cells entered: " + steps);
                    System.out.println("Points: " + score);
                    break;
                }

            }



    }

    private static Node findSafestSquare(ArrayList<Node> toCheck){ // повертає id найбезпечнішої точки
        Node best = null;
        for(Node n : toCheck){
            if(best == null){
                best = n;
            }else{
                if(best.wumpusNum + best.pitNum > n.wumpusNum + n.pitNum) {
                    best = n;
                }else if(best.wumpusNum + best.pitNum == n.wumpusNum + n.pitNum){
                    if(best.wumpusNum < n.wumpusNum){ // перевірка на найбільше число вампуса, щоб зрозуміти чи можна його вбити
                        best = n;
                    }
                }
            }
        }
	    return best;
    }


    private static void killWumpus(Node toKill){
        if(toKill.hasWumpus && arrow){//перевірка на крик вампуса, щоб зрозуміти чи агент убив його
	        toKill.hasWumpus = false;
	        wumpusAlive = false;
	        arrow = false;
	       System.out.println("Wumpus Screams");
	        wumpusWumps();
        } else{
           System.out.println("Failed wumpus kill");
            toKill.wumpusNum = 0;
        }

        arrow = false;
    }


    private static int breadthFirstZero(Node start) { // пошук в ширину для найближчого нуля
        clearTails();
        clearVisited();
    	LinkedList<Node> queue = new LinkedList<>();
    	queue.add(start);
    	Node current;
    	while(!queue.isEmpty()) {
    		current = queue.poll();
    		if(current.visited) {
    			continue;
    		}
    		if(current.wumpusNum <= 0 && current.pitNum <= 0 && !current.safe) {
    			return current.id;
    		}
    		current.visited = true;
    		current.tail.add(current);
    		for(Node n : current.friends) {
    			if((n.tail.size() <= current.tail.size() && n.tail.size() != 0) || (n.wumpusNum > 0 || current.pitNum > 0)) continue; // якщо місце безпечне, прибираємо можливість лін однакової довжини
                n.tail.addAll(removeDuplicates(current.tail));
    			queue.add(n);
    		}
    		
    	}
    	return -1;
    }

    private static int breadthFirstNoZero(Node start, Node end) {
        clearTails();
        clearVisited();
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(start);
        Node current;
        while(!queue.isEmpty()) {
            current = queue.poll();
            if(current.visited) {
                continue;
            }
            if(current == end) {
                return current.id;
            }
            current.visited = true;
            current.tail.add(current);
            for(Node n : current.friends) {
                if(n.tail.size() <= current.tail.size() && n.tail.size() != 0) continue;
                n.tail.addAll(removeDuplicates(current.tail));
                queue.add(n);
            }

        }
        return -1;
    }

    private static int breadthFirstEnd(Node start) {
        clearTails();
        clearVisited();
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(start);
        Node current;
        while(!queue.isEmpty()) {
            current = queue.poll();
            if(current.visited) {
                continue;
            }
            if(current.start) {

                return current.id;
            }
            current.visited = true;
            current.tail.add(current);
            for(Node n : current.friends) {
                if(n.tail.size() <= current.tail.size() && n.tail.size() != 0) { continue; }
                n.tail.addAll(removeDuplicates(current.tail));
                queue.add(n);
            }
        }
        return -1;
    }


    private static ArrayList<Node> removeDuplicates(ArrayList<Node> remove){
        Set<Node> temp = new HashSet<>(remove);
    	remove.clear();
    	remove.addAll(temp);
    	return remove;
    	
    }

    private static void clearTails(){ // прибирання хвостів після пошуку
        for(Node n : nodes){
            n.tail = new ArrayList<>();
        }
    }
    private static void clearVisited(){
        for(Node n : nodes){

            n.visited = false;
        }

    }

    //дефолтні значення для запахів і вампуса
    private static void wumpusWumps(){
        for(Node n : nodes){
            n.smell = false;
            n.wumpusNum = 0;
        }
    }

}
