//Dane przechowywane sa w dwuwymiarowej tablicy stringow. Poczatkowo zamieniamy miejscami kolumy
//(aby spelaniany warunki zadania) a nastepnie wywolywany jest quickSort w hybrydzie z selectionSort.
//Zarowno dla danych typu String jak i int uruchamiana jest ta sama funkcja - typy danych sa rozrozniane przez
//funkcje "compareRows". Pozbycie sie stosu umozliwilo oznaczanie elementow (int jako ujemny a String ma "]")
import java.util.Scanner;
public class Source {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int sets = Integer.parseInt(scanner.nextLine()); //liczba zestawow
        while (sets-- > 0) {
            String[] parameters = scanner.nextLine().split(","); //wczytujemy 3 wartosci
            String[] headerLine = scanner.nextLine().split(","); //naglowek
            int rowsNumber = Integer.parseInt(parameters[0]);
            int columnsNumber = headerLine.length;
            String[][] values = new String[rowsNumber][columnsNumber];
            for (int i = 0; i < rowsNumber; i++) {
                String inputLine = scanner.nextLine();
                String[] inputValues2 = inputLine.split(",");
                for (int j = 0; j < columnsNumber; j++) {
                    values[i][j] = inputValues2[j];
                }
            }
            Set set = new Set(parameters, headerLine, values);
            set.changeColumnOrder();
            set.quickSort();
            set.display();
        }
    }
}
class Set{
    int rowsNumber;
    int columnWeSort;
    boolean sortingOrder;
    String[] header;
    String[][] data;
    boolean numeric; //informacja czy sortujemy inty czy Stringi
    public Set(String[] parameters, String[] headerLine, String[][] data){
        rowsNumber = Integer.parseInt(parameters[0]);
        columnWeSort = Integer.parseInt(parameters[1]);
        sortingOrder = Integer.parseInt(parameters[2]) == 1;
        header = headerLine;
        this.data = data;
    }
    public void display(){
        int columnNumber = data[0].length;
        StringBuilder sb = new StringBuilder(); //uzywam Stringbuildera bo jest efektywniejszy(?)
        for(int i=0; i<columnNumber-1; i++){
            sb.append(header[i]).append(",");
        }
        sb.append(header[columnNumber-1]);
        System.out.println(sb);
        sb.setLength(0);
        for (String[] row : data) {
            for (int j = 0; j < columnNumber - 1; j++) {
                sb.append(row[j]).append(",");
            }
            sb.append(row[columnNumber - 1]);
            System.out.println(sb);
            sb.setLength(0);
        }
        System.out.println();
    }
    //Funkcja ktora wstawia kolumne ktora sortujemy najbardziej na lewo
    public void changeColumnOrder(){
        int columnWeSortID = columnWeSort-1;    //indeks wartosci w po ktorej sortujemy
        String tmp = header[columnWeSortID];
        for(int i=columnWeSortID; i>0;i--){
            header[i] = header[i-1];
        }
        header[0] = tmp;
        for(int i=0; i<rowsNumber; i++){
            changeOneRow(i,columnWeSortID);
        }
        numeric = isNumeric();
    }
    private void changeOneRow(int i, int columnWeSortID){
        String tmp = data[i][columnWeSortID];
        for(int j=columnWeSortID; j>0;j--){
            data[i][j] = data[i][j-1];
        }
        data[i][0] = tmp;
    }
    //sprawdzamy data[0][0] bo wywolujemy po zamianie kolumn
    private boolean isNumeric() {
        try {
            Integer.parseInt(data[0][0]);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    //algorytm wykorzystuje fakt, ze wartosci sa unikalne w kazdym wierszu.
    //dzieki temu nie mamy powtorzen i mozliwe jest nieuzycie stosu.
    //gdy dzielimy dane na mniejsza i wieksza czesc wzgledem pivota i przechodzimy do
    //mniejszego podzadania, zapamietujemy gorny indeks pierwotnego zadania (unikatowy wiec nie trzeba
    //kombinowac np. z wartoscami ujemnymi). Dzieki zastosowaniu Selectionsort srednia zlozonosc to
    // nlog(n) gdyz ma on lepsza efektywnosc od quicksort dla malych, prawie posortowanych podzadan
    public void quickSort(){
        //sortujemy zawsze po wartosci o indeksie 0 (bo wczesniej zamienilismy kolumny)
        int low = 0, i=0;
        int high = rowsNumber-1;
        int pivot;
        while(true){
            i--;
            while(high - low > 5){ //wykonuj dla podzadan o dlugosci > 5
                pivot = partition(low, high);
                codeCharacter(high);
                high = pivot-1;
                ++i;
            }
            if(high > low) selectionSort(low,high);
            if(i<0) break;
            low++;
            high = findNext(low, rowsNumber);
            uncodeCharacter(high);
        }
    }
    private void uncodeCharacter(int index){
        if(numeric){
            int num = Integer.parseInt(data[index][0]);
            num = -num;
            data[index][0] = String.valueOf(num);
        }
        else{
            data[index][0] = data[index][0].replaceFirst("]", "");
        }
    }
    private void codeCharacter(int index){
        if(numeric){
            int num = Integer.parseInt(data[index][0]);
            num = -num;
            data[index][0] = String.valueOf(num);
        }
        else{
            String oznakowany = "]"+data[index][0]; //sprawdzic inne wartosci?
            data[index][0] = oznakowany;
        }
    }
    private int findNext(int low, int rowsNumber){
        if(!numeric) { //dla stringow
            for (int i = low; i < rowsNumber; i++) {
                if(data[i][0].charAt(0) == ']') return i;
            }
        }
        else{ //dla liczb
            int num;
            for (int i = low; i < rowsNumber; i++) {
                num = Integer.parseInt(data[i][0]);
                if(num < 0) return i;
            }
        }
        return rowsNumber-1;
    }
    private int partition(int low, int high){
        String[] pivot = data[(low+high)/2];
        while(low<= high){
            if(sortingOrder){
                while(compareRows(data[high], pivot) > 0) high--;
                while(compareRows(data[low], pivot) < 0) low ++;
            }
            else{
                while(compareRows(data[high], pivot) < 0) high--;
                while(compareRows(data[low], pivot) > 0) low++;
            }
            if(low <= high){
                swapRows(low, high);
                low++;
                high--;
            }
        }
        return low;
    }
    private void selectionSort(int low, int high){
        int minimum; //maximum w sortowaniu malejacym
        for(int i=low; i<=high; i++){
            minimum = i;
            if(sortingOrder){
                for(int j=i+1; j<=high; j++){
                    if(compareRows(data[j],  data[minimum]) < 0) minimum = j;
                }
            }
            else{
                for(int j=i+1; j<=high; j++){
                    if(compareRows(data[j],  data[minimum]) > 0) minimum = j;
                }
            }
            swapRows(i,minimum);
        }
    }
    //porownojemy wiersze na podstawie 1 kolumny; inty parsujemy a Stringi porownojemy dzieki "compareTo"
    private int compareRows(String[] row1, String[] row2){
        String value1 = row1[0];
        String value2 = row2[0];
        if(numeric){
            int num1 = Integer.parseInt(value1);
            int num2 = Integer.parseInt(value2);
            return Integer.compare(num1, num2);
        }
        else {
            return value1.compareTo(value2);
        }
    }
    private void swapRows(int i, int j){
        String[] tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }
}

//INPUT:
//        1
//        15,2,-1
//        Album,Year,Songs
//        abc,1990,1
//        abca,1992,2
//        abcd,1991,5
//        asd,1993,6
//        dds,1481,7
//        assad,12421,23
//        asdadd,1323,8
//        dko,23110,24
//        jdasnin,321,9
//        sndank,572,123
//        kansd,1520,41
//        jfkak,2020,32
//        cdijji,2021,42
//        nawmdm asdkl,12657,3
//        kasnd j,8890,64
//OUTPUT
//        Year,Album,Songs
//        23110,dko,24
//        12657,nawmdm asdkl,3
//        12421,assad,23
//        8890,kasnd j,64
//        2021,cdijji,42
//        2020,jfkak,32
//        1993,asd,6
//        1992,abca,2
//        1991,abcd,5
//        1990,abc,1
//        1520,kansd,41
//        1481,dds,7
//        1323,asdadd,8
//        572,sndank,123
//        321,jdasnin,9
