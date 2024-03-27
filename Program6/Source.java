//porgram wykorzystuje dwie funkcje do rozwiazania tego samego problemu. Kazda z nich wspomaga sie inna
//funkcja, ktora tworzy stringa za pomoca stringbuildera wypisywanego jako wynik, po znalezieniu rozwiazania
import java.util.Scanner;
public class Source {
    public static Scanner scanner = new Scanner(System.in);
    public static boolean found = false;                //zmienna globalna

    public static void main(String[] args) {
        int sets = scanner.nextInt();
        while (sets-- > 0) {
            int capacity = scanner.nextInt();      //pojemnosc plecaka
            int elementsNumber = scanner.nextInt();     //liczba rozwazanych elementow
            found = false;
            int[] elements = new int[elementsNumber];
            StringBuilder sb = new StringBuilder();
            //przechowuje ktore indeksy skladaja sie na rozwiazanie
            boolean[] solution = new boolean[elementsNumber];
            for (int i = 0; i < elementsNumber; i++) {
                elements[i] = scanner.nextInt();
            }
            //rec_pakuj zwraca stringa, jezeli jest on pusty - wtedy nie mamy rozwiazania dla danego zestawu = "BRAK"
            String result = rec_pakuj(elements, capacity, 0, solution, sb);
            //jezeli rec_pakuj znalazlo rozwiazanie, wypisujemy je i uruchamiamy iter_pakuj
            if (result.length() > 1) {
                System.out.println("REC:  " + capacity + " =" + result);
                System.out.println("ITER: " +  capacity + " ="+ iter_pakuj(elements,capacity));
            } else System.out.println("BRAK");
        }
    }
    //funkcja uzywa wylacznie rekurencji do rozwiazania problemu. Zwraca ona Stringa, w ktorym wypisane sa liczby
    //skladajace sie na wynik (lub pusty String, jezeli rozwiazania brak)
    public static String rec_pakuj(int[] tab, int theRest, int index, boolean[] indices, StringBuilder sb) {
        //gdy znalezlismy juz leksykalnie pierwsze rozwiazanie, zwroc je
        if (found) {
            return sb.toString();
        }
        //gdy reszta < 0, wzielismy za duze liczbe:return
        if (theRest < 0) {
            return sb.toString();
        }
        //gdy reszta rowna zero, stworz stringa zawierajace wynik
        if (theRest == 0) {
            found = true;
            sb = rec_wypisz(tab, indices, 0, sb);
        } else if (index == tab.length) { //gdy wychodzimy poza dlugosc, wychodzimy z wywolania
            return sb.toString();
        } else {
            indices[index] = true;  //zakladamy, ze nastepna liczba bedzie odpowiednia
            theRest -= tab[index];
            rec_pakuj(tab, theRest, index + 1, indices, sb);    //wywolujemy i sprawdzamy nastepne liczby wlaczajac w to tab[index]
            theRest += tab[index];  //jesli nie znajdziemy wyniku, ta liczba nam nie pasuje
            indices[index] = false; //wyrzucamy ja z potencjalnego wyniku
            rec_pakuj(tab, theRest, index + 1, indices, sb); //i szukamy dalej bez niej
        }
        return sb.toString();
    }
    //funkcja rekurencyjna zwracajaca StringBuildera zawierajacego rozwiazanie
    public static StringBuilder rec_wypisz(int[] arr, boolean[] resultIndexes, int index, StringBuilder sb) {
        if (index == arr.length) {
            return sb;
        }
        //jezeli na indeksie liczby w naszej tablicy 'bol' znajduje sie liczba, dodajemy ja
        if (resultIndexes[index]) sb.append(" ").append(arr[index]);
        rec_wypisz(arr, resultIndexes, index + 1, sb);
        return sb;
    }
    //analogiczna funkcja lecz dla wersji iteracyjnej - sciagamy liczby ze stosu
    public static StringBuilder iter_wypisz(Stack stack, StringBuilder sb) {
        String s = stack.displayStack();
        sb.append(s);
        return sb;
    }
    public static String iter_pakuj(int[] tab, int capacity) {
        Stack stack = new Stack(tab.length);
        StringBuilder sb = new StringBuilder();
        int n = tab.length;
        int theRest = capacity;
        int currIndex = 0;
        int[] elem;
        //wykonujemy dopoki nie znajdziemy rozwiazania albo nie sprawdzimy wszystkich mozliwosci
        while (true) {
            if (theRest == 0) {
                iter_wypisz(stack, sb);
                break;
            }
            //jezeli wydzlismy poza tablice (i nie mamy wyniku)
            if(currIndex == n){
                elem = stack.pop(); //usuwamy ostatni element ze stosu
                theRest += elem[0];
                if(theRest == capacity && elem[1] == n-1) break;    //jezeli na stosie byl jedynie ostatni element, wyjdz z funkcji (brak rozwiazan)
                currIndex = elem[1]+1; //szukaj dalej
                continue;
            }
            theRest -= tab[currIndex];
            if (theRest < 0) {
                //gdy mamy za duza liczbe, nie dodajemy jej na stos
                theRest += tab[currIndex++];
                continue;
            }
            stack.push(tab[currIndex],currIndex++);
        }
        return sb.toString();
    }
}
//stos zawiera elementy skladajace sie z dwoch czesci: wartosci oraz indeksu, w celu zorientowania sie gdzie kontynouwac
// po sciagniecu liczby ze stosu
class Stack{
    int[][] values;
    int n;
    int index;
    int[] element;
    public Stack(int n){
        this.n = n;
        values = new int[n][2];
        index = -1;
        element = new int[2];
    }
    public void push(int x, int id){
        values[++index][0] = x;
        values[index][1] = id;
    }
    public int[] pop(){
        element[0] = values[index][0];
        element[1] = values[index--][1];
        return element;
    }
    public String displayStack(){
        StringBuilder sb = new StringBuilder();
        for(int i=index;i>=0;i--){
            sb.insert(0,values[i][0]);
            sb.insert(0," ");
        }
        return sb.toString();
    }
}
//INPUT
//        3
//        12
//        7
//        11 5 12 34 8 2 1
//        6
//        4
//        1 3 3 6
//        6
//        4
//        1 4 4 6
//OUTPUT
//        REC:  12 = 11 1
//        ITER: 12 = 11 1
//        REC:  6 = 3 3
//        ITER: 6 = 3 3
//        REC:  6 = 6
//        ITER: 6 = 6
