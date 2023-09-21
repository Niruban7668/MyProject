import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.lang.Math;

class RSA {
	int p,q,n,e,k,d,fi_n;
	char[] hexd= {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	RSA(int a,int b) {
		p=prime_gen(a);
		q=prime_gen(b);
		if(p==q) {q=prime_gen(q+1);}
		n=p*q;
		fi_n=(p-1)*(q-1);
		e=p+q;
		k=1;
		while(true) {
			if(gcd(e,n)==1 & gcd(e,fi_n)==1) {break;}
			e++;
		}
		if(e>=fi_n) {
			e=p+q;
			while(true) {
				if(gcd(e,n)==1 & gcd(e,fi_n)==1) {break;}
				e--;
			}
			if(e<2){System.out.println("  n-"+n+"  fi-"+fi_n+"  e-"+e+"  p-"+p+"  q-"+q);}
		}
		while((k*fi_n+1)%e!=0) {k++;}
		d=(k*fi_n+1)/e;
	}
	public boolean primecheck(int n) {
		for(int i=2;i<=Math.sqrt(n);i++) {if(n%i==0) {return false;}}
		return true;
	}
	public int prime_gen(int n) {
		while(true){
			if(primecheck(n)) {return n;}
			n++;
		}
	}
	
	public int gcd(int a,int b) {
		if(a==0) {return b;}
		return gcd(b%a,a);
	}

	
	public int enc(int pt) {
		long r=1;
		for(int i=e;i>0;i--) {r=r*pt%n;}
		return (int)r;
	}
	public int dec(int ct) {
		long r=1;
		for(int i=d;i>0;i--) {r=r*ct%n;}
		return (int)r;
	}
	public int[] tonum(String a) {
		int[] b=new int[a.length()];
		for(int i=0;i<a.length();i++) {b[i]=(int)a.charAt(i);}
		return b;
	}
	public int[] encrypt(int[] a) {
		int[] b=new int[a.length];
		for(int i=0;i<a.length;i++) {b[i]=enc(a[i]);}
		return b;
	}
	public int[] decrypt(int[] a) {
		int[] b=new int[a.length];
		for(int i=0;i<a.length;i++) {b[i]=dec(a[i]);}
		return b;
	}

	public int[] encrypt(String a) {return encrypt(tonum(a));}

	public String intToStr(int[] a) {
		String str="";
		for(int i:a) {
			String st="";
			do {
				st=hexd[i%16]+st;i=i/16;
			}while(i!=0);
			str+=st+"-";
		}
		return str;
	}
	public int[] strToInt(String a) {
		int c=0;
		for(int i=0;i<a.length();i++) {if(a.charAt(i)=='-') {c++;}}
		int[] r=new int[c];
		int n=0,m=0;
		for(int i=0;i<a.length();i++) {
			char ch=a.charAt(i);
			if(ch=='-') {r[m++]=n;n=0;continue;}
			for(int j=0;j<16;j++) {
				if(hexd[j]==ch) {n=n*16+j;}
			}
		}
		return r;
	}
	public String toStr(int[] a){
		String s="";
		for(int i:a){s+=(char)i;}
		return s;
	}
}


public class Client extends Thread{
	static Socket s;
	static DataOutputStream dout;
	static DataInputStream din;
	static int[] pub_key=new int[4];
	static int ka=1,kb=1,port;
	static RSA rsa;
	static void startConnection(){
		try {
			Scanner sc=new Scanner(System.in);
			System.out.print(" Server IP = ");
			String ip=sc.nextLine();
			System.out.print(" Port = ");
			port=sc.nextInt();
			s=new Socket(ip,port);
			dout=new DataOutputStream(s.getOutputStream());
			din=new DataInputStream(s.getInputStream());
			System.out.println("Connectef to server ... ");
		}
		catch(ConnectException e) {System.out.println("Server/Port not availabe for connection....\nTry again.. ");startConnection();}
		catch(Exception e) {System.out.println("error : "+e);}
	}

	static void receive_pubkey(){
		try{
			int i=0;
			System.out.print("Public Key from Server : ");
			while(i<4){
				if(din.available()!=0){
					int ii=din.readInt();
					System.out.print(ii+" ");
					pub_key[i++]=ii;
				}
			}
			System.out.println(" ");
		}	
		catch(Exception e) {System.out.println("Error : "+e);}
	}

	static void key_exchange(){
		int a=(int)(Math.random()*(pub_key[0]-10)+9);
		int b=(int)(Math.random()*(pub_key[2]-10)+9);
		System.out.println("a="+a+" b="+b);
		int ya=1,yb=1,xa=0,xb=0;
		for(int i=0;i<a;i++){ya=ya*pub_key[1]%pub_key[0];}
		for(int i=0;i<b;i++){yb=yb*pub_key[3]%pub_key[2];}
		try{
			dout.writeInt(ya);dout.flush();
			dout.writeInt(yb);dout.flush();
			xa=din.readInt();xb=din.readInt();
		}
		catch(Exception e) {System.out.println("Error : "+e);}
		for(int i=0;i<a;i++){ka=ka*xa%pub_key[0];}
		for(int i=0;i<b;i++){kb=kb*xb%pub_key[2];}
		if(ka<10){ka*=5;}
		if(ka<10){kb*=5;}
		if(ka<20){ka*=3;}
		if(kb<20){kb*=3;}
	}

	static void printReceived(){
		try{while(true){System.out.println("Redevied : "+rsa.toStr(rsa.decrypt(rsa.strToInt(din.readUTF()))));}}
		catch(Exception e) {System.out.println("Error : "+e);}
	}

	public void run(){printReceived();}

	public static void main(String[] args) throws Exception {
		startConnection();
		receive_pubkey();
		key_exchange();
		System.out.println(ka+" "+kb);
		rsa=new RSA(ka,kb);
		
		String str="Naveen";
		for(int i:rsa.tonum(str)){System.out.print(i+" ");}
		System.out.println("");
		for(int i:rsa.encrypt(str)){System.out.print(i+" ");}
		System.out.println("");
		for(int i:rsa.decrypt(rsa.encrypt(str))){System.out.print(i+" ");}
		System.out.println("\n");

		Client C=new Client();
		C.start();
		ServerSocket in_ss=new ServerSocket(port+1);
		try{Runtime.getRuntime().exec("cmd /c start ClientInput.bat "+String.valueOf(port+1));Thread.sleep(5000);}
		catch(Exception e){System.out.println(e);}
		
		Socket ins=in_ss.accept();
		DataInputStream in_dis=new DataInputStream(ins.getInputStream());
		
		try{
			while(true){
				String in_str=in_dis.readUTF();
				System.out.println("   Sent : "+in_str);
				dout.writeUTF(rsa.intToStr(rsa.encrypt(in_str)));
				dout.flush();
			}
		}
		catch(Exception e) {System.out.println("Error : "+e);}
		
	}
	
}
