import java.util.Scanner;

// Program sklada sie z trzech glownych klas (pomijajac klase Source), na ktorych wykonywane sa operacje
// Ponizsza klasa reprezentuje liste pociagow, ktore sa obecne w zabawie.
class TrainList {
    private Train first;    //referencja do pierwszego pociagu

    public TrainList(){
        first = null;       //brak pociagow
    }
    // nowy pociag wstawiamy na poczatek
    public void New(String name, String carName){
        Train newTrain = new Train(name, carName);
        newTrain.next = first;
        first = newTrain;
    }
    //wypisyweanie pociagow (zwykle wypisywanie listy wiazanej)
    public void TrainsList() {
        System.out.print("Trains:");
        Train current = first;               //zaczynamy od poczatku listy
        while(current != null){
            System.out.print(" "+current.name);
            current = current.next;
        }
        System.out.println();
    }
    //Wypisywanie pociagu (najpierw szukamy za pomoca metody, potem wyswietlamy)
    public void Display(String trainName) {
        locateTrain(trainName).display();
    }
    //szukanie pociagu, zakladajac, ze zawsze szukamy pociag ktory istnieje
    private Train locateTrain(String trainName){
        Train current = first;
        while(!current.name.equals(trainName)) {
            current = current.next;
        }
        return current;
    }
    public void InsertFirst(String trainName, String carName){
        locateTrain(trainName).InsertFirst(carName);
    }
    public void InsertLast(String trainName, String carName){
        locateTrain(trainName).InsertLast(carName);
    }
    public void Reverse(String trainName) {
        locateTrain(trainName).Reverse();
    }
    public void Union(String train1, String train2) {
        Train T1 = locateTrain(train1);
        Train T2 = locateTrain(train2);
        //dolacza T2 na koniec T1
        if (T1.last.next == null) {         //ostatni wagon w T1 nie jest odwrocony
            if (T2.first.prev == null) {    //pierwszy wagon w T2 nie jest odwrocony
                T1.last.next = T2.first;    //wtedy podepnij wagony
                T2.first.prev = T1.last;
            } else {                        //pierwszy wagon w T2 jest odwrocony
                T1.last.next = T2.first;
                T2.first.next = T1.last;
            }
        } else {                            //ostatni wagon w T1 jest odwrocony
            if (T2.first.prev == null) {    //pierwszy wagon w T2 nie jest odwrocony
                T1.last.prev = T2.first;
                T2.first.prev = T1.last;
            } else {                        //pierwszy wagon w T2 jest odwrocony
                T1.last.prev = T2.first;
                T2.first.next = T1.last;
            }
        }
        T1.last = T2.last;
        deleteTrain(train2);
    }
    //standardowe usuwanie elementu listy
    public void deleteTrain(String trainName){
        Train current = first;
        Train previous = first;
        while(!current.name.equals(trainName)){
            previous = current;
            current = current.next;
        }
        if(current == first) first = first.next;
        else previous.next = current.next;
    }
    public void DelFirst(String train1, String train2){
        Train T1 = locateTrain(train1);
        //jezeli jest to jedyny wagon, zrob z niego nowy i usun pierwszy pociag
        if(T1.first == T1.last){
            New(train2, T1.first.name);
            deleteTrain(T1.name);
        }
        else {
            New(train2, T1.first.name);
            T1.deleteFirst();
        }
    }
    public void DelLast(String Train1, String Train2){
        Train T1 = locateTrain(Train1);
        if(T1.first == T1.last){
            New(Train2, T1.last.name);
            deleteTrain(T1.name);
        }
        else {
            New(Train2, T1.last.name);
            T1.deleteLast();
        }
    }
}
//Ponizsza klasa reprezentuje pociag (nazwe lokomotywy i liste jego wagonow)
class Train {
    public String name;         //nazwa lokomotywy
    public Train next;          //Referencja do nastepnego

