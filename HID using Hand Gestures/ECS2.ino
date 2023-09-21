#include <MPU6050_tockn.h>
#include <Wire.h>
#include <Keyboard.h>
#include <Mouse.h>

MPU6050 mpu6050(Wire);
float x, y,ix, iy;
int xx,a,f1,f2,f3,fs1,fs2,fs3,cl,nl,mx,my,mode=0,mode2=0,mode1=0,power;
long c;
int d=0;
char b[5][6]={"idle","up","right","down","left"};
char ch[52]="URDL0123456789!@#$%^&*()-_=+[{]}\\|:;'\",<.>?/`~654321";


void keypress(char i){
  Keyboard.press(i);
  delay(10);
  Keyboard.releaseAll();  
}
void keypress2(int i,int f){
  Keyboard.press(i);
  if(f==1){
    delay(10);
    Keyboard.releaseAll();  
  }    
}

void nfs(){
  mpu6050.update();
  y = (mpu6050.getAccAngleY() - iy);
  x = (mpu6050.getAccAngleX() - ix);  
  flexsensor();
  if(y<-20){
    a=1;}
  if(x>15){
    a=2;}
  else if(x<-15){
    a=4;//left
    }    
 else{if(y>=-20){d=0;a=0;relarr2();}}//reset    
  
if(a==1){Keyboard.press(0xD9);}
else{Keyboard.release(0xD9);}
if(a==2){Keyboard.press(0xD7);}
else{Keyboard.release(0xD7);}
if(a==4){Keyboard.press(0xD8);}
else{Keyboard.release(0xD8);}
if(f3==1){Keyboard.press(0xB0);}
else{Keyboard.release(0xB0);}
if(f2==1){Keyboard.press(0xDA);}
else{Keyboard.release(0xDA);}
if(f1==0){Keyboard.press('w');}
else{Keyboard.release('w');}
delay(30);
}



void relarr(){Keyboard.release(0xDA);Keyboard.release(0xD7);Keyboard.release(0xD8);Keyboard.release(0xD9);}
void relarr2(){Keyboard.release(0xD7);Keyboard.release(0xD8);Keyboard.release(0xD9);}
void act(int i){
  if(i<26){    
    if(cl==0&nl==0){keypress(char(97+i));}
    else if(cl==1&nl==0){keypress(char(65+i));cl=0;}
    else{
      i+=26*cl;
      if(i==0){keypress2(0xDA,0);}
      else if(i==1){keypress2(0xD7,0);}
      else if(i==2){keypress2(0xD9,0);}
      else if(i==3){keypress2(0xD8,0);}
      else{keypress(ch[i]);}
      if(i>13){nl=0;cl=0;}
    }
  }
  if(i==26){cl=(cl+1)%2;}
  if(i==27){nl=(nl+1)%2;}
  if(i==28){
    //Serial.println("Space");
    keypress(' ');}
  if(i==29){
    //Serial.println("Enter")
    keypress2(0xB0,1);
          }
  if(i==30){
    keypress2(0xB2,1);
    }
  if(i==31){
    Keyboard.write(" Menu ");
    }  
}


void q(){if(xx<26){act(xx);xx++;q();}}
void flexsensor(){
  fs1=analogRead(8);
  fs2=analogRead(9);
  fs3=analogRead(10);
  if(fs1>550){f1=1;}
  if(fs1<500){f1=0;}
  if(fs2>600){f2=1;}
  if(fs2<550){f2=0;}
  if(fs3>520){f3=1;}
  if(fs3<470){f3=0;}
}
void keysetup(){    
nl=0;  
cl=0;
  mpu6050.update();
  iy = mpu6050.getAccAngleY();
  ix = mpu6050.getAccAngleX();
}
void keyloop() {  
  mpu6050.update();
  y = (mpu6050.getAccAngleY() - iy);
  x = (mpu6050.getAccAngleX() - ix);  
  flexsensor();
  //Serial.print(x);Serial.print("\t");Serial.print(y);Serial.print("\t");Serial.print(f1);Serial.print(f2);Serial.println(f3);
  if(y>18){
    if (a!=3){a=3;c=millis();}//down
    }
  else if(y<-20){
    if (a!=1){a=1;c=millis();}//up
    }
  else if(x>25){
    if (a!=2){a=2;c=millis();}//right
    }
  else if(x<-30){
    if (a!=4){a=4;c=millis();}//left
    }    
 if(x>-10&x<10&y<10&y>-10) {d=0;a=0;relarr();}//reset    
  if((millis()-c)>50){  
      if(d!=a){d=a;
        //Serial.print(b[a]);
        xx=f1*16+f2*8+f3*4+a-1;
      //  Serial.println(xx);
    //    delay(3000);
        act(xx);
        //Serial.println(char(64+f3*4+a));
        //Serial.println("\n");
      }
  }
}

void mouseloop(){
  mpu6050.update();
  mx=mpu6050.getAccAngleX()/3;
  my=mpu6050.getAccAngleY()/1.5;
  flexsensor();
  if(f3==1){
    if(!Mouse.isPressed(1)){Mouse.press(1);}   
  } 
  else{Mouse.release(1);} 
  if(f2==1){
    if(!Mouse.isPressed(2)){Mouse.press(2);}   
  } 
  else{Mouse.release(2);} 
  if(mx<0){mx*=3;}
  if(abs(mx)>3 | abs(my)>3 | abs(x)+abs(my)>5){
  Mouse.move(mx,my);
  }
  delay(30);  
}

void setup() {
  pinMode(0,1);
  pinMode(1,1);  
  pinMode(9,0);
  pinMode(10,0);
  pinMode(8,0);
  pinMode(12,0);
  pinMode(13,0);
  Serial.begin(9600);
  Wire.begin();
  Mouse.begin();
  mpu6050.begin();
  delay(2500);
  Serial.println("Program Started....\n");  
keysetup();    
}
void dp(){
Serial.print(a);
Serial.print("\t");
  Serial.print(f3);Serial.print("\t");
Serial.print(f2);Serial.print("\t");
Serial.println(f1);  
}
void loop(){
  flexsensor();
  dp();
  mode2=digitalRead(12);
  power=digitalRead(13);
  if(power==0){
    if(mode2!=mode1){
      mode1=mode2;
      if(mode2==1){
        Keyboard.releaseAll();
        mode=(mode+1)%3;        
digitalWrite(0,0);digitalWrite(1,0);
delay(500);        
        if(mode==1 | mode==2){keysetup();}
      }
    } 
  Serial.println(mode);     
  if(mode==0){mouseloop();digitalWrite(0,1);digitalWrite(1,0);}
  if(mode==1){keyloop();digitalWrite(0,0);digitalWrite(1,1);}
  if(mode==2){nfs();digitalWrite(0,1);digitalWrite(1,1);}
  }
else {        Keyboard.releaseAll();
delay(50);digitalWrite(0,0);digitalWrite(1,0);keysetup();}  
}
