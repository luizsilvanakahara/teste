import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.*;

import java.util.List;
import java.util.Iterator;

public class exemplo {

  public static void main(String[] args) {
    Scanner ler = new Scanner(System.in);
    

    //LinkedList<String> agenda=new LinkedList<String>();  
    String[] agenda = new String[100000];

    System.out.printf("Informe o nome de arquivo texto:\n");
    String nome = ler.nextLine();
    int lon=0;
    System.out.printf("\nConteúdo do arquivo texto:\n");
    try {
      FileReader arq = new FileReader(nome);
      BufferedReader lerArq = new BufferedReader(arq);
      String cada = new String();

      String linha = lerArq.readLine();// lê a primeira linha
      agenda[lon]=linha;
// @a variável "linha" recebe o valor "null" quando o processo
// de repetição atingir o final do arquivo texto
      while (linha != null) {
        System.out.printf("%s\n", linha);
        System.out.printf("%s\n", agenda[lon]);

        linha = lerArq.readLine(); // lêc da segunda até a última linha
               
        agenda[lon]=linha;
        
      }
      int conta = 0,i;
       
      
       

        // [ C ] mostrando os "n" contatos da agenda (usando o índice)
        // número de elementos da agenda: método size()
        System.out.printf("Percorrendo o ArrayList (usando o índice)\n");
        //int n = agenda.lastIndexOf();
        for(i=1;i<100000-1;i++) {
          System.out.printf("Posição - %d\n", i);
          cada  = agenda[i]; 
          System.out.printf("Posição - %s\n", agenda[i]);
          
          if(agenda[i].substring(agenda[i].length()-1)==("a"));
        		  
        	  
        		  conta++;
        		  
        }

        System.out.printf("\nInforme a posição a ser excluída:%d\n",conta);
       // i = ler.nextInt();
        
      

      arq.close();
    } catch (IOException e) {
        System.err.printf("Erro na abertura do arquivo: %s.\n",
          e.getMessage());
    }

    System.out.println();
  }
}