    public Carriage first;
    public Carriage last;
    public Train(String name, String carName){
        this.name = name;
        Carriage Car = new Carriage(carName);
        first = Car;
        last = Car;            //narazie mamy jeden wagon wiec first == last
    }
    public void display(){
        System.out.print(name + ":");
        Carriage current = first;
        boolean reversed;                           //zmienna kontrolujaca, czy dane miejsce w pociagu jest odwrocone
        if(first.prev == null) reversed = false;    //pierwszy wagon nieodwrocony
        else reversed = true;                       //pierwszy wagon odwrocony
        while(current != null) {                    //wykonuje sie dopoki nie natrafimy na koniec pociagu
            System.out.print(" " + current.name);
            if (!reversed) {                        //jezeli nie jest odwrocony, szukajmy za pomoca .next
                if (current.next != null) {
                    if (current.next.next == current) {
                        reversed = true;
                    }
                }
                current = current.next;
            }
            else{
                if(current.prev != null){           //jezeli odwrocony, kolejnych wagonow szukamy za pomoca .prev
                    if(current.prev.prev == current) {
                        reversed = false;
                    }
                }
                current = current.prev;
            }
        }
        System.out.println();
    }
    public void InsertFirst(String carName){
        Carriage newCarriage = new Carriage(carName);
        if(first.next == null) {        //poczatek nieodwrocony
            first.next = newCarriage;
            newCarriage.prev = first;
        }
        else {                          //odwrocony
            first.prev = newCarriage;
            newCarriage.next = first;
        }
        first = newCarriage;
    }
    public void InsertLast(String carName){
        Carriage newCarriage = new Carriage(carName);
        if(last.next == null) {
            last.next = newCarriage;
            newCarriage.prev = last;
        }
        else {
            last.prev = newCarriage;
            newCarriage.next = last;
        }
        last = newCarriage;
    }
    public void Reverse(){
        Carriage tmp;
        tmp = first;
        first = last;
        last = tmp;
    }
    public void deleteFirst(){
        // Gdy mamy wiecej niz 1 wagon
        if(first.prev == null) {                //pierwszy wagon nieodwrocony
            if(first.next.prev == first){       //drugi tez nieodwrocony
                first = first.next;
                first.prev = null;
            }
            else {                              //drugi wagon jest odwrocony
                first = first.next;
                first.next = null;
            }
        }
        else {                                  //pierwszy odwrocony
            if(first.prev.next == first) {      //drugi odwrocony
                first = first.prev;
                first.next = null;
            }
            else {                              //drugi nieodwrocony
                first = first.prev;
                first.prev = null;
            }
        }
    }
    //Analogicznie jak przy deleteFirst, z tym ze patrzymy na wagon ostatni i przedostatni, a nie pierwszy i drugi
    public void deleteLast(){
        if(last.next == null){
            if(last.prev.next == last){
                last = last.prev;
                last.next = null;
            }
            else {
                last = last.prev;
                last.prev = null;
            }
        }
        else {
            if(last.next.prev == last) {
                last = last.next;
                last.prev = null;
            }
            else {
                last = last.next;
                last.next = null;
            }
        }
    }
}
//Klasa reprezentujaca pojedyczny wagon, posiada nazwe i referencje do wagonow sasiednich
class Carriage {
    public String name;
    public Carriage next;
    public Carriage prev;
    public Carriage(String name){
        this.name = name;
    }
}

class Source {
    public static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        int sets = scanner.nextInt();                             //liczba zestawow danych
        while (sets-- > 0) {                                      //kazda iteracja obsluguje jeden zestaw danych
            int commandsNumber = scanner.nextInt();               //liczba komend w zestawie
            TrainList Trains = new TrainList();
            while(commandsNumber-- > 0){
                String command = scanner.nextLine();
                if(command.length() > 5){
                    String[] result = command.split(" ");
                    if(result[0].equals("New")){
                        Trains.New(result[1], result[2]);
                    }
                    else if(result[0].equals("InsertFirst")){
                        Trains.InsertFirst(result[1], result[2]);
                    }
                    else if(result[0].equals("InsertLast")){
                        Trains.InsertLast(result[1], result[2]);
                    }
                    else if(result[0].equals("Display")){
                        Trains.Display(result[1]);
                    }
                    else if(result[0].equals("TrainsList")){
                        Trains.TrainsList();
                    }
                    else if(result[0].equals("Reverse")){
                        Trains.Reverse(result[1]);
                    }
                    else if(result[0].equals("Union")){
                        Trains.Union(result[1], result[2]);
                    }
                    else if(result[0].equals("DelFirst")){
                        Trains.DelFirst(result[1], result[2]);
                    }
                    else if(result[0].equals("DelLast")){
                        Trains.DelLast(result[1], result[2]);
                    }
                }
                else commandsNumber++;
            }
        }
    }
}
//INPUT
//        1
//        17
//        New T1 W1
//        InsertLast T1 W0
//        InsertFirst T1 W2
//        Reverse T1
//        Display T1
//        DelFirst T1 T2
//        Union T1 T2
//        TrainsList
//        DelLast T1 T3
//        InsertFirst T3 G4
//        DelLast T3 T5
//        TrainsList
//        Union T5 T1
//        Display T5
//        Reverse T5
//        Display T5
//        TrainsList
//OUTPUT
//        T1: W0 W1 W2
//        Trains: T1
//        Trains: T5 T3 T1
//        T5: W0 W1 W2
//        T5: W2 W1 W0
//        Trains: T5 T3
