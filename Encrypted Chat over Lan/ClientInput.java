import java.io.*;
import java.net.*;
import java.util.Scanner;

class ClientInput{
	public static void main(String[] args){
		Scanner sc=new Scanner(System.in);
		try{
			Socket s=new Socket("localhost",Integer.parseInt(args[0]));
			DataOutputStream dout=new DataOutputStream(s.getOutputStream());
			while(true){dout.writeUTF(sc.nextLine());
new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();}
		}
		catch(Exception e){System.out.println(e);}
		sc.nextLine();
	}
}