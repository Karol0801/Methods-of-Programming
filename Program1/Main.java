// Program rozwaza z osobna 3 glowne przypadki: 1. tablica zlozona w calosci z wartosci ujemnych, 2. tablica nie posiadajaca zadnej liczby dodatniej,
// 3. tablica posiadajaca przynajmniej jedna liczbe dodatnia.
// Glownym przypadkiem jest przypadek nr 3, dla kazdej tablicy rozpatrywane sa wszystkie mozliwe podtablice i wyliczane sa ich maksymalne
// sumy, bazujac na algorytmie Kadane.
import java.util.Scanner;
class Source {
    public static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {

        byte sets = scanner.nextByte();                     //liczba zestawow danych

        while(sets-- > 0) {                                 //kazda iteracja obsluguje jeden zestaw danych
            byte actualSet = scanner.nextByte();            //numer aktualnego zestawu
            char semicolon = scanner.next().charAt(0);      //wczytanie srednika, aby nie wyrzucilo wyjatku
            byte rows = scanner.nextByte();
            byte columns = scanner.nextByte();
            short[][] table = new short[rows][columns];     //utworzenie tablicy przechowujacej wartosci zestawu
            boolean allNegatives = true;                    //zmienna umozliwiajaca osobne rozpatrzenie przypadku nr 1
            boolean noPositive = true;                      //zmienna umozliwiajaca osobne rozpatrzenie przypadku nr 2

            for (byte i = 0; i < rows; i++) {
                for (byte j = 0; j < columns; j++) {
                    table[i][j] = scanner.nextShort();          //wczytywanie kolejnych wartosci do tablicy
                    if(table[i][j] == 0) allNegatives = false;  //gdy chociaz jedna wartosc jest rowna 0, nie rozpatrujemy przypadku nr 1
                    if(table[i][j] > 0) {                       //gdy chociaz jedna wartosc jest wieksza od 0, to:
                        allNegatives = false;                   //nie rozpatrujemy przypadku nr 1
                        noPositive = false;                     //nie rozpatrujemy przypadku nr 2
                    }
                }
            }

            if(allNegatives) System.out.println(actualSet+": n = "+rows+" m = "+columns+", ms = 0, mst is empty");  //przypadek nr 1
            else if(noPositive) searchFirstZero(table,rows,columns,actualSet);                                      //przypadek nr 2
            else searchMaxTable(table, rows, columns, actualSet);                                                   //znajdz sume i indeksy najwiekszej podtablicy (przypadek nr 3)
        }
    }
    static void searchMaxTable(short[][] tab, byte rows, byte columns, byte actualSet) {        //metoda szuka najwiekszej podtablicy 2D (przypadek nr 3)
        short[] tmp = new short[columns];                                                       //tablica pomocnicza 1D o dlugosci rownej liczbie kolumn rozpatrywanego zestawu
        int max=0;                                                                              //przechowuje najwieksza sume
        byte startRow=0, endRow=0, startCol=0, endCol=0;                                        //przechowuja indeksy najwiekszej podtablicy
        byte actualRectSize, smallestRectSize=0;    //przechowuja rozmiary maksymalnej podtablicy, przydatne w przypadku, gdy mamy wiecej niz jedna podtablice o najwiekszej sumie
        //i chcemy zachowac ta o najmniejszym rozmiarze

        for(byte rowStart=0; rowStart<rows; rowStart++) {                   //dana iteracja mowi, od ktorego wiersza rozpoczyna sie rozpatrywana podtablica
            for(byte j=0; j<columns; j++) tmp[j]=0;                         //zerowanie tablicy pomocniczej
            for(byte rowEnd=rowStart; rowEnd<rows; rowEnd++) {              //dana iteracja wskazuje, na ktorym wierszu konczy sie rozpatrywana podtablica
                for(byte j=0; j<columns; j++) tmp[j] += tab[rowEnd][j];     //sumuje odpowiadajace elementy podtablicy wierszami i wpisuje te wartosci do tablicy pomocnicznej
                int[] result = biggestSum(tmp);                             //przechowuje wynik metody szukajacej maksymalnej podtablicy
                //jej postac to: [najwieksza suma, nr pierwszej kolumny podtablicy, nr ostatniej kolumny podtablicy]
                actualRectSize = (byte) ((rowEnd-rowStart) + (result[2]-result[1]));           //liczba proporcjonalna do rozmiaru znalezionej podtablicy
                if((result[0]>max) || (result[0]==max && (actualRectSize < smallestRectSize))) {//zapisujemy wartosci indeksow oraz najwyzsza sume podtablicy, jezeli
                    //suma ta jest wyzsza od wszystkich dotychczasowych lub jest rowna aktualnemu
                    //maksowi, lecz zawiera mniej elementow
                    max=result[0];
                    startCol = (byte) result[1];
                    endCol = (byte) result[2];
                    startRow = rowStart;
                    endRow = rowEnd;
                    smallestRectSize = actualRectSize;
                }
            }
        }
        System.out.println(actualSet+": n = "+rows+" m = "+columns+", ms = "+max+", mst = a["+startRow+".."+endRow+"]["+startCol+".."+endCol+"]");
    }
    //ponizsza metoda szukania maksymalnego podciagu wykorzystuje w tym celu algorytm Kadane
    static int[] biggestSum(short[] tab) {  //szuka maksymalna podtablice tablicy 1D (tej pomocnicznej, zawierajacej zsumowane poszczegolne wiersze)
        byte len = (byte) tab.length;
        int max = 0, sum = 0;               //wartosc sumy najwiekszej i sprawdzanej w danym momencie
        byte start=0, end=0, actualStart=0; //indeksy maksymalnego podciagu (start, end) oraz poczatek rozpatrywanego podciagu (actualStart)
        for (byte x = 0; x < len; x++) {
            sum += tab[x];                                                //do aktualnej sumy dodajemy element danej iteracji
            if (sum > max || (sum == max && end-start > x-actualStart)) { //gdy suma przewyzsza dotychczas maksymalna, ustawmy ja jako max i zapiszmy jej pozycje
                max = sum;
                end=x;
                start = actualStart;
            }
            if (sum <= 0) {                 //jezeli suma jest ujemna, ustawmy ja na 0 i zacznijmy szukac od nastepnego elementu na nowo
                sum = 0;
                actualStart = (byte) (x+1);
            }
        }
        return new int[]{max, start, end};  //zwraca tablice intow, bo maksymalna suma moze wykraczac poza short a tablica przechowuje 1 typ
    }
    static void searchFirstZero(short[][] tab, short rows, short columns, byte actualSet) { //metoda szuka pierwszego wystapienia "0" w tablicy zlozonej z zer i ewentualnie liczb ujemnych
        outerloop:
        for (byte i = 0; i < rows; i++) {
            for (byte j = 0; j < columns; j++) {
                if (tab[i][j] == 0) {       //gdy napotkamy 0, wypiszmy jego pozycje i przerwijmy obie petle konczac metode
                    System.out.println(actualSet+": n = "+rows+" m = "+columns+", ms = 0, mst = a["+i+".."+i+"]["+j+".."+j+"]");
                    break outerloop;
                }
            }
        }
    }
}
// Testowe zbiory danych:
//        7
//        1 : 1 7
//        -2 7 -4 8 -5 3 1
//        2 : 3 5
//        1 1 -5 -1 0
//        1 1 -1 -1 4
//        0 -4 -2 1 1
//        3 : 2 5
//        0 -1 -1 32767 1
//        4 -2 -2 0 -1
//        4 : 2 5
//        0 -1 -1 10 0
//        11 -2 -2 0 1
//        5 : 3 5
//        -1 -2 -3 -1 -2
//        -1 -1 -1 -1 -5
//        -3 -3 -2 -100 -32768
//        6 : 1 5
//        5 -3 2 -400 5
//        7 : 9 1
//        -4
//        3
//        4
//        4
//        -100
//        233
//        2
//        -340
//        233
// Output:
//        1: n = 1 m = 7, ms = 11, mst = a[0..0][1..3]
//        2: n = 3 m = 5, ms = 5, mst = a[1..2][4..4]
//        3: n = 2 m = 5, ms = 32768, mst = a[0..0][3..4]
//        4: n = 2 m = 5, ms = 16, mst = a[0..1][0..4]
//        5: n = 3 m = 5, ms = 0, mst is empty
//        6: n = 1 m = 5, ms = 5, mst = a[0..0][0..0]
//        7: n = 9 m = 1, ms = 235, mst = a[5..6][0..0]
