import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.lang.Math;

class Connect extends Thread {
	String name;
	int port;
	Socket s;
	DataInputStream dis;
	DataOutputStream dout;
	ServerSocket ss;
	int[] pub_key;
	
	Connect(int p,String n,int[] k){port=p;name=n;pub_key=k;}

	public void run(){
		try{
			ss=new ServerSocket(port);
			System.out.println(name+" : Connection waiting....");
			s=ss.accept();
			dis=new DataInputStream(s.getInputStream());
			dout=new DataOutputStream(s.getOutputStream());
			System.out.println(name+" : Connection Sucess.");
			Thread.sleep(10);
			for(int i:pub_key){dout.writeInt(i);dout.flush();}
			
			System.out.println(name+" : Public Keys sent.");
		}
		catch(Exception e){System.out.println("Error : "+e);}
	}
	
	
}

class Data_Transfer extends Thread{
	String fname,tname;
	DataInputStream dis;
	DataOutputStream dout;
	Data_Transfer (Connect c,Connect d){
			fname=c.name;tname=d.name;
			dis=c.dis;dout=d.dout;
	}
	public void run(){
		try{
			int x=dis.readInt(),y=dis.readInt();
			dout.writeInt(x);dout.flush();
			dout.writeInt(y);dout.flush();
			System.out.println("Key Exchange : [ "+fname+" -> "+tname+" ] : "+x+" "+y);
			while(true){
				String str=dis.readUTF();
				dout.writeUTF(str);dout.flush();
				System.out.println("[ "+fname+" -> "+tname+" ] : "+str);
			
			}
		}
		catch(SocketException e){System.out.println("[ "+fname+" ] : "+"Connection lost...");}
		catch(Exception e){System.out.println("Error : "+e);}
	}
}

public class Server{
	static boolean primecheck(int n) {
		for(int i=2;i<=Math.sqrt(n);i++) {if(n%i==0) {return false;}}
		return true;
	}
	static int prime_gen() {
		int n=(int) (Math.random()*100+20);
		while(true){
			if(primecheck(n)) {return n;}
			n++;
		}
	}
	static int[] public_key(){
		int p=prime_gen(),q=prime_gen();
		int[] k={p,(int)(Math.random()*(p-1)+1),q,(int)(Math.random()*(q-1)+1)};
		return k;
	}
	public static void main(String[] args){
		

		// gen pub key
		int[] k=public_key();

		String ip="not found";
		try{ip=InetAddress.getLocalHost().getHostAddress().trim();}	
		catch(Exception e) {System.out.println("error : "+e);}
		System.out.print("\nStarting Server.\n\n"+ip+"\n\nPublic Keys :  ");
		for(int i : k){System.out.print(i+" ");}
		System.out.println("\n");

		// Connection & Pub keys Transfer
		Connect a=new Connect(1234,"Alice",k);
		Connect b=new Connect(5678,"Bob  ",k);
		a.start();
		b.start();
		try{a.join();b.join();}	
		catch(Exception e) {System.out.println("error : "+e);}

		System.out.println("Connection Established Successfully.\n");
		//starting data transfer
		Data_Transfer DT1= new Data_Transfer(a,b);
		Data_Transfer DT2= new Data_Transfer(b,a);
		DT1.start();
		DT2.start();
		try{DT1.join();DT2.join();Thread.sleep(2000);}
		catch(Exception e) {System.out.println("error : "+e);}

	}
}
